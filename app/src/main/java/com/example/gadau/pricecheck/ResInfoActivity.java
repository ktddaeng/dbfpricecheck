package com.example.gadau.pricecheck;

import android.app.Activity;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gadau.pricecheck.data.DatabaseHandler;
import com.example.gadau.pricecheck.data.RestockItem;

public class ResInfoActivity extends AppCompatActivity {
    DatabaseHandler dB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res_info);
        dB = DatabaseHandler.getInstance(this);

    }

    // make function that check extras
    /*
    get is new item boolean (if is new item true, then don't need to check database, otherwise
        if is in database, then
        datepicker

     */
}
