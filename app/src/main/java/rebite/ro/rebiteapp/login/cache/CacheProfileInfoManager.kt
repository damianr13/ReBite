package rebite.ro.rebiteapp.login.cache

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import rebite.ro.rebiteapp.users.ProfileInfoProvider

class CacheProfileInfoManager(context: Context) {

    private val mContext = context
    private var mSharedPreferences : SharedPreferences

    init {
        mSharedPreferences = mContext.getSharedPreferences(PROFILE_INFO_PREFERENCES_KEY, MODE_PRIVATE)
    }

    fun cacheProfileInfoProvider(infoProvider: ProfileInfoProvider) {
        with(mSharedPreferences.edit()) {
            putString(DISPLAY_NAME_KEY, infoProvider.displayName)
            putString(EMAIL_KEY, infoProvider.email)
            putString(PROFILE_PICTURE_URL_KEY, infoProvider.profilePictureUri?.toString())

            apply()
        }
    }

    fun retrieveCachedProfileInfoProvider(): ProfileInfoProvider? {
        return mSharedPreferences.let {
            val displayName = it.getString(DISPLAY_NAME_KEY, null)
            val email = it.getString(EMAIL_KEY, null)
            val profilePictureURL = it.getString(PROFILE_PICTURE_URL_KEY, null)

            displayName?: return null
            email?: return null
            profilePictureURL?: return null

            CacheProfileInfoProvider(displayName, email, profilePictureURL)
        }
    }

    companion object {
        private const val PROFILE_INFO_PREFERENCES_KEY = "rebite.ro.rebiteapp.ProfileInfoPreferences"

        private const val DISPLAY_NAME_KEY = "DisplayName"
        private const val EMAIL_KEY = "email"
        private const val PROFILE_PICTURE_URL_KEY = "profilePictureURL"
    }
}