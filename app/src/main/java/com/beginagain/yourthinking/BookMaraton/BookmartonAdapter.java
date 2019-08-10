package com.beginagain.yourthinking.BookMaraton;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.beginagain.yourthinking.Adapter.BookApiAdapter;
import com.beginagain.yourthinking.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BookmartonAdapter extends BaseAdapter {
    private List<HashMap<String, String>> booksList;
    private int listSize;
    LayoutInflater inflater = null;

    public BookmartonAdapter(List<HashMap<String,String>> books) {
        if(books == null)
            booksList = new ArrayList<>();
        else
            booksList = books;
        listSize = booksList.size();
    }

    @Override
    public int getCount() {
        return listSize;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HashMap book = booksList.get(position);

        if (convertView == null)
        {
            final Context context = parent.getContext();
            if (inflater == null)
            {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
        }

        TextView bookTitle =  convertView.findViewById(R.id.ListView_item_title_TV);
        TextView pageNum =  convertView.findViewById(R.id.pageNum);
        TextView testURL = convertView.findViewById(R.id.currentPage);
        ImageView imageView = convertView.findViewById(R.id.iv_recommend_item_thumbnail2);
        TextView currentPage_TV = convertView.findViewById(R.id.currentPage);

        bookTitle.setText(book.get(BookMaratonActivity.TAG_Title).toString());
        pageNum.setText(book.get(BookMaratonActivity.TAG_PageNum).toString());
        currentPage_TV.setText(book.get(BookMaratonActivity.TAG_currentPageNum) + "p");
        String url = book.get(BookMaratonActivity.TAG_imgURL) + "";

        if(!url.equals("null"))
            Picasso.get().load(url).into(imageView);
        return convertView;
    }
}
