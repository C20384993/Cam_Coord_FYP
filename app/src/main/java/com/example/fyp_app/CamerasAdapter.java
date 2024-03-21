package com.example.fyp_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import models.CameraRecyclerItem;

//Used in the CameraListActivity for the Recycler View.
public class CamerasAdapter extends RecyclerView.Adapter<CamerasAdapter.ViewHolder> {

    private List<CameraRecyclerItem> cameraList;
    private ItemClickListener mItemListener;

    public CamerasAdapter(List<CameraRecyclerItem> cameraList, ItemClickListener itemClickListener) {
        this.cameraList = cameraList;
        this.mItemListener = itemClickListener;
    }//end Constructor

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_camera, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.cameraName.setText(cameraList.get(position).getCustomname());

        holder.itemView.setOnClickListener(view -> {
            mItemListener.onItemClick(cameraList.get(position)); //Returns item position.
        });
    }

    @Override
    public int getItemCount() {
        return cameraList.size();
    }

    public interface ItemClickListener{
        void onItemClick(CameraRecyclerItem cameraRecyclerItem);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView cameraName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cameraName = itemView.findViewById(R.id.cameraName);
        }
    }//end ViewHolder
}//end class