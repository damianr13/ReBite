package rebite.ro.rebiteapp.login.cache

import android.net.Uri
import rebite.ro.rebiteapp.users.ProfileInfoProvider

class CacheProfileInfoProvider(displayName: String, email: String, profilePictureURL: String) : ProfileInfoProvider() {

    private val mDisplayName = displayName
    private val mEmail = email
    private val mProfilePictureURL = profilePictureURL

    override fun getDisplayName(): String {
        return mDisplayName
    }

    override fun getEmail(): String {
        return mEmail
    }

    override fun getProfilePictureUri(): Uri {
        return Uri.parse(mProfilePictureURL)
    }
}