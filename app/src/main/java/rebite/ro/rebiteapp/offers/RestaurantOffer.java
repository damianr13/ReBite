package rebite.ro.rebiteapp.offers;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

import org.parceler.Parcel;
import org.parceler.Transient;

import rebite.ro.rebiteapp.users.restaurants.RestaurantInfo;

@Parcel
public class RestaurantOffer  {

    public enum OfferState {
        AVAILABLE, EXPIRED, IN_PROGRESS, FINISHED
    }

    @Exclude public static final String RESTAURANT_INFO_FIELD = "restaurant_info";
    @Exclude public static final String PICK_UP_TIME_FIELD = "pick_up_time";
    @Exclude public static final String QUANTITY_FIELD = "quantity";
    @Exclude public static final String DESCRIPTION_FIELD = "description";
    @Exclude public static final String STATE_FIELD = "state";

    @PropertyName(RESTAURANT_INFO_FIELD)
    @Transient public RestaurantInfo restaurantInfo;

    @PropertyName(PICK_UP_TIME_FIELD)
    public long pickUpTimestamp;

    @PropertyName(QUANTITY_FIELD)
    public int quantity;

    @PropertyName(DESCRIPTION_FIELD)
    public String description;

    @PropertyName(STATE_FIELD)
    public OfferState state = OfferState.AVAILABLE; //default value

    public static class Builder {
        private RestaurantOffer result;

        public Builder() {
            result = new RestaurantOffer();
        }

        public Builder setRestaurantInfo(RestaurantInfo restaurantInfo) {
            result.restaurantInfo = restaurantInfo;
            return this;
        }

        public Builder setPickUpTime(long pickUpTime) {
            result.pickUpTimestamp = pickUpTime;
            return this;
        }

        public Builder setQuantity(int quantity) {
            result.quantity = quantity;
            return this;
        }

        public Builder setDescription(String description) {
            result.description = description;
            return this;
        }

        public Builder setState(OfferState state) {
            result.state = state;
            return this;
        }

        public RestaurantOffer build() {
            return result;
        }
    }
}
