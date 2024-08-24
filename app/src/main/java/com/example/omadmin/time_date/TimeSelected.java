package com.example.omadmin.time_date;

public class TimeSelected {

    private String time_from;
    private String time_to;

    public  static  TimeSelected instance;

    public static TimeSelected getInstance(){
        if(instance==null){
            instance=new TimeSelected();
        }
        return instance;
    }


    public String getTime_from() {
        return time_from;
    }

    public void setTime_from(String time_from) {
        this.time_from = time_from;
    }

    public String getTime_to() {
        return time_to;
    }

    public void setTime_to(String time_to) {
        this.time_to = time_to;
    }
}
