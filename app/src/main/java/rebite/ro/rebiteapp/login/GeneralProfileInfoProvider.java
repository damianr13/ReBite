package rebite.ro.rebiteapp.login;

import android.net.Uri;

public class GeneralProfileInfoProvider implements ProfileInfoProvider {

    private static GeneralProfileInfoProvider INSTANCE;

    public static GeneralProfileInfoProvider getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GeneralProfileInfoProvider();
        }

        return INSTANCE;
    }

    private ProfileInfoProvider profileInfoProvider;

    private GeneralProfileInfoProvider() {

    }

    public void setProfileInfoProvider(ProfileInfoProvider profileInfoProvider) {
        this.profileInfoProvider = profileInfoProvider;
    }

    @Override
    public Uri getProfilePictureUri() {
        if (profileInfoProvider == null) {
            return null;
        }

        return profileInfoProvider.getProfilePictureUri();
    }

    @Override
    public String getDisplayName() {
        if (profileInfoProvider == null) {
            return null;
        }

        return profileInfoProvider.getDisplayName();
    }
}
