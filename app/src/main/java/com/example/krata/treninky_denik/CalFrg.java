package com.example.krata.treninky_denik;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.krata.treninky_denik.Callbacks.GetLessonsCallback;
import com.example.krata.treninky_denik.Callbacks.GetUserCallback;
import com.example.krata.treninky_denik.Fragments.LoginFrg;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class CalFrg extends Fragment implements View.OnClickListener
{

    UserLocalStore userLocalStore;
    CalendarView calView;
    Spinner spinner;

    TextView trainer, comment, timeView;
    LinearLayout players;

    ArrayList<Lesson> lessons = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frg_cal, container, false);

        trainer = (TextView)v.findViewById(R.id.calView_trainer);
        players = (LinearLayout) v.findViewById(R.id.playersAdd_layout);
        comment = (TextView)v.findViewById(R.id.textView_comment);
        timeView = (TextView)v.findViewById(R.id.calView_time);

        userLocalStore = new UserLocalStore(getContext());
        calView = (CalendarView)v.findViewById(R.id.cal_view);
        spinner = (Spinner)v.findViewById(R.id.lessonsHist_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(players.getChildCount() > 0)
                    players.removeAllViews();
                String selectedItem = parent.getItemAtPosition(position).toString();
                Lesson l = lessons.get(position);
                trainer.setText(l.trainer);
                comment.setText(l.comment);
                for(String nick : l.getPlayers())
                {
                    TextView tv = new TextView(getContext());
                    tv.setText(nick);
                    tv.setTextSize(15);
                    players.addView(tv);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        calView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int day) {
                month++;
                String d = Integer.toString(day);
                String m = Integer.toString(month);
                String y = Integer.toString(year);
                if (d.length() < 2)
                    d = "0" + d;
                if (m.length() < 2)
                    m = "0" + m;
                ServerRequests serverRequests = new ServerRequests(getContext());
                serverRequests.fetchLessonsByDateInBackground(userLocalStore.getLoggedUser(), d + "." + m + "." + y, new GetLessonsCallback() {
                    @Override
                    public void done(ArrayList<Lesson> ls) {
                        lessons = ls;
                        ArrayList<String> lessonTimes = new ArrayList<>();

                        for(int i = 0; i < ls.size(); i++)
                        {
                            String time = ls.get(i).getDayOfWeek().substring(11);
                            lessonTimes.add(time.replace("-", " - "));
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                                android.R.layout.simple_spinner_item, lessonTimes);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);
                    }
                });
            }
        });
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {

        }
    }

    private void registerUser(User user)
    {
        ServerRequests serverRequests = new ServerRequests(getContext());
        serverRequests.storeUserDataInBackground(user, new GetUserCallback() {
            @Override
            public void done(User returnedUser) {
                getFragmentManager().beginTransaction().replace(R.id.frg_container, new LoginFrg()).commit();
            }
        });
    }

    private boolean isEmailValid(String email)
    {
        if (email == null)
            return false;
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        return pat.matcher(email).matches();
    }
}
