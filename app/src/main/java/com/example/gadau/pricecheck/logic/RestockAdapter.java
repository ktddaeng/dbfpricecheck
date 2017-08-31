package com.example.gadau.pricecheck.logic;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gadau.pricecheck.R;
import com.example.gadau.pricecheck.data.DataItem;
import com.example.gadau.pricecheck.data.LogItem;

import java.util.List;

/**
 * Created by gadau on 8/24/2017.
 */

public class RestockAdapter extends RecyclerView.Adapter<RestockAdapter.ResListItem> {
    private List<DataItem> listOfData;
    private ItemClickListener clickListener;

    public RestockAdapter(List<DataItem> list) {
        listOfData = list;
    }

    @Override
    public ResListItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_restock_log, parent, false);
        return new ResListItem(v);
    }

    @Override
    public void onBindViewHolder(ResListItem holder, int position) {
        holder.iID.setText(listOfData.get(position).getID());
        String s = listOfData.get(position).getDesc();
        if (s.length() > 10){
            s = s.substring(0,11);
        }
        holder.iDesc.setText(s);
    }

    public void setClickListener(ItemClickListener clickListener){
        this.clickListener = clickListener;
    }

    public void removeItem(int position) {
        listOfData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, listOfData.size());
    }

    @Override
    public int getItemCount() {
        return listOfData != null ? listOfData.size() : 0;
    }

    public DataItem getDataItem(int position){
        return listOfData.get(position);
    }

    public void updateData(List<DataItem> list){
        listOfData.clear();
        listOfData.addAll(list);
        notifyDataSetChanged();
    }

    public class ResListItem extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView iID;
        public TextView iDesc;

        public ResListItem(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            iID = (TextView) itemView.findViewById(R.id.restock_id);
            iDesc = (TextView) itemView.findViewById(R.id.restock_desc);
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(v, getAdapterPosition());
        }
    }
}
