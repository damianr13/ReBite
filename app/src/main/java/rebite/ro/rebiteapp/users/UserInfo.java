package rebite.ro.rebiteapp.users;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused", "WeakerAccess"})
public class UserInfo {

    private static final String TAG = UserInfo.class.getName();

    public enum Role {
        ADMIN
    }

    @PropertyName("roles")
    public List<String> roles;

    @PropertyName("display_name")
    public String displayName;

    @PropertyName("email")
    public String email;

    public UserInfo() {
        this(null, null);
    }

    private UserInfo(String displayName, String email) {
        this.displayName = displayName;
        this.email = email;
        roles = new ArrayList<>();
    }

    public UserInfo(@NonNull ProfileInfoProvider infoProvider) {
        this(infoProvider.getDisplayName(), infoProvider.getEmail());
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
