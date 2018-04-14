package com.shlominet.mytodolist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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

import com.shlominet.util.SpinnerAdapter;
import com.shlominet.util.TaskAdapter;
import com.shlominet.util.DBUtil;
import com.shlominet.util.TaskObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private ListView listView;
    private static ArrayList<TaskObject> taskArrayList;
    private TaskAdapter taskAdapter;
    private DBUtil dbUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        dbUtil = new DBUtil(this);

        taskArrayList = new ArrayList<>();
        fillArrayListFromDB();
        sortArrayList();

        taskAdapter = new TaskAdapter(this, taskArrayList, dbUtil);
        listView = findViewById(R.id.list_view);
        listView.setAdapter(taskAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TaskObject object = (TaskObject)taskAdapter.getItem(i);
                dialogPreview(object);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                TaskObject object = (TaskObject)taskAdapter.getItem(i);
                //open dialog to delete from array and db
                dialogDelete(object, i);
                return true;
            }
        });
    }

    private void dialogDelete(final TaskObject object, final int index) {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Delete Task");
        alertDialog.setMessage("Are you sure?");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteFromArray(index);
                        deleteFromDb(object.getId());
                    }
                });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void deleteFromDb(long id) {
        int result = dbUtil.deleteRow(id);
        if(result != 0) Toast.makeText(this, "delete item from db successfully", Toast.LENGTH_SHORT).show();
        else Toast.makeText(ListActivity.this, "delete item error", Toast.LENGTH_SHORT).show();

    }

    private void deleteFromArray(int index) {
        taskArrayList.remove(index);
        taskAdapter.notifyDataSetChanged();
    }

    private void dialogPreview(final TaskObject object) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_preview);
        dialog.setCanceledOnTouchOutside(true);
        //init
        final TextView titleTv, descTV, dateTV;
        titleTv = dialog.findViewById(R.id.title_prev_tv);
        descTV = dialog.findViewById(R.id.desc_prev_tv);
        dateTV = dialog.findViewById(R.id.date_prev_tv);
        titleTv.setText(object.getTitle());
        descTV.setText(object.getDescription());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
        String date = dateFormat.format(object.getDateAdded());
        dateTV.setText(date);
        Button ok = dialog.findViewById(R.id.ok_btn);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void fillArrayListFromDB() {
        Cursor cursor = dbUtil.getData();
        //String str = "";
        while(cursor.moveToNext()) {
            TaskObject taskObject = new TaskObject();

            taskObject.setId(cursor.getInt(0));                     //get id
            taskObject.setTitle(cursor.getString(1));               //get title
            taskObject.setDescription(cursor.getString(2));         //get desc
            taskObject.setPriority(cursor.getString(3));            //get priority
            taskObject.setDateAdded(new Date(cursor.getLong(4)));   //get date
            taskObject.setCompleted((cursor.getInt(5)==0)?false:true);//get is completed

            taskArrayList.add(taskObject);
        }
    }

    public static void sortArrayList() {
        Collections.sort(taskArrayList, new Comparator<TaskObject>() {
            @Override
            public int compare(TaskObject o1, TaskObject o2) {
                //by complete
                boolean comp1 = o1.isCompleted();
                boolean comp2 = o2.isCompleted();
                if(!comp1 && comp2) return -1;
                if(comp1 && !comp2) return 1;

                //by priority
                String prio1 = o1.getPriority();
                String prio2 = o2.getPriority();
                int comp = prio2.compareToIgnoreCase(prio1);
                if(comp != 0) return comp;

                //by date
                Date d1 = o1.getDateAdded();
                Date d2 = o2.getDateAdded();
                if(d1.before(d2)) return -1;
                if(d1.after(d2)) return 1;
                return 0;
            }
        });
    }

        //test:
//            str+="id: "+cursor.getString(0)+"\n";
//            str+="title: "+cursor.getString(1)+"\n";
//            str+="desc: "+cursor.getString(2)+"\n";
//            str+="prio: "+cursor.getString(3)+"\n";
//            str+="date: "+new Date(cursor.getLong(4))+"\n";
//            str+="comp: "+cursor.getInt(5)+"\n";
//            str+="----------------\n";
//    public void showData(String str) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setCancelable(true);
//        builder.setTitle(DBUtil.DATA_NAME);
//        builder.setMessage(str);
//        builder.show();
//    }

    public void add_new_click(View view) {
        //make spinner entries and put in adapter
        String[] priorities = new String[]{
                "Urgent",
                "Medium",
                "Low"
        };

        final List<String> prioritiesList = new ArrayList<>(Arrays.asList(priorities));
        final SpinnerAdapter spinnerArrayAdapter = new SpinnerAdapter(
                this,R.layout.support_simple_spinner_dropdown_item,prioritiesList);

        //make dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_task);
        dialog.setCanceledOnTouchOutside(true);

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
                    case 0: str = "Urgent";  break;
                    case 1: str = "medium";  break;
                    case 2: str = "Low";     break;
                    default:                 break;
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
                long rowNum = dbUtil.saveToSql(TitleET.getText().toString(), DescET.getText().toString(), prioHelper.getText().toString());
                taskArrayList.add(new TaskObject(rowNum, TitleET.getText().toString(), DescET.getText().toString(),
                        prioHelper.getText().toString(),new Date(System.currentTimeMillis()), false));
                if(rowNum != -1) Toast.makeText(ListActivity.this, "insert data successfully", Toast.LENGTH_SHORT).show();
                else Toast.makeText(ListActivity.this, "insert data error", Toast.LENGTH_SHORT).show();

                sortArrayList();
                taskAdapter.notifyDataSetChanged();

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
