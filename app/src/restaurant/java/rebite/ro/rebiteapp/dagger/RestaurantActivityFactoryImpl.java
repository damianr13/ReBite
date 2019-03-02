package rebite.ro.rebiteapp.dagger;

import android.content.Context;
import android.content.Intent;

import rebite.ro.rebiteapp.RestaurantProfileActivity;

public class RestaurantActivityFactoryImpl implements RestaurantActivityFactory {
    @Override
    public Intent getIntentForProfileActivity(Context context) {
        return new Intent(context, RestaurantProfileActivity.class);
    }
}
