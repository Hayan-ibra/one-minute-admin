package com.example.omadmin.time_date;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.omadmin.uploadingfood.RestaurantProfileCreation;

import java.util.Calendar;

public  class TimePickerFragmentForStoress extends DialogFragment  implements TimePickerDialog.OnTimeSetListener {


    public timeCallback callback;


    public TimePickerFragmentForStoress(timeCallback callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(),this,hour,minute, true);

    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        TimeSelected time=TimeSelected.getInstance();


       // RestaurantProfileCreation.time_from_int=i+":"+i1;
        String time_from;




        int result;
        if(i>12 && i<24){
            result=i-12;

            String iStr=String.valueOf(result);
            String i1Str=String.valueOf(i1);

            if (iStr.length() == 1){
                iStr="0"+iStr;
            }
            if (i1Str.length() == 1){
                i1Str="0"+i1Str;
            }
            //RestaurantProfileCreation.tv_from.setText(iStr+":"+i1Str+" Pm");
            //time.setTime_from(iStr+":"+i1Str+" Pm");


            time_from=iStr+":"+i1Str+" Pm";



        } else if (i==12){
            result=12;
            String i1Str=String.valueOf(i1);
            if (i1Str.length() == 1){
                i1Str="0"+i1Str;
            }
            //RestaurantProfileCreation.tv_from.setText(result+":"+i1Str+" Pm");

            //time.setTime_from(result+":"+i1Str+" Pm");
            time_from=result+":"+i1Str+" Pm";
        }else {
            String iStr=String.valueOf(i);
            String i1Str=String.valueOf(i1);

            if (iStr.length() == 1){
                iStr="0"+iStr;
            }
            if (i1Str.length() == 1){
                i1Str="0"+i1Str;
            }
           // RestaurantProfileCreation.tv_from.setText(iStr+":"+i1Str+" Am");
           // time.setTime_from(iStr+":"+i1Str+" Am");

            time_from=iStr+":"+i1Str+" Am";


        }

        callback.onTimeSetFrom(time_from);

    }

    public interface timeCallback{
        public void onTimeSetFrom(String timeFrom);

    }






}
