package com.example.socialmediaapp;

import android.util.TimeUtils;

public class timeutils {
    private long sec_mil=1000;
    private long min_mil=60*sec_mil;
    private long hour_mil=60*min_mil;
    private long days_mil=24*hour_mil;
    public String getTime(long value)
    {
        long currentTime=System.currentTimeMillis();
        if(value>currentTime || value<=0)
        {
            return null;
        }
        long diff=currentTime-value;
        if(diff<min_mil)
           {
            return "just now";
           }
        else if(diff<2*min_mil)
        {
            return "a minute ago";
        }
        else if(diff<50*min_mil)
        {
            return Long.toString(diff/min_mil)+" "+"minutes ago";
        }
        else if(diff<24*hour_mil)
        {
            int hour=(int)((currentTime-value)/(1000*60*60))%24;
            if(hour==0)
            {
                return "an hour ago";
            }
            return String.format("%d hour ago", hour);
        }
        else if(diff<48*hour_mil)
        {
            return "yesterday";
        }
        return Long.toString(diff/days_mil)+" days ago";
    }
}
