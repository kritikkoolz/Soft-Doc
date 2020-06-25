package com.koolz.edoc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity {
    private EditText e1,e2;
    private ProgressBar pb;
    private LinearLayout b1;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        e1 = (EditText) findViewById(R.id.Email1);
        e2 = (EditText) findViewById(R.id.password1);
        auth = FirebaseAuth.getInstance();
        pb=(ProgressBar)findViewById(R.id.progressBar2);
        pb.setVisibility(View.INVISIBLE);
        b1=(LinearLayout) findViewById(R.id.Login);
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

    public void Login(View view) {
        String email = e1.getText().toString();
        String password = e2.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Empty Credentials", Toast.LENGTH_LONG).show();
        } else if (password.length() < 8) {
            Toast.makeText(this, "Password Must Be Of Length 8", Toast.LENGTH_LONG).show();
        } else {
            login_user(email, password);
            b1.setVisibility(View.INVISIBLE);
            b1.setEnabled(false);
            pb.setVisibility(View.VISIBLE);
        }

    }

    private void login_user(final String email, String password) {
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(login.this, new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                user=FirebaseAuth.getInstance().getCurrentUser();
                if(user.isEmailVerified()){
                Toast.makeText(login.this,"Login Sucessfully",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(login.this,home_screen.class);
                startActivity(intent);
                finish();
                }
                else{
                    showCustomDialog_error();
                }
                }
        });
        auth.signInWithEmailAndPassword(email, password).addOnFailureListener(login.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(login.this,"Invalid Credentials",Toast.LENGTH_SHORT).show();
                pb.setVisibility(View.INVISIBLE);
                b1.setVisibility(View.VISIBLE);
                b1.setEnabled(true);
            }
        });
    }
    private void showCustomDialog_error() {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.error_custom, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        Button bt= (Button) dialogView.findViewById(R.id.buttoncancel);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Toast.makeText(login.this,"Email Not Verified",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(login.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        Button bt2= (Button) dialogView.findViewById(R.id.buttonsent);
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                user.sendEmailVerification();
                showCustomDialog_tick();
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
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        Button bt = (Button) dialogView.findViewById(R.id.buttonOk);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Intent intent = new Intent(login.this, MainActivity.class);
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
    private void showCustomDialog_tick1() {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.tick_custom_password, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        Button bt = (Button) dialogView.findViewById(R.id.buttonOk);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Toast.makeText(login.this,"Thank You",Toast.LENGTH_LONG).show();
            }
        });
        Button bt1 = (Button) dialogView.findViewById(R.id.buttonsent);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                auth.sendPasswordResetEmail(email).addOnSuccessListener(login.this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showCustomDialog_tick();
                    }
                });
            }
        });
    }
    public void Sent_password(View view){
        final AlertDialog.Builder alert = new AlertDialog.Builder(login.this);
        alert.setTitle("Reset Password ?");
        alert.setIcon(R.drawable.icon);
        alert.setMessage("Enter mail to get password reset link.");
        final EditText input = new EditText(this);
        alert.setView(input);
        alert.setPositiveButton("Submit", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton) {
                if(TextUtils.isEmpty(input.getText().toString())){
                    Toast.makeText(login.this,"Empty Field",Toast.LENGTH_LONG).show();
                }
                else {
                    auth.sendPasswordResetEmail(input.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            email=input.getText().toString();
                            showCustomDialog_tick1();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(login.this,"Password Reset Email Sent failed",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(login.this,"Thank You",Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alertDialog=alert.create();
        alertDialog.show();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
    }
}