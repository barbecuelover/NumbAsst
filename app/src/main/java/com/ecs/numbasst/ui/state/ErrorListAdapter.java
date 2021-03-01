package com.ecs.numbasst.ui.state;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ecs.numbasst.R;

import java.util.List;

public class ErrorListAdapter extends RecyclerView.Adapter<ErrorListAdapter.ViewHolder> {

    private List<String> errorList;

    public ErrorListAdapter(List<String> errorList) {
        this.errorList = errorList;
    }

    public void clear() {
        errorList.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<String> list){
        errorList.clear();
        errorList.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_error_info,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String errorStr = errorList.get(position);
        holder.tvNumber.setText(String.valueOf(position+1));
        holder.tvErrorStr.setText(errorStr);
    }

    @Override
    public int getItemCount() {
        return errorList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder extends  RecyclerView.ViewHolder{
        TextView tvNumber;
        TextView tvErrorStr;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.tv_error_info_number);
            tvErrorStr = itemView.findViewById(R.id.tv_error_info_detail);
        }
    }

}
