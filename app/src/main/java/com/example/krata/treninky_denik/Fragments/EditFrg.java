package com.example.krata.treninky_denik.Fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.krata.treninky_denik.Callbacks.GetLessonCallback;
import com.example.krata.treninky_denik.Callbacks.GetPlayersCallBack;
import com.example.krata.treninky_denik.Data;
import com.example.krata.treninky_denik.Lesson;
import com.example.krata.treninky_denik.R;
import com.example.krata.treninky_denik.ServerRequests;
import com.example.krata.treninky_denik.TimePickerFragment;
import com.example.krata.treninky_denik.UserLocalStore;

import android.text.format.DateFormat;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class EditFrg extends Fragment implements View.OnClickListener{

    UserLocalStore userLocalStore;
    private String[] players;

    TextView lessonStart, lessonEnd, periodStart, periodEnd;
    RadioGroup dayGroup;
    Button btnSave;
    LinearLayout playerLayout;
    int playerCount = 0;
    String crossRecognizer = "938271";
    ArrayList<TextView> crosses = new ArrayList<>();
    ArrayList<String> selectedPlayers = new ArrayList<>();
    String dayOfWeek = "";
    View v;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frg_edit, container, false);
        this.v = v;
        playerLayout = (LinearLayout)v.findViewById(R.id.players_layout);
        dayGroup = (RadioGroup)v.findViewById(R.id.dayOfWeek_group);
        btnSave = (Button)v.findViewById(R.id.btn_saveLesson);
        btnSave.setOnClickListener(this);
        final Spinner s = (Spinner) v.findViewById(R.id.spinner_players);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                final String selectedItem = parent.getItemAtPosition(position).toString();
                if (!selectedItem.equals("Výběr hráče"))
                {
                    Toast.makeText(getContext(), selectedItem, Toast.LENGTH_LONG).show();

                    LinearLayout ll = new LinearLayout(getContext());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    ll.setLayoutParams(params);

                    ll.setOrientation(LinearLayout.HORIZONTAL);
                    ll.setGravity(Gravity.CENTER_HORIZONTAL);
                    ll.setId(playerCount);

                    TextView tv = new TextView(getContext());
                    tv.setText(selectedItem);
                    tv.setTextSize(25);
                    tv.setTextColor(Color.BLACK);
                    tv.layout(0, 5, 0, 10);
                    tv.setGravity(Gravity.LEFT);

                    final TextView cross = new TextView(getContext());
                    cross.setText(selectedItem);
                    cross.setTextSize(30);
                    cross.setTextColor(Color.RED);
                    cross.setText("X");
                    cross.layout(0, 5, 0, 10);
                    cross.setGravity(Gravity.RIGHT);
                    cross.setId(Integer.parseInt(crossRecognizer + playerCount));
                    cross.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String id = Integer.toString(v.getId());
                            int curPlayer = Integer.parseInt(id.substring(crossRecognizer.length(), id.length()));
                            playerLayout.removeViewAt(curPlayer);
                            crosses.remove(curPlayer);
                            selectedPlayers.remove(curPlayer);
                            playerCount--;
                            for (int i = 0; i < crosses.size(); i++)
                            {
                                crosses.get(i).setId(Integer.parseInt(crossRecognizer + i));
                            }
                        }
                    });

                    selectedPlayers.add(selectedItem);

                    ll.addView(tv);
                    ll.addView(cross);
                    crosses.add(cross);
                    playerLayout.addView(ll);
                    s.setSelection(0);
                    playerCount++;
                }
            } // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
        TextView hraciView = (TextView)v.findViewById(R.id.hraci_view);

        getPlayers(s, hraciView);
        lessonStart = (TextView)v.findViewById(R.id.lesson_start);
        lessonStart.setOnClickListener(this);

        lessonEnd = (TextView)v.findViewById(R.id.lesson_end);
        lessonEnd.setOnClickListener(this);


        periodStart = (TextView)v.findViewById(R.id.period_start);
        periodStart.setOnClickListener(this);

        periodEnd = (TextView)v.findViewById(R.id.period_end);
        periodEnd.setOnClickListener(this);

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
            case R.id.lesson_start:
                showTimePicker("Lekce začne v", lessonStart);
                break;
            case R.id.lesson_end:
                showTimePicker("Lekce končí v", lessonEnd);
                break;
            case R.id.period_start:
                showDatePicker("Lekce začnou od", periodStart);
                break;
            case R.id.period_end:
                showDatePicker("Lekce zkončí v", periodEnd);
                break;
            case R.id.btn_saveLesson:
                // get selected radio button from radioGroup
                int selectedId = dayGroup.getCheckedRadioButtonId();
                // find the radiobutton by returned id
                RadioButton rBtn = this.v.findViewById(selectedId);
                dayOfWeek = rBtn.getText().toString();

                String startTime = lessonStart.getText().toString();
                String endTime = lessonEnd.getText().toString();
                String trainer = userLocalStore.getLoggedUser().getNick();
                Lesson lesson = new Lesson(dayOfWeek, startTime, endTime, trainer, selectedPlayers.toArray(new String[selectedPlayers.size()]));
                Data.lessons.add(lesson);
                storeLesson(lesson);
                break;
        }
    }

    private void storeLesson(Lesson lesson)
    {
        ServerRequests serverRequests = new ServerRequests(getContext());
        serverRequests.storeLessonInBackground(lesson, new GetLessonCallback() {
            @Override
            public void done(Lesson lesson) {
                Toast.makeText(getContext(), "Lekce uložena", Toast.LENGTH_SHORT).show();
            }
        });
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
            }
        }, year, month - 1, day);
        mTimePicker.setTitle(title);
        mTimePicker.show();
    }

    private void showTimePicker(String title, final TextView textView)
    {
        String text = textView.getText().toString();
        int hour;
        int minute;
        if (text.contains(":"))
        {
            String[] time = text.split(":");
            hour = Integer.parseInt(time[0]);
            minute = Integer.parseInt(time[1]);
        }
        else
        {
            Calendar mcurrentTime = Calendar.getInstance();
            hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            minute = mcurrentTime.get(Calendar.MINUTE);
        }
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String hour = Integer.toString(selectedHour);
                String minute = Integer.toString(selectedMinute);
                if (hour.length() < 2)
                    hour = "0" + hour;
                if (minute.length() < 2)
                    minute = "0" + minute;
                textView.setText( hour + ":" + minute);
            }
        }, hour, minute, DateFormat.is24HourFormat(getActivity()));//Yes 24 hour time
        mTimePicker.setTitle(title);
        mTimePicker.show();
    }
}

