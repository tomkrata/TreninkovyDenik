package com.example.krata.treninky_denik.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.krata.treninky_denik.Callbacks.GetUserCallback;
import com.example.krata.treninky_denik.Data;
import com.example.krata.treninky_denik.Lesson;
import com.example.krata.treninky_denik.MainActivity;
import com.example.krata.treninky_denik.News.ExampleAdapter;
import com.example.krata.treninky_denik.News.ExampleItem;
import com.example.krata.treninky_denik.R;
import com.example.krata.treninky_denik.ServerRequests;
import com.example.krata.treninky_denik.User;
import com.example.krata.treninky_denik.UserLocalStore;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MyTrainsFrg extends Fragment implements View.OnClickListener{

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<ExampleItem> exampleList = new ArrayList<>();

    UserLocalStore userLocalStore;

    ArrayList<Lesson> lessons = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frg_mytrains, container, false);
        //getSupportFragmentManager().beginTransaction().replace(R.id.frg_container, new LoginFrg()).commit();

        userLocalStore = new UserLocalStore(getContext());

        lessons = Data.lessons;
        ArrayList<String> stringLessons = new ArrayList<String>();
        ArrayList<String> stringSubLessons = new ArrayList<String>();



        for(Lesson l : lessons)
        {
            String players = "";
            for(int i = 0; i < l.getPlayers().length - 1; i++)
            {
                players += l.getPlayers()[i] + ", ";
            }
            players += l.getPlayers()[l.getPlayers().length - 1];
            exampleList.add(new ExampleItem("https://cdn.bleacherreport.net/images/team_logos/328x328/tennis.png",
                    l.getDayOfWeek() + " " + l.getStartTime() + "-" + l.getEndTime(),
                    "Hráči: " + players + " Trenér: " + l.getTrainer(), ""));
        }

        mRecyclerView = v.findViewById(R.id.lst_trains);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new ExampleAdapter(exampleList, getContext());

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        return v;
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {

        }
    }

}

