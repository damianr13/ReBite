package rebite.ro.rebiteapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rebite.ro.rebiteapp.adapters.RestaurantOfferAdapter;
import rebite.ro.rebiteapp.offers.RestaurantOffer;
import rebite.ro.rebiteapp.persistence.PersistenceManager;
import rebite.ro.rebiteapp.persistence.callbacks.RestaurantOffersRetrieverCallbacks;
import rebite.ro.rebiteapp.state.StateManager;
import rebite.ro.rebiteapp.users.restaurants.RestaurantInfo;

public class RestaurantProfileActivity extends AppCompatActivity
        implements RestaurantOffersRetrieverCallbacks{

    @BindView(R.id.iv_logo) ImageView mLogoImageView;
    @BindView(R.id.tv_restaurant_name) TextView mNameTextView;
    @BindView(R.id.tv_restaurant_address) TextView mAddressTextView;
    @BindView(R.id.rv_past_offers) RecyclerView mPastOffersRecyclerView;

    private RestaurantOfferAdapter mAdapter;

    private static final String TAG = RestaurantProfileActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_profile);
        ButterKnife.bind(this);

        RestaurantInfo currentRestaurantInfo = StateManager.getInstance().getRestaurantInfo();
        String logoURL = currentRestaurantInfo.image;
        if (logoURL != null) {
            Picasso.get().load(logoURL).into(mLogoImageView);
        }
        mNameTextView.setText(currentRestaurantInfo.name);
        mAddressTextView.setText(currentRestaurantInfo.address);

        mAdapter = new RestaurantOfferAdapter(this);
        mPastOffersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mPastOffersRecyclerView.setAdapter(mAdapter);

        PersistenceManager.getInstance().retrieveAllOffersByRestaurant(
                StateManager.getInstance().getRestaurantInfo(), this);
    }

    @Override
    public void onRestaurantOffersRetrieved(List<RestaurantOffer> result) {
        mAdapter.swapRestaurantOffers(result);
    }

    @OnClick(R.id.btn_new_offer)
    public void launchPostOfferActivity(View v) {
        Intent offerCreatorActivity = new Intent(this,
                OfferCreatorActivity.class);
        startActivity(offerCreatorActivity);
    }
}
