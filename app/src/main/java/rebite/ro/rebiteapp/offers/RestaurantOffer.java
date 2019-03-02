package rebite.ro.rebiteapp.offers;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

import rebite.ro.rebiteapp.users.restaurants.RestaurantInfo;

public class RestaurantOffer {
    @Exclude public static final String RESTAURANT_INFO_FIELD = "restaurant_info";
    @Exclude public static final String PICK_UP_TIME_FIELD = "pick_up_time";
    @Exclude public static final String QUANTITY_FIELD = "quantity";


    @PropertyName(RESTAURANT_INFO_FIELD)
    public RestaurantInfo restaurantInfo;

    @PropertyName(PICK_UP_TIME_FIELD)
    public long pickUpTimestamp;

    @PropertyName(QUANTITY_FIELD)
    public int quantity;

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

        public RestaurantOffer build() {
            return result;
        }
    }
}
