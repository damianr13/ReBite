package rebite.ro.rebiteapp.utils;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;

import rebite.ro.rebiteapp.R;

public class UIUtils {

    public static void enableNavDrawerMenu(AppCompatActivity activity) {
        DrawerLayout dl = activity.findViewById(R.id.dl_container);
        ActionBarDrawerToggle t = new ActionBarDrawerToggle(activity, dl,
                R.string.nav_drawer_open, R.string.nav_drawer_close);

        dl.addDrawerListener(t);
        t.syncState();
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
