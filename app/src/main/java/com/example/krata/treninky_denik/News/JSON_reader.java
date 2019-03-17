package com.example.krata.treninky_denik.News;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.krata.treninky_denik.Fragments.NewsFrg;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

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
    private static final String ns = null;

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
        JSONObject jsonObject = null;
        jsonObject = getObject();
        try {
            JSONArray articles = jsonObject.getJSONArray("item");
            List<Article> articleList = new ArrayList<>();
            String author = jsonObject.getString("title");
            for(int i = 0; i < articles.length(); i++)
            {
                JSONObject article = articles.getJSONObject(i);
                String title = article.getString("title");
                String description = article.getString("description");
                String url = article.getString("link");
                String urlToImage = article.getJSONObject("enclosure").getString("url");
                String publishedAt = article.getString("pubDate");
                articleList.add(new Article(author,title,description,url,urlToImage,publishedAt));
            }
            return new News("ok", articleList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONObject getObject()
    {
        try{
            return XML.toJSONObject(getContent()).getJSONObject("rss").getJSONObject("channel");
        }catch(Exception e)
        {
            return getObject();
        }
    }

    private String getContent()
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

            return buffer.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
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
        frg.setRefreshing(false);
    }
}
