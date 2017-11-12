package com.ginkomix.calendar;

import com.prolificinteractive.materialcalendarview.CalendarDay;

class Event {
    private int id;
    private String image;
    private String title;
    private String event;
    private int hours;
    private int minutes;
    private CalendarDay calendarDay;

    Event() {
    }

    Event(String image, String event, int hours, int minutes, CalendarDay calendarDay) {
        this.image = image;
        title = image;
        if(event.isEmpty())
            this.event = "Миссия";
        else
        this.event = event;
        this.hours = hours;
        this.minutes = minutes;
        this.calendarDay = calendarDay;
    }

    String getImage() {
        return image;
    }

    void setImage(String image) {
        this.image = image;
    }

    String getEvent() {
        return event;
    }

    void setEvent(String event) {
        this.event = event;
    }

    int getHours() {
        return hours;
    }

    void setHours(int hours) {
        this.hours = hours;
    }

    int getMinutes() {
        return minutes;
    }

    void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    CalendarDay getCalendarDay() {
        return calendarDay;
    }

    void setCalendarDay(CalendarDay calendarDay) {
        this.calendarDay = calendarDay;
    }

    int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    String getTitle() {
        return title;
    }

    void setTitle(String title) {
        this.title = title;
    }

}
