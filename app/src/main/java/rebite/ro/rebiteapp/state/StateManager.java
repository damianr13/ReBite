package rebite.ro.rebiteapp.state;

import android.content.Context;

import rebite.ro.rebiteapp.users.UserInfo;
import rebite.ro.rebiteapp.users.restaurants.RestaurantInfo;


public class StateManager {
    private static final Object LOCK = new Object();
    private static StateManager INSTANCE;

    private UserInfo mUserInfo;

    private StateManager() {
    }

    public static StateManager getInstance() {
        synchronized (LOCK) {
            if (INSTANCE == null) {
                INSTANCE = new StateManager();
            }

            return INSTANCE;
        }
    }

    public void setExtraUserInfo(UserInfo userInfo) {
        mUserInfo = userInfo;
    }

    public RestaurantInfo getRestaurantInfo() {
        if (mUserInfo == null) {
            return null;
        }

        return mUserInfo.restaurantInfo;
    }

    public boolean hasAdminRights() {
        return mUserInfo.getCurrentUserRoles().contains(UserInfo.Role.ADMIN);
    }
}
