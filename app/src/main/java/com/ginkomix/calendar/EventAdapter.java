package com.ginkomix.calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class EventAdapter extends BaseAdapter {
    private Context ctx;
    private LayoutInflater lInflater;
    private ArrayList<Event> objects;
    public EventAdapter(Context context, ArrayList<Event> events){
        ctx = context;
        objects = events;
        lInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int i) {
        return objects.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view1, ViewGroup viewGroup) {
        View view = view1;
        if(view==null)
            view = lInflater.inflate(R.layout.item, viewGroup, false);
        Event event = (Event)getItem(i);
        ((ImageView)view.findViewById(R.id.item_img)).setImageResource((int)MainActivity.map.get(event.getImage()));
        ((TextView)view.findViewById(R.id.item_event)).setText(event.getEvent());
        String hours;
        String minutes;
        if(event.getHours()==0 && event.getMinutes()==0){
            ((TextView)view.findViewById(R.id.item_time)).setText("Весь день");
        }
        else {
            if (event.getHours() < 10)
                hours = "0" + String.valueOf(event.getHours());
            else hours = String.valueOf(event.getHours());
            if (event.getMinutes() < 10)
                minutes = "0" + String.valueOf(event.getMinutes());
            else minutes = String.valueOf(event.getMinutes());
            ((TextView) view.findViewById(R.id.item_time)).setText(hours + ":" + minutes);
        }
        return view;
    }

}
