package rebite.ro.rebiteapp.dagger;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = RestaurantModule.class)
public interface RestaurantComponent extends FlavorSpecificComponent {
}
