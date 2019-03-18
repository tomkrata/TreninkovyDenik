package com.example.krata.treninky_denik.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.krata.treninky_denik.Callbacks.GetPlayersCallBack;
import com.example.krata.treninky_denik.Callbacks.GetUserCallback;
import com.example.krata.treninky_denik.Data;
import com.example.krata.treninky_denik.MainActivity;
import com.example.krata.treninky_denik.News.ExampleAdapter;
import com.example.krata.treninky_denik.News.ExampleItem;
import com.example.krata.treninky_denik.R;
import com.example.krata.treninky_denik.ServerRequests;
import com.example.krata.treninky_denik.User;
import com.example.krata.treninky_denik.UserLocalStore;

import java.util.ArrayList;

public class SearchFrg extends Fragment implements View.OnClickListener {


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<ExampleItem> exampleList = new ArrayList<>();

    ServerRequests serverRequests;

    private final String PICTURE = "http://training-diary.000webhostapp.com/pictures/profilePic_";

    EditText etSearch;

    UserLocalStore userLocalStore;


    Button btn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frg_searchplayer, container, false);
        serverRequests = new ServerRequests(getContext());
        Data.fragments.push(getClass().getName());

//        btn = (Button)v.findViewById(R.id.btn_selectPlayer);
//        btn.setOnClickListener(this);

        etSearch = (EditText) v.findViewById(R.id.search_textview);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getPlayers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mRecyclerView = v.findViewById(R.id.lst_players);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new ExampleAdapter(exampleList, getContext());

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        //mAdapter.notifyItemInserted(exampleList.size() - 1); -- when item is added

        getPlayers("");

        //getSupportFragmentManager().beginTransaction().replace(R.id.frg_container, new LoginFrg()).commit();

        userLocalStore = new UserLocalStore(getContext());

        return v;
    }

    private void getPlayers(String prefix)
    {
        serverRequests.fetchPrefixPlayersInBackground(new GetPlayersCallBack() {
            @Override
            public void done(ArrayList<String> nicks) {
                exampleList.clear();
                mAdapter.notifyDataSetChanged();
                for (String nick : nicks)
                {
                    String trainer = nick.charAt(0) == '1' ? "Trenér" : "Hráč";
                    nick = nick.substring(1);
                    exampleList.add(new ExampleItem(PICTURE + nick + ".JPG", nick, trainer, ""));
                    mAdapter.notifyItemInserted(exampleList.size() - 1);
                }
            }
        }, prefix);
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
//            case R.id.btn_selectPlayer:
//                Data.writeTo = etSearch.getText().toString();
//                getFragmentManager().beginTransaction().replace(R.id.frg_container, new ChatFrg()).commit();
//                break;
        }
    }


}

