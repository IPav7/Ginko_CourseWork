package com.ginkomix.calendar;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.style.BackgroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;


public class EventsDecorator implements DayViewDecorator {
    @Override
    public boolean shouldDecorate(CalendarDay day) {
        for (Event event :
                MainActivity.dbHelper.getAllEvents()) {
            if (day.equals(event.getCalendarDay()))
                return true;
            else if(event.getCalendarDay().getYear()==1&&day.getMonth()==event.getCalendarDay().getMonth()&&day.getDay()==event.getCalendarDay().getDay())
                return true;
        }
        return false;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new StyleSpan(Typeface.BOLD));
        view.addSpan(new RelativeSizeSpan(1.2f));
    }
}
