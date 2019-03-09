package rebite.ro.rebiteapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import org.parceler.Parcels;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rebite.ro.rebiteapp.offers.RestaurantOffer;
import rebite.ro.rebiteapp.persistence.PersistenceManager;
import rebite.ro.rebiteapp.state.StateManager;

public class OfferCreatorActivity extends AppCompatActivity {
    private static final String TAG = OfferCreatorActivity.class.getName();

    public static final String OFFER_RESULT = "result";

    @BindView(R.id.tp_pickup_time) TimePicker mPickupTimeTimePicker;
    @BindView(R.id.et_quantity) EditText mQuantityEditText;
    @BindView(R.id.et_description) EditText mDescriptionEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_creator);

        ButterKnife.bind(this);

        mPickupTimeTimePicker.setIs24HourView(DateFormat.is24HourFormat(this));
    }

    @OnClick(R.id.btn_post_offer)
    public void postNewOffer(View v) {
        Calendar pickupTimeCalendar = getSelectedTime();
        String quantityString = mQuantityEditText.getText().toString();

        if (!checkUserInput(pickupTimeCalendar, quantityString)) {
            return ;
        }

        int quantity = Integer.valueOf(quantityString);

        RestaurantOffer offer = new RestaurantOffer.Builder()
                .setQuantity(quantity)
                .setRestaurantInfo(StateManager.getInstance().getRestaurantInfo())
                .setPickUpTime(pickupTimeCalendar.getTimeInMillis())
                .setDescription(mDescriptionEditText.getText().toString())
                .build();
        Intent returnIntent = new Intent();
        returnIntent.putExtra(OFFER_RESULT, Parcels.wrap(offer));
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private Calendar getSelectedTime() {
        int hour, minute;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hour = mPickupTimeTimePicker.getHour();
            minute = mPickupTimeTimePicker.getMinute();
        } else {
            hour = mPickupTimeTimePicker.getCurrentHour();
            minute = mPickupTimeTimePicker.getCurrentMinute();
        }
        Calendar result = Calendar.getInstance();
        result.set(Calendar.HOUR_OF_DAY, hour);
        result.set(Calendar.MINUTE, minute);
        result.set(Calendar.SECOND, 0);

        return result;
    }

    private boolean checkUserInput(Calendar pickupTimeCalendar, String quantityString) {
        if (pickupTimeCalendar.before(Calendar.getInstance())) {
            Toast.makeText(this, "Please choose a time in the future",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if ("".equals(quantityString)) {
            Toast.makeText(this, "Please specify quantity", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
