package com.beginagain.yourthinking.BookMaraton;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.beginagain.yourthinking.Item.RecommendBookItem;
import com.beginagain.yourthinking.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BookApiAdapter_for_maraton extends RecyclerView.Adapter<BookApiAdapter_for_maraton.MyViewHolder> {
    Context context;
    ArrayList<RecommendBookItem> items;
    String title, author, company, ISBN, thumbnail_URL;
    int itemLayout;

    public BookApiAdapter_for_maraton(Context context, ArrayList<RecommendBookItem> items, int itemLayout) {
        this.context = context;
        this.items = items;
        this.itemLayout = itemLayout;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recommend_book, null);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        if (getItemCount() <= 0 && position >= getItemCount()) {
            return;
        }

        RecommendBookItem item = items.get(myViewHolder.getAdapterPosition());
        myViewHolder.cardView.setTag(position);
        myViewHolder.mTitle.setText(item.getTitle());
        myViewHolder.mAuthor.setText(item.getAuthor());
        myViewHolder.mCompany.setText(item.getPublisher());
        myViewHolder.mISBN.setText(item.getIsbn());
        Picasso.get().load(item.getImage()).into(myViewHolder.mThumbnail);

    }

    @Override
    public int getItemCount() {
        // null error 방지
        if (items == null) {
            return 0;
        }
        return this.items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView mTitle, mAuthor, mCompany, mISBN;
        public ImageView mThumbnail;
        public CardView cardView;

        SharedPreferences searchResult;
        SharedPreferences.Editor editor;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.cardview_recommend_item);
            mTitle = (TextView) itemView.findViewById(R.id.text_view_recommend_item_title);
            mAuthor = (TextView) itemView.findViewById(R.id.text_view_recommend_item_author);
            mCompany = (TextView) itemView.findViewById(R.id.text_view_recommend_item_company);
            mISBN = (TextView) itemView.findViewById(R.id.text_view_recommend_item_isbn);
            mThumbnail = (ImageView)itemView.findViewById(R.id.iv_recommend_item_thumbnail);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition() ;
                    RecommendBookItem data = items.get(pos);

                    title = data.getTitle();
                    author = data.getAuthor();
                    company = data.getPublisher();
                    ISBN = data.getIsbn();
                    thumbnail_URL = data.getImage();

                    String bookTitle = BookMaratonActivity.bookTitle_ET.getText().toString();
                    List<HashMap<String, String>> books = BookMaratonActivity.books;
                    ListView listview = BookMaratonActivity.listView;
                    String targetTilte = null;

                    for(int i = 0; i < books.size(); i++) {
                        HashMap book = books.get(i);

                        targetTilte = book.get(BookMaratonActivity.TAG_Title).toString();
                        if(bookTitle.equals(targetTilte)) {
                            book.put(BookMaratonActivity.TAG_imgURL, thumbnail_URL);
                        }
                    }
                    Toast.makeText(view.getContext(), title + " 선택" , Toast.LENGTH_LONG).show();

                    listview.setAdapter(new BookmartonAdapter(books));
                    /*
                    searchResult = view.getContext().getSharedPreferences("searchResult", Context.MODE_PRIVATE);
                    editor = searchResult.edit();

                    String str = et.getText().toString(); // 사용자가 입력한 값
                    editor.putString("name", str); // 입력
                    editor.putString("xx", "xx"); // 입력
                    editor.commit(); // 파일에 최종 반영함
*/

                }
            });
        }
    }
}