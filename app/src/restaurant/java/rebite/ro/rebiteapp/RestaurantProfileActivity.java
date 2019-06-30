package rebite.ro.rebiteapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import rebite.ro.rebiteapp.persistence.UsersManager;
import rebite.ro.rebiteapp.persistence.callbacks.RestaurantOffersRetrieverCallbacks;
import rebite.ro.rebiteapp.state.StateManager;
import rebite.ro.rebiteapp.users.restaurants.RestaurantInfo;
import rebite.ro.rebiteapp.utils.UIUtils;

public class RestaurantProfileActivity extends AppCompatActivity
        implements RestaurantOffersRetrieverCallbacks, NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = RestaurantProfileActivity.class.getName();

    private static final int CREATE_OFFER_REQUEST = 100;

    @BindView(R.id.iv_profile_picture) ImageView mLogoImageView;
    @BindView(R.id.tv_display_name) TextView mNameTextView;
    @BindView(R.id.vp_offers_tabs) ViewPager mOffersTabsViewPager;
    @BindView(R.id.nv_menu) NavigationView mNavigationView;

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

        mNavigationView.setNavigationItemSelectedListener(this);
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

    public void onLogOut() {
        UsersManager.getInstance().logOutCurrentUser();

        Intent homeIntent = new Intent(this, MainActivity.class);
        startActivity(homeIntent);
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.it_log_out:
                onLogOut();
                return true;
        }

        Log.w(TAG, "Unknown menu item: " + menuItem.getTitle().toString());
        return false;
    }
}
