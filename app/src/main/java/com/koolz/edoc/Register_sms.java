package com.koolz.edoc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.concurrent.TimeUnit;

public class Register_sms extends AppCompatActivity {

    private TextInputLayout e1;
    private TextInputEditText e11;
    private TextInputEditText e21;
    private TextInputEditText e31;
    private TextInputLayout e2;
    private TextInputLayout e3;
    private TextInputLayout e4;
    private AutoCompleteTextView e41;
    private LinearLayout linearLayout;
    private ProgressBar pb;
    private ProgressBar pb1;
    private LinearLayout b1;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mcallback;
    private String username;
    private String phone;
    private String mycredentials;
    private PhoneAuthProvider.ForceResendingToken token11;
    private MediaPlayer success;
    private BottomNavigationView bottomNavigationView;
    public static final String[] countryAreaCodes = {"+93", "+355", "+213", "+376", "+244", "+672", "+54", "+374", "+297", "+61", "+43",
            "+994", "+973", "+880", "+375", "+32", "+501", "+229", "+975", "+591", "+387", "+267", "+55", "+673", "+359", "+226", "+95", "+257", "+855",
            "+237", "+55", "+673", "+359", "+226", "+95", "+257", "+855", "+237", "+1", "+238", "+236", "+235", "+56", "+86", "+61", "+61",
            "+57", "+269", "+242", "+682", "+506", "+385", "+53", "+357", "+420", "+45", "+253", "+670", "+593", "+20", "+503", "+240", "+291", "+372", "+251", "+500",
            "+298", "+679", "+358", "+33", "+689", "+241", "+220", "+995", "+49", "+233", "+350", "+30", "+299", "+502", "+224", "+245", "+592", "+509", "+504",
            "+852", "+36", "+91", "+62", "+98", "+964", "+353", "+44", "+972", "+39", "+225", "+1876", "+81", "+962", "+7", "+254", "+686", "+965", "+996",
            "+856", "+371", "+961", "+266", "+231", "+218", "+423", "+370", "+352", "+853", "+389", "+261", "+265", "+60", "+960", "+223", "+356", "+692", "+222",
            "+230", "+262", "+52", "+691", "+373", "+377", "+976", "+382", "+212", "+258", "+264", "+674", "+977", "+31", "+687", "+64", "+505", "+227", "+234", "+683", "+850", "+47",
            "+968", "+92", "+680", "+507", "+675", "+595", "+51", "+63", "+870", "+48", "+351", "+974", "+40", "+7", "+250", "+590", "+685", "+378", "+239", "+966",
            "+221", "+381", "+248", "+232", "+65", "+421", "+386", "+677", "+252", "+27", "+82", "+34", "+94", "+290", "+508", "+249", "+597", "+268", "+46", "+41", "+963", "+886", "+992",
            "+255", "+66", "+228", "+690", "+676", "+216", "+90", "+993", "+688", "+971", "+256" ,"+380", "+598", "+998", "+678", "+39", "+58", "+681", "+967", "+260", "+263"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_sms);
        e1=(TextInputLayout) findViewById(R.id.Phone_no);
        e11=(TextInputEditText) findViewById(R.id.Phone_no_ed);
        e11.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > e1.getCounterMaxLength())
                    e1.setError("Max character length is " + e1.getCounterMaxLength());
                else
                    e1.setError(null);
            }
        });
        e2=(TextInputLayout) findViewById(R.id.otp);
        e3=(TextInputLayout) findViewById(R.id.username);
        e4=(TextInputLayout) findViewById(R.id.Country_code);
        e21=(TextInputEditText) findViewById(R.id.otp_ed);
        e21.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > e2.getCounterMaxLength())
                    e2.setError("Max character length is " + e2.getCounterMaxLength());
                else
                    e2.setError(null);
            }
        });
        e31=(TextInputEditText) findViewById(R.id.username_ed);
        e31.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > e3.getCounterMaxLength())
                    e3.setError("Max character length is " + e3.getCounterMaxLength());
                else
                    e3.setError(null);
            }
        });
        e4 =(TextInputLayout)findViewById(R.id.Country_code);
        e41=(AutoCompleteTextView)findViewById(R.id.code);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.text,countryAreaCodes);
        e41.setAdapter(adapter);
        bottomNavigationView=findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.Register_with_Ph_no);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.Register_with_Ph_no:
                        return true;
                    case R.id.Register_with_email:
                        startActivity(new Intent(Register_sms.this,Register.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                }
                return false;
            }
        });
        auth=FirebaseAuth.getInstance();
        pb=(ProgressBar)findViewById(R.id.progressBar1);
        pb1=(ProgressBar)findViewById(R.id.progress_bar);
        b1=(LinearLayout) findViewById(R.id.Register2);
        linearLayout =(LinearLayout)findViewById(R.id.Generate);
        success=MediaPlayer.create(this,R.raw.success);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action);
        View view =getSupportActionBar().getCustomView();
        ImageView img= (ImageView) view.findViewById(R.id.image_view);
        img.setImageResource(R.drawable.icon);
        TextView textView=(TextView)view.findViewById(R.id.name);
        textView.setText("Soft-Doc");
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        mcallback=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            }
            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(Register_sms.this,"Invalid Credentials",Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(Register_sms.this,"You Have Reached Maximum No. Of Times"+"\nTry After Some Time",Toast.LENGTH_SHORT).show();
                    pb1.setVisibility(View.VISIBLE);
                    linearLayout.setEnabled(true);
                    linearLayout.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Toast.makeText(Register_sms.this,"OTP Sent Successfully",Toast.LENGTH_LONG).show();
                pb1.setVisibility(View.VISIBLE);
                linearLayout.setEnabled(true);
                linearLayout.setVisibility(View.VISIBLE);
                mycredentials=s;
                token11=forceResendingToken;
            }
        };
    }
    public void Generate_otp(View view){
        String phone_no = e1.getEditText().getText().toString();
        String code=e4.getEditText().getText().toString();
        username = e3.getEditText().getText().toString();
        if(TextUtils.isEmpty(phone_no)||TextUtils.isEmpty(code)||TextUtils.isEmpty(username)){
            Toast.makeText(this,"Empty Fields..",Toast.LENGTH_LONG).show();
        }
        else if (phone_no.length()<10){
            Toast.makeText(this,"Phone No. Must Be Of Length 10",Toast.LENGTH_LONG).show();
        }
        else if(phone_no.length()>10){
            e2.setError("Max character length is " + e2.getCounterMaxLength());
        }
        else if(username.length()>20){
            e3.setError("Max character length is " + e3.getCounterMaxLength());
        }
        else {
            phone= code + "" + phone_no;
            linearLayout.setVisibility(View.INVISIBLE);
            linearLayout.setEnabled(false);
            pb1.setVisibility(View.VISIBLE);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            ref.orderByChild("Phone_Number").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        linearLayout.setVisibility(View.VISIBLE);
                        linearLayout.setEnabled(true);
                        pb1.setVisibility(View.INVISIBLE);
                        Toast.makeText(Register_sms.this, "User Already Present.", Toast.LENGTH_LONG).show();
                    }
                    else{
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(phone,120, TimeUnit.SECONDS, Register_sms.this,mcallback,token11);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
    public void register(View v){
        String otp= e2.getEditText().getText().toString();
        if(TextUtils.isEmpty(otp)||otp.length()<6){
            e2.setError("Min character length is " + e2.getCounterMaxLength());
        }
        else {
            b1.setVisibility(View.INVISIBLE);
            b1.setEnabled(false);
            pb.setVisibility(View.VISIBLE);
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mycredentials, otp);
            signInWithPhoneAuthCredential(credential);
        }
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(Register_sms.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("phone");
                            ref.child("Phone_Number").setValue(phone);
                            user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(username).build();
                                user.updateProfile(profileUpdates);
                                success.start();
                                Toast.makeText(Register_sms.this, "Registration Successfully..", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Register_sms.this, home_screen.class));
                                finish();
                            } else {
                                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                    Toast.makeText(Register_sms.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                                    b1.setVisibility(View.VISIBLE);
                                    b1.setEnabled(true);
                                    pb.setVisibility(View.INVISIBLE);
                                }
                            }
                        }
                    }
                });
    }
}
