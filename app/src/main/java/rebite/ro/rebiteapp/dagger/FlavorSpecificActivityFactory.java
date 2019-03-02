package rebite.ro.rebiteapp.dagger;

import android.content.Context;
import android.content.Intent;

public interface FlavorSpecificActivityFactory {
    Intent getIntentForProfileActivity(Context context);
}
