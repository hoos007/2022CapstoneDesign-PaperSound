package org.techtown.paper_sound_original;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> implements  OnBookItemClickListener{
    ArrayList<Book> items = new ArrayList<Book>();
    OnBookItemClickListener listener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.book_item, viewGroup, false);

        return new ViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Book item = items.get(position);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onItemClick(ViewHolder holder, View view, int position) {
        if(listener != null)
        {
            listener.onItemClick(holder, view, position);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView writer;
        ImageView image;

        public ViewHolder(View itemView, final OnBookItemClickListener listener) {
            super(itemView);

            title = itemView.findViewById(R.id.textView);
            writer = itemView.findViewById(R.id.textView2);
            image = itemView.findViewById(R.id.imageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    if (listener != null) {
                        listener.onItemClick(ViewHolder.this, view, position);
                    }
                }
            });
        }

        public void setItem(Book item) {
            title.setText(item.getTitle());
            writer.setText(item.getWriter());
            Glide.with(itemView).load("http://hoos007.cafe24.com/FileDown/image?bookid="+item.getBookid()).into(image);
        }
    }
    public void setOnItemClickListener(OnBookItemClickListener listener) {
        this.listener = listener;
    }

    public void addItem(Book item)
    {
        items.add(item);
    }

    public void setItems(ArrayList<Book> items)
    {
        this.items = items;
    }

    public Book getItem(int position)
    {
        return items.get(position);
    }

    public void setItem(int position, Book item)
    {
        items.set(position, item);
    }
}
