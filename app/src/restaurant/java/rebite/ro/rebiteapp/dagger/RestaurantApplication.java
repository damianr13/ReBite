package rebite.ro.rebiteapp.dagger;

public class RestaurantApplication extends AbstractMainApplication {
    @Override
    protected FlavorSpecificComponent createFlavorSpecificComponent() {
        return DaggerRestaurantComponent.create();
    }
}
