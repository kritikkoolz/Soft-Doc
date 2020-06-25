package com.koolz.edoc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class customAdapter extends ArrayAdapter<String>{
    public customAdapter(@NonNull Context context, ArrayList<String> Documents) {
        super(context,R.layout.custom_list, Documents);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inf = LayoutInflater.from(getContext());
        View custonview = inf.inflate(R.layout.custom_list,parent,false);

        String document = getItem(position);
        TextView TV = (TextView) custonview.findViewById(R.id.list_text);
        ImageView IV = (ImageView) custonview.findViewById(R.id.list_icon);
        TV.setText(document);
        IV.setImageResource(R.drawable.list);
        return custonview;
    }
}
