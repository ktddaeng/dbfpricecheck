package com.example.gadau.pricecheck;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.gadau.pricecheck.data.Contants;
import com.example.gadau.pricecheck.data.DataItem;
import com.example.gadau.pricecheck.data.DatabaseHandler;
import com.example.gadau.pricecheck.data.LogItem;
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
    private LottieAnimationView animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        Toast.makeText(this, "Swipe Right to Close", Toast.LENGTH_SHORT).show();

        TextView infoID = (TextView) findViewById(R.id.info_id);
        TextView infoDesc = (TextView) findViewById(R.id.info_desc);
        TextView infoDollars = (TextView) findViewById(R.id.info_dollars);
        TextView infoCents = (TextView) findViewById(R.id.info_cents);
        ImageView cancelButton = (ImageView) findViewById(R.id.button_cancel);

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
        infoDollars.setText(price.substring(0, index));
        infoCents.setText(price.substring(index + 1));

        if (getIntent().getExtras().getBoolean(Contants.ISMASTER)){
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

    private void setUpRestock(String id){
        final String identification = id;
        animationView = (LottieAnimationView) findViewById(R.id.animation_view);
        animationView.setVisibility(View.VISIBLE);
        if (dB.isOnRestock(id)){
            isActivated = true;
            animationView.setProgress(1);
        } else {
            isActivated = false;
            animationView.setProgress(0);
        }

        animationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRestock(identification);
            }
        });
    }

    private void onRestock(String id){
        isActivated = !isActivated;

        if (isActivated){
            animationView.playAnimation();
            dB.addRestockItem(id);
            Toast.makeText(this, "Restock Set", Toast.LENGTH_SHORT).show();
        } else {
            animationView.setProgress(0);
            dB.deleteRestockItem(id);
            Toast.makeText(this, "No Restock", Toast.LENGTH_SHORT).show();
        }
    }
}
