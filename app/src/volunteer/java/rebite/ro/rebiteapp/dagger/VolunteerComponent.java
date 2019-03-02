package rebite.ro.rebiteapp.dagger;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = VolunteerModule.class)
public interface VolunteerComponent extends FlavorSpecificComponent {
}
