package rebite.ro.rebiteapp.dagger;

public class VolunteerApplication extends AbstractMainApplication {
    @Override
    protected FlavorSpecificComponent createFlavorSpecificComponent() {
        return DaggerVolunteerComponent.create();
    }
}
