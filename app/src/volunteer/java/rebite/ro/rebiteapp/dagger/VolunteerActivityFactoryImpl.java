package rebite.ro.rebiteapp.dagger;

import android.content.Context;
import android.content.Intent;

import rebite.ro.rebiteapp.VolunteerProfileActivity;

public class VolunteerActivityFactoryImpl implements VolunteerActivityFactory {
    @Override
    public Intent getIntentForProfileActivity(Context context) {
        return new Intent(context, VolunteerProfileActivity.class);
    }
}
