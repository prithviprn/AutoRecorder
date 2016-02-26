package ua.moskovkin.autorecorder;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class PassActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btnOne;
    private Button btnTwo;
    private Button btnThree;
    private Button btnFour;
    private Button btnFive;
    private Button btnSix;
    private Button btnSeven;
    private Button btnEight;
    private Button btnNine;
    private Button btnZero;
    private Button btnBackspace;
    private Button btnOk;
    private EditText passEditText;
    private ImageView locker;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pass_activity);

        settings = PreferenceManager.getDefaultSharedPreferences(this);

        btnOne = (Button) findViewById(R.id.btnOne);
        btnOne.setOnClickListener(this);
        btnTwo = (Button) findViewById(R.id.btnTwo);
        btnTwo.setOnClickListener(this);
        btnThree = (Button) findViewById(R.id.btnThree);
        btnThree.setOnClickListener(this);
        btnFour = (Button) findViewById(R.id.btnFour);
        btnFour.setOnClickListener(this);
        btnFive = (Button) findViewById(R.id.btnFive);
        btnFive.setOnClickListener(this);
        btnSix = (Button) findViewById(R.id.btnSix);
        btnSix.setOnClickListener(this);
        btnSeven = (Button) findViewById(R.id.btnSeven);
        btnSeven.setOnClickListener(this);
        btnEight = (Button) findViewById(R.id.btnEight);
        btnEight.setOnClickListener(this);
        btnNine = (Button) findViewById(R.id.btnNine);
        btnNine.setOnClickListener(this);
        btnZero = (Button) findViewById(R.id.btnZero);
        btnZero.setOnClickListener(this);
        btnBackspace = (Button) findViewById(R.id.btnBackspace);
        btnBackspace.setOnClickListener(this);
        btnOk = (Button) findViewById(R.id.btnOk);
        btnOk.setEnabled(false);
        btnOk.setOnClickListener(this);

        passEditText = (EditText) findViewById(R.id.passEditText);
        passEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals(settings.getString("password", " "))) {
                    setResult(RESULT_OK);
                    finish();
                }
                if (s.length() > 0) {
                    btnOk.setEnabled(true);
                } else {
                    btnOk.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        locker = (ImageView) findViewById(R.id.lockerImg);

    }

    @Override
    public void onClick(View v) {
        String buttonText = ((Button) v).getText().toString();
        if (buttonText.equals(btnOk.getText().toString())) {
            if (passEditText.getText().toString()
                    .equals(settings.getString("password", " "))) {
                setResult(RESULT_OK);
                finish();
            } else {
                passEditText.setText("");
                Animation lockerAnim = AnimationUtils.loadAnimation(this, R.anim.locker_animation);
                locker.startAnimation(lockerAnim);
            }
        } else if (buttonText.equals(btnBackspace.getText().toString())) {
            String text = passEditText.getText().toString();
            if (text.length() > 0) {
                passEditText.setText(text.substring(0, text.length() - 1));
            }
        } else {
            passEditText.append(buttonText);
        }
    }
}
