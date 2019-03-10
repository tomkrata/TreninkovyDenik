package com.example.krata.treninky_denik.Fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.krata.treninky_denik.Callbacks.GetLessonCallback;
import com.example.krata.treninky_denik.Callbacks.GetLessonsCallback;
import com.example.krata.treninky_denik.Callbacks.GetPlayersCallBack;
import com.example.krata.treninky_denik.Data;
import com.example.krata.treninky_denik.Lesson;
import com.example.krata.treninky_denik.R;
import com.example.krata.treninky_denik.ServerRequests;
import com.example.krata.treninky_denik.TimePickerFragment;
import com.example.krata.treninky_denik.UserLocalStore;
import com.google.api.client.util.DateTime;
import com.google.common.base.FinalizableSoftReference;

import java.util.ArrayList;
import java.util.Calendar;

public class WriteFrg extends Fragment implements View.OnClickListener{

    UserLocalStore userLocalStore;
    private String[] players;

    TextView dateView, lessonEnd, periodStart, periodEnd;
    EditText commentText;
    Spinner lessonsSpinner;
    LinearLayout linearLayout;
    Button btnSave;

    ArrayList<Lesson> lessons = new ArrayList<>();
    String lessTime = "";

    View v;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frg_write, container, false);
        Data.fragments.push(getClass().getName());
        this.v = v;

        commentText = (EditText)v.findViewById(R.id.comment_view);

        btnSave = (Button)v.findViewById(R.id.btn_saveHistory);
        btnSave.setOnClickListener(this);

        dateView = (TextView)v.findViewById(R.id.chooseDate_view);
        dateView.setOnClickListener(this);

        linearLayout = (LinearLayout)v.findViewById(R.id.players_income);

        lessonsSpinner = (Spinner)v.findViewById(R.id.spinner_lessonsByDay);
        lessonsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(linearLayout.getChildCount() > 0)
                    linearLayout.removeAllViews();
                String selectedItem = parent.getItemAtPosition(position).toString();
                lessTime = selectedItem;
                Lesson lesson = lessons.get(position);
                for(String nick : lesson.getPlayers())
                {
                    CheckBox checkBox = new CheckBox(getContext());
                    checkBox.setChecked(true);
                    checkBox.setText(nick);
                    checkBox.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        }
                    });
                    linearLayout.addView(checkBox);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        userLocalStore = new UserLocalStore(getContext());

        return v;
    }

    private void getPlayers(final Spinner s, final TextView hraciView)
    {
        ServerRequests serverRequests = new ServerRequests(getContext());
        serverRequests.fetchAllPlayersInBackground(new GetPlayersCallBack() {
            @Override
            public void done(ArrayList<String> nicks) {
                hraciView.setText("Hráči");
                nicks.add(0, "Výběr hráče");
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_item, nicks);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                s.setAdapter(adapter);
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        DialogFragment timePicker = new TimePickerFragment();
        switch(v.getId())
        {
            case R.id.chooseDate_view:
                showDatePicker("Vybrat datum", dateView);
                break;
            case R.id.btn_saveHistory:
                storeLesson();
                break;
        }
    }

    private void storeLesson()
    {
        Lesson lesson = new Lesson();
        String date = dateView.getText().toString() + " " + lessTime.replace(" ", "");
        ArrayList<String> names = new ArrayList<>();
        for(int i = 0; i < linearLayout.getChildCount(); i++)
        {
            CheckBox checkBox = (CheckBox)linearLayout.getChildAt(i);
            if (checkBox.isChecked())
                names.add(checkBox.getText().toString());
        }
        lesson.setComment(commentText.getText().toString());
        lesson.setPlayers(names.toArray(new String[names.size()]));
        lesson.setTrainer(userLocalStore.getLoggedUser().getNick());

        ServerRequests serverRequests = new ServerRequests(getContext());
        serverRequests.storeLessonHistInBackground(date, lesson, new GetLessonCallback() {
            @Override
            public void done(Lesson ls) {
                Toast.makeText(getContext(), "Lekce uložena", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getLessons(String dow)
    {
        ServerRequests serverRequests = new ServerRequests(getContext());
        serverRequests.fetchLessonsByDayInBackground(userLocalStore.getLoggedUser().getNick(), dow, new GetLessonsCallback() {
            @Override
            public void done(ArrayList<Lesson> ls) {
                lessons = ls;
                ArrayList<String> lessonTimes = new ArrayList<>();
                for(int i = 0; i < lessons.size(); i++)
                {
                    lessonTimes.add(lessons.get(i).getStartTime() + " - " + lessons.get(i).getEndTime());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_item, lessonTimes);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                lessonsSpinner.setAdapter(adapter);
            }
        });
    }

    private String getDayOfWeek(Calendar cal)
    {
        switch (cal.get(Calendar.DAY_OF_WEEK))
        {
            case Calendar.TUESDAY:
                return "Út";
            case Calendar.WEDNESDAY:
                return "St";
            case Calendar.THURSDAY:
                return "Čt";
            case Calendar.FRIDAY:
                return "Pá";
            case Calendar.SATURDAY:
                return "So";
            case Calendar.SUNDAY:
                return "Ne";
            case Calendar.MONDAY:
            default:
                return "Po";
        }
    }

    private void showDatePicker(String title, final TextView textView)
    {
        String text = textView.getText().toString();
        int day;
        int month;
        int year;
        if (text.contains("."))
        {
            String[] time = text.split("\\.");
            day = Integer.parseInt(time[0]);
            month = Integer.parseInt(time[1]);
            year = Integer.parseInt(time[2]);
        }
        else
        {
            Calendar mcurrentTime = Calendar.getInstance();
            day = mcurrentTime.get(Calendar.DAY_OF_MONTH);
            month = mcurrentTime.get(Calendar.MONTH);
            year = mcurrentTime.get(Calendar.YEAR);
        }
        DatePickerDialog mTimePicker;
        mTimePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                String day = Integer.toString(d);
                String month = Integer.toString(m + 1);
                String year = Integer.toString(y);
                if (day.length() < 2)
                    day = "0" + day;
                if (month.length() < 2)
                    month = "0" + month;
                textView.setText( day + "." + month + "." + year);
                Calendar myCal = Calendar.getInstance();
                myCal.set(Calendar.YEAR, y);
                myCal.set(Calendar.MONTH, m);
                myCal.set(Calendar.DAY_OF_MONTH, d);
                getLessons(getDayOfWeek(myCal));
            }
        }, year, month - 1, day);
        mTimePicker.setTitle(title);
        mTimePicker.show();
    }
}

