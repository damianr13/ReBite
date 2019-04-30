package rebite.ro.rebiteapp.users.restaurants;

import android.location.Location;

import com.google.firebase.firestore.PropertyName;

import org.parceler.Parcel;

@Parcel
public class SimpleLocation {

    @PropertyName("latitude")
    public double latitude;

    @PropertyName("longitude")
    public double longitude;

    public SimpleLocation() {
        latitude = 0;
        longitude = 0;
    }

    public SimpleLocation(Location l) {
        this.latitude = l.getLatitude();
        this.longitude = l.getLongitude();
    }
}
