package com.example.gadau.pricecheck.logic;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CheckedTextView;

import com.example.gadau.pricecheck.R;
import com.example.gadau.pricecheck.data.DataItem;

import java.util.List;

public class SuggestedItemAdapter extends RecyclerView.Adapter<SuggestedItemAdapter.SuggestedItem> {
    private List<DataItem> mListOfData;
    private SparseBooleanArray mItemStateArray = new SparseBooleanArray();

    public SuggestedItemAdapter (List<DataItem> list) {
        mListOfData = list;
    }

    @Override
    public SuggestedItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_suggested, parent, false);
        return new SuggestedItem(v);
    }

    @Override
    public void onBindViewHolder(SuggestedItem holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mListOfData != null ? mListOfData.size() : 0;
    }

    public SparseBooleanArray getItemStateArray() {
        return mItemStateArray;
    }

    public class SuggestedItem extends RecyclerView.ViewHolder implements View.OnClickListener {
        public CheckBox iCheckedItem;

        public SuggestedItem(View itemView) {
            super(itemView);
            //itemView.setOnClickListener(this);
            iCheckedItem = (CheckBox) itemView.findViewById(R.id.suggested_checkbox);
            iCheckedItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            if (!mItemStateArray.get(adapterPosition, false)) {
                iCheckedItem.setChecked(true);
                mItemStateArray.put(adapterPosition, true);
            } else {
                iCheckedItem.setChecked(false);
                mItemStateArray.put(adapterPosition, false);
            }
        }

        void bind(int position) {
            if(!mItemStateArray.get(position, false)) {
                iCheckedItem.setChecked(false);
            } else {
                iCheckedItem.setChecked(true);
            }

            String s = mListOfData.get(position).getDesc();
            if (s.length() > 20) {
                s = s.substring(0, 21);
            }
            iCheckedItem.setText(s);
        }
    }
}
