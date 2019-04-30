package rebite.ro.rebiteapp.users.restaurants;

import android.location.Location;

import com.google.firebase.firestore.PropertyName;

import org.parceler.Parcel;

import java.util.Objects;

@Parcel
public class RestaurantInfo {

    @PropertyName("address")
    public String address;

    @PropertyName("name")
    public String name;

    @PropertyName("image")
    public String image;

    @PropertyName("description")
    public String description;

    @PropertyName("location")
    public Location location;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RestaurantInfo that = (RestaurantInfo) o;

        if (!Objects.equals(address, that.address)) return false;
        if (!Objects.equals(name, that.name)) return false;
        if (!Objects.equals(image, that.image)) return false;
        if (!Objects.equals(description, that.description))
            return false;
        return location != null ? location.equals(that.location) : that.location == null;
    }

    @Override
    public int hashCode() {
        int result = address != null ? address.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (image != null ? image.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        return result;
    }
}
