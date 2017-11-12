package com.ginkomix.calendar;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    MaterialCalendarView calendarView;
    FloatingActionButton fab;
    ListView listEvent;
    ArrayList<Event> todayList;
    EventAdapter eventAdapter;
    TextView time;
    AlertDialog dialog;
    EditText text;
    public static DBHelper dbHelper;

    public static Map map = new HashMap<String, Integer>(){{
        put("Выбрать тип", R.drawable.ic_announcement_black_24dp);
        put("Шоппинг", R.drawable.ic_add_shopping_cart_black_24dp);
        put("Путешествие", R.drawable.ic_airplanemode_active_black_24dp);
        put("Тренировка", R.drawable.ic_fitness_center_black_24dp);
        put("День рождения", R.drawable.ic_local_pizza_black_24dp);
        put("Работа", R.drawable.ic_work_black_24dp);
        put("Праздник", R.drawable.ic_filter_vintage_black_24dp);
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DBHelper(this);
        todayList = new ArrayList<>();
        eventAdapter = new EventAdapter(this, todayList);
        listEvent = findViewById(R.id.EventList);
        listEvent.setAdapter(eventAdapter);
        calendarView = findViewById(R.id.calendarView);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(viewClickListener);
        calendarView.setSelectedDate(Calendar.getInstance().getTime());
        calendarView.addDecorators(new WeekendsDecorator(), new TodayDecorator(), new EventsDecorator());
        calendarView.setOnDateChangedListener(onDateSelectedListener);
        listEvent.setOnItemClickListener(itemClickListener);
    }

    private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int i, int i1) {
            Calendar c = Calendar.getInstance();
            final int year = c.get(Calendar.YEAR);
            final int month = c.get(Calendar.MONTH);
            final int day = c.get(Calendar.DAY_OF_MONTH);
            final int hour = c.get(Calendar.HOUR_OF_DAY);
            final int minute = c.get(Calendar.MINUTE);
            CalendarDay today = calendarView.getSelectedDate();
            if(i == 0 && i1 == 0)
                time.setText("Весь день");
            else if(year==today.getYear() && month==today.getMonth()&&day==today.getDay()){
                if(i<hour || (i1<minute&&i==hour)){
                    Toast.makeText(MainActivity.this, "Прошлое уже не вернуть", Toast.LENGTH_SHORT).show();
                    time.performClick();
                }
                else time.setText(i + ":" + i1);
            }
            else
                time.setText(i + ":" + i1);
        }
    };

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            final Event event = todayList.get(position);
            final View editView = getLayoutInflater().inflate(R.layout.add_dialog, null);
            builder.setTitle("Изменить событие");
            final Spinner spinner = editView.findViewById(R.id.dialog_img);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,
                    android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.eventNames));
            spinner.setAdapter(adapter);
            ArrayList<String> arr = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.eventNames)));
            spinner.setSelection(arr.indexOf(event.getTitle()));
            text = editView.findViewById(R.id.dialog_text);
            text.setText(event.getEvent());
            time = editView.findViewById(R.id.dialog_time);
            if(event.getHours()==0 && event.getMinutes()==0){
                time.setText("Весь день");
            }
            else {
                String hours;
                String minutes;
                if (event.getHours() < 10)
                    hours = "0" + String.valueOf(event.getHours());
                else hours = String.valueOf(event.getHours());
                if (event.getMinutes() < 10)
                    minutes = "0" + String.valueOf(event.getMinutes());
                else minutes = String.valueOf(event.getMinutes());
                time.setText(hours + ":" + minutes);
            }
            time.setOnClickListener(viewClickListener);
            final CheckBox check = editView.findViewById(R.id.chEveryYear);
            check.setChecked(event.getCalendarDay().getYear() == 1);
            builder.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Event newEvent = new Event();
                    newEvent.setId(event.getId());
                    newEvent.setEvent(text.getText().toString());
                    newEvent.setImage(spinner.getSelectedItem().toString());
                    int year = event.getCalendarDay().getYear();
                    if(check.isChecked()) year = 1;
                    newEvent.setCalendarDay(CalendarDay.from(year, event.getCalendarDay().getMonth(), event.getCalendarDay().getDay()));
                    int hour = 0;
                    int min = 0;
                    if(!time.getText().equals("Весь день"))
                    {
                        String[] str = time.getText().toString().split(":");
                        hour = Integer.valueOf(str[0]);
                        min = Integer.valueOf(str[1]);
                    }
                    newEvent.setHours(hour);
                    newEvent.setMinutes(min);
                    dbHelper.deleteEventByID(event.getId());
                    dbHelper.addEvent(newEvent);
                    onDateSelectedListener.onDateSelected(calendarView, calendarView.getSelectedDate(), true);
                    eventAdapter.notifyDataSetChanged();
                }
            });
            builder.setNegativeButton("Удалить", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dbHelper.deleteEventByID(todayList.get(position).getId());
                    onDateSelectedListener.onDateSelected(calendarView, calendarView.getSelectedDate(), true);
                    eventAdapter.notifyDataSetChanged();
                    calendarView.invalidateDecorators();
                }
            });
            builder.setNeutralButton("Отмена", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.setView(editView);
            dialog = builder.create();
            dialog.show();
        }
    };

    private View.OnClickListener viewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.dialog_time:
                    Calendar c = Calendar.getInstance();
                    final int hour = c.get(Calendar.HOUR_OF_DAY);
                    final int minute = c.get(Calendar.MINUTE);
                    TimePickerDialog timeDialog = new TimePickerDialog(MainActivity.this,
                            timeSetListener, hour, minute, DateFormat.is24HourFormat(MainActivity.this));
                    timeDialog.show();
                    break;
                case R.id.fab:
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    final View mView = getLayoutInflater().inflate(R.layout.add_dialog, null);
                    builder.setTitle("Добавить событие");
                    final Spinner spinner = mView.findViewById(R.id.dialog_img);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,
                            android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.eventNames));
                    spinner.setSelection(0);
                    spinner.setAdapter(adapter);

                    text = mView.findViewById(R.id.dialog_text);

                    time = mView.findViewById(R.id.dialog_time);
                    time.setOnClickListener(viewClickListener);

                    builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

                    builder.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            int hour = 0;
                            int min = 0;
                            if(!time.getText().equals("Весь день"))
                            {
                                String[] str = time.getText().toString().split(":");
                                hour = Integer.valueOf(str[0]);
                                min = Integer.valueOf(str[1]);
                            }
                            CheckBox checkYear = mView.findViewById(R.id.chEveryYear);
                            CalendarDay calendarDay = calendarView.getSelectedDate();
                            if(checkYear.isChecked())
                                calendarDay = CalendarDay.from(1, calendarDay.getMonth(), calendarDay.getDay());

                            Event event = new Event(spinner.getSelectedItem().toString(), text.getText().toString(),
                                    hour, min, calendarDay);
                            dbHelper.addEvent(event);
                            onDateSelectedListener.onDateSelected(calendarView, calendarView.getSelectedDate(), true);
                            eventAdapter.notifyDataSetChanged();
                            calendarView.invalidateDecorators();
                        }
                    });
                    builder.setView(mView);
                    dialog = builder.create();
                    dialog.show();
                    break;
            }
        }
    };


    private OnDateSelectedListener onDateSelectedListener = new OnDateSelectedListener() {
        @Override
        public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
            CalendarDay today = CalendarDay.today();
            todayList.clear();
            for (Event event : dbHelper.getAllEvents()) {
                if(event.getCalendarDay().equals(date) ||
                        (event.getCalendarDay().getYear()==1&&date.getMonth()==event.getCalendarDay().getMonth()&&
                                date.getDay()==event.getCalendarDay().getDay()))
                    todayList.add(event);
            }
            eventAdapter.notifyDataSetChanged();
            if(date.isBefore(today)){
                fab.setVisibility(View.INVISIBLE);
            }
            else {
                fab.setVisibility(View.VISIBLE);
            }
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.mMonth:
                calendarView.state().edit().setCalendarDisplayMode(CalendarMode.MONTHS).commit();
                calendarView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        0, 3.0f));
                (findViewById(R.id.rl)).setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        0, 1.0f));
                item.setChecked(true);
                break;
            case R.id.mWeek:
                calendarView.state().edit().setCalendarDisplayMode(CalendarMode.WEEKS).commit();
                calendarView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        0, 1.0f));
                (findViewById(R.id.rl)).setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        0, 3.0f));
                item.setChecked(true);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
