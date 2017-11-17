/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.endlessmaze.game.Controls;

import com.badlogic.gdx.utils.StringBuilder;


/**
 *
 * @author Corey
 */
public class Timer {
    long StartTime = 0;
    long Accumulated = 0;
    long LastTime = 0;
    long CurrTime;
    boolean Running = false;
    static StringBuilder sb = new StringBuilder(10);
    public void Restart()
    {
        StartTime = 0;
        Accumulated = 0;
        LastTime = 0;
        Running = false;
    }
    public void Start()
    {
        if(!Running)
        {
            StartTime = System.currentTimeMillis();
            Running = true;
        }
    }
    public void Pause()
    {
        if(!Running) return;
        if(StartTime>0)
        {
            Accumulated = (System.currentTimeMillis()-StartTime) + Accumulated;
        }
        else
        {
            Accumulated = 0;
        }
        Running = false;
    }
    
    public long GetTimeMs()
    {
        if(Running && StartTime>0)
        {
            return (System.currentTimeMillis()-StartTime) + Accumulated;
        }
        else if(!Running)
        {
            return Accumulated;
        }
        else
        {
            return 0;
        }
    }
    
    public StringBuilder GetElapsedTime()
    {
        if(Running && StartTime>0)
        {
            CurrTime = (System.currentTimeMillis()-StartTime) + Accumulated;
            if(CurrTime-LastTime>=100) LastTime = CurrTime;
            return GetTimeFormat(LastTime);
        }
        else if(!Running)
        {
            return GetTimeFormat(Accumulated);
        }
        else        
        {
            return GetTimeFormat(0);
        }
        
    }
    
    public static StringBuilder GetTimeFormat(long timeMs) {
        int mins = (int) timeMs / 60000;
        int seconds = (int) (timeMs / 1000)%60;
        int ms = (int) (timeMs%1000)/100;
        sb.setLength(0);
        sb.append(mins, 2, '0');
        sb.append(':');
        sb.append(seconds, 2, '0');
        sb.append(':');
        sb.append(ms);

        return sb;
    }
    public static String PadLeft(String s, int c)
    {
        if(s.length()>c) return s.substring(0, 1);
        else if(s.length()==c)  return s;
        while(s.length()!=c)
        {
            s = '0'+s;
        }
        return s;
    }
    
    
}
