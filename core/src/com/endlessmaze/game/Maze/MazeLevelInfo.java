/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.endlessmaze.game.Maze;

import com.badlogic.gdx.utils.reflect.ClassReflection;

/**
 *
 * @author Corey
 */
public class MazeLevelInfo
{
    public boolean DiamondFound = false;
    public int Level=1;
    public long Seed = 65900;
    public long Time = 0;
    public long TimeLen = 0;
    public int Length = 0;
    public int OneTime = 0;
    public int TwoTime = 0;
    public int FiveTime = 0;
    
    public MazeLevelInfo(){}
    public MazeLevelInfo(int level, long seed, long time, long timeLen, int length, int oTime, int tTime, int fTime, boolean dfound)
    {
        Level = level;
        Seed = seed;
        Time = time;
        TimeLen = timeLen;
        Length = length;
        DiamondFound = dfound;
        OneTime = oTime;
        TwoTime = tTime;
        FiveTime = fTime;
    }
    @Override
    public boolean equals(Object o)
    {
        if(o==null) return false;
        
        try{
        //if(!(MazeLink.class).isAssignableFrom((Class)o.getClass())) return false;
            if(ClassReflection.isAssignableFrom((MazeLevelInfo.class),(Class)o.getClass()))
            {
                MazeLevelInfo m = (MazeLevelInfo)o;
                return (m.Seed == this.Seed);
            }
            else if(ClassReflection.isAssignableFrom((Long.class),(Class)o.getClass()))
            {
                Long m = (Long)o;
                return (m == this.Seed);
            }
        }
        catch(Exception e)
        {
            return false;
        }
        return false;

    }

}