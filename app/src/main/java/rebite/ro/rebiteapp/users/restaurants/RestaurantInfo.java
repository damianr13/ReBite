package rebite.ro.rebiteapp.users.restaurants;

import com.google.firebase.firestore.PropertyName;

public class RestaurantInfo {

    @PropertyName("address")
    public String address;

    @PropertyName("name")
    public String name;

    @PropertyName("image")
    public String image;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RestaurantInfo that = (RestaurantInfo) o;

        if (address != null ? !address.equals(that.address) : that.address != null) return false;
        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        int result = address != null ? address.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
