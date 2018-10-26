package rebite.ro.rebiteapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import rebite.ro.rebiteapp.maps.GoogleMapController;
import rebite.ro.rebiteapp.utils.PermissionHandler;

import static rebite.ro.rebiteapp.utils.PublicKeys.LAT_LNG_DESTINATION_KEY;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback{


    private GoogleMap mGoogleMap;
    private GoogleMapController mMapController;

    private LatLng mDestinationLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mDestinationLatLng = getIntent().getParcelableExtra(LAT_LNG_DESTINATION_KEY);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        if (!PermissionHandler.hasAccessToLocation(this)) {
            PermissionHandler.askForAccessToLocation(this);
            onMapReady(googleMap);
            return ;
        }

        mGoogleMap.setMyLocationEnabled(true);
        mMapController = new GoogleMapController(this, googleMap);
        mMapController.startListeningForLocation();

        mGoogleMap.addMarker(new MarkerOptions().position(mDestinationLatLng));
        mMapController.showDirectionsTo(mDestinationLatLng);
    }
}
