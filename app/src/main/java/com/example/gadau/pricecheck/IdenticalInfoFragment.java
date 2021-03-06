package com.example.gadau.pricecheck;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
import com.example.gadau.pricecheck.data.DatabaseContract;
import com.example.gadau.pricecheck.data.DatabaseHandler;
import com.example.gadau.pricecheck.logic.AnyOrientationActivity;
import com.example.gadau.pricecheck.logic.IdenticalItemAdapter;
import com.example.gadau.pricecheck.logic.ItemClickListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

public class IdenticalInfoFragment extends Fragment implements ItemClickListener {
    private IdenticalItemAdapter mAdapter;
    private View mRootView;
    private View mEmptyView;
    private RecyclerView mRecycleView;
    private DatabaseHandler mDb;
    private DataItem mDi;
    private List<DataItem> mListOfData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_info_identical, container, false);
        mDb = DatabaseHandler.getInstance(getActivity());
        mDi = getActivity().getIntent().getParcelableExtra(Contants.EXTRA_DATAITEM);
        mEmptyView = mRootView.findViewById(R.id.empty_view);

        setUpRecycler();
        return mRootView;
    }

    private void setUpRecycler(){
        String barcode = mDi.getID();
        mRecycleView = (RecyclerView) mRootView.findViewById(R.id.identical_log);

        if (mDb.getIdenticalTagById(barcode) < 1) {
            mRecycleView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            mRecycleView.setLayoutManager(layoutManager);
            mListOfData = mDb.getListIdentical(mDi.getID());
            mAdapter = new IdenticalItemAdapter(mListOfData);
            mAdapter.setClickListener(this);
            mRecycleView.setAdapter(mAdapter);

            mRecycleView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }
    }

    private void updateRecycler() {

    }

    @Override
    public void onClick(View view, int position) {
        final DataItem di = mListOfData.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Remove " + di.getID() + " from identical list?");
        builder
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mDb.deleteIdenticalItem(di.getID());
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void refreshPage() {

        if (mDb.getIdenticalTagById(mDi.getID()) < 1) {
            mRecycleView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            List<DataItem> listOfData = mDb.getListIdentical(mDi.getID());

            if (listOfData.size() < 1) {
                mRecycleView.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);

                // Deletes the item from the table to save some space
                mDb.deleteIdenticalItem(mDi.getID());

            } else {
                mAdapter.updateData(listOfData);
                mAdapter.setClickListener(this);
                mRecycleView.setAdapter(mAdapter);

                mRecycleView.setVisibility(View.VISIBLE);
                mEmptyView.setVisibility(View.GONE);
            }

        }
    }
}