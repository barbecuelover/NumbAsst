package com.ecs.numbasst.ui.scan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ecs.numbasst.R;

import java.util.List;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder> {

    private List<BleDeviceInfo> deviceList;
    private OnItemPressedListener clickedCallBack;

    public static final int STATUS_CONNECTED = 2;
    public static final int STATUS_CONNECTING = 1;



    public void setItemClickListener(OnItemPressedListener callBack ){
        this.clickedCallBack = callBack;
    }

    public DeviceListAdapter(List<BleDeviceInfo> deviceList) {
        this.deviceList = deviceList;
    }


    public void addDevice(BleDeviceInfo device) {
        if (!deviceList.contains(device)) {
            deviceList.add(device);
            notifyDataSetChanged();
        }
    }

    public BleDeviceInfo getDevice(int position) {
        return deviceList.get(position);
    }

    public void clear() {
        deviceList.clear();
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device_info,parent,false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BleDeviceInfo device = deviceList.get(position);
        holder.tvName.setText(device.getName());
        holder.tvMac.setText(device.getAddress());
        holder.itemContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickedCallBack!=null){
                    clickedCallBack.onItemViewClicked(position);
                }
            }
        });
        holder.itemContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (clickedCallBack!=null){
                    clickedCallBack.onItemViewLongClicked(position);
                }
                return false;
            }
        });
        if (device.getStatus() == STATUS_CONNECTED){
            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.tvStatus.setText("已连接");
        }else if (device.getStatus() == STATUS_CONNECTING){
            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.tvStatus.setText("正在连接...");
        }
        else {
            holder.tvStatus.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder extends  RecyclerView.ViewHolder{
        TextView tvName;
        TextView tvMac;
        TextView tvStatus;
        LinearLayout itemContainer;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.device_name);
            tvMac = itemView.findViewById(R.id.device_address);
            tvStatus = itemView.findViewById(R.id.device_status);
            itemContainer = itemView.findViewById(R.id.device_info_container);
        }
    }

    public interface OnItemPressedListener {
        void onItemViewClicked(int position);
        void onItemViewLongClicked(int position);
    }


}
