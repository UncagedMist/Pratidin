package com.bihar.pratidin.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bihar.pratidin.Common.Common;
import com.bihar.pratidin.Interface.CategoryClickListener;
import com.bihar.pratidin.Model.Category;
import com.bihar.pratidin.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CatViewHolder> {

    Context context;
    List<Category> categoryList;
    CategoryClickListener clickListener;

    public CategoryAdapter(Context context, List<Category> categoryList, CategoryClickListener clickListener) {
        this.context = context;
        this.categoryList = categoryList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public CatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_cat,parent,false);

        return new CatViewHolder(view);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull CatViewHolder holder, int position) {
        holder.txtCat.setText(Common.stripHtml(categoryList.get(position).name));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onCategoryClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }


    public class CatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtCat;

        public CatViewHolder(@NonNull View itemView) {
            super(itemView);

            txtCat = itemView.findViewById(R.id.catTitle);

            txtCat.setSelected(true);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            clickListener.onCategoryClick(getAdapterPosition());
        }
    }

}
