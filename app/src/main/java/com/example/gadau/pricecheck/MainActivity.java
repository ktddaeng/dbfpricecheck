package com.example.gadau.pricecheck;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gadau.pricecheck.data.Contants;
import com.example.gadau.pricecheck.data.DataItem;
import com.example.gadau.pricecheck.data.DatabaseHandler;
import com.example.gadau.pricecheck.data.MenuOption;
import com.example.gadau.pricecheck.logic.AnyOrientationActivity;
import com.example.gadau.pricecheck.logic.CategoryAdapter;
import com.example.gadau.pricecheck.logic.ItemClickListener;
import com.example.gadau.pricecheck.logic.MainAdapter;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private DatabaseHandler dB;
    private SharedPreferences preferences;
    private AlertDialog progressDialog;
    private AlertDialog.Builder progressBuild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = getSharedPreferences(Contants.SETTINGS, MODE_PRIVATE);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        CategoryAdapter adapter = new CategoryAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        dB = DatabaseHandler.getInstance(this);
        tabLayout.setupWithViewPager(viewPager);
        setUpToolbar();
    }

    private void setUpToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_start);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(R.string.app_name); toolbar.setSubtitle("");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
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

    public void identifyID (String gottenId) {
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
        Intent i = new Intent(this, InformationActivity.class);
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
        AlertDialog.Builder alertA = new AlertDialog.Builder(this);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Item Information");
        View subView = this.getLayoutInflater().inflate(R.layout.fragment_edit_newitem, null);
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
                            Toast.makeText(MainActivity.this, "ID must be entered!", Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.isEmpty(inDesc.getText())){
                            Toast.makeText(MainActivity.this, "Description must be entered!", Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.isEmpty(inPrice.getText())){
                            Toast.makeText(MainActivity.this, "Price must be entered!", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(MainActivity.this, "Item has been added", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "Entry Canceled", Toast.LENGTH_SHORT).show();
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void showMenu(View v){
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_options, popup.getMenu());

        popup.show();
        Log.i("Main", "Admin Mode " + preferences.getBoolean(Contants.ISMASTER, true));
        popup.getMenu().getItem(1).setChecked(preferences.getBoolean(Contants.ISMASTER, true));
        popup.setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        final MenuItem it = item;
        switch(item.getItemId()) {
            case R.id.options_main_help:
                //TODO: Help page
                Toast.makeText(this, "Under Construction", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.options_main_purge:
                onPurge();
                return true;
            case R.id.options_sync_data:
                syncDatabase();
                return true;
            case R.id.options_main_master:
                if (!item.isChecked()){
                    //Set permission for admin mode
                    final AlertDialog.Builder alertA = new AlertDialog.Builder(this);
                    View subView = getLayoutInflater().inflate(R.layout.fragment_pass, null);
                    alertA.setView(subView);
                    final EditText inPass = (EditText) subView.findViewById(R.id.input_pass);
                    inPass.requestFocus();
                    alertA.setTitle("Enter PIN")
                            .setCancelable(true)
                            .setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (inPass.getText().toString().equals(Contants.PINPASS)){
                                        toggleButton(it);
                                        Toast.makeText(MainActivity.this, "Set Admin Mode", Toast.LENGTH_SHORT).show();
                                    } else {
                                        dialog.dismiss();
                                        Toast.makeText(MainActivity.this, "Password Not Accepted", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Action Canceled", Toast.LENGTH_SHORT).show();
                                }
                            });
                    AlertDialog alertDialog = alertA.create();
                    alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                    alertDialog.show();
                } else {
                    //set regular mode
                    toggleButton(item);
                    Toast.makeText(this, "Set Regular Mode", Toast.LENGTH_SHORT).show();
                }
                return true;
        }
        return false;
    }

    private void toggleButton(MenuItem mi){
        mi.setChecked(!(mi.isChecked()));
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(Contants.ISMASTER, mi.isChecked());
        edit.commit();
    }

    private void syncDatabase(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle("Warning")
                .setMessage(R.string.dialog_syncwarn)
                .setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        canImportPlease();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "Sync Canceled", Toast.LENGTH_SHORT).show();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    private void importData() {
        new importHandler().execute();
    }

    private class importHandler extends AsyncTask<Void, Void, Void> {
        DatabaseHandler db = DatabaseHandler.getInstance(MainActivity.this);
        private String s;

        @Override
        protected void onPreExecute() {
            progressBuild = new AlertDialog.Builder(MainActivity.this);
            View dialogView = getLayoutInflater().inflate(R.layout.fragment_progress, null);
            progressBuild.setView(dialogView)
                    .setCancelable(false);
            progressDialog = progressBuild.create();
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            s = db.importDatabase(preferences.getBoolean(Contants.ISMASTER, true));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
        }
    }

    private void onPurge() {
        AlertDialog.Builder alertA = new AlertDialog.Builder(this);
        alertA.setTitle("Delete the Database?");
        //should make icons to follow the different options.
        alertA
                .setMessage("Are you sure you want to delete the database?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        //Go Edit Data
                        dialog.dismiss();
                        clearDatabase();
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

    private void clearDatabase(){
        dB.clearDatabase();
        Toast.makeText(this, "Database has been cleared!", Toast.LENGTH_SHORT);
    }

    public void canImportPlease(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Contants.MY_PERMISSIONS_REQUEST);
            }
        } else {
            importData();
        }
    }

}
