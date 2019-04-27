package rebite.ro.rebiteapp.adapters

import android.content.Context
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.SparseArray
import rebite.ro.rebiteapp.R
import rebite.ro.rebiteapp.fragments.RestaurantOfferFragment
import rebite.ro.rebiteapp.offers.RestaurantOffer
import rebite.ro.rebiteapp.utils.RestaurantOffersManager

class OffersListPageAdapter(fm: FragmentManager, context: Context) : FragmentPagerAdapter(fm) {

    private var mAllRestaurantOffers : List<RestaurantOffer>? = null
    private var mFragmentsMap : SparseArray<RestaurantOfferFragment> = SparseArray()


    private val mContext: Context = context

    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): RestaurantOfferFragment {
        val result : RestaurantOfferFragment =  RestaurantOfferFragment.newInstance(
                getRestaurantOfferList(position))
        mFragmentsMap.put(position, result)
        return result
    }

    private fun getRestaurantOfferList(position: Int) : List<RestaurantOffer> {
        return when (position) {
            0 -> RestaurantOffersManager.filterAvailableOffers(mAllRestaurantOffers.orEmpty())
            1 -> RestaurantOffersManager.filterExpiredOffers(mAllRestaurantOffers.orEmpty())
            else -> emptyList()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> mContext.getString(R.string.available_offers)
            1 -> mContext.getString(R.string.expired_offers)
            else -> ""
        }
    }

    fun swapRestaurantOffersList(restaurantOfferList: List<RestaurantOffer>?) {
        mAllRestaurantOffers = restaurantOfferList
        updateFragments()
    }

    private fun updateFragments() {
        (0 until count).forEach { updateFragment(it) }
    }

    private fun updateFragment(position: Int) {
        val fragment = mFragmentsMap[position]
        fragment.swapRestaurantOffersList(getRestaurantOfferList(position))
    }
}