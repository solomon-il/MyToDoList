package com.shlominet.util;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shlominet.mytodolist.ListActivity;
import com.shlominet.mytodolist.R;

import java.util.ArrayList;

public class TaskAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<TaskObject> arrayList;
    private DBUtil dbUtil;

    public TaskAdapter(Context context, ArrayList<TaskObject> arrayList, DBUtil dbUtil) {
        this.context = context;
        this.arrayList = arrayList;
        this.dbUtil = dbUtil;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.list_item, null);

        FrameLayout prioColorFL = view.findViewById(R.id.prio_color_fl);
        final TextView taskTitleTV = view.findViewById(R.id.task_title_tv);
        CheckBox completeCB = view.findViewById(R.id.complete_cb);

        String color = getColor(i);
        prioColorFL.setBackgroundColor(Color.parseColor(color));


        taskTitleTV.setText(arrayList.get(i).getTitle());
        taskTitleTV.setTextColor(Color.parseColor("#EC0F0723"));
        if(arrayList.get(i).isCompleted())
            taskTitleTV.setTextColor(Color.parseColor("#C887AC87"));


            completeCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean check = arrayList.get(i).isCompleted();
                arrayList.get(i).setCompleted(!check);
                //update sql
                long id = arrayList.get(i).getId();
                long numRows = dbUtil.updateComp(id, !check);
                if(numRows != 0) Toast.makeText(view.getContext(), "update data successfully", Toast.LENGTH_SHORT).show();
                else Toast.makeText(view.getContext(), "update data error", Toast.LENGTH_SHORT).show();

                if(!check) taskTitleTV.setTextColor(Color.parseColor("#C887AC87"));//set grey for complete
                TaskAdapter.this.notifyDataSetChanged();
                ListActivity.sortArrayList();
            }
        });
        completeCB.setChecked(arrayList.get(i).isCompleted());

        return view;
    }



    private String getColor(int i) {
        String prio = arrayList.get(i).getPriority().toLowerCase();
        switch (prio){
            case "urgent": return "#FF810002";//red
            case "medium": return "#FF844900";//orange
            case "low": return "#FF978B00";//yellow
            default: return "#027b00"; //if error: green
        }
    }
}
