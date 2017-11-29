package eric.myapplication.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import eric.myapplication.Database.TravelDBHelper;
import eric.myapplication.Misc.TSPFastSolver;
import eric.myapplication.Misc.TSPPath;
import eric.myapplication.Misc.TSPRoute;
import eric.myapplication.R;

import static eric.myapplication.Database.TravelContract.TravelEntry.MBS;

public class PlanMapsActivity extends AppCompatActivity implements OnMapReadyCallback, DirectionCallback {
    private GoogleMap mMap;
    private TSPRoute bestRoute;
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
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapsFragment);
        mapFragment.getMapAsync(this);

        // Get list of selected attractions from previous intent
        Intent intent = getIntent();
        selectedAttrNames = (ArrayList<String>)
                intent.getBundleExtra("LIST").getSerializable("SELECTED");

        if (selectedAttrNames != null) {
            for (String attrName : selectedAttrNames) {
                selectedAttrDBNames.add(attrName.replace("'", "").replace(" ", "_"));
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add azure marker to Marina Bay Sands first
        mMap.addMarker(new MarkerOptions().position(originLatLng).title("Marina Bay Sands"))
                .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

        List<Address> addressList;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            // Initializing list of waypoints
            for (String attrName : selectedAttrNames) {
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

        if (selectedAttrNames.isEmpty()) {
            double latitude = originLatLng.latitude;
            double longitude = originLatLng.longitude;
            double constant = 0.005;

            LatLng southwest = new LatLng(latitude - constant, longitude - constant);
            LatLng northeast = new LatLng(latitude + constant, longitude + constant);
            LatLngBounds bounds = new LatLngBounds(southwest, northeast);
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

            Snackbar.make(findViewById(android.R.id.content), "Empty list.",
                    Snackbar.LENGTH_LONG).show();
            return;
        }

        TravelDBHelper travelDBHelper = new TravelDBHelper(this);
        TSPFastSolver tspSolver = new TSPFastSolver(travelDBHelper.getReadableDatabase());
        bestRoute = tspSolver.findBestRoute(MBS, selectedAttrDBNames, 20);
        getDirections(bestRoute.getPaths());
    }

    private void getDirections(List<TSPPath> paths) {
        Snackbar.make(findViewById(android.R.id.content), "Getting Directions...",
                Snackbar.LENGTH_LONG).show();
        GoogleDirectionConfiguration.getInstance().setLogEnabled(true);

        /*
        // Set Marina Bay Sands as start
        DirectionDestinationRequest destinationRequest = GoogleDirection.withServerKey(serverKey)
                .from(originLatLng);
        // Add waypoints
        for (int i = 0; i < waypoints.size(); i++) {
            destinationRequest.and(waypoints.get(i));
        }
        // Set Marina Bay Sands as end
        destinationRequest.to(originLatLng)
                .transportMode(TransportMode.DRIVING)
                .execute(this);
        */

        TSPPath path = paths.get(0);
        String mode = determineMode(path.getTransportMode());
        Log.i("eric1", path.toString());

        GoogleDirection.withServerKey(serverKey)
                .from(originLatLng)
                .to(waypoints.get(0))
                .transportMode(mode)
                .transitMode(TransitMode.BUS)
                .execute(this);

        for (int i = 0; i < waypoints.size() - 1; i++) {
            path = paths.get(i + 1);
            mode = determineMode(path.getTransportMode());
            Log.i("eric1", path.toString());

            GoogleDirection.withServerKey(serverKey)
                    .from(waypoints.get(i))
                    .to(waypoints.get(i + 1))
                    .transportMode(TransportMode.TRANSIT)
                    .transitMode(TransitMode.BUS)
                    .execute(this);
        }
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        Log.i("eric1", "DirectionSuccess.");

        if (direction.isOK()) {
            Log.i("eric1", "DirectionOK.");
            // Add markers to all selected attractions
            for (int i = 0; i < waypoints.size(); i++) {
                LatLng attractionLatLng = waypoints.get(i);
                String attrName = selectedAttrNames.get(i);
                mMap.addMarker(new MarkerOptions().position(attractionLatLng).title(attrName));
            }

            Route route = direction.getRouteList().get(0);
            int legCount = route.getLegList().size();

            // Add route highlighting (RED for transit, BLUE for walking)
            for (int j = 0; j < legCount; j++) {
                Leg leg = route.getLegList().get(j);
                List<Step> stepList = leg.getStepList();
                ArrayList<PolylineOptions> polylineOptionsList = DirectionConverter
                        .createTransitPolyline(this, stepList, 5, Color.RED, 3, Color.BLUE);

                for (PolylineOptions polylineOptions : polylineOptionsList) {
                    // mMap.addPolyline(polylineOptions).setStartCap(new CustomCap(
                    //                 BitmapDescriptorFactory.fromResource(R.drawable.arrowhead), 21));
                    mMap.addPolyline(polylineOptions).setStartCap(new SquareCap());
                }
            }

            setCameraWithCoordinationBounds(route);

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

    // Setting camera to show the whole route
    private void setCameraWithCoordinationBounds(Route route) {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    private String determineMode(String transportMode) {
        String mode = TransportMode.DRIVING;

        if (transportMode.equals("BUS")) {
            mode = TransportMode.TRANSIT;
        } else if (transportMode.equals("WALK")) {
            mode = TransportMode.WALKING;
        }

        return mode;
    }
}
