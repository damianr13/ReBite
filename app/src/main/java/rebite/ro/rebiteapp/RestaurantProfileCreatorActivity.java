package rebite.ro.rebiteapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rebite.ro.rebiteapp.persistence.UsersManager;

public class RestaurantProfileCreatorActivity extends AppCompatActivity {

    @BindView(R.id.et_username) EditText mUsernameEditText;
    @BindView(R.id.et_password) EditText mPasswordEditText;
    @BindView(R.id.et_confirm_password) EditText mConfirmPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_profile_creator);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_create)
    public void createRestaurantProfile(View view) {
        String username = mUsernameEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        String passwordConfirmation = mConfirmPasswordEditText.getText().toString();

        if (!password.equals(passwordConfirmation)) {
            Toast.makeText(this, R.string.password_mismatch, Toast.LENGTH_SHORT).show();
            return ;
        }

        UsersManager.getInstance().createNewUser(this, username, password);
        onBackPressed();
    }
}
