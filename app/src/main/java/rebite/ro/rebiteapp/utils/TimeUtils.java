package rebite.ro.rebiteapp.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimeUtils {

    private static final String DATE_FORMAT = "dd/MM HH:mm";

    public static String format (long timestamp) {
        DateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    private static long getTimestampAfter(long seconds) {
        return System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(seconds);
    }

    public static String formatTimestampAfter(long seconds) {
        return format(getTimestampAfter(seconds));
    }
}
