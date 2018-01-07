package com.example.gadau.pricecheck;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.airbnb.lottie.LottieAnimationView;
import com.example.gadau.pricecheck.data.Contants;
import com.example.gadau.pricecheck.data.DataItem;
import com.example.gadau.pricecheck.data.DatabaseHandler;
import com.example.gadau.pricecheck.data.LogItem;
import com.example.gadau.pricecheck.data.RestockItem;
import com.example.gadau.pricecheck.data.MenuOption;
import com.example.gadau.pricecheck.logic.LogAdapter;
import com.example.gadau.pricecheck.logic.MainAdapter;
import com.example.gadau.pricecheck.logic.SwipeDismissBaseActivity;

import java.util.List;

public class InformationActivity extends SwipeDismissBaseActivity {
    private DatabaseHandler dB;
    private DataItem di;
    private RecyclerView mRecycleView;
    private List<LogItem> listOfData;
    private LogAdapter mAdapter;
    private boolean isActivated;
    private SharedPreferences preferences;
    //private LottieAnimationView animationView;
    private ToggleButton toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        Toast.makeText(this, "Swipe Right to Close", Toast.LENGTH_SHORT).show();
        preferences = getSharedPreferences(Contants.SETTINGS, MODE_PRIVATE);

        TextView infoID = (TextView) findViewById(R.id.info_id);
        TextView infoDesc = (TextView) findViewById(R.id.info_desc);
        TextView infoDollars = (TextView) findViewById(R.id.info_dollars);
        TextView infoCents = (TextView) findViewById(R.id.info_cents);
        ImageView cancelButton = (ImageView) findViewById(R.id.button_cancel);
        View root = findViewById(R.id.info_main);
        if (getIntent().getExtras().getBoolean(Contants.EXTRA_ISREALDATA)){
            //change background to deep red
            root.setBackgroundResource(R.color.colorPrimary);
        } else {
            Toast.makeText(this, "Item needs to be registered in database!", Toast.LENGTH_SHORT).show();
            root.setBackgroundResource(R.color.colorRedBG);
        }
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InformationActivity.this.finish();
            }
        });

        di = getIntent().getParcelableExtra(Contants.EXTRA_DATAITEM);

        String price = di.getPrice();
        int index = price.indexOf(".");

        infoID.setText(di.getID());
        infoDesc.setText(di.getDesc());
        //TODO: The line below truncates the price string where the decimal point is. Account for situation where we don't have a decimal point. meaning index < 0
        if (index < 0) {
            price += ".00";
            index = price.indexOf(".");
        }
        infoDollars.setText(price.substring(0, index));
        infoCents.setText(price.substring(index + 1));

        if (getIntent().getExtras().getBoolean(Contants.ISMASTER) && getIntent().getExtras().getBoolean(Contants.EXTRA_ISREALDATA)){
            setUpRecycler();
            setUpRestock(di.getID());
        }
    }

    private void setUpRecycler(){
        dB = DatabaseHandler.getInstance(this);
        listOfData = dB.getListofData(di.getID());
        mRecycleView = (RecyclerView) findViewById(R.id.rec_log);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecycleView.setLayoutManager(layoutManager);

        mAdapter = new LogAdapter(listOfData);
        mRecycleView.setAdapter(mAdapter);
    }

    private void setUpRestockInformation(){
        RestockItem ri = dB.getRestockItem(di.getID());
        if (ri == null){
            return;
        }
        View resView = findViewById(R.id.rec_restock_info);
        TextView recDate = (TextView) findViewById(R.id.rec_restock_lastdate);
        TextView recLoc = (TextView) findViewById(R.id.rec_restock_location);
        TextView recShowQty = (TextView) findViewById(R.id.rec_restock_showroom);
        TextView recBackQty = (TextView) findViewById(R.id.rec_restock_backstore);
        TextView recOther1 = (TextView) findViewById(R.id.rec_restock_other1);
        TextView recOther2 = (TextView) findViewById(R.id.rec_restock_other2);
        TextView recOther3 = (TextView) findViewById(R.id.rec_restock_other3);
        TextView recOther4 = (TextView) findViewById(R.id.rec_restock_other4);

        recDate.setText(ri.getLo_logdate());
        recLoc.setText(ri.getLo_location());
        recShowQty.setText(ri.getLo_sqty());
        recBackQty.setText(ri.getLo_bqty());
        recOther1.setText(ri.getLo_other1());
        recOther2.setText(ri.getLo_other2());
        recOther3.setText(ri.getLo_other3());
        recOther4.setText(ri.getLo_other4());
        resView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchRestockPage(false);
            }
        });
    }

    private void launchRestockPage(boolean isNewItem){
        if (!preferences.getBoolean(Contants.ISMASTER, true)){
            return;
        }
        //TODO: Pass information here
        Intent i = new Intent(this, ResInfoActivity.class);
        i.putExtra(Contants.EXTRA_DATAITEM, di.getID());
        if (isNewItem){
            i.putExtra(Contants.EXTRA_ISNEWITEM, true);
        } else {
            i.putExtra(Contants.EXTRA_ISNEWITEM, false);
        }

        startActivityForResult(i, Contants.WAS_CHANGED);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Contants.WAS_CHANGED){
            if (resultCode == RESULT_OK) {
                setUpRestockInformation();
            } else if (resultCode == RESULT_CANCELED){
                onRestock(di.getID());
                setUpRestock(di.getID());
            }
        }
    }

    private void setUpRestock(String id){
        final String identification = id;
        toggle = (ToggleButton) findViewById(R.id.button_restock);
        toggle.setVisibility(View.VISIBLE);
        if (dB.isOnRestock(id)){
            isActivated = true;
            View resView = findViewById(R.id.rec_restock_info);
            resView.setVisibility(View.VISIBLE);
            toggle.setChecked(true);
            setUpRestockInformation();
        } else {
            isActivated = false;
            View resView = findViewById(R.id.rec_restock_info);
            resView.setVisibility(View.GONE);
            toggle.setChecked(false);
        }
        toggle.setText("None");
        toggle.setTextOn("Rstk");
        toggle.setTextOff("None");
        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRestock(identification);
            }
        });
    }

    private void onRestock(String id){
        if (isActivated == false){
            isActivated = true;
            toggle.setChecked(true);
            launchRestockPage(true);
        } else {
            isActivated = false;
            toggle.setChecked(false);
            dB.deleteRestockItem(id);
            refreshPage();
        }
    }

    private void refreshPage(){
        finish();
        startActivity(getIntent());
    }
}
