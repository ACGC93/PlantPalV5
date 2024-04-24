package com.example.myapplication;


import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Calendar;

public class NotificationScreen extends AppCompatActivity {

    Button notifTimeButton;
    Button createNotifButton;
    Button toWeatherButton;
    TextView currentNotifTime;
    int hour, minute = -1;

    ArrayList<String> notifications;
    ArrayAdapter<String> listAdapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notifications_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        //get references to screen elements
        notifTimeButton = findViewById(R.id.notifTimePicker);
        createNotifButton = findViewById(R.id.createNotif);
        toWeatherButton = findViewById(R.id.toWeatherButton);

        currentNotifTime = findViewById(R.id.notifTimeText);

        listView = findViewById(R.id.notifsList);

        createNotifButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newNotifInList(v);
            }
        });

        toWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), MainActivity.class));
            }
        });

        notifications = new ArrayList<>();
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notifications);
        listView.setAdapter(listAdapter);
        setUpListViewListener();

    }

    private void setUpListViewListener() {
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Removed notification", Toast.LENGTH_LONG).show();

                notifications.remove(position);
                listAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    private void newNotifInList(View v){
        EditText inputField = findViewById(R.id.NotifNameInput);

        if (minute == -1) {
            Toast.makeText(this, "Input a time", Toast.LENGTH_SHORT).show();
            return;
        }

        String text;
        String textTime;

        //display single-digit minute values correctly
        if (minute > 9) {
            textTime = " at " + String.valueOf(hour) + ':' + String.valueOf(minute);
        } else {
            textTime = " at " + String.valueOf(hour) + ":0" + String.valueOf(minute);
        }

        text = inputField.getText().toString();

        if (text.isEmpty()) {
            Toast.makeText(this, "Plant input field is empty", Toast.LENGTH_LONG).show();
        } else {
            listAdapter.add(text + textTime);
            setNotificationAlarm(hour, minute, text);
            inputField.setText("");
        }
    }

    public void popNotifTimeSettings(View view){
        //create a listener that takes the input time out of the prompt
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker tp, int hr, int min) {
                //when the user clicks OK on the phone's time selector, pass the values into the class variables hour and minute
                hour = hr;
                minute = min;
                String tempText;

                if (minute > 9) {
                    tempText = String.valueOf(hour) + ':' + String.valueOf(minute);
                } else {
                    tempText = String.valueOf(hour) + ":0" + String.valueOf(minute);
                }
                currentNotifTime.setText(tempText);
            }
        };

        //create the prompt for the user to input a time
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, android.R.style.Theme_DeviceDefault_Dialog_Alert, onTimeSetListener, hour, minute, true);

        timePickerDialog.setTitle("Configure Notification");
        timePickerDialog.show();
    }

    private void setNotificationAlarm(int hour, int minute, String notifText) {
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent notifIntent = new Intent(this, NotificationBroadcast.class);

        //avoiding NullPointerException,
        //pass notification input text into the extra data of the notification intent
        //this can be pulled by NotificationBroadcast.class to supply the notification with this text.
        if (notifText != null) {
            notifIntent.putExtra("notifText", notifText); //supply extra text with the intent. can be used to pass along the notification body text
        }

        //create a calendar instance to pass into the AlarmManager.
        //it stores the time at which the notif should fire
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);

        //create the intent to deliver the notification
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notifIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

}