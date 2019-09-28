package com.beginagain.yourthinking.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beginagain.yourthinking.Item.MaratonPageItem;
import com.beginagain.yourthinking.R;

import org.w3c.dom.Text;

import java.util.List;

public class BookMaratonPageAdapter extends RecyclerView.Adapter<BookMaratonPageAdapter.MaratonViewHolder> {
    private List<MaratonPageItem> mMaratonPageList;
    private Context context;

    public BookMaratonPageAdapter(List<MaratonPageItem> mMaratonPageList, Context context) {
        this.mMaratonPageList = mMaratonPageList;
        this.context = context;
    }

    @NonNull
    @Override
    public MaratonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MaratonViewHolder (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_maraton_book_add, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MaratonViewHolder holder, int position) {
        MaratonPageItem data = mMaratonPageList.get(position);
        holder.maratonPageTextView.setText(data.getReadPage()+ "페이지를 읽었습니다.");
        holder.maratonDateTextView.setText(data.getDate());
        holder.maratonPointTextView.setText(data.getPoint());
    }

    @Override
    public int getItemCount() {
        return mMaratonPageList.size();
    }

    class MaratonViewHolder extends RecyclerView.ViewHolder {

        private TextView maratonPageTextView;
        private TextView maratonDateTextView;
        private TextView maratonPointTextView;

        public MaratonViewHolder(View itemView) {
            super(itemView);
            maratonPageTextView = itemView.findViewById(R.id.text_view_maraton_history_item_page);
            maratonDateTextView = itemView.findViewById(R.id.text_view_maraton_history_item_date);
            maratonPointTextView = itemView.findViewById(R.id.text_view_maraton_history_item_point);
        }
    }
}
