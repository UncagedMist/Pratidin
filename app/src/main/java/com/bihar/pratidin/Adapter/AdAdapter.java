package com.bihar.pratidin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bihar.pratidin.Interface.ItemClickListener;
import com.bihar.pratidin.Model.Ad;
import com.bihar.pratidin.R;
import com.github.ybq.android.spinkit.SpinKitView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdAdapter extends RecyclerView.Adapter<AdAdapter.AdViewHolder> {

    Context context;
    List<Ad> adList;

    public AdAdapter(Context context, List<Ad> adList) {
        this.context = context;
        this.adList = adList;
    }

    @NonNull
    @Override
    public AdViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_ad,parent,false);

        return new AdViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdViewHolder holder, int position) {

        Picasso.get()
                .load("google.com")
                .into(holder.imageAd, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.spinKitView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        holder.spinKitView.setVisibility(View.GONE);
                    }
                });

        holder.txtTitle.setText("कार बीमा पर 85% की छूट|");
        holder.txtCompany.setText("Acko Car Insurance");
        holder.txtType.setText("Sponsored");

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return adList.size();
    }

    public class AdViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageAd;
        TextView txtTitle, txtCompany, txtType;
        SpinKitView spinKitView;

        ItemClickListener itemClickListener;

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        public AdViewHolder(@NonNull View itemView) {
            super(itemView);

            imageAd = itemView.findViewById(R.id.imgAd);
            txtTitle = itemView.findViewById(R.id.titleAd);
            txtCompany = itemView.findViewById(R.id.txtCompany);
            txtType = itemView.findViewById(R.id.txtType);
            spinKitView = itemView.findViewById(R.id.spin_kit);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v);
        }
    }
}
