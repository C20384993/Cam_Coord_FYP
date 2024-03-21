package com.example.fyp_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import models.RecordingRecyclerItem;

//Used in the RecordingListActivity for the Recycler View.
public class RecordingsAdapter extends RecyclerView.Adapter<RecordingsAdapter.ViewHolder> {

    private List<RecordingRecyclerItem> recordingList;
    private ItemClickListener mItemListener;

    public RecordingsAdapter(List<RecordingRecyclerItem> recordingList, ItemClickListener itemClickListener) {
        this.recordingList = recordingList;
        this.mItemListener = itemClickListener;
    }//end RecordingsAdapter

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_recording, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordingsAdapter.ViewHolder holder, int position) {
        holder.recordingName.setText(recordingList.get(position).getCustomname());

        holder.itemView.setOnClickListener(view -> {
            mItemListener.onItemClick(recordingList.get(position)); //Returns item position.
        });
    }

    @Override
    public int getItemCount() { return recordingList.size(); }

    public interface ItemClickListener{
        void onItemClick(RecordingRecyclerItem recordingRecyclerItem);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView recordingName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            recordingName = itemView.findViewById(R.id.recordingName);
        }//end ViewHolder
    }//end class
}//end RecordingsAdapter
