package com.example.gadau.pricecheck.logic;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gadau.pricecheck.IdenticalInfoFragment;
import com.example.gadau.pricecheck.R;
import com.example.gadau.pricecheck.data.DataItem;

import org.w3c.dom.Text;

import java.util.List;

public class IdenticalItemAdapter extends RecyclerView.Adapter<IdenticalItemAdapter.IdenticalListItem> {
    private List<DataItem> mListOfData;
    private Context mContext;
    private ItemClickListener mClickListener;

    public IdenticalItemAdapter(List<DataItem> list) {
        mListOfData = list;
    }

    @Override
    public IdenticalListItem onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View v = LayoutInflater.from(mContext).inflate(R.layout.list_item_identical, parent, false);
        return new IdenticalListItem(v);
    }

    @Override
    public int getItemCount() {
        return mListOfData != null ? mListOfData.size() : 0;
    }

    @Override
    public void onBindViewHolder(IdenticalListItem holder, int position) {
        DataItem dataItem = mListOfData.get(position);
        holder.iID.setText(dataItem.getID());
        String s = mListOfData.get(position).getDesc();
        if (s.length() > 20) {
            s = s.substring(0, 21);
        }
        holder.iDesc.setText(s);
        holder.iBackQty.setText(dataItem.getBQty());
        holder.iShowQty.setText(dataItem.getSQty());
    }

    public void updateData(List<DataItem>  list) {
        mListOfData.clear();
        mListOfData.addAll(list);
        notifyDataSetChanged();
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        if (mClickListener == null) {
            mClickListener = itemClickListener;
        }
    }

    public class IdenticalListItem extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView iID;
        public TextView iDesc;
        public TextView iShowQty;
        public TextView iBackQty;

        public IdenticalListItem (View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            iID = (TextView) itemView.findViewById(R.id.identical_id_textview);
            iDesc = (TextView) itemView.findViewById(R.id.identical_desc_textview);
            iShowQty = (TextView) itemView.findViewById(R.id.identical_showqty_textview);
            iBackQty = (TextView) itemView.findViewById(R.id.identical_backqty_textview);
        }

        @Override
        public void onClick(View view) {
            mClickListener.onClick(view, getAdapterPosition());
        }
    }
}
