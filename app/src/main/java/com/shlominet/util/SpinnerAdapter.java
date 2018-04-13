package com.shlominet.util;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class SpinnerAdapter extends ArrayAdapter {

    public SpinnerAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        TextView tv = (TextView) view;
        if(position == 0) {
            tv.setBackgroundColor(Color.parseColor("#FFFF0000")); //red for urgent
        }
        else if(position == 1){
            tv.setBackgroundColor(Color.parseColor("#93FF5100")); //orange for medium
        }
        else if(position == 2){
            tv.setBackgroundColor(Color.parseColor("#93FADD00")); //yellow for low
        }
        return view;
    }
}
