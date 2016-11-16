package com.example.macbookpro.booklisting;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by macbookpro on 6/28/16.
 */
public class NewsAdapter extends ArrayAdapter<News>{

    public NewsAdapter(Context context, ArrayList<News> bookz) {
        super(context, 0, bookz);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_news, parent, false);
        }
        // Get the {@link Word} object located at this position in the list
        final News currentNews = getItem(position);
        TextView BookTitle = (TextView) listItemView.findViewById(R.id.title);
        BookTitle.setText(currentNews.getMtitle());
        TextView BookWriter = (TextView) listItemView.findViewById(R.id.date);
        BookWriter.setText(currentNews.getMdate());
        final ImageView profile = (ImageView) listItemView.findViewById(R.id.image);
        new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    final Bitmap bitmap = BitmapFactory.decodeStream((InputStream) currentNews.getImage().getContent());
                    profile.post(new Runnable()
                    {
                        public void run()
                        {
                            if(profile !=null)
                            {
                                profile.setImageBitmap(bitmap);
                            }
                        }
                    });
                } catch (Exception e)
                {

                }
            }
        }).start();
        return listItemView;
    }
}
