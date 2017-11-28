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
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.config.GoogleDirectionConfiguration;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.request.DirectionDestinationRequest;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.maps.model.SquareCap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import eric.myapplication.R;
import eric.myapplication.Misc.Attraction;

public class PlanMapsActivity extends AppCompatActivity implements OnMapReadyCallback, DirectionCallback {
    private GoogleMap mMap;
    private ArrayList<Attraction> selectedAttractions;
    // Origin set to Marina Bay Sands
    private LatLng originLatLng = new LatLng(1.2845442, 103.8595898);
    private List<LatLng> waypoints = new ArrayList<>();

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
        selectedAttractions = (ArrayList<Attraction>)
                intent.getBundleExtra("LIST").getSerializable("SELECTED");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        List<Address> addressList;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            for (Attraction attr : selectedAttractions) {
                addressList = geocoder.getFromLocationName(attr.getName()
                        + " Singapore", 1);
                double latitude = addressList.get(0).getLatitude();
                double longitude = addressList.get(0).getLongitude();

                // Initializing list of waypoints
                LatLng attractionLatLng = new LatLng(latitude, longitude);
                waypoints.add(attractionLatLng);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        getDirections();
    }

    // Add Marina Bay Sands and waypoints to the direction request
    private void getDirections() {
        Snackbar.make(findViewById(android.R.id.content), "Getting Directions...",
                Snackbar.LENGTH_LONG).show();
        GoogleDirectionConfiguration.getInstance().setLogEnabled(true);

        DirectionDestinationRequest destinationRequest = GoogleDirection.withServerKey(serverKey)
                .from(originLatLng);

        for (int i = 0; i < waypoints.size(); i++)
            destinationRequest.and(waypoints.get(i));

        destinationRequest.to(originLatLng)
                .transportMode(TransportMode.DRIVING)
                .execute(this);
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        Log.i("eric1", "DirectionSuccess");
        // Add azure marker to Marina Bay Sands first
        mMap.addMarker(new MarkerOptions().position(originLatLng).title("Marina Bay Sands"))
                .setIcon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

        if (direction.isOK()) {
            Log.i("eric1", "DirectionOK");
            // Add markers to all selected attractions
            for (int i = 0; i < waypoints.size(); i++) {
                LatLng attractionLatLng = waypoints.get(i);
                Attraction attr = selectedAttractions.get(i);
                mMap.addMarker(new MarkerOptions().position(attractionLatLng).title(attr.getName()));
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
                    // List<PatternItem> pattern = Arrays.asList(
                    //         new Dot(), new Gap(20), new Dash(30), new Gap(20));
                    mMap.addPolyline(polylineOptions).setStartCap(new RoundCap());
                }
            }

            if (waypoints.isEmpty()) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(originLatLng));
                mMap.moveCamera(CameraUpdateFactory.zoomTo(zoomLevel));
                Snackbar.make(findViewById(android.R.id.content), "No waypoints added.",
                        Snackbar.LENGTH_INDEFINITE).show();

            } else {
                setCameraWithCoordinationBounds(route);
            }
        } else {
            Snackbar.make(findViewById(android.R.id.content),
                    "Unable to find route connecting all waypoints.", Snackbar.LENGTH_INDEFINITE)
                    .show();
            Log.i("eric1", direction.getStatus());
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {
        Log.i("eric1", "DirectionFailure");
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
}
