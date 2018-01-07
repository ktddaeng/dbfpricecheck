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
import android.util.Log;
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
    boolean update_flag;
    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res_info);
        dB = DatabaseHandler.getInstance(this);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        setUpToolbar();
        TextView saveButton = (TextView) findViewById(R.id.button_save);
        ImageView cancelButton = (ImageView) findViewById(R.id.button_cancel);

        Intent i = getIntent();
        String id = i.getExtras().getString(Contants.EXTRA_DATAITEM);
        boolean newboy = i.getExtras().getBoolean(Contants.EXTRA_ISNEWITEM);

        //CALENDAR
        final Calendar calendar = Calendar.getInstance();
        calendar_text = (TextView) findViewById(R.id.input_dialog_date);
        View calendar_text_wrapper = findViewById(R.id.input_dateWrapper);

        String dateFormat = "MM/dd/yy";
        SimpleDateFormat simpleDate = new SimpleDateFormat(dateFormat, Locale.US);
        calendar_text.setText(simpleDate.format(calendar.getTime()));
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
        calendar_text_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(ResInfoActivity.this, date, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        location_text = (EditText) findViewById(R.id.input_dialog_location);
        View location_text_wrapper = findViewById(R.id.input_locWrapper);
        location_text_wrapper.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    location_text.requestFocus();
                    imm.showSoftInput(location_text, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    imm.hideSoftInputFromInputMethod(location_text.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
            }
        });
        location_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    location_text.requestFocus();
                    imm.showSoftInput(location_text, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    imm.hideSoftInputFromInputMethod(location_text.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
            }
        });

        showroom_qty_tv = (TextView) findViewById(R.id.input_dialog_showqty);
        View showroom_qty_wrapper = findViewById(R.id.input_show_qtyWrapper);
        showroom_qty_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseDialog(showroom_qty_tv);
            }
        });

        backstore_qty_tv = (TextView) findViewById(R.id.input_dialog_backqty);
        View backstore_qty_wrapper = findViewById(R.id.input_back_qtyWrapper);
        backstore_qty_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseDialog(backstore_qty_tv);
            }
        });

        other1Text = (EditText) findViewById(R.id.input_dialog_other1);
        other2Text = (EditText) findViewById(R.id.input_dialog_other2);
        other3Text = (EditText) findViewById(R.id.input_dialog_other3);
        other4Text = (EditText) findViewById(R.id.input_dialog_other4);

        if (newboy){
            update_flag = false;
            ri = new RestockItem();
            ri.setID(id);
        } else {
            update_flag = true;
            ri = dB.getRestockItem(id);
            calendar_text.setText(ri.getLo_logdate());
            location_text.setText(ri.getLo_location());
            showroom_qty_tv.setText(ri.getLo_sqty());
            backstore_qty_tv.setText(ri.getLo_bqty());
            other1Text.setText(ri.getLo_other1());
            other2Text.setText(ri.getLo_other2());
            other3Text.setText(ri.getLo_other3());
            other4Text.setText(ri.getLo_other4());
        }

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

    private void chooseDialog(TextView wrapper){
        AlertDialog.Builder builder = new AlertDialog.Builder(ResInfoActivity.this);
        builder.setTitle("Choose Quantity");
        View subView = getLayoutInflater().inflate(R.layout.fragment_num_picker, null);
        final TextView innerWrapper = wrapper;
        int initQty = Integer.valueOf(innerWrapper.getText().toString());
        int tens = (initQty % 100)/10, ones = (initQty % 100) % 10;

        final NumberPicker np = (NumberPicker) subView.findViewById(R.id.num_pick_tens);
        np.setMaxValue(9);
        np.setMinValue(0);
        np.setValue(tens);
        np.setWrapSelectorWheel(true);
        final NumberPicker np2 = (NumberPicker) subView.findViewById(R.id.num_pick_ones);
        np2.setMaxValue(9);
        np2.setMinValue(0);
        np2.setValue(ones);
        np2.setWrapSelectorWheel(true);
        builder.setView(subView);

        builder
                .setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String s = Integer.toString(np.getValue()*10 + np2.getValue());
                        innerWrapper.setText(s);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Toast.makeText(ResInfoActivity.this, "Action Canceled", Toast.LENGTH_SHORT).show();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void cancelEdit(){
        Toast.makeText(ResInfoActivity.this, "Action Canceled", Toast.LENGTH_SHORT).show();
        Intent returnIntent = new Intent();
        if (!update_flag){
            setResult(Activity.RESULT_CANCELED, returnIntent);
        } else {
            setResult(Activity.RESULT_FIRST_USER, returnIntent);
        }
        ResInfoActivity.this.finish();
    }

    private void saveItem(){
        if (!safetyCheck()) {
            Toast.makeText(this, "Action Invalid. Please fill in location field.", Toast.LENGTH_SHORT).show();
            return;
        }
        ri.setLo_logdate(calendar_text.getText().toString());
        ri.setLo_location(location_text.getText().toString());
        ri.setLo_bqty(backstore_qty_tv.getText().toString());
        ri.setLo_sqty(showroom_qty_tv.getText().toString());

        if (other1Text.getText().toString() == null) {
            ri.setLo_other1("");
        } else {
            ri.setLo_other1(other1Text.getText().toString());
        }
        if (other2Text.getText().toString() == null) {
            ri.setLo_other2("");
        } else {
            ri.setLo_other2(other2Text.getText().toString());
        }
        if (other3Text.getText().toString() == null) {
            ri.setLo_other3("");
        } else {
            ri.setLo_other3(other3Text.getText().toString());
        }
        if (other4Text.getText().toString() == null) {
            ri.setLo_other4("");
        } else {
            ri.setLo_other4(other4Text.getText().toString());
        }
        //add stuff to DB!
        if (update_flag) {
            dB.updateRestockItem(ri);
        } else {
            dB.addRestockItem(ri);
        }
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