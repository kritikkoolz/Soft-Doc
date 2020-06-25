package com.koolz.edoc;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

public class home_screen extends AppCompatActivity {
    private static String Id;
    private static String user_id;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseUser user;
    private int providerId=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
        Id=user.getEmail();
        user_id=user.getUid();
        List<? extends UserInfo> infos = user.getProviderData();
        for (UserInfo ui : infos) {
            if (ui.getProviderId().equals(GoogleAuthProvider.PROVIDER_ID)) {
                providerId=1;
            }
            if (ui.getProviderId().equals(FacebookAuthProvider.PROVIDER_ID)) {
                providerId=2;

            }
            if(ui.getProviderId().equals(FirebaseAuthProvider.PROVIDER_ID)) {
                providerId = 3;
            }
        }}
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action);
        View view =getSupportActionBar().getCustomView();
        ImageView img= (ImageView) view.findViewById(R.id.image_view);
        img.setImageResource(R.drawable.icon);
        TextView textView=(TextView)view.findViewById(R.id.name);
        if(providerId==1){
            textView.setText(Id);
        }
        else if(providerId==2||providerId==3){
            textView.setText(user.getDisplayName());
        }
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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out:
                if (providerId==1){
                    signOut_g();
                    Intent intent1 = new Intent(this,MainActivity.class);
                    startActivity(intent1);
                    finish();
                }
                else if (providerId==2){
                    signOut_fb();
                    Intent intent1 = new Intent(this,MainActivity.class);
                    startActivity(intent1);
                    finish();
                }
                else {
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(home_screen.this,"Sign Out Successful",Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(this,MainActivity.class);
                    startActivity(intent1);
                    finish();
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void showCustomDialog() {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialogue, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
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
        //Profile.setCurrentProfile(null);
        /*AccessToken accessToken = AccessToken.getCurrentAccessToken();
        accessToken.getExpires();*/
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
            Intent intent6 = new Intent(Intent.ACTION_MAIN);
            intent6.addCategory(Intent.CATEGORY_HOME);
            intent6.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent6);
        }
    }
}