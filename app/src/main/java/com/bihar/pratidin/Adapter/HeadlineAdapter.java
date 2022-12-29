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

import com.bihar.pratidin.Activity.NewsDetailActivity;
import com.bihar.pratidin.Common.Common;
import com.bihar.pratidin.Interface.ItemClickListener;
import com.bihar.pratidin.Model.Headline;
import com.bihar.pratidin.R;
import com.bihar.pratidin.Remote.IMyAPI;
import com.github.ybq.android.spinkit.SpinKitView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class HeadlineAdapter extends RecyclerView.Adapter<HeadlineAdapter.HeadlineViewHolder> {

    Context context;
    List<Headline> headlineList;
    IMyAPI mService;

    public HeadlineAdapter(Context context, List<Headline> headlineList) {
        this.context = context;
        this.headlineList = headlineList;
    }

    @NonNull
    @Override
    public HeadlineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        mService = Common.getAPI();

        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_headline,parent,false);
        return new HeadlineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HeadlineViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.spinKitView.setVisibility(View.VISIBLE);

        if (headlineList.get(position).getImage_big() != null)   {
            Picasso.get()
                    .load(new StringBuilder(Common.IMAGE_URL)
                            .append(headlineList.get(position).getImage_big()).toString())
                    .error(R.drawable.logo)
                    .into(holder.imgHeadline, new Callback() {
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
                    .load(headlineList.get(position).getImage_url().trim())
                    .error(R.drawable.logo)
                    .into(holder.imgHeadline, new Callback() {
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

        holder.txtTitle.setText(Common.stripHtml(headlineList.get(position).title));

        String timeAgo = Common.calculateTimeAgo(headlineList.get(position).created_at);
        holder.txtTime.setText(timeAgo);

        holder.txtView.setText(headlineList.get(position).pageviews);

        holder.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String slug = "";
                if(headlineList.get(position).title_slug.length() > 20)
                    slug = headlineList.get(position).title_slug.substring(0,20)+"...";
                else
                    slug = headlineList.get(position).title_slug;

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String body = headlineList.get(position).title + "\n\n"+Common.IMAGE_URL+headlineList.get(position).title_slug +"\n\nShared from "+context.getString(R.string.app_name)+" \n\n" +
                        "Download "+context.getString(R.string.app_name)+" to get Latest News and Updates : https://play.google.com/store/apps/details?id="+context.getPackageName();
                intent.putExtra(Intent.EXTRA_TEXT,body);
                context.startActivity(Intent.createChooser(intent,"Share with : "));
            }
        });

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view) {
                updateViewCount(headlineList.get(position));
                Intent intent = new Intent(context, NewsDetailActivity.class);
                Common.CURRENT_HEADLINE = headlineList.get(position);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return headlineList.size();
    }

    public class HeadlineViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtTitle;
        ImageView imgHeadline;
        TextView txtTime,txtView;
        TextView btnShare;
        SpinKitView spinKitView;

        ItemClickListener itemClickListener;

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        public HeadlineViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.txtTitle);
            imgHeadline = itemView.findViewById(R.id.imgHeadline);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtView = itemView.findViewById(R.id.txtView);
            btnShare = itemView.findViewById(R.id.btnShare);
            spinKitView = itemView.findViewById(R.id.spin_kit);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v);
        }
    }

    private void updateViewCount(Headline headline) {

        int prev = Integer.parseInt(headline.pageviews);
        int count = prev + 1;

        mService.updateViewCount(
                String.valueOf(count),
                headline.id)
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
