package com.bihar.pratidin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bihar.pratidin.Model.Author;
import com.bihar.pratidin.R;

import java.util.List;

public class AuthorAdapter extends RecyclerView.Adapter<AuthorAdapter.AuthorViewHolder> {

    Context context;
    List<Author> authorList;

    public AuthorAdapter(Context context, List<Author> authorList) {
        this.context = context;
        this.authorList = authorList;
    }

    @NonNull
    @Override
    public AuthorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_author,parent,false);

        return new AuthorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AuthorViewHolder holder, int position) {
        holder.txtAuthor.setText(authorList.get(position).username);
    }

    @Override
    public int getItemCount() {
        return authorList.size();
    }

    public class AuthorViewHolder extends RecyclerView.ViewHolder {

        TextView txtAuthor;

        public AuthorViewHolder(@NonNull View itemView) {
            super(itemView);

            txtAuthor = itemView.findViewById(R.id.txtAuthor);
        }
    }
}
