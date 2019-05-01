package rebite.ro.rebiteapp.dagger;

import rebite.ro.rebiteapp.MainActivity;
import rebite.ro.rebiteapp.fragments.RestaurantOfferFragment;

public interface FlavorSpecificComponent {
    void inject(MainActivity mainActivity);
    void inject(RestaurantOfferFragment offersFragment);
}
