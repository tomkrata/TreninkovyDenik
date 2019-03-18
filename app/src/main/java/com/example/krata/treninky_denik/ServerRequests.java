package com.example.krata.treninky_denik;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.util.Base64;
import android.widget.Toast;

import com.example.krata.treninky_denik.Callbacks.GetLessonCallback;
import com.example.krata.treninky_denik.Callbacks.GetLessonsCallback;
import com.example.krata.treninky_denik.Callbacks.GetMessageCallback;
import com.example.krata.treninky_denik.Callbacks.GetPlayersCallBack;
import com.example.krata.treninky_denik.Callbacks.GetUserCallback;
import com.example.krata.treninky_denik.Fragments.ChatFrg;

import co.intentservice.chatui.models.ChatMessage;

public class ServerRequests {

    ProgressDialog progressDialog;
    public static final int CONNECTION_TIMEOUT = 1000*15;
    public static final String SERVER_ADDRESS = "http://training-diary.000webhostapp.com/";

    public ServerRequests(Context context)
    {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Makám na tom");
        progressDialog.setMessage("Prosím čekejte...");

    }

    public ServerRequests()
    {

    }

    public void storeUserDataInBackground(User user, GetUserCallback callBack)
    {
        progressDialog.show();
        new StoreUserDataAsyncTask(user, callBack).execute();
    }

    public void storeLessonInBackground(Lesson lesson, GetLessonCallback callBack)
    {
        progressDialog.show();
        new StoreLessonAsyncTask(lesson, callBack).execute();
    }
    public void storeLessonHistInBackground(String date, Lesson lesson, GetLessonCallback callBack)
    {
        progressDialog.show();
        new StoreLessonHistAsyncTask(date, lesson, callBack).execute();
    }

    public void fetchUserDataInBackground(User user, GetUserCallback callBack)
    {
        progressDialog.show();
        new FetchUserDataAsyncTask(user, callBack).execute();
    }

    public void receiveMessage(GetMessageCallback callBack, Socket cs)
    {
        new ReceiveMessage(callBack, cs).execute();
    }

    public void fetchLessonsInBackground(User user, GetLessonsCallback callBack)
    {
        new FetchLessonsForPlayer(user, callBack).execute();
    }
    public void fetchLessonsByDayInBackground(String trainer, String dow, GetLessonsCallback callBack)
    {
        new FetchLessonsByDay(trainer, dow, callBack).execute();
    }
    public void fetchLessonsByDateInBackground(User player, String date, GetLessonsCallback callBack)
    {
        new FetchLessonsByDate(player, date, callBack).execute();
    }

    public void sendMessage(String message, Socket cs)
    {
        new SendMessage(message, cs).execute();
    }

    public void fetchAllPlayersInBackground(GetPlayersCallBack callBack)
    {
        new FetchAllPlayers(callBack, "").execute();
    }
    public void fetchPrefixPlayersInBackground(GetPlayersCallBack callBack, String prefix)
    {
        new FetchAllPlayers(callBack, prefix).execute();
    }

    public void storePictureInBackground(Bitmap image, String name)
    {
        progressDialog.show();
        new UploadImage(image, name).execute();
    }


    public class StoreUserDataAsyncTask extends AsyncTask<Void, Void, Void> {
        User user;
        GetUserCallback callBack;

        public StoreUserDataAsyncTask(User user, GetUserCallback callBack) {
            this.user = user;
            this.callBack = callBack;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            int trainerOrNot = user.trainer ? 1 : 0;
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("nick", user.nick));
            dataToSend.add(new BasicNameValuePair("mail", user.mail));
            dataToSend.add(new BasicNameValuePair("pass", user.pass));
            dataToSend.add(new BasicNameValuePair("trainer", trainerOrNot + ""));

            HttpParams httpRequestParams = getHttpRequestParams();

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "Register.php");

            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                client.execute(post);
            } catch (Exception e) {
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            progressDialog.dismiss();
            callBack.done(null);

            super.onPostExecute(aVoid);
        }
    }

    public class StoreLessonAsyncTask extends AsyncTask<Void, Void, Void>
    {
        Lesson lesson;
        GetLessonCallback callBack;

        public StoreLessonAsyncTask(Lesson lesson, GetLessonCallback callBack) {
            this.lesson = lesson;
            this.callBack = callBack;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("dow", lesson.dayOfWeek));
            dataToSend.add(new BasicNameValuePair("time", lesson.startTime + "-" + lesson.endTime));
            String players = ",";
            for(String plyrs : lesson.players)
            {
                players += plyrs + ",";
            }
            dataToSend.add(new BasicNameValuePair("players", players));
            dataToSend.add(new BasicNameValuePair("trainer", lesson.trainer));

            HttpParams httpRequestParams = getHttpRequestParams();

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "StoreLesson.php");

            try{
                post.setEntity(new UrlEncodedFormEntity(dataToSend, HTTP.UTF_8));
                HttpResponse httpResponse = client.execute(post);

                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);
                JSONObject jObject = new JSONObject(result);
            } catch (Exception e){
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            progressDialog.dismiss();
            callBack.done(null);
            super.onPostExecute(aVoid);
        }
    }

    public class StoreLessonHistAsyncTask extends AsyncTask<Void, Void, Void>
    {
        Lesson lesson;
        GetLessonCallback callBack;
        String date;

        public StoreLessonHistAsyncTask(String date, Lesson lesson, GetLessonCallback callBack) {
            this.lesson = lesson;
            this.callBack = callBack;
            this.date = date;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("date", date));
            String players = ",";
            for(String plyrs : lesson.players)
            {
                players += plyrs + ",";
            }
            dataToSend.add(new BasicNameValuePair("players", players));
            dataToSend.add(new BasicNameValuePair("trainer", lesson.trainer));
            dataToSend.add(new BasicNameValuePair("comment", lesson.comment));

            HttpParams httpRequestParams = getHttpRequestParams();

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "StoreLessonHistory.php");

            try{
                post.setEntity(new UrlEncodedFormEntity(dataToSend, HTTP.UTF_8));
                HttpResponse httpResponse = client.execute(post);

                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);
                JSONObject jObject = new JSONObject(result);
            } catch (Exception e){
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            progressDialog.dismiss();
            callBack.done(null);
            super.onPostExecute(aVoid);
        }
    }

    public class FetchUserDataAsyncTask extends AsyncTask<Void, Void, User> {
        User user;
        GetUserCallback callBack;

        public FetchUserDataAsyncTask(User user, GetUserCallback callBack) {
            this.user = user;
            this.callBack = callBack;
        }

        @Override
        protected User doInBackground(Void... params) {
            ArrayList<NameValuePair>  dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("nick", user.nick));
            dataToSend.add(new BasicNameValuePair("pass", user.pass));

            HttpParams httpRequestParams = getHttpRequestParams();

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "FetchUserData.php");

            User returnedUser = null;
            try{
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse httpResponse = client.execute(post);

                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);
                JSONObject jObject = new JSONObject(result);

                if (jObject.length() == 0)
                    returnedUser = null;
                else
                {
                    String mail = jObject.getString("mail");
                    boolean trainer = jObject.getInt("trainer") == 1 ? true : false;
                    Bitmap image = new DownloadImage(user.nick).getImage();

                    returnedUser = new User(user.nick, mail, user.pass, trainer, image);
                }

            } catch (Exception e){
            }

            return returnedUser;
        }

        @Override
        protected void onPostExecute(User returnedUser) {
            progressDialog.dismiss();
            callBack.done(returnedUser);
            super.onPostExecute(returnedUser);
        }
    }

    public class FetchAllPlayers extends AsyncTask<Void, Void, ArrayList<String>> {
        GetPlayersCallBack callBack;
        String prefix;

        public FetchAllPlayers(GetPlayersCallBack callBack,String prefix) {
            this.callBack = callBack;
            this.prefix = prefix;
        }

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("prefix", prefix));

            HttpParams httpRequestParams = getHttpRequestParams();

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "FetchAllPlayers.php");

            ArrayList<String> nicks = new ArrayList<>();
            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend, HTTP.UTF_8));
                HttpResponse httpResponse = client.execute(post);

                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);
                JSONArray jsonArray = new JSONArray(result);

                if (jsonArray.length() == 0)
                    nicks = null;
                else {
                    for (int i = 0; i < jsonArray.length(); i++)
                        nicks.add(jsonArray.get(i).toString());
                }

            } catch (Exception e) {
            }
            return nicks;
        }

        @Override
        protected void onPostExecute(ArrayList<String> nicks) {
            callBack.done(nicks);
            super.onPostExecute(nicks);
        }
    }

    public class FetchLessonsForPlayer extends AsyncTask<Void, Void, ArrayList<Lesson>> {
        GetLessonsCallback callBack;
        User user;

        public FetchLessonsForPlayer(User user, GetLessonsCallback callBack) {
            this.callBack = callBack;
            this.user = user;
        }

        @Override
        protected ArrayList<Lesson> doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            int trainerOrNot = user.trainer ? 1 : 0;
            dataToSend.add(new BasicNameValuePair("user", user.nick));
            dataToSend.add(new BasicNameValuePair("trainer", trainerOrNot + ""));

            HttpParams httpRequestParams = getHttpRequestParams();

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "FetchLessons.php");

            ArrayList<Lesson> lessons = new ArrayList<>();
            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend, HTTP.UTF_8));
                HttpResponse httpResponse = client.execute(post);

                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);
                JSONArray jsonArray = new JSONArray(result);
                if (jsonArray.length() <= 0)
                    return null;
                for (int i = 0; i < jsonArray.getJSONArray(0).length(); i++)
                {
                    String dayOfWeek = jsonArray.getJSONArray(0).get(i).toString();
                    String[] time = jsonArray.getJSONArray(1).get(i).toString().split("-");
                    String playersTemp = jsonArray.getJSONArray(2).get(i).toString();
                    if (playersTemp.charAt(0) == ',')
                        playersTemp.replaceFirst(",", "");
                    String[] players = playersTemp.split(",");
                    String trainer = jsonArray.getJSONArray(3).get(i).toString();

                    Lesson lesson = new Lesson(dayOfWeek, time[0], time[1], trainer, players);
                    lessons.add(lesson);
                }

            } catch (Exception e) {
            }
            return lessons;
        }

        @Override
        protected void onPostExecute(ArrayList<Lesson> lessons) {
            callBack.done(lessons);
            super.onPostExecute(lessons);
        }
    }

    public class FetchLessonsByDay extends AsyncTask<Void, Void, ArrayList<Lesson>> {
        GetLessonsCallback callBack;
        String dayOfWeek;
        String trainer;

        public FetchLessonsByDay(String trainer, String dayOfWeek, GetLessonsCallback callBack) {
            this.callBack = callBack;
            this.dayOfWeek = dayOfWeek;
            this.trainer = trainer;
        }

        @Override
        protected ArrayList<Lesson> doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("dow", dayOfWeek));
            dataToSend.add(new BasicNameValuePair("trainer", trainer));

            HttpParams httpRequestParams = getHttpRequestParams();

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "FetchLessonsByDay.php");

            ArrayList<Lesson> lessons = new ArrayList<>();
            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend, HTTP.UTF_8));
                HttpResponse httpResponse = client.execute(post);

                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);
                JSONArray jsonArray = new JSONArray(result);
                if (jsonArray.length() <= 0)
                    return null;
                for (int i = 0; i < jsonArray.getJSONArray(0).length(); i++)
                {
                    String dayOfWeek = jsonArray.getJSONArray(0).get(i).toString();
                    String[] time = jsonArray.getJSONArray(1).get(i).toString().split("-");
                    String playersTemp = jsonArray.getJSONArray(2).get(i).toString();
                    if (playersTemp.charAt(0) == ',')
                        playersTemp = playersTemp.replaceFirst(",", "");
                    String[] players = playersTemp.split(",");
                    String trainer = jsonArray.getJSONArray(3).get(i).toString();

                    Lesson lesson = new Lesson(dayOfWeek, time[0], time[1], trainer, players);
                    lessons.add(lesson);
                }

            } catch (Exception e) {
            }
            return lessons;
        }

        @Override
        protected void onPostExecute(ArrayList<Lesson> lessons) {
            callBack.done(lessons);
            super.onPostExecute(lessons);
        }
    }

    public class FetchLessonsByDate extends AsyncTask<Void, Void, ArrayList<Lesson>> {
        GetLessonsCallback callBack;
        User player;
        String date;

        public FetchLessonsByDate(User user, String date, GetLessonsCallback callBack) {
            this.callBack = callBack;
            this.player = user;
            this.date = date;
        }

        @Override
        protected ArrayList<Lesson> doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            int trainerOrNot = player.trainer ? 1 : 0;
            dataToSend.add(new BasicNameValuePair("date", date));
            dataToSend.add(new BasicNameValuePair("player", player.getNick()));
            dataToSend.add(new BasicNameValuePair("isTrainer", trainerOrNot + ""));

            HttpParams httpRequestParams = getHttpRequestParams();

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "FetchLessonsByDate.php");

            ArrayList<Lesson> lessons = new ArrayList<>();
            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend, HTTP.UTF_8));
                HttpResponse httpResponse = client.execute(post);

                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);
                JSONArray jsonArray = new JSONArray(result);
                if (jsonArray.length() <= 0)
                    return null;
                for (int i = 0; i < jsonArray.getJSONArray(0).length(); i++)
                {
                    String date = jsonArray.getJSONArray(0).get(i).toString();
                    String playersTemp = jsonArray.getJSONArray(1).get(i).toString();
                    if (playersTemp.charAt(0) == ',')
                        playersTemp = playersTemp.replaceFirst(",", "");
                    String[] players = playersTemp.split(",");
                    String trainer = jsonArray.getJSONArray(2).get(i).toString();
                    String comment = jsonArray.getJSONArray(3).get(i).toString();

                    Lesson lesson = new Lesson();
                    lesson.setDayOfWeek(date);
                    lesson.setPlayers(players);
                    lesson.setTrainer(trainer);
                    lesson.setComment(comment);
                    lessons.add(lesson);
                }

            } catch (Exception e) {
            }
            return lessons;
        }

        @Override
        protected void onPostExecute(ArrayList<Lesson> lessons) {
            callBack.done(lessons);
            super.onPostExecute(lessons);
        }
    }

    private class UploadImage extends AsyncTask<Void, Void, Void>
    {
        Bitmap image;
        String name;
        public UploadImage(Bitmap image, String name)
        {
            this.image = image;
            this.name = name;
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, byteStream);
            String encodedImage = Base64.encodeToString(byteStream.toByteArray(), Base64.DEFAULT);

            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("image", encodedImage));
            dataToSend.add(new BasicNameValuePair("name", "profilePic_" + name)); // name of a file with no extension

            HttpParams httpRequestParams = getHttpRequestParams();
            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "SavePicture.php");

            try
            {
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                client.execute(post);

            } catch(Exception e)
            {

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }
    }


    private class SendMessage extends AsyncTask<Void, Void, Void>
    {
        String message;
        Socket cs;

        public SendMessage(String message, Socket cs) {
            this.message = message;
            this.cs = cs;
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
//                if (cs == null) {
//                    cs = new Socket("192.168.46.1", 1111);
//                }
                DataOutputStream outToServer = new DataOutputStream(cs.getOutputStream());
                outToServer.writeBytes(message + "\n");

            } catch(Exception e)
            {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private class ReceiveMessage extends AsyncTask<Void, Void, String>
    {
        GetMessageCallback callback;
        Socket cs;

        public ReceiveMessage(GetMessageCallback callback, Socket cs) {
            this.callback = callback;
            this.cs = cs;
        }

        @Override
        protected String doInBackground(Void... params)
        {
            String id = "";
            try {
                if (cs == null) {
                    cs = new Socket("192.168.46.1", 1111);
                }
                BufferedReader inFromServer = null;
                inFromServer = new BufferedReader(new InputStreamReader(cs.getInputStream()));
                id = inFromServer.readLine();
                ChatMessage message = new ChatMessage(id, 0, ChatMessage.Type.RECEIVED);
//                        chatView.addMessage(message);
                //                JOptionPane.showMessageDialog(null, id, "InfoBox", JOptionPane.INFORMATION_MESSAGE);
                String subString = "";
            }
            catch (IOException ex)
            {
                Logger.getLogger(ClientSocket.class.getName()).log(Level.SEVERE, null, ex);
            }
            return id;
        }

        @Override
        protected void onPostExecute(String message) {
            callback.done(message);
            this.execute();
        }
    }

    private class DownloadImage
    {
        String name;
        public DownloadImage(String name)
        {
            this.name = name;
        }

        public Bitmap getImage()
        {
            String url = SERVER_ADDRESS + "pictures/profilePic_" + name + ".JPG";

            try
            {
                URLConnection connection = new URL(url).openConnection();
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                connection.setReadTimeout(CONNECTION_TIMEOUT);

                Bitmap image = BitmapFactory.decodeStream((InputStream) connection.getContent(), null, null);
                return image;

            }catch(Exception e)
            {
                return null;
            }
        }
    }

    private HttpParams getHttpRequestParams()
    {
        HttpParams httpRequestParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);
        return httpRequestParams;
    }
}
