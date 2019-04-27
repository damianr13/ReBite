package rebite.ro.rebiteapp.maps;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult.Callback;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rebite.ro.rebiteapp.R;
import rebite.ro.rebiteapp.utils.PermissionHandler;

public class GoogleMapController implements LocationListener, Callback<DirectionsResult> {

    private static final float MAP_DEFAULT_ZOOM = 15f;
    private static final String TAG = GoogleMapController.class.getName();

    private GoogleMap mGoogleMap;
    private Activity mActivity;

    private Location mLastKnownLocation;

    public GoogleMapController(Activity activity, GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mActivity = activity;
    }

    @SuppressLint("MissingPermission")
    public void startListeningForLocation() {
        LocationManager locationManager = (LocationManager) mActivity
                .getSystemService(Context.LOCATION_SERVICE);
        if (!PermissionHandler.hasAccessToLocation(mActivity)) {
            PermissionHandler.askForAccessToLocation(mActivity);
            startListeningForLocation();
            return ;
        }

        if (locationManager == null) {
            Log.w(TAG, "Unable to get access to a LocationManager");
            return ;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                0, 0, this);
        mLastKnownLocation = getCurrentLocation(locationManager);
        animateMapTo(mLastKnownLocation);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastKnownLocation = location;
        animateMapTo(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public void animateMapTo(Location location) {
        LatLng center = new LatLng(location.getLatitude(), location.getLongitude());
        animateMapTo(center);
    }

    public void animateMapToCurrentLocation() {
        animateMapTo(getCurrentLocation());
    }

    private void animateMapTo(LatLng currentPosition) {
        if (mGoogleMap != null) {
            mGoogleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(currentPosition, MAP_DEFAULT_ZOOM));
        }
    }

    private GeoApiContext getGeoContext() {
        return new GeoApiContext()
                .setApiKey(mActivity.getString(R.string.google_maps_key))
                .setQueryRateLimit(3)
                .setConnectTimeout(3, TimeUnit.SECONDS)
                .setReadTimeout(3, TimeUnit.SECONDS)
                .setWriteTimeout(3, TimeUnit.SECONDS);
    }

    public void showDirectionsTo(LatLng destination) {
        com.google.maps.model.LatLng origin = new com.google.maps.model.LatLng(
                mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
        DirectionsApi.newRequest(getGeoContext())
                .mode(TravelMode.WALKING)
                .origin(origin)
                .destination(convertLatLng(destination))
                .setCallback(this);
    }

    private static com.google.maps.model.LatLng convertLatLng(LatLng input) {
        return new com.google.maps.model.LatLng(input.latitude, input.longitude);
    }

    private static LatLng convertLatLng(com.google.maps.model.LatLng input) {
        return new LatLng(input.lat, input.lng);
    }

    private static List<LatLng> convertLatLng(List<com.google.maps.model.LatLng> inputs) {
        ArrayList<LatLng> result = new ArrayList<>();
        for (com.google.maps.model.LatLng input : inputs) {
            result.add(convertLatLng(input));
        }
        return result;
    }

    /**
     * Returns the current location of the device as indicated by the most accurate available
     * provider.
     *
     * This method assumes you already asked for Location permissions
     * See: <a href="https://stackoverflow.com/questions/20438627/getlastknownlocation-returns-null">
     *     this StackOverflow answer</a>
     * @return current location of the device
     */
    @SuppressLint("MissingPermission")
    private Location getCurrentLocation(LocationManager locationManager) {
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() > bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    private Location getCurrentLocation() {
        LocationManager locationManager = (LocationManager) mActivity
                .getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            return null;
        }
        return getCurrentLocation(locationManager);
    }

    @Override
    public void onResult(DirectionsResult result) {
        List<LatLng> decodedPath = convertLatLng(result.routes[0].overviewPolyline.decodePath());
        mActivity.runOnUiThread(() -> mGoogleMap.addPolyline(new PolylineOptions().addAll(decodedPath)));
    }

    @Override
    public void onFailure(Throwable e) {
        Log.e(TAG, "Unable to fetch directions", e);
    }
}
