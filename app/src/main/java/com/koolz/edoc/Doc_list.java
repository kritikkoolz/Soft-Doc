package com.koolz.edoc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.List;

public class Doc_list extends AppCompatActivity {
    private ArrayList<String> Document;
    private String user_id;
    private String ID;
    private ProgressDialog pd;
    private boolean list;
    private int providerId=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_list);
        Intent intent3=getIntent();
        list=intent3.getBooleanExtra("list",false);
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
        img.setImageResource(R.drawable.list2);
        TextView textView=(TextView)view.findViewById(R.id.name);
        textView.setText(user.getDisplayName());
        final ListView Doc_list =(ListView) findViewById(R.id.doc_list);
        Document = new ArrayList<>();
        pd =new ProgressDialog(this);
        pd.setMessage("Adding Document info...");
        pd.show();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
        final ArrayAdapter Aa = new customAdapter(this,Document);
        Doc_list.setAdapter(Aa);
        DatabaseReference dataref= FirebaseDatabase.getInstance().getReference().child(user_id);
        dataref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Document.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Document.add(snapshot.getKey().toString());
                    Document.remove("feedback");
                }
                Aa.notifyDataSetChanged();
                pd.dismiss();
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                if (Document.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Doc_list.this);
                    builder.setMessage("Do you want to add document ?");
                    builder.setTitle("No Document Found");
                    builder.setCancelable(false);
                    builder.setIcon(R.drawable.no_doc);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent1 = new Intent(Doc_list.this,add.class);
                            startActivity(intent1);
                            finish();
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent2 = new Intent(Doc_list.this,home_screen.class);
                            startActivity(intent2);
                            finish();
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
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Doc_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String Doc = (String) Aa.getItem(position);
                if(list==false) {
                    Intent intent = new Intent(Doc_list.this, View_doc.class);
                    intent.putExtra("doc",Doc);
                    startActivity(intent);
                    finish();
                }
                else{
                    Intent intent = new Intent(Doc_list.this,Update_doc.class);
                    intent.putExtra("document",Doc);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}


