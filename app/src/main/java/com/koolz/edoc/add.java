package com.koolz.edoc;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class add extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE=1;
    private static final int REQUEST_IMAGE_SELECT=0;
    private static String user_id;
    private static String ID;
    private ImageView iv;
    private EditText e1;
    private EditText e2;
    private Spinner choose;
    private Button b1;
    private ImageView iv2;
    private TextView Tv;
    private Button b2;
    private ProgressDialog progress;
    private LottieAnimationView myAnim;
    private UploadTask uploadTask;
    private Bitmap bt=null;
    private ByteArrayOutputStream baos;
    private byte[] data1;
    private String Doc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        iv =(ImageView) findViewById(R.id.Iv);
        e1 = (EditText) findViewById(R.id.ed1);
        e2 = (EditText) findViewById(R.id.ed2);
        choose = (Spinner) findViewById(R.id.chooser);
        b1= (Button) findViewById(R.id.Capture);
        iv2 =(ImageView) findViewById(R.id.arrow);
        iv2.setVisibility(View.INVISIBLE);
        iv2.setEnabled(false);
        progress = new ProgressDialog(this);
        Tv = (TextView)findViewById(R.id.tv);
        Tv.setVisibility(View.INVISIBLE);
        b2=(Button)findViewById(R.id.add2);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user_id=user.getUid();
        ID=user.getEmail();
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action);
        View view =getSupportActionBar().getCustomView();
        ImageView img= (ImageView) view.findViewById(R.id.image_view);
        img.setImageResource(R.drawable.add_doc);
        TextView textView=(TextView)view.findViewById(R.id.name);
        textView.setText(ID);
        myAnim= findViewById(R.id.animationView4);
        baos = new ByteArrayOutputStream();
    }
    public void select(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
        /*
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your Image");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto ,R);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);*/
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            bt = (Bitmap) extras.get("data");
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent2 = new Intent(this,home_screen.class);
        startActivity(intent2);

    }

    public void Add(View view){
        Doc = String.valueOf(choose.getSelectedItem());
        String T1= e1.getText().toString();
        String T2= e2.getText().toString();
        if(Doc.equals("Choose Document")){
            Toast.makeText(getApplicationContext(), "Select Document", Toast.LENGTH_SHORT).show();
        }
        else if(T1.isEmpty()||T2.isEmpty()) {
            Toast.makeText(this,"Empty Fields",Toast.LENGTH_SHORT).show();
            }
        else if(bt==null){
            Toast.makeText(this,"Capture Document",Toast.LENGTH_SHORT).show();
        }
        else{
            progress.setMessage("Adding Document...");
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