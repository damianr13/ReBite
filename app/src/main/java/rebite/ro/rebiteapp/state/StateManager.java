package rebite.ro.rebiteapp.state;

import android.content.Context;

import rebite.ro.rebiteapp.users.UserInfo;


public class StateManager {
    private static final Object LOCK = new Object();
    private static StateManager INSTANCE;

    private Context mContext;
    private UserInfo mUserInfo;

    private StateManager(Context context) {
        mContext = context;
    }

    public static StateManager getInstance(Context context) {
        synchronized (LOCK) {
            if (INSTANCE == null || !INSTANCE.mContext.equals(context)) {
                INSTANCE = new StateManager(context);
            }

            return INSTANCE;
        }
    }

    public void setExtraUserInfo(UserInfo userInfo) {
        mUserInfo = userInfo;
    }

    public boolean hasAdminRights() {
        return mUserInfo.getCurrentUserRoles().contains(UserInfo.Role.ADMIN);
    }
}
