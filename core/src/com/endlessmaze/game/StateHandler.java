/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.endlessmaze.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.endlessmaze.game.Maze.MazeLevelInfo;
import com.endlessmaze.game.Maze.MazeUtilities;
import java.util.ArrayList;

/**
 *
 * @author Corey
 */
public class StateHandler {
    public static final long StartSeed = 69500;
    
    public ArrayList<MazeLevelInfo> StoredSeeds = new ArrayList<MazeLevelInfo>();
    public long TheSeed = StartSeed;
    public int Level = 1;
    public int ResetLength = 10;
    public int SizeToComplete = 20;
    public Color CurrentColor = new Color(.2f,.2f,1f,1f);
    private Preferences theSave;
    public StateHandler()
    {
        theSave = Gdx.app.getPreferences("Save2");

        StoredSeeds.clear();
        if (theSave.contains("Level2")) {
            Level = theSave.getInteger("Level2");
            TheSeed = theSave.getLong("Seed");
            SizeToComplete = theSave.getInteger("CompleteLength");
            ResetLength = theSave.getInteger("Length");
            if(theSave.contains("ColorRed"))
            {
                float r = theSave.getFloat("ColorRed");
                float g = theSave.getFloat("ColorGreen");
                float b = theSave.getFloat("ColorBlue");
                CurrentColor = new Color(r,g,b,1f);
            }
            else
            {
                CurrentColor = new Color(.2f,.2f,1f,1f);
            }
            int l = 0;
            while (theSave.contains("L" + Integer.toString(++l))) {
                Long seed = theSave.getLong("L" + Integer.toString(l));
                MazeLevelInfo li = new MazeLevelInfo();
                if (theSave.contains("DF" + Integer.toString(l))) {
                    li.DiamondFound = theSave.getBoolean("DF" + Integer.toString(l));
                }
                if(theSave.contains("T"+ Integer.toString(l)))
                {
                    li.Time = theSave.getLong("T"+ Integer.toString(l));
                }
                if(theSave.contains("GL" + Integer.toString(l))){
                    li.Length = theSave.getInteger("GL" + Integer.toString(l));
                }
                if(theSave.contains("TL" + Integer.toString(l))){
                    li.TimeLen = theSave.getLong("TL" + Integer.toString(l));
                }
                
                if(theSave.contains(MazeUtilities.MODE_TO_STR[MazeUtilities.ONE_MODE] + Integer.toString(l))){
                    li.OneTime = theSave.getInteger(MazeUtilities.MODE_TO_STR[MazeUtilities.ONE_MODE] + Integer.toString(l));
                }
                if(theSave.contains(MazeUtilities.MODE_TO_STR[MazeUtilities.TWO_MODE] + Integer.toString(l))){
                    li.TwoTime = theSave.getInteger(MazeUtilities.MODE_TO_STR[MazeUtilities.TWO_MODE] + Integer.toString(l));
                }
                if(theSave.contains(MazeUtilities.MODE_TO_STR[MazeUtilities.FIVE_MODE] + Integer.toString(l))){
                    li.FiveTime = theSave.getInteger(MazeUtilities.MODE_TO_STR[MazeUtilities.FIVE_MODE] + Integer.toString(l));
                }
                li.Level = l;
                li.Seed = seed;               
                StoredSeeds.add(li);
            }
        }
    }
    
    public void NextLevel()
    {
        Level++;
        SizeToComplete+=20;
    }
        
    public void SaveLevel(int length)
    {
        theSave.putInteger("Level2", Level);
        MazeLevelInfo mli = new MazeLevelInfo(Level, TheSeed, 0,0,0,0,0,0, false);
        if(!theSave.contains("L"+Integer.toString(Level)) && !StoredSeeds.contains(mli))
        {               
            theSave.putLong("L"+Integer.toString(Level), TheSeed);
            StoredSeeds.add(mli);
        }
        
        theSave.putInteger("Length",length);
        theSave.putInteger("CompleteLength", SizeToComplete);
        theSave.flush();
    }
    public void SaveSeed()
    {
        theSave.putLong("Seed", TheSeed);
        theSave.flush();
        System.out.println(Level + " " + TheSeed);
        
    }
    public void SaveNewTime(long time)
    {
        if(theSave.contains("T"+ Integer.toString(Level)))
        {
            long oldTime = theSave.getLong("T"+ Integer.toString(Level));
            //System.out.println(oldTime);
            if(oldTime==0 || time<oldTime)
            {
                SaveTime(time);
            }
        }
        else
        {
            SaveTime(time);
        }
    }
    
    public void SaveNewTimeLength(long time, int length)
    {
        if(theSave.contains("GL"+ Integer.toString(Level)))
        {
            int oldLength = theSave.getInteger("GL"+ Integer.toString(Level));
            //System.out.println(oldTime);            
            if(oldLength==0 || length>oldLength)
            {
                //We are a longer snake
                SaveTimeLength(time,length);
            }
            else if(length==oldLength)
            {
                long oldTime = theSave.getLong("TL"+ Integer.toString(Level));
                if(oldTime==0 || time<oldTime)
                {
                    //We are the same snake, with better time
                    SaveTimeLength(time,length);
                }
                
            }
        }
        else
        {
            SaveTimeLength(time,length);
        }
    }
    
    public void SaveANewTimedLength(int timer, int length)
    {
        if(theSave.contains(MazeUtilities.MODE_TO_STR[timer]+ Integer.toString(Level)))
        {
            int oldLength = theSave.getInteger(MazeUtilities.MODE_TO_STR[timer]+ Integer.toString(Level));         
            if(oldLength==0 || length>oldLength)
            {
                //We are a longer snake
                SaveATimed(timer,length);
            }
        }
        else
        {
             SaveATimed(timer,length);
        }
    }

    public void SaveTime(long time)
    {
        theSave.putLong("T"+ Integer.toString(Level),time);
        theSave.flush();
        int i = StoredSeeds.indexOf(new MazeLevelInfo(Level,TheSeed,0,0,0,0,0,0,true));
        if(i>=0)
        {
            StoredSeeds.get(i).Time = time;
        }
    }
    
    public void SaveTimeLength(long time, int length)
    {
        theSave.putLong("TL"+ Integer.toString(Level),time);
        theSave.putInteger("GL"+ Integer.toString(Level),length);
        theSave.flush();
        int i = StoredSeeds.indexOf(new MazeLevelInfo(Level,TheSeed,0,0,0,0,0,0,true));
        if(i>=0)
        {
            StoredSeeds.get(i).TimeLen = time;
            StoredSeeds.get(i).Length = length;
        }
    }
    
    
    public void SaveATimed(int timeSave, int length)
    {
        theSave.putInteger(MazeUtilities.MODE_TO_STR[timeSave]+ Integer.toString(Level),length);
        theSave.flush();
        int i = StoredSeeds.indexOf(new MazeLevelInfo(Level,TheSeed,0,0,0,0,0,0,true));
        if(i>=0)
        {
            switch(timeSave)
            {
                case MazeUtilities.ONE_MODE: StoredSeeds.get(i).OneTime = length; break;
                case MazeUtilities.TWO_MODE: StoredSeeds.get(i).TwoTime = length; break;
                case MazeUtilities.FIVE_MODE: StoredSeeds.get(i).FiveTime = length; break;
            }
            
        }
    }
    /*
    public void SaveTwoTime(int length)
    {
        theSave.putInteger("TWOLEN"+ Integer.toString(Level),length);
        theSave.flush();
        int i = StoredSeeds.indexOf(new MazeLevelInfo(Level,TheSeed,0,0,0,0,0,0,true));
        if(i>=0)
        {
            StoredSeeds.get(i).TwoTime = length;
        }
    }
    
    public void SaveFiveTime(int length)
    {
        theSave.putInteger("FIVELEN"+ Integer.toString(Level),length);
        theSave.flush();
        int i = StoredSeeds.indexOf(new MazeLevelInfo(Level,TheSeed,0,0,0,0,0,0,true));
        if(i>=0)
        {
            StoredSeeds.get(i).FiveTime = length;
        }
    }
    */
    
    public void SetDiamondFound()
    {
        theSave.putBoolean("DF"+Integer.toString(Level), true);
        theSave.flush();
        for(MazeLevelInfo ml : StoredSeeds)
        {
            if(ml.Seed == TheSeed)
            {
                ml.DiamondFound = true;
                break;
            }
        }
    }    
    
    public boolean GetIsDiamondFound()
    {
        if(theSave.contains("DF"+Integer.toString(Level)))
        {
            return theSave.getBoolean("DF"+Integer.toString(Level));
        }
        else
        {
            return false;
        }
    }
    
    public void SaveColor(Color c)
    {
        CurrentColor = c;
        theSave.putFloat("ColorRed",c.r);
        theSave.putFloat("ColorGreen",c.g);
        theSave.putFloat("ColorBlue",c.b);
        theSave.flush();
        
    }
    

    
    
}
