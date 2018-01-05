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

import com.example.gadau.pricecheck.data.DatabaseHandler;
import com.example.gadau.pricecheck.data.RestockItem;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ResInfoActivity extends AppCompatActivity {
    DatabaseHandler dB;
    private TextView calendar_text;
    private TextView showroom_qty_tv;
    private TextView backstore_qty_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res_info);
        dB = DatabaseHandler.getInstance(this);

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
    }

    private void showroomDialog() {
        final Dialog d = new Dialog(ResInfoActivity.this);
        d.setTitle("NumPick");
        d.setContentView(R.layout.fragment_num_picker);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.num_pick);

        np.setMaxValue(100);
        np.setMinValue(0);
        np.setWrapSelectorWheel(true);

        np.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showroom_qty_tv.setText((String.valueOf(np.getValue())));
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

        np.setMaxValue(100);
        np.setMinValue(0);
        np.setWrapSelectorWheel(true);

        np.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backstore_qty_tv.setText((String.valueOf(np.getValue())));
                d.dismiss();
            }
        });
        d.show();
    }
}