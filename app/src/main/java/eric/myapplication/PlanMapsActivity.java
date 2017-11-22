package eric.myapplication;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import eric.myapplication.misc.Attraction;

public class PlanMapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ArrayList<Attraction> selectedAttractions;

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

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        float zoomLevel = 11;

        List<Address> addressList;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            // Default camera to Marina Bay Sands if list is empty
            LatLng originLatLng = new LatLng(1.2845442, 103.8595898);
            mMap.addMarker(new MarkerOptions().position(originLatLng).title("Marina Bay Sands"))
                    .setIcon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(originLatLng));

            // Add markers to all selected attractions
            for (Attraction attr : selectedAttractions) {
                addressList = geocoder.getFromLocationName(attr.getName(), 1);
                double latitude = addressList.get(0).getLatitude();
                double longitude = addressList.get(0).getLongitude();
                Log.i("eric1", latitude + ", " + longitude);

                LatLng attractionLatLng = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(attractionLatLng).title(attr.getName
                        ()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(attractionLatLng));
            }

            mMap.moveCamera(CameraUpdateFactory.zoomTo(zoomLevel));

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
