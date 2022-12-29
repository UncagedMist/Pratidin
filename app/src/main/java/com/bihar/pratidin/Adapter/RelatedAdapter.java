package com.bihar.pratidin.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bihar.pratidin.Activity.RelatedActivity;
import com.bihar.pratidin.Common.Common;
import com.bihar.pratidin.Interface.ItemClickListener;
import com.bihar.pratidin.Model.Related;
import com.bihar.pratidin.R;
import com.github.ybq.android.spinkit.SpinKitView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RelatedAdapter extends RecyclerView.Adapter<RelatedAdapter.RelatedViewHolder> {

    Context context;
    List<Related> relatedList;

    public RelatedAdapter(Context context, List<Related> relatedList) {
        this.context = context;
        this.relatedList = relatedList;
    }

    @NonNull
    @Override
    public RelatedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_related,parent,false);

        return new RelatedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RelatedViewHolder holder, @SuppressLint("RecyclerView") int position) {

        if (relatedList.get(position).image_big != null)   {
            Picasso.get()
                    .load(new StringBuilder(Common.IMAGE_URL)
                            .append(relatedList.get(position).image_big).toString())
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
                    .load(relatedList.get(position).image_url.trim())
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

        holder.txtTitle.setText(Common.stripHtml(relatedList.get(position).title));

        String timeAgo = Common.calculateTimeAgo(relatedList.get(position).created_at);
        holder.txtTime.setText(timeAgo);

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, RelatedActivity.class);
                Common.CURRENT_RELATED = relatedList.get(position);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return relatedList.size();
    }

    public class RelatedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        TextView txtTitle, txtTime;
        SpinKitView spinKitView;

        ItemClickListener itemClickListener;

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        public RelatedViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.layout_image);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtTime = itemView.findViewById(R.id.txtTime);
            spinKitView = itemView.findViewById(R.id.spin_kit);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v);
        }
    }
}
