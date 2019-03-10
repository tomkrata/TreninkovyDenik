package com.example.krata.treninky_denik.Fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.krata.treninky_denik.News.Article;
import com.example.krata.treninky_denik.News.ExampleAdapter;
import com.example.krata.treninky_denik.News.ExampleItem;
import com.example.krata.treninky_denik.News.JSON_reader;
import com.example.krata.treninky_denik.News.News;
import com.example.krata.treninky_denik.R;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NewsFrg extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    String tennisURL = "https://newsapi.org/v2/everything?q=tennis&apiKey=09587b074e35476ea92bd2387c8802c5";
    JSON_reader reader;
    public News news;
    List<Article> articles = new ArrayList<>();
    ArrayList<ExampleItem> exampleList = new ArrayList<>();

    TextView textAuthor, topTitle;
    KenBurnsView topImage;
    RecyclerView lstView;

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frg_news, container, false);
        reader = new JSON_reader(this, getContext(), tennisURL);
        reader.execute();

        textAuthor = (TextView)v.findViewById(R.id.top_author);
        topTitle = (TextView)v.findViewById(R.id.top_title);
        topImage = (KenBurnsView) v.findViewById(R.id.top_image);
        lstView = (RecyclerView) v.findViewById(R.id.lst_news);

        mRecyclerView = v.findViewById(R.id.lst_news);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new ExampleAdapter(exampleList, getContext());

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        return v;
    }

    public void setLayout()
    {
        articles = news.getArticles();
        String imageUrl = articles.get(0).getUrlToImage();
        textAuthor.setText(articles.get(0).getAuthor());
        topTitle.setText(articles.get(0).getTitle());
        for (int i = 1; i < articles.size(); i++)
        {
            Article art = articles.get(i);
            exampleList.add(new ExampleItem(art.getUrlToImage(), art.getTitle(), art.getDescription()));
            mAdapter.notifyItemInserted(exampleList.size() - 1);
        }
        //Drawable image = loadImage(news.getArticles().get(0).getUrlToImage());
        //topImage.setImageDrawable(image);
        loadImageFromUrl(imageUrl);
    }

    private void loadImageFromUrl(String url)
    {
        try
        {
            Picasso.with(getContext()).load(url).placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(topImage, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                        }
                    });
        } catch(Exception e)
        {

        }
    }

    private Drawable loadImage(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }
}

