package com.koolz.edoc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;

import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity{
    private FirebaseAuth.AuthStateListener mauth;
    private FirebaseAuth auth;
    private static int SIGN_IN=0;
    private GoogleSignInClient mGoogleSignInClient;
    private ProgressDialog progress;
    private ProgressDialog progress1;
    private CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action);
        View view = getSupportActionBar().getCustomView();
        ImageView img = (ImageView) view.findViewById(R.id.image_view);
        img.setImageResource(R.drawable.icon);
        TextView textView = (TextView) view.findViewById(R.id.name);
        String id = "Soft-Doc";
        textView.setText(id);
        user = FirebaseAuth.getInstance().getCurrentUser();
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        progress = new ProgressDialog(this);
        progress.setMessage("Signing In...");
        progress.setCanceledOnTouchOutside(false);
        progress.setCancelable(false);
        progress1 = new ProgressDialog(this);
        progress1.setMessage("Signing In...");
        progress1.setCanceledOnTouchOutside(false);
        progress1.setCancelable(false);
        auth = FirebaseAuth.getInstance();
        /*mauth = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
                /*if (user != null) {
                    final Intent intent = new Intent(MainActivity.this, home_screen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent)
                }
            }
        };*/
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        }

        private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progress1.dismiss();
                            final Intent intent = new Intent(MainActivity.this, home_screen.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void signIn() {
        progress.show();
        SIGN_IN=1;
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (SIGN_IN==1) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            progress.dismiss();
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
                progress.show();
            } catch (ApiException e) {
                Toast.makeText(MainActivity.this,"Sign Failed",Toast.LENGTH_SHORT).show();
            }
        }
        else if(SIGN_IN==2) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
            progress1.show();
        }

    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progress.dismiss();
                            Toast.makeText(MainActivity.this,"Sign In Successful",Toast.LENGTH_SHORT).show();
                            final Intent intent = new Intent(MainActivity.this, home_screen.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(MainActivity.this,"Sign In Unsuccessful",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void fblogin(View view){
        SIGN_IN=2;
        callbackManager = CallbackManager.Factory.create();
        FacebookSdk.sdkInitialize(getApplicationContext());
        /*if (AccessToken.getCurrentAccessToken() != null) {
            new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest.Callback() {
                @Override
                public void onCompleted(GraphResponse graphResponse) {
                    AccessToken.setCurrentAccessToken(null);
                    LoginManager.getInstance().logOut();
                }
            }).executeAsync();
        }*/
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());

            }
            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this,"Sign In Canceled",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(MainActivity.this,"Sign In Unsuccessful",Toast.LENGTH_SHORT).show();

            }
        });
    }
    public void Login1(View view){
        Intent intent1 = new Intent(this,login.class);
        startActivity(intent1);
    }
    public void Register1(View view){
        Intent intent2 = new Intent(this,Register.class);
        startActivity(intent2);
    }

    public void Google_Login(View view){
        signIn();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (user != null) {
            List<? extends UserInfo> infos = user.getProviderData();
            for (UserInfo ui : infos) {
                if (ui.getProviderId().equals(FirebaseAuthProvider.PROVIDER_ID)) {
                    if(user.isEmailVerified()){
                        final Intent intent = new Intent(MainActivity.this, home_screen.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                    else if(!user.isEmailVerified()){
                        FirebaseAuth.getInstance().signOut();
                    }
                }
                else if (ui.getProviderId().equals(FacebookAuthProvider.PROVIDER_ID)){
                    final Intent intent = new Intent(MainActivity.this, home_screen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                else if (ui.getProviderId().equals(GoogleAuthProvider.PROVIDER_ID)){
                    final Intent intent = new Intent(MainActivity.this, home_screen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        }//auth.addAuthStateListener(mauth);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
            Intent intent6 = new Intent(Intent.ACTION_MAIN);
            intent6.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent6);
    }
}
