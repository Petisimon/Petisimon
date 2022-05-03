package com.example.bookstore;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> implements Filterable {
    private ArrayList<Book> mBooksData;
    private ArrayList<Book> mBooksDataAll;
    private Context mContext;
    private int lastPos = -1;

    BookAdapter(Context context, ArrayList<Book> booksData) {
        this.mBooksData = booksData;
        this.mBooksDataAll = booksData;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item,parent,false));
    }

    @Override
    public void onBindViewHolder(BookAdapter.ViewHolder holder, int position) {
        Book currBook = mBooksData.get(position);

        holder.bindTo(currBook);

        if(holder.getAdapterPosition() > lastPos){
            Animation animation = AnimationUtils.loadAnimation(mContext,R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            lastPos = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return mBooksData.size();
    }

    @Override
    public Filter getFilter() {
        return shoppingFilter;
    }

    private Filter shoppingFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Book> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if(charSequence == null || charSequence.length() == 0){
                results.count = mBooksDataAll.size();
                results.values = mBooksDataAll;
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (Book book : mBooksDataAll){
                    if(book.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(book);
                    }
                }
                results.count = filteredList.size();
                results.values = filteredList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mBooksData = (ArrayList) filterResults.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView mTitleText;
        private TextView mInfoText;
        private TextView mPriceText;
        private ImageView mBookImage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mTitleText = itemView.findViewById(R.id.itemTitle);
            mInfoText = itemView.findViewById(R.id.subTitle);
            mPriceText = itemView.findViewById(R.id.price);
            mBookImage = itemView.findViewById(R.id.itemImage);

            itemView.findViewById(R.id.addToCartButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ShopListActivity)mContext).updateAlertIcon();
                }
            });
        }

        public void bindTo(Book currBook) {
            mTitleText.setText(currBook.getName());
            mInfoText.setText(currBook.getInfo());
            mPriceText.setText(currBook.getPrice());
            Glide.with(mContext).load(currBook.getImageResource()).into(mBookImage);
        }
    }
}