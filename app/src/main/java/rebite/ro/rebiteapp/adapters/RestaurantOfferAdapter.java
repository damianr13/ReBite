package rebite.ro.rebiteapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import rebite.ro.rebiteapp.R;
import rebite.ro.rebiteapp.offers.RestaurantOffer;

public class RestaurantOfferAdapter extends
        RecyclerView.Adapter<RestaurantOfferAdapter.RestaurantOfferViewHolder>{

    private List<RestaurantOffer> mRestaurantOfferList;
    private Context mContext;

    public RestaurantOfferAdapter(Context context) {
        mContext = context;
    }

    public void swapRestaurantOffers(List<RestaurantOffer> offers) {
        mRestaurantOfferList = offers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RestaurantOfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View result = LayoutInflater.from(mContext)
                .inflate(R.layout.item_restaurant_offer, parent, false);

        return new RestaurantOfferViewHolder(result);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantOfferViewHolder holder, int position) {
        RestaurantOffer offer = mRestaurantOfferList.get(position);

        holder.addressTextView.setText(offer.restaurantInfo.address);
        holder.nameTextView.setText(offer.restaurantInfo.name);
        holder.quantityTextView.setText(String.valueOf(offer.quantity));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.getDefault());
        Calendar pickUpTimeCalendar = Calendar.getInstance();
        pickUpTimeCalendar.setTimeInMillis(offer.pickUpTimestamp);

        holder.pickUpTimeTextView.setText(sdf.format(pickUpTimeCalendar.getTime()));
    }

    @Override
    public int getItemCount() {
        if (mRestaurantOfferList == null) {
            return 0;
        }

        return mRestaurantOfferList.size();
    }

    class RestaurantOfferViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_name)
        TextView nameTextView;

        @BindView(R.id.tv_address)
        TextView addressTextView;

        @BindView(R.id.tv_pick_up_time)
        TextView pickUpTimeTextView;

        @BindView(R.id.tv_quantity)
        TextView quantityTextView;

        RestaurantOfferViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
