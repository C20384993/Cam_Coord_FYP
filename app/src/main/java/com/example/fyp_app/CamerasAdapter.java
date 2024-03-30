package com.example.fyp_app;

import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import models.CameraRecyclerItem;

//Used in the CameraListActivity for the Recycler View.
public class CamerasAdapter extends RecyclerView.Adapter<CamerasAdapter.ViewHolder> {

    private List<CameraRecyclerItem> cameraList;
    private ItemClickListener mItemListener;
    private String cameraIp;

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

        //Get IP portion of RTSP URL.
        cameraIp = cameraList.get(position).getRtspurl();
        cameraIp = cameraIp.substring(cameraIp.indexOf("@") + 1);
        cameraIp = cameraIp.substring(0, cameraIp.indexOf(":"));

        // Check RTSP URL availability
        new CheckRTSPTask(holder, cameraIp).execute();

        new CheckHLSTask(holder, cameraList.get(position).getStreampath()).execute();

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
        TextView localStatusTitle;
        TextView remoteStatusTitle;
        TextView localStatus;
        TextView remoteStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cameraName = itemView.findViewById(R.id.cameraName);
            localStatusTitle = itemView.findViewById(R.id.textView_localStatusTitle);
            remoteStatusTitle = itemView.findViewById(R.id.textView_remoteStatusTitle);
            localStatus = itemView.findViewById(R.id.textView_localStatus);
            remoteStatus = itemView.findViewById(R.id.textView_remoteStatus);
        }
    }//end ViewHolder

    //Check if the RTSP stream of the camera can be connected to.
    private static class CheckRTSPTask extends AsyncTask<Void, Void, Boolean> {
        private ViewHolder holder;
        private String rtspIp;

        public CheckRTSPTask(ViewHolder holder, String ip) {
            this.holder = holder;
            this.rtspIp = ip;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean available = false; //True = Camera can be connected to.
            int port = 554;

            try {
                //Create a socket object, attempt to make a connection through it.
                SocketAddress rtspSocketAddress = new InetSocketAddress(rtspIp, port);
                Socket rtspSocket = new Socket();
                int timeoutMs = 2000;   // 2 seconds
                rtspSocket.connect(rtspSocketAddress, timeoutMs);
                available = true;
                rtspSocket.close(); // Close the socket after successful connection
            } catch (IOException e) {
            }
            return available;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                holder.localStatus.setText("Available");
            } else {
                holder.localStatus.setText("Not Available");
            }
        }
    }//end CheckURLTask


    //Check if the HLS stream can be connected to/is available.
    private static class CheckHLSTask extends AsyncTask<Void, Void, Boolean> {
        private ViewHolder holder;
        private String hlsUrl;

        public CheckHLSTask(ViewHolder holder, String url) {
            this.holder = holder;
            this.hlsUrl = url;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean exists = false;
            HttpsURLConnection remoteStreamConnection = null;

            try {
                HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        // Retrieve the actual hostname from the SSL session
                        String actualHostname = session.getPeerHost();

                        // Perform hostname verification by comparing actual hostname with expected hostname
                        return hostname.equalsIgnoreCase(actualHostname);
                    }
                });

                //Create a connection to the HLS streaming URL to test if it is up.
                URL remoteUrl = new URL(hlsUrl);
                remoteStreamConnection = (HttpsURLConnection) remoteUrl.openConnection();
                remoteStreamConnection.setConnectTimeout(2000);
                remoteStreamConnection.connect();
                exists = true;
            } catch (IOException e) {

            } finally {
                if (remoteStreamConnection != null) {
                    remoteStreamConnection.disconnect();
                }
            }
            return exists;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                holder.remoteStatus.setText("Available");
            } else {
                holder.remoteStatus.setText("Not Available");
            }
        }
    }

}//end class
