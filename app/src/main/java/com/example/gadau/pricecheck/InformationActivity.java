package com.example.gadau.pricecheck;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gadau.pricecheck.data.Contants;
import com.example.gadau.pricecheck.data.DataItem;
import com.example.gadau.pricecheck.data.DatabaseContract;
import com.example.gadau.pricecheck.data.DatabaseHandler;
import com.example.gadau.pricecheck.logic.AnyOrientationActivity;
import com.example.gadau.pricecheck.logic.InfoAdapter;
import com.example.gadau.pricecheck.logic.SuggestedItemAdapter;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.List;

public class InformationActivity extends AppCompatActivity {//extends SwipeDismissBaseActivity {
    private IntentIntegrator mQrScan;
    private DataItem mDi;
    private DatabaseHandler mDb;
    private SuggestedItemAdapter mSuggestedAdapter;
    private InfoAdapter mInfoAdapter;
    private SwipeRefreshLayout mSwiperResfresh;
    //private LottieAnimationView animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        mDb = new DatabaseHandler(this);
//        Toast.makeText(this, "Swipe Right to Close", Toast.LENGTH_SHORT).show();

        TextView infoID = (TextView) findViewById(R.id.info_id);
        TextView infoDesc = (TextView) findViewById(R.id.info_desc);
        TextView infoDollars = (TextView) findViewById(R.id.info_dollars);
        TextView infoCents = (TextView) findViewById(R.id.info_cents);
        ImageView cancelButton = (ImageView) findViewById(R.id.button_cancel);
        TextView infoSQty = (TextView) findViewById(R.id.output_inv_qty_s);
        TextView infoBQty = (TextView) findViewById(R.id.output_inv_qty_b);
        TextView infoSDate = (TextView) findViewById(R.id.output_inv_date_s);
        TextView infoBDate = (TextView) findViewById(R.id.output_inv_date_b);
        View root = findViewById(R.id.info_main);

        if (getIntent().getExtras().getBoolean(Contants.EXTRA_ISREALDATA)) {
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

        mDi = getIntent().getParcelableExtra(Contants.EXTRA_DATAITEM);

        String price = mDi.getPrice();
        int index = price.indexOf(".");

        infoID.setText(mDi.getID());
        infoDesc.setText(mDi.getDesc());
        //TODO: The line below truncates the price string where the decimal point is. Account for situation where we don't have a decimal point. meaning index < 0
        if (index < 0) {
            price += ".00";
            index = price.indexOf(".");
        }
        infoDollars.setText(price.substring(0, index));
        infoCents.setText(price.substring(index + 1));
        infoSQty.setText(mDi.getSQty());
        infoBQty.setText(mDi.getBQty());
        infoSDate.setText(mDi.getSDateString());
        infoBDate.setText(mDi.getBDateString());

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        mInfoAdapter = new InfoAdapter(this, getSupportFragmentManager());
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager.setAdapter(mInfoAdapter);
        tabLayout.setupWithViewPager(viewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.identical_add_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertA = new AlertDialog.Builder(InformationActivity.this);
                alertA.setTitle("Adding New Identical Item");
                alertA.setMessage("Would you like to add a new identical item?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                addIdenticalInfoDialog();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                AlertDialog alertDialog = alertA.create();
                alertDialog.show();
            }
        });

        setupSwiper();
    }

    private void setupSwiper() {
        mSwiperResfresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwiperResfresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshPage();
            }
        });
    }

    private void refreshPage() {
        mInfoAdapter.updateData();
        mSwiperResfresh.setRefreshing(false);
    }

    private void addIdenticalInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View subView = getLayoutInflater().inflate(R.layout.fragment_edit_id, null);
        final EditText inId = (EditText) subView.findViewById(R.id.input_dialog_IDNo);
        builder.setView(subView);
        inId.requestFocus();

        builder
                .setCancelable(true)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String inIdString = inId.getText().toString().trim();
                        identifyID(inIdString);
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setNeutralButton("Scan ID", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        barcodeMode();
                        dialogInterface.dismiss();
                    }
                });

        final AlertDialog dialog = builder.create();
        if (getResources().getConfiguration().hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
            inId.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (inId.getText().length() >= 12) {
                        identifyID(inId.getText().toString());
                        dialog.dismiss();
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    //Launches the Barcode Scanner
    private void barcodeMode() {
        mQrScan = new IntentIntegrator(this);
        mQrScan.setCaptureActivity(AnyOrientationActivity.class);
        mQrScan.setOrientationLocked(false);
        mQrScan.setPrompt("Scan a barcode");
        mQrScan.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        mQrScan.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                identifyID(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void identifyID(final String gottenId) {
        if (gottenId.equals(mDi.getID())) {
            Toast.makeText(this, "Cannot be the same exact item.", Toast.LENGTH_SHORT).show();
            return;
        }

        DataItem di = mDb.getItemByID(gottenId);
        Log.i("Main", "Status" + (mDb.getItemCount()));
        if (di != null) {
            String itemDesc = di.getDesc();
            final String id = gottenId;
            AlertDialog.Builder alertA = new AlertDialog.Builder(this);
            alertA.setTitle("Adding " + id);
            alertA
                    .setMessage("Add " + itemDesc + " as an identical item?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            int rows = addIdenticalItem(gottenId);
//                            Toast.makeText(InformationActivity.this, "Changed " + rows + "rows", Toast.LENGTH_SHORT).show();
                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
            AlertDialog alertDialog = alertA.create();
            alertDialog.show();
        } else {
            wouldLikeNewItem(gottenId);
        }
    }

    private int addIdenticalItem(String gottenId) {
        int tag = mDb.getIdenticalTagById(mDi.getID());
        int rowsChanged = 0;

        if (tag < 1) {
            tag = DatabaseContract.TaggedItemEntry.tagCount++;
            rowsChanged += mDb.addToIdenticalTag(mDi.getID(), tag);
        }

        if (mDb.getIdenticalTagById(gottenId) < 1) {
            rowsChanged += mDb.addToIdenticalTag(gottenId, tag);
        } else {
            addMultipleDialog(gottenId, tag);
        }

        return rowsChanged;
    }

    /**
     * If the item scanned/entered exits on the table then provide a checkbox
     * list of what to add and an option to add all
     *
     * @param gottenId
     * @return number of rows changed
     */
    private void addMultipleDialog(final String gottenId, int tag) {
        AlertDialog.Builder builder = new AlertDialog.Builder(InformationActivity.this);
        builder.setTitle("Suggested Identicals");
        View subView = getLayoutInflater().inflate(R.layout.fragment_suggested_items, null);
        final List<DataItem> listOfData = mDb.getListIdentical(gottenId);
        setUpIdenticalSuggestedRecycler(subView, listOfData);
        builder.setView(subView);

        final int thisTag = tag;
        builder
                .setCancelable(true)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SparseBooleanArray checkedItems = mSuggestedAdapter.getItemStateArray();
                        int rowsChanged = mDb.addToIdenticalTag(gottenId, thisTag);
                        rowsChanged += addMultipleIdenticals(listOfData, checkedItems, thisTag);
                        Toast.makeText(InformationActivity.this, "Changed " + checkedItems.size() + "rows", Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setNeutralButton("Add All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int rowsChanged = mDb.addToIdenticalTag(gottenId, thisTag);
                        rowsChanged += mDb.addTags(listOfData, thisTag);
                        Toast.makeText(InformationActivity.this, "Changed " + rowsChanged + "rows", Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    private int addMultipleIdenticals(List<DataItem> list, SparseBooleanArray checkedItems, int tag) {
        int rowsChanged = 0;;

        for (int i = 0; i < checkedItems.size(); i++) {
            if (checkedItems.get(i)) {
                mDb.addToIdenticalTag(list.get(i).getID(), tag);
            }
        }
        return rowsChanged;
    }

    private void setUpIdenticalSuggestedRecycler(View dialogView, List<DataItem> list) {
        RecyclerView recycleView = (RecyclerView) dialogView.findViewById(R.id.suggested_log);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycleView.setLayoutManager(layoutManager);
        mSuggestedAdapter = new SuggestedItemAdapter(list);
        recycleView.setAdapter(mSuggestedAdapter);
//
//        recycleView.setVisibility(View.VISIBLE);
    }

    private void wouldLikeNewItem(String gottenId) {
        AlertDialog.Builder alertA = new AlertDialog.Builder(this);
        final String id = gottenId;
        alertA.setTitle("Found: " + id);
        //should make icons to follow the different options.
        alertA
                .setMessage("This item doesn't exist in the database. Would you like to add this item?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
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

    private void addNewItemToLog(String gottenId) {
        final String id = gottenId;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Item Information");
        View subView = getLayoutInflater().inflate(R.layout.fragment_edit_newitem, null);
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
                            Toast.makeText(InformationActivity.this, "ID must be entered!", Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.isEmpty(inDesc.getText())) {
                            Toast.makeText(InformationActivity.this, "Description must be entered!", Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.isEmpty(inPrice.getText())) {
                            Toast.makeText(InformationActivity.this, "Price must be entered!", Toast.LENGTH_SHORT).show();
                        } else {
                            DataItem di = new DataItem();
                            di.setID(inID.getText().toString());
                            String s = inDesc.getText().toString();
                            if (inDesc.getText().length() > 24) {
                                s = s.substring(0, 25);
                            }
                            di.setDesc(s);
                            di.setPrice(inPrice.getText().toString());
                            mDb.addNewItem(di);
                            Toast.makeText(InformationActivity.this, "Item has been added", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Toast.makeText(InformationActivity.this, "Entry Canceled", Toast.LENGTH_SHORT).show();
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }
}
