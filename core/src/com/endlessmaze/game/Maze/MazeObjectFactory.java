/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.endlessmaze.game.Maze;

import java.util.*;

/**
 *
 * @author Corey
 */
public class MazeObjectFactory
{
    static ArrayList<MazeObjectType> MazeObjectTypeList = new ArrayList<MazeObjectType>();
    static ArrayList<MazeObjectCoordType> MazeCoordObjectTypeList = new ArrayList<MazeObjectCoordType>();
    static long TheSeed = 6700;
    static Coord DiamondObject;
    public static void StartFactory(long seed)
    {
        TheSeed = seed;
        ResetQuantities();
    }
    public static void AddNewMazeObjectType(MazeObjectType mazeObj)
    {
        MazeObjectTypeList.add(mazeObj);
    }
    
    public static void ClearMazeObjectCoords()
    {
        MazeCoordObjectTypeList.clear();
    }
    public static void ClearMazeObjects()
    {
        MazeObjectTypeList.clear();
    }
    public static void AddNewCoordMazeObjectType(MazeObjectCoordType mazeObj)
    {
        MazeCoordObjectTypeList.add(mazeObj);
    }
    
    public static void ResetQuantities()
    {
        for(MazeObjectType mot : MazeObjectTypeList)
        {
            mot.ResetQuantity();
        }
    }
    
    public static MazeObject RandomlyGetNewObject(int x, int y, int offset)
    {
        
        for(MazeObjectCoordType moct : MazeCoordObjectTypeList)
        {
            if(moct.CoordInRange(x, y)) return moct.CreateNewMazeObject(x, y);
        }
        
        double num = ((((((double)(x+1)*TheSeed*100)/(44721359.0*y+1) + ((double)(y+1)*TheSeed*100)/(45269999.0*x+1)))*100000000));
        long rand = (long)(Math.abs((long)(num*100)))%10000;
        rand=((rand+offset)>10000 ? 10000 : rand+offset);
        
        //System.out.println(x + "  "+ y + " " + rand + " " + num);
        for(int i=0; i<MazeObjectTypeList.size(); i++)
        {
            MazeObjectType curr = MazeObjectTypeList.get(i);
            if(curr.CanCreateObject(rand))
            {
                return curr.CreateNewMazeObject(x, y);
            }
        }
            return null;
    }
}