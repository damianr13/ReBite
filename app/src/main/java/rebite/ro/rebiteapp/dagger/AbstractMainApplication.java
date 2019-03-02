package rebite.ro.rebiteapp.dagger;

import android.app.Application;

public abstract class AbstractMainApplication extends Application {

    private FlavorSpecificComponent mFlavorSpecificComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mFlavorSpecificComponent = createFlavorSpecificComponent();
    }

    public FlavorSpecificComponent getFlavorSpecificComponent() {
        return mFlavorSpecificComponent;
    }

    protected abstract FlavorSpecificComponent createFlavorSpecificComponent();

}

