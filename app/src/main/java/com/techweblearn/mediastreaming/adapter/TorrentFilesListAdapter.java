package com.techweblearn.mediastreaming.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.techweblearn.mediastreaming.R;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TorrentFilesListAdapter extends RecyclerView.Adapter<TorrentFilesListAdapter.ViewHolder> {


    private Context context;
    ArrayList<File>fileArrayList;

    public TorrentFilesListAdapter(Context context, ArrayList<File> fileArrayList) {
        this.context = context;
        this.fileArrayList = fileArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.torrent_file_list_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.filenametextview.setText(fileArrayList.get(position).getName());
        holder.filepathtextview.setText(fileArrayList.get(position).getAbsolutePath());
    }

    @Override
    public int getItemCount() {
        return fileArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.file_name_textview)TextView filenametextview;
        @BindView(R.id.file_path_textview)TextView filepathtextview;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            itemClickCallback.onClick(fileArrayList.get(getAdapterPosition()));

        }
    }


    private ItemClickCallback itemClickCallback;
    public void setItemClickCallback(ItemClickCallback itemClickCallback)
    {
        this.itemClickCallback=itemClickCallback;
    }
    public interface ItemClickCallback
    {
        void onClick(File file);
    }


}
