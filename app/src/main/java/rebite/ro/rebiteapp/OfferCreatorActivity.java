package rebite.ro.rebiteapp;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.parceler.Parcels;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rebite.ro.rebiteapp.offers.RestaurantOffer;
import rebite.ro.rebiteapp.state.StateManager;
import rebite.ro.rebiteapp.utils.TimeUtils;

public class OfferCreatorActivity extends AppCompatActivity {
    private static final String TAG = OfferCreatorActivity.class.getName();

    public static final String OFFER_RESULT = "result";

    @BindView(R.id.et_pick_up_time) EditText mPickUpTimeEditText;
    @BindView(R.id.et_quantity) EditText mQuantityEditText;
    @BindView(R.id.et_description) EditText mDescriptionEditText;

    private Calendar mSelectedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_creator);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_post_offer)
    public void postNewOffer(View v) {
        if (mSelectedTime == null) {
            Toast.makeText(this, "Please select a pick up time", Toast.LENGTH_SHORT)
                    .show();
            return ;
        }

        Calendar pickupTimeCalendar = mSelectedTime;
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

    @OnClick(R.id.btn_pick_up_time)
    public void setPickupTime(View v) {
        Calendar now = Calendar.getInstance();
        int currentHour = now.get(Calendar.HOUR_OF_DAY);
        int currentMinute = now.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (picker, hour, minute) -> {
                    mSelectedTime = buildTodayCalendar(hour, minute);
                    mPickUpTimeEditText.setText(TimeUtils.format(mSelectedTime.getTimeInMillis()));
                },
                currentHour + 1, currentMinute, DateFormat.is24HourFormat(this));
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    private Calendar buildTodayCalendar(int hour, int minute) {
        Calendar result = Calendar.getInstance();
        result.set(Calendar.HOUR_OF_DAY, hour);
        result.set(Calendar.MINUTE, minute);
        result.set(Calendar.SECOND, 0);

        return result;
    }
}
