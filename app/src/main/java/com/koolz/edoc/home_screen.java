package com.koolz.edoc;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.app.AlertDialog;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

public class home_screen extends AppCompatActivity {
    private static String Id;
    private static String user_id;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseUser user;
    private int providerId=0;
    private ProgressDialog pd1;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
        Id=user.getEmail();
        user_id=user.getUid();
        pd1=new ProgressDialog(this);
        List<? extends UserInfo> infos = user.getProviderData();
        for (UserInfo ui : infos) {
            if (ui.getProviderId().equals(GoogleAuthProvider.PROVIDER_ID)) {
                providerId=1;
            }
            if (ui.getProviderId().equals(FacebookAuthProvider.PROVIDER_ID)) {
                providerId=2;

            }
            if(ui.getProviderId().equals(EmailAuthProvider.PROVIDER_ID)) {
                providerId=3;
            }
            if(ui.getProviderId().equals(PhoneAuthProvider.PROVIDER_ID)){
                providerId=4;
            }
        }}
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action);
        View view =getSupportActionBar().getCustomView();
        ImageView img= (ImageView) view.findViewById(R.id.image_view);
        img.setImageResource(R.drawable.icon);
        auth = FirebaseAuth.getInstance();
        TextView textView=(TextView)view.findViewById(R.id.name);
        textView.setText(user.getDisplayName());
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.custom_menu2, menu);
        if(providerId==1||providerId==2||providerId==4){
            menu.findItem(R.id.change_password).setVisible(false);
            menu.findItem(R.id.change_password).setEnabled(false);

        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out:
                if (providerId==1){
                    signOut_g();
                    user_to_home();
                }
                else if (providerId==2){
                    signOut_fb();
                    user_to_home();
                }
                else {
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(home_screen.this,"Sign Out Successful",Toast.LENGTH_SHORT).show();
                    user_to_home();
                }
                return true;
            case R.id.Send_feedback:
                final AlertDialog.Builder alert = new AlertDialog.Builder(home_screen.this);
                alert.setTitle(Id);
                alert.setIcon(R.drawable.feedback);
                alert.setMessage("Enter your feedback");
                final EditText input = new EditText(this);
                alert.setView(input);
                alert.setPositiveButton("Submit", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(TextUtils.isEmpty(input.getText().toString())){
                            Toast.makeText(home_screen.this,"Empty Field",Toast.LENGTH_LONG).show();
                    }
                        else {
                            FirebaseDatabase.getInstance().getReference().child(user_id).child("feedback").push().setValue(input.getText().toString());
                            showCustomDialog();
                            Toast.makeText(home_screen.this,"Thanks For Your Feedback",Toast.LENGTH_LONG).show();
                        }
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(home_screen.this,"Please Add Your Feedback",Toast.LENGTH_LONG).show();
                        showCustomDialog1();
                    }
                });
                AlertDialog alertDialog=alert.create();
                alertDialog.show();
                alertDialog.setCancelable(false);
                alertDialog.setCanceledOnTouchOutside(false);
                return true;
            case R.id.delete_account:
                AlertDialog.Builder builder = new AlertDialog.Builder(home_screen.this);
                builder.setMessage("If you delete all your data will be lost");
                builder.setTitle("Do You Really Want To Delete Your Account?");
                builder.setCancelable(false);
                builder.setIcon(R.drawable.delete_24);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final AlertDialog.Builder alert = new AlertDialog.Builder(home_screen.this);
                        alert.setTitle("Delete Account ?");
                        alert.setIcon(R.drawable.icon);
                        alert.setMessage("Enter mail to contiue.");
                        final EditText input = new EditText(home_screen.this);
                        alert.setView(input);
                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if(TextUtils.isEmpty(input.getText().toString())||!Id.equals(input.getText().toString())){
                                    Toast.makeText(home_screen.this,"Invalid Input",Toast.LENGTH_LONG).show();
                                }
                                else {
                                    pd1=new ProgressDialog(home_screen.this);
                                    pd1.setMessage("Deleting Your Account...");
                                    if (providerId==1){
                                        mGoogleSignInClient.signOut();
                                    }
                                    else if (providerId==2){
                                        LoginManager.getInstance().logOut();
                                    }
                                    pd1.show();
                                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    pd1.setCanceledOnTouchOutside(false);
                                    pd1.setCancelable(false);
                                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                FirebaseDatabase.getInstance().getReference().child(user_id).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        pd1.dismiss();
                                                        Toast.makeText(home_screen.this,"Account Deleted..",Toast.LENGTH_SHORT).show();
                                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                        user_to_home();
                                                    }
                                                });
                                                }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            pd1.dismiss();
                                            Toast.makeText(home_screen.this,"Account Deleting Failed..",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Toast.makeText(home_screen.this,"Thank You",Toast.LENGTH_SHORT).show();
                            }
                        });
                        AlertDialog alertDialog=alert.create();
                        alertDialog.show();
                        alertDialog.setCancelable(false);
                        alertDialog.setCanceledOnTouchOutside(false);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog alertDialog1 = builder.create();
                alertDialog1.show();
                alertDialog1.setCanceledOnTouchOutside(false);
                alertDialog1.setCancelable(false);
                return true;
            case R.id.change_password:
                Sent_password();
                return true;
            case R.id.change_username:
                final AlertDialog.Builder alert1 = new AlertDialog.Builder(home_screen.this);
                alert1.setTitle("Change Username ?");
                alert1.setIcon(R.drawable.person_24);
                alert1.setMessage("Enter new username..");
                final EditText input1 = new EditText(this);
                alert1.setView(input1);
                alert1.setPositiveButton("Submit", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(TextUtils.isEmpty(input1.getText().toString())){
                            Toast.makeText(home_screen.this,"Empty Field",Toast.LENGTH_LONG).show();
                        }
                        else {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(input1.getText().toString()).build();
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                               showCustomDialog_tick();
                                            }
                                        }
                                    });
                        }
                    }
                });
                alert1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(home_screen.this,"Thank You",Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alertDialog2=alert1.create();
                alertDialog2.show();
                alertDialog2.setCancelable(false);
                alertDialog2.setCanceledOnTouchOutside(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void showCustomDialog_tick() {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.tick_custom_password, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        Button bt = (Button) dialogView.findViewById(R.id.buttonOk);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Toast.makeText(home_screen.this,"Thank You",Toast.LENGTH_LONG).show();
                user_exit();
            }
        });
    }
    private void showCustomDialog_tick1() {
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
        tv.setText(getResources().getString(R.string.password_reset_email_sent_successfully));
        TextView tv2=(TextView)dialogView.findViewById(R.id.customtv1);
        tv2.setText(getResources().getString(R.string.change_password_and_then_login_again));
        Button bt = (Button) dialogView.findViewById(R.id.buttonOk);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Toast.makeText(home_screen.this,"Thank You",Toast.LENGTH_LONG).show();
            }
        });
        Button bt1 = (Button) dialogView.findViewById(R.id.buttonsent);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                auth.sendPasswordResetEmail(Id).addOnSuccessListener(home_screen.this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showCustomDialog_tick1();
                    }
                });
            }
        });
    }
    public void Sent_password(){
        final AlertDialog.Builder alert = new AlertDialog.Builder(home_screen.this);
        alert.setTitle("Change Password ?");
        alert.setIcon(R.drawable.icon);
        alert.setMessage("Password reset link sent to following email.");
        final TextView input = new TextView(this);
        input.setText(Id);
        input.setTextSize(20);
        input.setTextColor(Color.RED);
        input.setGravity(Gravity.CENTER_HORIZONTAL);
        alert.setView(input);
        alert.setPositiveButton("Submit", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton) {
                auth.sendPasswordResetEmail(Id).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showCustomDialog_tick1();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(home_screen.this,"Password Reset Email Sent failed",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(home_screen.this,"Thank You",Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alertDialog=alert.create();
        alertDialog.show();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
    }
    private void showCustomDialog() {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialogue, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        TextView textView=(TextView) dialogView.findViewById(R.id.customtv);
        textView.setText("Feedback Submitted");
        Button bt= (Button) dialogView.findViewById(R.id.buttonOk);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }
    private void showCustomDialog1() {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dailogue2, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        TextView textView=(TextView) dialogView.findViewById(R.id.customtv);
        textView.setText("Click Ok for Further");
        Button bt= (Button) dialogView.findViewById(R.id.buttonOk);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }
    public void update(View v){
        Intent intent3 = new Intent(this,Doc_list.class);
        intent3.putExtra("list",true);
        startActivity(intent3);
    }
    public void view(View v){
        Intent intent4 = new Intent(this,Doc_list.class);
        intent4.putExtra("list",false);
        startActivity(intent4);

    }
    public void add(View v){
        Intent intent2 = new Intent(this,add.class);
        startActivity(intent2);
    }
    public void signOut_fb() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        Toast.makeText(home_screen.this,"Sign Out Successful",Toast.LENGTH_SHORT).show();
    }
    private void signOut_g() {
        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(home_screen.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task){
                        Toast.makeText(home_screen.this,"Sign Out Successful",Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (user!=null){
         user_exit();
        }
        else {
            user_to_home();
        }
    }
    public void user_exit(){
        Intent intent6 = new Intent(Intent.ACTION_MAIN);
        intent6.addCategory(Intent.CATEGORY_HOME);
        intent6.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent6);
        finish();
    }
    public void user_to_home(){
        Intent intent1 = new Intent(this,MainActivity.class);
        startActivity(intent1);
        finish();
    }
}