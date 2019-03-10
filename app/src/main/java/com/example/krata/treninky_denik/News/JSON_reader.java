package com.example.krata.treninky_denik.News;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.krata.treninky_denik.Fragments.NewsFrg;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class JSON_reader extends AsyncTask<Void, Void, News>
{
    String url;
//    ProgressDialog progressDialog;
    Context context;

    private NewsFrg frg;

    public JSON_reader(NewsFrg frg, Context context, String url) {
        this.context = context;
        this.frg = frg;
        this.url = url;
//        progressDialog = new ProgressDialog(context);
//        progressDialog.setCancelable(false);
//        progressDialog.setTitle("Stahování novinek");
//        progressDialog.setMessage("Prosím čekejte...");
//        progressDialog.show();
    }

    public void JSON_readInBackground(Context context, String string)
    {
//        progressDialog.show();
        new JSON_reader(frg, context, string).execute();
    }

    @Override
    protected News doInBackground(Void... params) {
        JSONObject jsonObject = getJSON();
        try {
            JSONArray articles = jsonObject.getJSONArray("articles");
            List<Article> articleList = new ArrayList<>();
            for(int i = 0; i < articles.length(); i++)
            {
                JSONObject article = articles.getJSONObject(i);
                String author = article.getString("author");
                String title = article.getString("title");
                String description = article.getString("description");
                String url = article.getString("url");
                String urlToImage = article.getString("urlToImage");
                String publishedAt = article.getString("publishedAt");
                articleList.add(new Article(author,title,description,url,urlToImage,publishedAt));
            }
            return new News(jsonObject.getString("status").toString(), articleList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONObject getJSON()
    {HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(this.url);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream is = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(is));

            StringBuffer buffer =  new StringBuffer();

            String line = "";
            while((line = reader.readLine()) != null)
            {
                buffer.append(line);
            }

            return new JSONObject(buffer.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (connection != null)
                connection.disconnect();
            try{
                if (reader != null){
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(News result) {
//        progressDialog.dismiss();
        frg.news = result;
        frg.setLayout();
    }
}
