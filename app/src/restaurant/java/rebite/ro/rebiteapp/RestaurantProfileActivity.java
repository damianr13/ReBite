package rebite.ro.rebiteapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rebite.ro.rebiteapp.adapters.OffersListPageAdapter;
import rebite.ro.rebiteapp.offers.RestaurantOffer;
import rebite.ro.rebiteapp.persistence.PersistenceManager;
import rebite.ro.rebiteapp.persistence.callbacks.RestaurantOffersRetrieverCallbacks;
import rebite.ro.rebiteapp.state.StateManager;
import rebite.ro.rebiteapp.users.restaurants.RestaurantInfo;
import rebite.ro.rebiteapp.utils.UIUtils;

public class RestaurantProfileActivity extends AppCompatActivity
        implements RestaurantOffersRetrieverCallbacks{

    private static final int CREATE_OFFER_REQUEST = 100;

    @BindView(R.id.iv_profile_picture) ImageView mLogoImageView;
    @BindView(R.id.tv_display_name) TextView mNameTextView;
    @BindView(R.id.vp_offers_tabs) ViewPager mOffersTabsViewPager;

    private OffersListPageAdapter mOffersListPageAdapter;
    private RestaurantInfo mCurrentRestaurantInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        UIUtils.enableNavDrawerMenu(this);

        mCurrentRestaurantInfo = StateManager.getInstance().getRestaurantInfo();
        mOffersListPageAdapter = new OffersListPageAdapter(getSupportFragmentManager(), this);

        pullOffersFromServer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ButterKnife.bind(this);

        String logoURL = mCurrentRestaurantInfo.image;
        if (logoURL != null) {
            Picasso.get().load(logoURL).into(mLogoImageView);
        }
        mNameTextView.setText(mCurrentRestaurantInfo.name);
        mOffersTabsViewPager.setAdapter(mOffersListPageAdapter);

        return super.onCreateOptionsMenu(menu);
    }

    private void pullOffersFromServer() {
        PersistenceManager.getInstance().retrieveAllOffersByRestaurant(
                StateManager.getInstance().getRestaurantInfo(), this);
    }

    @Override
    public void onRestaurantOffersRetrieved(List<RestaurantOffer> result) {
        mOffersListPageAdapter.swapRestaurantOffersList(result);
    }

    @OnClick(R.id.btn_new_offer)
    public void launchPostOfferActivity(View v) {
        Intent offerCreatorActivity = new Intent(this,
                OfferCreatorActivity.class);
        startActivityForResult(offerCreatorActivity, CREATE_OFFER_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return ;
        }

        if (requestCode == CREATE_OFFER_REQUEST) {
            RestaurantOffer offer =
                    Parcels.unwrap(data.getParcelableExtra(OfferCreatorActivity.OFFER_RESULT));

            PersistenceManager.getInstance().persistOfferToGlobalDatabase(this, offer);
            pullOffersFromServer();
        }
    }
}
