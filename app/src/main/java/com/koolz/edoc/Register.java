package com.koolz.edoc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class Register extends AppCompatActivity {

    private TextInputLayout e1;
    private TextInputEditText e21;
    private TextInputEditText e31;
    private TextInputLayout e2;
    private TextInputLayout e3;
    private ProgressBar pb;
    private LinearLayout b1;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private String username;
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        e1=(TextInputLayout) findViewById(R.id.Email);
        e2=(TextInputLayout) findViewById(R.id.password);
        e3=(TextInputLayout) findViewById(R.id.username);
        e21=(TextInputEditText) findViewById(R.id.password_ed);
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
        bottomNavigationView=findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.Register_with_email);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.Register_with_email:
                        return true;
                    case R.id.Register_with_Ph_no:
                        startActivity(new Intent(Register.this,Register_sms.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                }
                return false;
            }
        });
        auth=FirebaseAuth.getInstance();
        pb=(ProgressBar)findViewById(R.id.progressBar1);
        b1=(LinearLayout) findViewById(R.id.Register2);
        pb.setVisibility(View.INVISIBLE);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action);
        View view =getSupportActionBar().getCustomView();
        ImageView img= (ImageView) view.findViewById(R.id.image_view);
        img.setImageResource(R.drawable.icon);
        TextView textView=(TextView)view.findViewById(R.id.name);
        textView.setText("Soft-Doc");
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
    }
    public void register(View view){
        String email = e1.getEditText().getText().toString();
        String password = e2.getEditText().getText().toString();
        username = e3.getEditText().getText().toString();
        if(TextUtils.isEmpty(email)||TextUtils.isEmpty(password)||TextUtils.isEmpty(username)){
            Toast.makeText(this,"Empty Credentials",Toast.LENGTH_LONG).show();
        }
        else if (password.length()<8){
            Toast.makeText(this,"Password Must Be Of Length 8",Toast.LENGTH_LONG).show();
        }
        else if(password.length()>20){
            e2.setError("Max character length is " + e2.getCounterMaxLength());
        }
        else if(username.length()>20){
            e3.setError("Max character length is " + e3.getCounterMaxLength());
        }
        else {
            register_user(email,password);
            b1.setVisibility(View.INVISIBLE);
            b1.setEnabled(false);
            pb.setVisibility(View.VISIBLE);
        }
    }
    private void register_user(final String email, String password) {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    user=FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(username).build();
                        user.updateProfile(profileUpdates);
                        user.sendEmailVerification().addOnSuccessListener(Register.this,
                                new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        pb.setVisibility(View.INVISIBLE);
                                        showCustomDialog_tick();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Register.this,"Verification Email Sent failed",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                else {
                    try
                    {
                        throw task.getException();
                    }
                    catch (FirebaseAuthWeakPasswordException weakPassword)
                    {
                        Toast.makeText(Register.this,"Weak Password",Toast.LENGTH_SHORT).show();
                        pb.setVisibility(View.INVISIBLE);
                        b1.setVisibility(View.VISIBLE);
                        b1.setEnabled(true);
                    }
                    catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                    {
                        Toast.makeText(Register.this,"Invalid Credentials",Toast.LENGTH_SHORT).show();
                        pb.setVisibility(View.INVISIBLE);
                        b1.setVisibility(View.VISIBLE);
                        b1.setEnabled(true);
                    }
                    catch (FirebaseAuthUserCollisionException existEmail)
                    {
                        Toast.makeText(Register.this,"Email Already Exists"+"\nOr Login to Verify Previous Registered Email",Toast.LENGTH_LONG).show();
                        pb.setVisibility(View.INVISIBLE);
                        b1.setVisibility(View.VISIBLE);
                        b1.setEnabled(true);
                    }
                    catch (Exception e) {
                    }
                }
            }
        });
    }
    private void showCustomDialog_tick() {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.tick_custom, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        TextView tv=(TextView)dialogView.findViewById(R.id.customtv);
        tv.setText(getResources().getString(R.string.verification_email_sent_successfully));
        TextView tv2=(TextView)dialogView.findViewById(R.id.customtv1);
        tv2.setText(getResources().getString(R.string.verify_email_and_then_login_again));
        Button bt = (Button) dialogView.findViewById(R.id.buttonOk);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
                Intent intent = new Intent(Register.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        Button bt1 = (Button) dialogView.findViewById(R.id.buttonsent);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                user.sendEmailVerification();
                showCustomDialog_tick();
            }
        });
    }
}