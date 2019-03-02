package rebite.ro.rebiteapp.users.restaurants;

import com.google.firebase.firestore.PropertyName;

public class RestaurantInfo {

    @PropertyName("address")
    public String address;

    @PropertyName("name")
    public String name;
}
