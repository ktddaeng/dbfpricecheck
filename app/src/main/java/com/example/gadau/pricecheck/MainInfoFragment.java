package com.example.gadau.pricecheck;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.gadau.pricecheck.data.Contants;
import com.example.gadau.pricecheck.data.DataItem;
import com.example.gadau.pricecheck.data.DatabaseHandler;
import com.example.gadau.pricecheck.data.LogItem;
import com.example.gadau.pricecheck.data.RestockItem;
import com.example.gadau.pricecheck.logic.LogAdapter;

import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class MainInfoFragment extends Fragment {

    private DatabaseHandler dB;
    private DataItem mDi;
    private View rootView;
    private RecyclerView mRecycleView;
    private List<LogItem> listOfData;
    private LogAdapter mAdapter;
    private boolean isActivated;
    private ToggleButton toggle;
    private SharedPreferences preferences;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_info_main, container, false);
        mDi = getActivity().getIntent().getParcelableExtra(Contants.EXTRA_DATAITEM);
        dB = DatabaseHandler.getInstance(getContext());
        preferences = getActivity().getSharedPreferences(Contants.SETTINGS, MODE_PRIVATE);

        if (getActivity().getIntent().getExtras().getBoolean(Contants.ISMASTER) &&
                getActivity().getIntent().getExtras().getBoolean(Contants.EXTRA_ISREALDATA)){
            setUpRestock(mDi.getID());
            setUpRecycler();
        }

        return rootView;
    }

    private void setUpRecycler(){
        dB = DatabaseHandler.getInstance(getContext());
        listOfData = dB.getListofData(mDi.getID());
        mRecycleView = (RecyclerView) rootView.findViewById(R.id.rec_log);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecycleView.setLayoutManager(layoutManager);

        mAdapter = new LogAdapter(listOfData);
        mRecycleView.setAdapter(mAdapter);
    }

    private void setUpRestockInformation(){
        RestockItem ri = dB.getRestockItem(mDi.getID());
        if (ri == null){
            return;
        }
        View resView = rootView.findViewById(R.id.rec_restock_info);
        TextView recDate = (TextView) rootView.findViewById(R.id.rec_restock_lastdate);
        TextView recLoc = (TextView) rootView.findViewById(R.id.rec_restock_location);
        TextView recShowQty = (TextView) rootView.findViewById(R.id.rec_restock_showroom);
        TextView recBackQty = (TextView) rootView.findViewById(R.id.rec_restock_backstore);
        TextView recOther1 = (TextView) rootView.findViewById(R.id.rec_restock_other1);
        TextView recOther2 = (TextView) rootView.findViewById(R.id.rec_restock_other2);
        TextView recOther3 = (TextView) rootView.findViewById(R.id.rec_restock_other3);
        TextView recOther4 = (TextView) rootView.findViewById(R.id.rec_restock_other4);

        recDate.setText(ri.getLo_logdate());
        recLoc.setText(ri.getLo_location());
        recShowQty.setText(ri.getLo_sqty());
        recBackQty.setText(ri.getLo_bqty());

        if (TextUtils.isEmpty(ri.getLo_other1())) {
            recOther1.setVisibility(View.GONE);
        } else {
            recOther1.setText(ri.getLo_other1());
            recOther1.setVisibility(View.VISIBLE);
        }

        if (TextUtils.isEmpty(ri.getLo_other2())) {
            recOther2.setVisibility(View.GONE);
        } else {
            recOther2.setText(ri.getLo_other2());
            recOther2.setVisibility(View.VISIBLE);
        }

        if (TextUtils.isEmpty(ri.getLo_other3())) {
            recOther3.setVisibility(View.GONE);
        } else {
            recOther3.setText(ri.getLo_other3());
            recOther3.setVisibility(View.VISIBLE);
        }

        if (TextUtils.isEmpty(ri.getLo_other4())) {
            recOther4.setVisibility(View.GONE);
        } else {
            recOther4.setText(ri.getLo_other4());
            recOther4.setVisibility(View.VISIBLE);
        }

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
        //Pass information here
        Intent i = new Intent(getContext(), ResInfoActivity.class);
        i.putExtra(Contants.EXTRA_DATAITEM, mDi.getID());
        if (isNewItem){
            i.putExtra(Contants.EXTRA_ISNEWITEM, true);
        } else {
            i.putExtra(Contants.EXTRA_ISNEWITEM, false);
        }

        startActivityForResult(i, Contants.WAS_CHANGED);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Contants.WAS_CHANGED){
            if (resultCode == RESULT_OK) {
                setUpRestockInformation();
            } else if (resultCode == RESULT_CANCELED){
                onRestock(mDi.getID());
                setUpRestock(mDi.getID());
            }
        }
    }

    private void setUpRestock(String id){
        final String identification = id;
        toggle = (ToggleButton) getActivity().findViewById(R.id.button_restock);
        toggle.setVisibility(View.VISIBLE);
        if (dB.isOnRestock(id)){
            isActivated = true;
            View resView = rootView.findViewById(R.id.rec_restock_info);
            resView.setVisibility(View.VISIBLE);
            toggle.setChecked(true);
            setUpRestockInformation();
        } else {
            isActivated = false;
            View resView = rootView.findViewById(R.id.rec_restock_info);
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
        getActivity().finish();
        startActivity(getActivity().getIntent());
    }

    public void refresh() {
        setUpRestock(mDi.getID());
    }
}
