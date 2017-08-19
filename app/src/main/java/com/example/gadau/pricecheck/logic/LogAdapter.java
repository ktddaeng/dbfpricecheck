package com.example.gadau.pricecheck.logic;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gadau.pricecheck.R;
import com.example.gadau.pricecheck.data.LogItem;

import java.util.List;

/**
 * Created by gadau on 8/18/2017.
 */

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogListItem> {
    private List<LogItem> listOfData;

    public LogAdapter(List<LogItem> list) {
        listOfData = list;
    }

    @Override
    public LogListItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_log, parent, false);
        return new LogListItem(v);
    }

    @Override
    public void onBindViewHolder(LogListItem holder, int position) {
        holder.iVendor.setText(listOfData.get(position).getVendor());
        holder.iReceive.setText(listOfData.get(position).getReceive());
        holder.iDate.setText(listOfData.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return listOfData != null ? listOfData.size() : 0;
    }

    public class LogListItem extends RecyclerView.ViewHolder {
        public TextView iVendor;
        public TextView iReceive;
        public TextView iDate;

        public LogListItem(View itemView) {
            super(itemView);
            iVendor = (TextView) itemView.findViewById(R.id.list_vendor);
            iReceive = (TextView) itemView.findViewById(R.id.list_receive);
            iDate = (TextView) itemView.findViewById(R.id.list_date);
        }
    }
}
