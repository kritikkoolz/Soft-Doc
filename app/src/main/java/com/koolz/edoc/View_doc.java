package com.koolz.edoc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
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
import java.util.List;

public class View_doc extends AppCompatActivity {
    private String user_id;
    private String ID;
    private String Doc;
    private TextView Tv1;
    private TextView Tv3;
    private TextView Tv2;
    private PhotoView Iv1;
    private final long One_MEGABYTE = 1024 * 1024;
    private ProgressDialog pd;
    private ProgressDialog pd1;
    private String t1;
    private String t2;
    private int providerId=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_doc);
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
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action);
        View view =getSupportActionBar().getCustomView();
        ImageView img= (ImageView) view.findViewById(R.id.image_view);
        img.setImageResource(R.drawable.view_d);
        TextView textView=(TextView)view.findViewById(R.id.name);
        textView.setText(user.getDisplayName());
        Intent intent= getIntent();
        Doc=intent.getStringExtra("doc");
        Tv1=(TextView)findViewById(R.id.tv6);
        Tv2=(TextView)findViewById(R.id.tv2);
        Tv3=(TextView)findViewById(R.id.tv3);
        Iv1=(PhotoView) findViewById(R.id.iv);
        pd=new ProgressDialog(this);
        pd.setMessage("Loading "+Doc+" ...");
        pd.show();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        Tv1.setText("Your "+Doc+" Here");
        DatabaseReference Dbref= FirebaseDatabase.getInstance().getReference().child(user_id).child(Doc);
        StorageReference stref= FirebaseStorage.getInstance().getReference().child(user_id).child(Doc);
        Dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                information info = dataSnapshot.getValue(information.class);
                if (info != null) {
                    t1 = info.getHolder_name();
                    t2 = info.getUnique_Id();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        stref.getBytes(One_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Bitmap b = Bitmap.createScaledBitmap(bitmap,Iv1.getWidth(),Iv1.getHeight(),true);
                Iv1.setImageBitmap(bitmap);
                pd.dismiss();
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Tv3.setText(t1);
                Tv2.setText(t2);
                Toast.makeText(View_doc.this,Doc+" Loaded",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(View_doc.this,"Sorry Image Can't Be Loaded",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.custom_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(View_doc.this);
                builder.setMessage("If you delete all your data will be lost");
                builder.setTitle("Do You Really Want To Delete "+Doc+" ?");
                builder.setCancelable(false);
                builder.setIcon(R.drawable.no_doc);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pd1=new ProgressDialog(View_doc.this);
                        pd1.setMessage("Deleting "+Doc);
                        pd1.show();
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        pd1.setCanceledOnTouchOutside(false);
                        pd1.setCancelable(false);
                        DatabaseReference Dbref= FirebaseDatabase.getInstance().getReference().child(user_id).child(Doc);
                        Dbref.child("Holder_name").setValue(null);
                        Dbref.child("Unique_Id").setValue(null);
                        Dbref.setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                StorageReference stref= FirebaseStorage.getInstance().getReference().child(user_id).child(Doc);
                                stref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        pd1.dismiss();
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                        Intent intent2=new Intent(View_doc.this,Doc_list.class);
                                        startActivity(intent2);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd1.dismiss();
                                        Toast.makeText(View_doc.this,"Image Can't Deleted..",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setCancelable(false);
                return true;
            case R.id.update_doc:
                Intent intent1=new Intent(this,Update_doc.class);
                intent1.putExtra("document",Doc);
                startActivity(intent1);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}