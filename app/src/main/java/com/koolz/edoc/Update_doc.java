package com.koolz.edoc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.HashMap;
import java.util.List;

public class Update_doc extends AppCompatActivity {
    private String Doc;
    private String user_id;
    private String ID;
    private TextView Tv1;
    private EditText Ed1;
    private EditText Ed2;
    private PhotoView Iv;
    private ProgressDialog pd;
    private ProgressDialog pd1;
    private String t1;
    private String t2;
    private Bitmap bt=null;
    private ByteArrayOutputStream baos;
    private byte[] data1;
    private UploadTask uploadTask;
    private final long One_MEGABYTE = 1024 * 1024;
    private boolean del=false;
    private int providerId=0;
    private MediaPlayer add_music;
    private Uri PicUri;
    private static final int REQUEST_IMAGE_SELECT=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_doc);
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
        add_music=MediaPlayer.create(this,R.raw.add);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action);
        View view =getSupportActionBar().getCustomView();
        ImageView img= (ImageView) view.findViewById(R.id.image_view);
        img.setImageResource(R.drawable.update_d);
        TextView textView=(TextView)view.findViewById(R.id.name);
        textView.setText(user.getDisplayName());
        Intent intent= getIntent();
        Doc=intent.getStringExtra("document");
        Tv1 = (TextView)findViewById(R.id.tv);
        Tv1.setText("Update "+Doc);
        Ed1=(EditText)findViewById(R.id.ed1);
        Ed2=(EditText)findViewById(R.id.ed2);
        Iv=(PhotoView) findViewById(R.id.iv);
        pd=new ProgressDialog(this);
        baos = new ByteArrayOutputStream();
        pd.setMessage("Loading "+Doc+" for Update..");
        pd.show();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
        Tv1.setText("Your "+Doc+" Here");
        DatabaseReference Dbref= FirebaseDatabase.getInstance().getReference().child(user_id).child(Doc);
        StorageReference stref= FirebaseStorage.getInstance().getReference().child(user_id).child(Doc);
        Dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                information info = dataSnapshot.getValue(information.class);
                t1=info.getHolder_name();
                t2=info.getUnique_Id();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        stref.getBytes(One_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Bitmap b = Bitmap.createScaledBitmap(bitmap,Iv.getWidth(),Iv.getHeight(),true);
                Iv.setImageBitmap(b);
                pd.dismiss();
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Ed1.setText(t1);
                Ed2.setText(t2);
                Toast.makeText(Update_doc.this,Doc+" Loaded",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Ed1.setText(t1);
                Ed2.setText(t2);
                Toast.makeText(Update_doc.this,"Sorry Image Can't Be Loaded",Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void Update_img(View view) {
        showCustomDialog();
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
                Intent intent = new Intent(Update_doc.this, ScanActivity.class);
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
                Intent intent = new Intent(Update_doc.this, ScanActivity.class);
                intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
                startActivityForResult(intent,REQUEST_IMAGE_SELECT);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_SELECT && resultCode == RESULT_OK) {
            PicUri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
            try {
                bt = MediaStore.Images.Media.getBitmap(this.getContentResolver(), PicUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap b = Bitmap.createScaledBitmap(bt,Iv.getWidth(),Iv.getHeight(),true);
            boolean bitmap;
            bitmap = b.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            data1 = baos.toByteArray();
            Iv.setImageBitmap(b);
            del=true;

        }
    }
    public void Update(View view){
        pd1=new ProgressDialog(this);
        pd1.setMessage("Updating "+Doc+" ..");
        pd1.show();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        pd1.setCanceledOnTouchOutside(false);
        pd1.setCancelable(false);
        if(del==true){
            StorageReference stref= FirebaseStorage.getInstance().getReference().child(user_id).child(Doc);
            stref.delete();
            if(data1!=null) {
                uploadTask = (UploadTask) stref.putBytes(data1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        add_music.start();
                        Toast.makeText(getApplicationContext(), "Document Updated", Toast.LENGTH_SHORT).show();
                        pd1.dismiss();
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                });
            }
        }
        if((Ed1.getText().toString()).isEmpty()||(Ed2.getText().toString()).isEmpty()) {
            Toast.makeText(this,"Empty Fields",Toast.LENGTH_SHORT).show();
        }
        else{
            HashMap<String , Object> map = new HashMap<>();
            map.put("Holder_name",(Ed1.getText().toString()));
            map.put("Unique_Id",(Ed2.getText().toString()));
            FirebaseDatabase.getInstance().getReference().child(user_id).child(Doc).updateChildren(map);
            Toast.makeText(getApplicationContext(), "Document Updated", Toast.LENGTH_SHORT).show();
            pd1.dismiss();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }

}