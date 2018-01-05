package com.example.gadau.pricecheck;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gadau.pricecheck.data.Contants;
import com.example.gadau.pricecheck.data.DatabaseHandler;
import com.example.gadau.pricecheck.data.RestockItem;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ResInfoActivity extends AppCompatActivity {
    DatabaseHandler dB;
    RestockItem ri;
    private TextView calendar_text;
    private EditText location_text;
    private TextView showroom_qty_tv;
    private TextView backstore_qty_tv;
    private EditText other1Text;
    private EditText other2Text;
    private EditText other3Text;
    private EditText other4Text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res_info);
        dB = DatabaseHandler.getInstance(this);

        setUpToolbar();
        TextView saveButton = (TextView) findViewById(R.id.button_save);
        ImageView cancelButton = (ImageView) findViewById(R.id.button_cancel);

        Intent i = getIntent();
        String id = i.getExtras().getString(Contants.EXTRA_DATAITEM);
        boolean newboy = i.getExtras().getBoolean(Contants.EXTRA_ISNEWITEM);

        if (newboy){
            ri = new RestockItem();
        } else {
            ri = dB.getRestockItem(id);
        }

        final Calendar calendar = Calendar.getInstance();
        calendar_text = (TextView) findViewById(R.id.input_dialog_date);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);

                String dateFormat = "MM/dd/yy";
                SimpleDateFormat simpleDate = new SimpleDateFormat(dateFormat, Locale.US);

                calendar_text.setText(simpleDate.format(calendar.getTime()));
            }
        };
        calendar_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(ResInfoActivity.this, date, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        location_text = (EditText) findViewById(R.id.input_dialog_location);

        showroom_qty_tv = (TextView) findViewById(R.id.input_dialog_showqty);
        showroom_qty_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showroomDialog();
            }
        });

        backstore_qty_tv = (TextView) findViewById(R.id.input_dialog_backqty);
        backstore_qty_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backstoreDialog();
            }
        });

        other1Text = (EditText) findViewById(R.id.input_dialog_other1);
        other2Text = (EditText) findViewById(R.id.input_dialog_other2);
        other3Text = (EditText) findViewById(R.id.input_dialog_other3);
        other4Text = (EditText) findViewById(R.id.input_dialog_other4);

        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveItem();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cancelEdit();
            }
        });
    }

    private void setUpToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_edit);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(""); toolbar.setSubtitle("");
    }

    private void showroomDialog() {
        final Dialog d = new Dialog(ResInfoActivity.this);
        d.setTitle("NumPick");
        d.setContentView(R.layout.fragment_num_picker);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.num_pick);
        final NumberPicker np2 = (NumberPicker) d.findViewById(R.id.num_pick2);

        np.setMaxValue(9);
        np.setMinValue(0);
        np.setWrapSelectorWheel(true);
        np2.setMaxValue(9);
        np2.setMinValue(9);
        np2.setWrapSelectorWheel(true);

        np.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showroom_qty_tv.setText((String.valueOf(np.getValue())) + (String.valueOf(np2.getValue())));
                d.dismiss();
            }
        });
        d.show();
    }

    private void backstoreDialog() {
        final Dialog d = new Dialog(ResInfoActivity.this);
        d.setTitle("NumPick");
        d.setContentView(R.layout.fragment_num_picker);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.num_pick);
        final NumberPicker np2 = (NumberPicker) d.findViewById(R.id.num_pick2);

        np.setMaxValue(9);
        np.setMinValue(0);
        np.setWrapSelectorWheel(true);
        np2.setMaxValue(9);
        np2.setMinValue(9);
        np2.setWrapSelectorWheel(true);

        np.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backstore_qty_tv.setText((String.valueOf(np.getValue())) + (String.valueOf(np2.getValue())));
                d.dismiss();
            }
        });
        d.show();
    }

    private void cancelEdit(){
        Toast.makeText(ResInfoActivity.this, "Action Canceled", Toast.LENGTH_SHORT).show();
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        ResInfoActivity.this.finish();
    }


    private void saveItem(){
        if (!safetyCheck()) {
            Toast.makeText(this, "Action Invalid. Please fill in location field.", Toast.LENGTH_SHORT).show();
            return;
        }
        String loc = location_text.getText().toString(),
                o1 = other1Text.getText().toString(),
                o2 = other2Text.getText().toString(),
                o3 = other3Text.getText().toString(),
                o4 = other4Text.getText().toString();

        /*
        ri.setID(id);
        ri.setVendor(vendor);
        ri.setLocation(loc);
        ri.setQty(qty);
        */
        //add stuff to DB!
        /*
        if (update_flag) {
            dB.updateItem(di);
        } else {
            dB.addItem(di);
        }*/
        Toast.makeText(ResInfoActivity.this, "Item Saved!", Toast.LENGTH_SHORT).show();
        //Ensure Info page is updated, too
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        ResInfoActivity.this.finish();
    }

    private boolean safetyCheck(){
        String s = location_text.getText().toString().trim();
        if (s.isEmpty() || s.length() == 0 || s.equals("") || s == null){
            return false;
        }
        return true;
    }
}