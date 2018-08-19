package com.example.gadau.pricecheck;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gadau.pricecheck.data.Contants;
import com.example.gadau.pricecheck.data.DataItem;
import com.example.gadau.pricecheck.data.DatabaseHandler;
import com.example.gadau.pricecheck.data.MenuOption;
import com.example.gadau.pricecheck.logic.AnyOrientationActivity;
import com.example.gadau.pricecheck.logic.ItemClickListener;
import com.example.gadau.pricecheck.logic.MainAdapter;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements ItemClickListener{
    private IntentIntegrator qrScan;
    private List<MenuOption> listOfData;
    private RecyclerView mRecycleView;
    private MainAdapter mAdapter;
    private DatabaseHandler dB;
    private View rootView;
    SharedPreferences preferences;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search, container, false);
        preferences = getActivity().getSharedPreferences(Contants.SETTINGS, Context.MODE_PRIVATE);
        dB = DatabaseHandler.getInstance(getActivity());

        listOfData = new ArrayList<>();
        listOfData.add(new MenuOption(R.string.header1, R.string.desc1, R.color.colorAccent));
        listOfData.add(new MenuOption(R.string.header2, R.string.desc2, R.color.colorPrimary));

        mRecycleView = (RecyclerView) rootView.findViewById(R.id.rec_main);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecycleView.setLayoutManager(layoutManager);

        mAdapter = new MainAdapter(listOfData);
        mRecycleView.setAdapter(mAdapter);
        mAdapter.setClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view, int position) {
        final MenuOption data = listOfData.get(position);
        switch (data.getHeader()) {
            case R.string.header1:
                barcodeMode();
                break;
            case R.string.header2:
                inputNumberSearch();
                break;
        }
    }

    private void inputNumberSearch(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Input ID");
        View subView = getActivity().getLayoutInflater().inflate(R.layout.fragment_edit_id, null);
        final EditText inID = (EditText) subView.findViewById(R.id.input_dialog_IDNo);
        builder.setView(subView);
        inID.requestFocus();

        builder
                .setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        identifyID(inID.getText().toString());
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Toast.makeText(getActivity(), "Search Canceled", Toast.LENGTH_SHORT).show();
                    }
                });
        final AlertDialog dialog = builder.create();
        if (getResources().getConfiguration().hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO){
            Toast.makeText(getActivity(), "Bluetooth Scanner Detected", Toast.LENGTH_SHORT).show();
            inID.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (inID.getText().length() >= 12){
                        identifyID(inID.getText().toString());
                        dialog.dismiss();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    //Launches the Barcode Scanner
    private void barcodeMode(){
        qrScan = new IntentIntegrator(getActivity());
        qrScan.setCaptureActivity(AnyOrientationActivity.class);
        qrScan.setOrientationLocked(false);
        qrScan.setPrompt("Scan a barcode");
        qrScan.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        qrScan.initiateScan();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(getActivity(), "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                identifyID(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void identifyID (String gottenId) {
        DataItem di = dB.getItemByID(gottenId);
        Log.i("Main", "Status" + (dB.getItemCount()));
        if (di != null) {
            launchInfoPage(di, true);
        } else {
            di = dB.getNewItembyID(gottenId);
            if (di != null) {
                launchInfoPage(di, false);
                return;
            }
            wouldLikeNewItem(gottenId);
            return;
        }
    }

    private void launchInfoPage(DataItem di, boolean isRealData){
        Intent i = new Intent(getActivity(), InformationActivity.class);
        i.putExtra(Contants.EXTRA_DATAITEM, di);
        i.putExtra(Contants.ISMASTER, preferences.getBoolean(Contants.ISMASTER, true));
        if (isRealData){
            i.putExtra(Contants.EXTRA_ISREALDATA, true);
        } else {
            i.putExtra(Contants.EXTRA_ISREALDATA, false);
        }
        startActivity(i);
    }

    private void wouldLikeNewItem(String gottenId){
        AlertDialog.Builder alertA = new AlertDialog.Builder(getActivity());
        final String id = gottenId;
        alertA.setTitle("Found: " + id);
        //should make icons to follow the different options.
        alertA
                .setMessage("This item doesn't exist in the database. Would you like to add this item?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        //Go Edit Data
                        dialog.dismiss();
                        addNewItemToLog(id);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Cancel the dialog
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = alertA.create();
        alertDialog.show();
    }

    private void addNewItemToLog(String gottenId){
        final String id = gottenId;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Enter Item Information");
        View subView = getActivity().getLayoutInflater().inflate(R.layout.fragment_edit_newitem, null);
        final EditText inID = (EditText) subView.findViewById(R.id.input_dialog_id);
        final EditText inDesc = (EditText) subView.findViewById(R.id.input_dialog_desc);
        final EditText inPrice = (EditText) subView.findViewById(R.id.input_dialog_price);
        builder.setView(subView);
        inID.setText(id);
        inDesc.requestFocus();

        builder
                .setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (TextUtils.isEmpty(inID.getText())) {
                            Toast.makeText(getActivity(), "ID must be entered!", Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.isEmpty(inDesc.getText())){
                            Toast.makeText(getActivity(), "Description must be entered!", Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.isEmpty(inPrice.getText())){
                            Toast.makeText(getActivity(), "Price must be entered!", Toast.LENGTH_SHORT).show();
                        } else {
                            DataItem di = new DataItem();
                            di.setID(inID.getText().toString());
                            String s = inDesc.getText().toString();
                            if (inDesc.getText().length() > 24) {
                                s = s.substring(0, 25);
                            }
                            di.setDesc(s);
                            di.setPrice(inPrice.getText().toString());
                            dB.addNewItem(di);
                            Toast.makeText(getActivity(), "Item has been added", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Toast.makeText(getActivity(), "Entry Canceled", Toast.LENGTH_SHORT).show();
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }
}
