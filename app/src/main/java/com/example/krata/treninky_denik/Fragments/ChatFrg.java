package com.example.krata.treninky_denik.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.krata.treninky_denik.Callbacks.GetMessageCallback;
import com.example.krata.treninky_denik.Callbacks.GetUserCallback;
import com.example.krata.treninky_denik.ClientSocket;
import com.example.krata.treninky_denik.Data;
import com.example.krata.treninky_denik.MainActivity;
import com.example.krata.treninky_denik.R;
import com.example.krata.treninky_denik.ServerRequests;
import com.example.krata.treninky_denik.User;
import com.example.krata.treninky_denik.UserLocalStore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import co.intentservice.chatui.ChatView;
import co.intentservice.chatui.models.ChatMessage;

public class ChatFrg extends Fragment implements View.OnClickListener{

    CalendarView calView;
    EditText et_userName, et_pass;
    TextView tw_register, head_nick, head_mail;

    public String from = "Alice";
    public String to = "Tom";

    ChatView chatView;

    boolean selectPlayer = true;

    UserLocalStore userLocalStore;
    Socket cs = null;

    Thread readThread;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frg_chat, container, false);
        String[] dots = getClass().getName().split("\\.");
        Data.fragments.push(dots[dots.length - 1]);
        userLocalStore = new UserLocalStore(getContext());
        chatView = (ChatView)v.findViewById(R.id.chat_view);
        from = userLocalStore.getLoggedUser().getNick();
        to = Data.writeTo;

//        try {
//            cs = new ClientSocket("Alice", "Tom");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        chatView.setOnSentMessageListener(new ChatView.OnSentMessageListener(){
            @Override
            public boolean sendMessage(ChatMessage chatMessage){
                //chatView.addMessage(message);
                try {
                    writeToServer(chatMessage.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });

//        ServerRequests serverRequests = new ServerRequests(getContext());
//        serverRequests.receiveMessage(new GetMessageCallback() {
//            @Override
//            public void done(String text) {
//                ChatMessage message = new ChatMessage(text, 0, ChatMessage.Type.RECEIVED);
//                chatView.addMessage(message);
//            }
//        }, cs);

        // Get a handler that can be used to post to the main thread



        readThread = new Thread(){
            public void run()
            {
                try {
                    cs = new Socket("40.127.191.146", 2343);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                writeToServer(from + "/" + to);
                while(true)
                {
                    try {
                        BufferedReader inFromServer = null;
                        inFromServer = new BufferedReader(new InputStreamReader(cs.getInputStream()));
                        String id = inFromServer.readLine();
                        final ChatMessage message = new ChatMessage(id, System.currentTimeMillis(), ChatMessage.Type.RECEIVED);
                        try {
                            getActivity().runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    chatView.addMessage(message);
                                }
                            });
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
//                        chatView.addMessage(message);
                        //                JOptionPane.showMessageDialog(null, id, "InfoBox", JOptionPane.INFORMATION_MESSAGE);
                        String subString = "";
                    }
                    catch (IOException ex)
                    {
                        this.destroy();
                        getFragmentManager().beginTransaction().replace(R.id.frg_container, new ManageFrg()).commit();
                    }
                }
            }
        };
        readThread.start();


        return v;
    }

    public void writeToServer(String text)
    {
        ServerRequests serverRequests = new ServerRequests();
        serverRequests.sendMessage(text, cs);
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {

        }
    }

}

