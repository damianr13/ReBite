package rebite.ro.rebiteapp.users.restaurants;

import android.location.Location;

public class SimpleLocation extends Location {

    public SimpleLocation() {
        super("Database");
    }

    public SimpleLocation(Location l) {
        super(l);
    }
}
