package com.koolz.edoc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.airbnb.lottie.LottieAnimationView;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class add extends AppCompatActivity {

    private static final int REQUEST_IMAGE_SELECT=0;
    private static String user_id;
    private static String ID;
    private PhotoView iv;
    private TextInputLayout e1;
    private TextInputLayout e2;
    private Spinner choose;
    private LinearLayout b1;
    private ImageView iv2;
    private TextView Tv;
    private LinearLayout b2;
    private ProgressDialog progress;
    private LottieAnimationView myAnim;
    private UploadTask uploadTask;
    private Bitmap bt=null;
    private ByteArrayOutputStream baos;
    private byte[] data1;
    private String Doc;
    private ArrayList<String> Document;
    private int providerId=0;
    private MediaPlayer add_music;
    private Uri PicUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        iv =(PhotoView) findViewById(R.id.Iv);
        e1 = (TextInputLayout) findViewById(R.id.ed1);
        e2 = (TextInputLayout) findViewById(R.id.ed2);
        choose = (Spinner) findViewById(R.id.chooser);
        b1= (LinearLayout) findViewById(R.id.Capture);
        iv2 =(ImageView) findViewById(R.id.arrow);
        iv2.setVisibility(View.INVISIBLE);
        iv2.setEnabled(false);
        Document = new ArrayList<>();
        add_music=MediaPlayer.create(this,R.raw.add);
        progress = new ProgressDialog(this);
        Tv = (TextView)findViewById(R.id.tv);
        Tv.setVisibility(View.INVISIBLE);
        b2=(LinearLayout) findViewById(R.id.add2);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            ID=user.getEmail();
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
        DatabaseReference dataref= FirebaseDatabase.getInstance().getReference().child(user_id);
        if(dataref!=null) {
            dataref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Document.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Document.add(snapshot.getKey().toString());
                        Document.remove("feedback");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action);
        View view =getSupportActionBar().getCustomView();
        ImageView img= (ImageView) view.findViewById(R.id.image_view);
        img.setImageResource(R.drawable.add_doc);
        TextView textView=(TextView)view.findViewById(R.id.name);
        textView.setText(user.getDisplayName());
        myAnim= findViewById(R.id.animationView4);
        baos = new ByteArrayOutputStream();
    }
    public void select(View view){
        showCustomDialog();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_SELECT && resultCode == Activity.RESULT_OK) {
            PicUri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
            try {
                bt = MediaStore.Images.Media.getBitmap(this.getContentResolver(),PicUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap b = Bitmap.createScaledBitmap(bt,iv.getWidth(),iv.getHeight(),true);
            boolean bitmap;
            bitmap = b.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            data1 = baos.toByteArray();
            myAnim.setVisibility(View.INVISIBLE);
            iv.setImageBitmap(b);
            iv.setFocusableInTouchMode(true);
            b1.setEnabled(false);
            b1.setVisibility(View.INVISIBLE);
        }
    }
    private void showCustomDialog() {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_option, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        Button bt= (Button) dialogView.findViewById(R.id.Cancel);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        LinearLayout Take= (LinearLayout) dialogView.findViewById(R.id.Take_photo);
        Take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                int preference = ScanConstants.OPEN_CAMERA;
                Intent intent = new Intent(add.this, ScanActivity.class);
                intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
                startActivityForResult(intent,REQUEST_IMAGE_SELECT);
            }
        });
        LinearLayout Select= (LinearLayout) dialogView.findViewById(R.id.Select_image);
        Select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                int preference = ScanConstants.OPEN_MEDIA;
                Intent intent = new Intent(add.this, ScanActivity.class);
                intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
                startActivityForResult(intent,REQUEST_IMAGE_SELECT);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent2 = new Intent(this,home_screen.class);
        startActivity(intent2);

    }

    public void Add(View view){
        Doc = String.valueOf(choose.getSelectedItem());
        String T1= e1.getEditText().getText().toString();
        String T2= e2.getEditText().getText().toString();
        if(Doc.equals("Choose Document")){
            Toast.makeText(getApplicationContext(), "Select Document", Toast.LENGTH_SHORT).show();
        }
        else if(Document.contains(Doc)){
            AlertDialog.Builder builder = new AlertDialog.Builder(add.this);
            builder.setMessage("Do you want to update existing document ?");
            builder.setTitle("Document Already Exists");
            builder.setCancelable(false);
            builder.setIcon(R.drawable.update_d);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent1 = new Intent(add.this,Update_doc.class);
                    intent1.putExtra("document",Doc);
                    startActivity(intent1);
                    finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog alertDialog = builder.create();
            try {
                alertDialog.show();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setCancelable(false);
            }
            catch (WindowManager.BadTokenException e) {
            }
        }
        else if(T1.isEmpty()||T2.isEmpty()) {
            Toast.makeText(this,"Empty Fields",Toast.LENGTH_SHORT).show();
            }
        else if(bt==null){
            Toast.makeText(this,"Capture Document",Toast.LENGTH_SHORT).show();
        }
        else{
            progress.setMessage("Adding " +Doc+" ...");
            progress.show();
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            progress.setCanceledOnTouchOutside(false);
            progress.setCancelable(false);
            HashMap<String , Object> map = new HashMap<>();
            map.put("Holder_name",T1);
            map.put("Unique_Id",T2);
            FirebaseDatabase.getInstance().getReference().child(user_id).child(Doc).updateChildren(map);
            if(data1!=null){
                StorageReference stref = FirebaseStorage.getInstance().getReference().child(user_id).child(Doc);
                uploadTask= (UploadTask) stref.putBytes(data1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        add_music.start();
                        Toast.makeText(getApplicationContext(), "Document Added", Toast.LENGTH_SHORT).show();
                        b2.setVisibility(View.INVISIBLE);
                        b2.setEnabled(false);
                        iv2.setVisibility(View.VISIBLE);
                        Tv.setVisibility(View.VISIBLE);
                        iv2.setEnabled(true);
                        progress.dismiss();
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        e1.setEnabled(false);
                        e2.setEnabled(false);
                        choose.setEnabled(false);
                    }
                });
            }
            }
        }
    public void view(View v){
        Intent intent1 = new Intent(this,View_doc.class);
        intent1.putExtra("doc",Doc);
        startActivity(intent1);
        finish();
    }
}