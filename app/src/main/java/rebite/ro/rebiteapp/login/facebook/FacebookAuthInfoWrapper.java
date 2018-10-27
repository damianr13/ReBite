package rebite.ro.rebiteapp.login.facebook;

import com.facebook.AccessToken;

import org.json.JSONException;
import org.json.JSONObject;

public class FacebookAuthInfoWrapper {
    private AccessToken mAccessToken;
    private JSONObject mJSONObject;

    public FacebookAuthInfoWrapper(AccessToken token, JSONObject object) {
        mAccessToken = token;
        mJSONObject = object;
    }

    public AccessToken getAccessToken() {
        return mAccessToken;
    }

    public String getEmail() {
        try {
            return mJSONObject.getString(FacebookAuthenticationProvider.EMAIL_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
