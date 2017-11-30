package eric.myapplication.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.config.GoogleDirectionConfiguration;
import com.akexorcist.googledirection.constant.TransitMode;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import eric.myapplication.Adapter.DetailsListAdapter;
import eric.myapplication.Database.TravelDBHelper;
import eric.myapplication.Misc.TSPBruteForce;
import eric.myapplication.Misc.TSPFastSolver;
import eric.myapplication.Misc.TSPPath;
import eric.myapplication.Misc.TSPRoute;
import eric.myapplication.R;

import static eric.myapplication.Activity.PlanActivity.*;
import static eric.myapplication.Database.TravelContract.TravelEntry.MBS;

public class PlanMapsActivity extends AppCompatActivity implements OnMapReadyCallback, DirectionCallback {
    private GoogleMap mMap;
    private TSPRoute bestRoute;
    private Button detailsBtn;
    private double budget;
    private double elapsedTime;
    private boolean bfBool;

    // Origin set to Marina Bay Sands
    private List<LatLng> waypoints = new ArrayList<>();
    private ArrayList<String> selectedAttrNames;
    private List<String> selectedAttrDBNames = new ArrayList<>();
    private LatLng originLatLng = new LatLng(1.2845442, 103.8595898);

    private static final String serverKey = "AIzaSyCZaK53Pgt6k_ShHb2b7UeH-69aZ8Uf19Q";
    private static final float zoomLevel = 15;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapsFragment);
        mapFragment.getMapAsync(this);

        // Get budget and list of selected attractions from previous intent
        Intent intent = getIntent();
        String budgetStr = intent.getStringExtra(BUDGET_KEY);
        budget = budgetStr.equals("") ? 20 : Double.parseDouble(budgetStr);
        bfBool = intent.getBooleanExtra(BRUTE_FORCE_KEY, false);
        Log.i("eric1", bfBool + "");
        selectedAttrNames = (ArrayList<String>)
                intent.getBundleExtra(LIST_KEY).getSerializable(SELECTED_KEY);

        // Convert to DBNames as TravelDB uses a slightly different naming
        if (selectedAttrNames != null) {
            for (String attrName : selectedAttrNames) {
                selectedAttrDBNames.add(attrName.replace("'", "")
                        .replace(" ", "_"));
            }
        }

        // Show or hide the route details
        detailsBtn = findViewById(R.id.details_btn);
        detailsBtn.setText(R.string.show_details);
        detailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mapFragment.getView().getVisibility() == View.VISIBLE) {
                    mapFragment.getView().setVisibility(View.INVISIBLE);
                    detailsBtn.setText(R.string.hide_details);

                } else {
                    mapFragment.getView().setVisibility(View.VISIBLE);
                    detailsBtn.setText(R.string.show_details);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add azure marker to Marina Bay Sands first
        mMap.addMarker(new MarkerOptions().position(originLatLng).title("Marina Bay Sands"))
                .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

        TravelDBHelper travelDBHelper = new TravelDBHelper(this);
        List<Address> addressList;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        // Solve the Travelling Salesman Problem
        long startTime = System.nanoTime();

        if (bfBool) {
            TSPBruteForce tspBruteForce = new TSPBruteForce(travelDBHelper.getReadableDatabase());
            bestRoute = tspBruteForce.findBestRoute(MBS, selectedAttrDBNames, budget);
        } else {
            TSPFastSolver tspFastSolver = new TSPFastSolver(travelDBHelper.getReadableDatabase());
            bestRoute = tspFastSolver.findBestRoute(MBS, selectedAttrDBNames, budget);
        }

        long endTime = System.nanoTime();
        elapsedTime = (endTime - startTime) / 1000000000.0;

        try {
            // Initializing list of waypoints
            for (String attrDBName : bestRoute.getPlaces()) {
                String attrName = attrDBName.replace("_", " ");
                Log.i("eric1", attrName);
                addressList = geocoder.getFromLocationName(attrName + " Singapore", 1);
                double latitude = addressList.get(0).getLatitude();
                double longitude = addressList.get(0).getLongitude();

                LatLng attractionLatLng = new LatLng(latitude, longitude);
                waypoints.add(attractionLatLng);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            Log.i("eric1", "Error occured: " + ex.toString() + ".");
        }

        getDirections(bestRoute.getPaths());
    }

    private void getDirections(List<TSPPath> paths) {
        Snackbar.make(findViewById(android.R.id.content), "Getting Directions...",
                Snackbar.LENGTH_LONG).show();
        GoogleDirectionConfiguration.getInstance().setLogEnabled(true);

        // Google Direction does not allow same places in waypoints
        // So, the starting place (MBS) is taken out since its also the end
        waypoints.remove(0);

        TSPPath path = paths.get(0);
        String mode = determineMode(path.getTransportMode());

        GoogleDirection.withServerKey(serverKey)
                .from(originLatLng)
                .to(waypoints.get(0))
                .transportMode(mode)
                .transitMode(TransitMode.BUS)
                .execute(this);

        // Getting directions for subsequent waypoints
        for (int i = 0; i < waypoints.size() - 1; i++) {
            path = paths.get(i);
            mode = determineMode(path.getTransportMode());

            GoogleDirection.withServerKey(serverKey)
                    .from(waypoints.get(i))
                    .to(waypoints.get(i + 1))
                    .transportMode(mode)
                    .execute(this);
        }
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        Log.i("eric1", "DirectionSuccess.");

        if (direction.isOK()) {
            Log.i("eric1", "DirectionOK.");
            // Add markers to all selected attractions
            for (int i = 0; i < waypoints.size() - 1; i++) {
                LatLng attractionLatLng = waypoints.get(i);
                String attrName = bestRoute.getPlaces().get(i + 1).replace("_", " ");
                mMap.addMarker(new MarkerOptions().position(attractionLatLng).title(attrName));
            }

            Route route = direction.getRouteList().get(0);
            int legCount = route.getLegList().size();

            // Add route highlighting (RED for transit, BLUE for walking)
            for (int j = 0; j < legCount; j++) {
                Leg leg = route.getLegList().get(j);
                List<Step> stepList = leg.getStepList();
                ArrayList<PolylineOptions> polylineOptionsList = DirectionConverter
                        .createTransitPolyline(this, stepList,
                                5, Color.RED, 3, Color.BLUE);

                for (PolylineOptions polylineOptions : polylineOptionsList) {
                    // mMap.addPolyline(polylineOptions).setStartCap(new CustomCap(
                    //                 BitmapDescriptorFactory.fromResource(R.drawable.arrowhead), 21));
                    mMap.addPolyline(polylineOptions).setStartCap(new SquareCap());
                }
            }

            detailsBtn.setVisibility(View.VISIBLE);

            // Initialize ListView behind map
            DetailsListAdapter adapter = new DetailsListAdapter(this, bestRoute.getPaths());
            ListView detailsListView = findViewById(R.id.details_listview);
            detailsListView.setAdapter(adapter);

            // Set text for total details
            TextView totalText = findViewById(R.id.total);
            String totalTime = "Total time: " +
                    Math.round(bestRoute.getTimeWeight() * 1000.0) / 1000.0 + " min";
            String totalCost = "Total cost: " +
                    Math.round(bestRoute.getCostWeight() * 1000.0) / 1000.0 + " SGD";
            String computeTime = "Computation time: " +
                    Math.round(elapsedTime * 1000.0) / 1000.0 + " s";
            String totalStr = totalTime + "\n" + totalCost + "\n" + computeTime;
            totalText.setText(totalStr);

        } else {
            Snackbar.make(findViewById(android.R.id.content),
                    "Unable to find route connecting all waypoints.", Snackbar.LENGTH_INDEFINITE)
                    .show();
            Log.i("eric1", "DirectionError: " + direction.getStatus() + ".");
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {
        Log.i("eric1", "DirectionFailure.");
        Snackbar.make(findViewById(android.R.id.content), t.getMessage(),
                Snackbar.LENGTH_SHORT).show();
    }

    // Animating camera to show MBS
    private void setCamera() {
        CameraUpdate origCam = CameraUpdateFactory.newLatLngZoom(originLatLng, zoomLevel);
        mMap.animateCamera(origCam);
    }

    // Determine mode of transport
    private String determineMode(String transportMode) {
        String mode = TransportMode.DRIVING;

        if (transportMode.equals("PT")) {
            mode = TransportMode.TRANSIT;
        } else if (transportMode.equals("WALK")) {
            mode = TransportMode.WALKING;
        }

        return mode;
    }
}
