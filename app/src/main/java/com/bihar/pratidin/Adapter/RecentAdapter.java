package com.bihar.pratidin.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bihar.pratidin.Activity.RecentActivity;
import com.bihar.pratidin.Common.Common;
import com.bihar.pratidin.Interface.ItemClickListener;
import com.bihar.pratidin.Model.Recent;
import com.bihar.pratidin.R;
import com.bihar.pratidin.Remote.IMyAPI;
import com.github.ybq.android.spinkit.SpinKitView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.RecentViewHolder> {

    Context context;
    List<Recent> recentList;
    IMyAPI mService;

    public RecentAdapter(Context context, List<Recent> recentList) {
        this.context = context;
        this.recentList = recentList;
    }

    @NonNull
    @Override
    public RecentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mService = Common.getAPI();

        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_recent,parent,false);

        return new RecentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.spinKitView.setVisibility(View.VISIBLE);

        if (recentList.get(position).image_big != null)   {
            Picasso.get()
                    .load(new StringBuilder(Common.IMAGE_URL)
                            .append(recentList.get(position).image_big).toString())
                    .error(R.drawable.logo)
                    .into(holder.imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.spinKitView.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            holder.spinKitView.setVisibility(View.GONE);
                        }
                    });
        }
        else {
            Picasso.get()
                    .load(recentList.get(position).image_url.trim())
                    .error(R.drawable.logo)
                    .into(holder.imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.spinKitView.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            holder.spinKitView.setVisibility(View.GONE);
                        }
                    });
        }


        holder.txtTitle.setText(Common.stripHtml(recentList.get(position).title));

        String timeAgo = Common.calculateTimeAgo(recentList.get(position).created_at);
        holder.txtTime.setText(timeAgo);


        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view) {
                updateViewCount(recentList.get(position));
                Intent intent = new Intent(context, RecentActivity.class);
                Common.CURRENT_RECENT = recentList.get(position);
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return recentList.size();
    }

    public class RecentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        TextView txtTitle, txtTime;
        SpinKitView spinKitView;

        ItemClickListener itemClickListener;

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        public RecentViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.layoutImage);
            txtTitle = itemView.findViewById(R.id.layoutTitle);
            spinKitView = itemView.findViewById(R.id.spin_kit);
            txtTime = itemView.findViewById(R.id.txtTime);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v);
        }
    }

    private void updateViewCount(Recent recent) {
        int prev = Integer.parseInt(recent.pageviews);
        int count = prev + 1;

        mService.updateViewCount(
                        String.valueOf(count),
                        recent.id)
                .enqueue(new retrofit2.Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
    }
}
