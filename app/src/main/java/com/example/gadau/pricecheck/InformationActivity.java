package com.example.gadau.pricecheck;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gadau.pricecheck.data.Contants;
import com.example.gadau.pricecheck.data.DataItem;
import com.example.gadau.pricecheck.logic.SwipeDismissBaseActivity;

public class InformationActivity extends SwipeDismissBaseActivity {

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

        //TODO: Load Extra information from Intent Extras
        //Unverified
        DataItem di = getIntent().getParcelableExtra(Contants.EXTRA_DATAITEM);

        //TODO: Parse the string from EXTRA_PRICE to display on info page
        //Unverified
        String price = di.getPrice();
        int index = price.indexOf(".");

        infoID.setText(di.getID());
        infoDesc.setText(di.getDesc());
        infoDollars.setText(price.substring(0, index));
        infoCents.setText(price.substring(index + 1));
    }
}
