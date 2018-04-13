package com.shlominet.mytodolist;

import android.app.Dialog;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.shlominet.util.PriorityEnum;
import com.shlominet.util.SpinnerAdapter;
import com.shlominet.util.TaskAdapter;
import com.shlominet.util.DBUtil;
import com.shlominet.util.TaskObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<TaskObject> taskArrayList;
    private TaskAdapter taskAdapter;
    private DBUtil dbUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        dbUtil = new DBUtil(this);

        listView = findViewById(R.id.list_view);
        fillListViewFromDB();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //
            }
        });
    }

    private void fillListViewFromDB() {
        Cursor cursor = dbUtil.getData();
        TaskObject temp = new TaskObject();
    }

    public void add_new_click(View view) {
        //make spinner entries and put in adapter
        String[] priorities2 = new String[]{
                PriorityEnum.URGENT.toString(),
                PriorityEnum.MEDIUM.toString(),
                PriorityEnum.LOW.toString()
        };
        String[] priorities = new String[]{
                "Urgent",
                "Medium",
                "Low"
        };
        final List<String> prioritiesList = new ArrayList<>(Arrays.asList(priorities2));
        final SpinnerAdapter spinnerArrayAdapter = new SpinnerAdapter(
                this,R.layout.support_simple_spinner_dropdown_item,prioritiesList);

        //make dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_task);

        final EditText TitleET = dialog.findViewById(R.id.task_title_et);
        final EditText DescET = dialog.findViewById(R.id.task_desc_et);
        final TextView prioHelper = dialog.findViewById(R.id.prio_helper_tv);

        //make spinner
        Spinner prioSp = dialog.findViewById(R.id.priority_spinner);
        prioSp.setPrompt("Select Priority");
        prioSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String str = "";
                switch (i){
                    case 0: str = PriorityEnum.URGENT.toString();  break;
                    case 1: str = PriorityEnum.MEDIUM.toString();  break;
                    case 2: str = PriorityEnum.LOW.toString();     break;
                    default:                                       break;
                }
                prioHelper.setText(str);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        prioSp.setAdapter(spinnerArrayAdapter);

        //make save and cancel buttons
        Button saveBt = dialog.findViewById(R.id.save_btn);
        Button cancelBt = dialog.findViewById(R.id.cancel_btn);

        //save to SQLite
        saveBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isOK = dbUtil.saveToSql(TitleET.getText().toString(), DescET.getText().toString(), prioHelper.getText().toString());
                if(isOK) Toast.makeText(ListActivity.this, "insert data successfully", Toast.LENGTH_SHORT).show();
                else Toast.makeText(ListActivity.this, "insert data error", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        cancelBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
