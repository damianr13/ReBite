package rebite.ro.rebiteapp.dagger;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class VolunteerModule {

    @Singleton
    @Provides
    static FlavorSpecificActivityFactory getActivityFactory() {
        return new VolunteerActivityFactoryImpl();
    }
}
