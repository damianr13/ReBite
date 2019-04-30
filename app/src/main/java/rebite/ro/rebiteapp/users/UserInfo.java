package rebite.ro.rebiteapp.users;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import rebite.ro.rebiteapp.users.restaurants.RestaurantInfo;

@SuppressWarnings({"unused", "WeakerAccess"})
public class UserInfo {

    private static final String TAG = UserInfo.class.getName();

    public enum Role {
        ADMIN, RESTAURANT
    }

    @PropertyName("roles")
    public List<String> roles;

    @PropertyName("display_name")
    public String displayName;

    @PropertyName("email")
    public String email;

    @Nullable @PropertyName("restaurant_info")
    public RestaurantInfo restaurantInfo;

    public UserInfo() {
        this("","");
    }

    private UserInfo(String displayName, String email) {
        this.displayName = displayName;
        this.email = email;
        roles = new ArrayList<>();
    }

    public UserInfo(@NonNull ProfileInfoProvider infoProvider) {
        this(infoProvider.getDisplayName(), infoProvider.getEmail());
    }

    public UserInfo(String email, RestaurantInfo restaurantInfo) {
        this(restaurantInfo.name, email);
        this.restaurantInfo = restaurantInfo;

        roles.add(Role.RESTAURANT.name());
    }

    @Exclude
    public List<Role> getCurrentUserRoles() {
        if (roles == null) {
            return new ArrayList<>();
        }

        List<Role> result = new ArrayList<>();

        for (String roleToken : roles) {
            try {
                result.add(Role.valueOf(roleToken));
            } catch (IllegalArgumentException ex) {
                Log.e(TAG, String.format("Unknown role: %s", roleToken));
            }
        }

        return result;
    }
}
