package com.example.gadau.pricecheck.logic;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gadau.pricecheck.R;
import com.example.gadau.pricecheck.data.DataItem;

import java.util.List;

/**
 * Created by gadau on 9/18/2017.
 */

public class NewItemAdapter extends RecyclerView.Adapter<NewItemAdapter.NewListItem> {
    private List<DataItem> listOfData;
    private ItemClickListener clickListener;

    public NewItemAdapter(List<DataItem> list) {
        listOfData = list;
    }

    @Override
    public NewListItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_newitem_log, parent, false);
        return new NewListItem(v);
    }

    @Override
    public void onBindViewHolder(NewListItem holder, int position) {
        holder.iID.setText(listOfData.get(position).getID());
        String s = listOfData.get(position).getDesc();
        if (s.length() > 20){
        s = s.substring(0,21);
        }
        holder.iDesc.setText(s);
        holder.iPrice.setText(listOfData.get(position).getPrice());
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

    public class NewListItem extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView iID;
        public TextView iDesc;
        public TextView iPrice;

        public NewListItem (View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            iID = (TextView) itemView.findViewById(R.id.newitem_id);
            iDesc = (TextView) itemView.findViewById(R.id.newitem_desc);
            iPrice = (TextView) itemView.findViewById(R.id.newitem_price);
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(v, getAdapterPosition());
        }
    }
}
