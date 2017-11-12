package com.ginkomix.calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.List;


public class DBHelper extends SQLiteOpenHelper {

    private final static String DB_NAME = "EventsDB";
    private final static String TABLE_NAME = "EventsDB";
    private final static int VERSION = 1;
    private static final String KEY_ID = "id";
    private static final String image = "image";
    private static final String year = "year";
    private static final String month = "month";
    private static final String day = "day";
    private static final String hours = "hours";
    private static final String minutes = "minutes";
    private static final String title = "title";
    private static final String text = "text";


    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    public void addEvent(Event event){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(image, event.getImage());
        values.put(year, event.getCalendarDay().getYear());
        values.put(month, event.getCalendarDay().getMonth());
        values.put(day, event.getCalendarDay().getDay());
        values.put(hours, event.getHours());
        values.put(minutes, event.getMinutes());
        values.put(title, event.getTitle());
        values.put(text, event.getEvent());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void deleteEventByID(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_ID + " = ?", new String[]{ String.valueOf(id)});
        db.close();
    }

    public List<Event> getAllEvents(){
        List<Event> list = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do{
                Event event = new Event();
                event.setId(cursor.getInt(0));
                event.setImage(cursor.getString(1));
                event.setTitle(cursor.getString(2));
                event.setEvent(cursor.getString(3));
                event.setCalendarDay(CalendarDay.from(cursor.getInt(4),
                        cursor.getInt(5), cursor.getInt(6)));
                event.setHours(cursor.getInt(7));
                event.setMinutes(cursor.getInt(8));
                list.add(event);
            }while (cursor.moveToNext());
        }
        return list;
    }

    public int getEventsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        return cursor.getCount();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + TABLE_NAME + " (" +
                                KEY_ID + " INTEGER PRIMARY KEY," +
                                image + " text,"+
                                title + " text,"+
                                text + " text,"+
                                year + " integer,"+
                                month + " integer,"+
                                day + " integer,"+
                                hours + " integer,"+
                                minutes + " integer);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
