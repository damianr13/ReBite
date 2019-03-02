package rebite.ro.rebiteapp.login.email;

import android.net.Uri;

import rebite.ro.rebiteapp.users.ProfileInfoProvider;

public class EmailProfileInfoProvider extends ProfileInfoProvider {

    @Override
    public Uri getProfilePictureUri() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return "";
    }

    @Override
    public String getEmail() {
        return "";
    }
}
