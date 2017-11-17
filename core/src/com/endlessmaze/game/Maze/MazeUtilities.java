/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.endlessmaze.game.Maze;

import com.badlogic.gdx.graphics.Color;

/**
 *
 * @author Corey
 */

public class MazeUtilities {
    
    //STATIC VARIABLES
    
    public static final int NORTH=0, EAST=1, SOUTH=2, WEST=3;
    public static final int MAZE_MODE=0, 
                            ONE_MODE=1, 
                            TWO_MODE=2, 
                            FIVE_MODE=3, 
                            ENDLESS_MODE=4, 
                            MAZE_MODE_COUNT=5;
    public static final String[] MODE_TO_STR = {"MAZE","ONELEN","TWOLEN","FIVELEN","ENDLESS"};
    static long TheSeed;
    
    static int FinishRegion = 500;
    static int FinishBoundingBox = 5000;
    static Coord FinishPoint;
    static boolean FoundFinish = false;
    
     // ------- STATICS -------
    public static void InitRandomizer(long seed, int level)
    {
        FoundFinish = false;
        TheSeed = seed;
        FinishPoint = null;
        FinishBoundingBox = 500 + level*500;
        //System.out.println(TheSeed + " " + level + " " + FinishBoundingBox);
    }
    public static int GetDistance(float x, float y, float x2, float y2)
    {
        int dist = (int)Math.sqrt(Math.pow((x2-x),2)+Math.pow((y2-y),2));
        return dist;
    }
    public static int GetDistance(Coord c1, Coord c2)
    {
        int dist = (int)Math.sqrt(Math.pow((c2.X-c1.X),2)+Math.pow((c2.Y-c1.Y),2));
        return dist;
    }
    public static float GetAngle(float x, float y, float x2, float y2)
    {
        return (float)Math.atan2(y2-y, x2-x);
    }
    public static int RotationToDegrees(int d)
    {
        switch(d)
         {
             case NORTH: return 0;
             case SOUTH: return 180;
             case WEST: return 90;
             case EAST: return 270;
             default: return 0;
         }
    }
    public static void SetFinishPointRange(int range)
    {
        FinishBoundingBox = range;
    }
    
    public static Coord GenerateFinishRegion()
    {
        FinishPoint = GetRandomCoord(TheSeed,FinishBoundingBox);
        int dist = GetDistance(FinishPoint, Coord.GetCoord(0,0));
        if(dist<FinishBoundingBox)
        {
            FinishPoint.X = (int)((float)FinishBoundingBox/(float)dist)*FinishPoint.X;
            FinishPoint.Y = (int)((float)FinishBoundingBox/(float)dist)*FinishPoint.Y;
        }
        return FinishPoint;
    }
    public static Color GetAColor()
    {
        switch((int)(TheSeed&0xFFFF)%8)
        {
            case 0:return Color.TEAL;
            case 1:return Color.GREEN;
            case 2:return Color.SCARLET;
            case 3:return Color.GOLDENROD;
            case 4:return Color.WHITE;
            case 5:return Color.SALMON;
            case 6:return Color.TAN;
            case 7:return Color.LIME;
            default: return Color.GREEN;
        }
    }
    
    public static Coord GenerateDiamondPoint(int bound)
    {
        return GetRandomCoord((TheSeed*3+1)/2,bound);
    }
    
    public static Coord GetRandomCoord(long seed, int bound)
    {
        float sX = (((float)seed/45269999));
        long intX = (long)sX;
        sX = (sX - intX)*100000;        
        intX = (long)sX;        
        float decX = (sX - intX)*10000;         
        int x = ((int)decX)%bound;
        
        
        float sY = ((float)(x*10000)/44721359)*10000;
        long intY = (long)sY;
        float decY = (sY - intY)*100000;
        intY = (long)sY;
        decY = (sY - intY)*10000;        
        int y = ((int)sY)%bound;
        
        x = (((seed*100)/23%100-50))>0 ? x : -x;
        y = (((seed*100)/17%100-50))>0 ? y : -y;
        //System.out.println(TheSeed + " " + sX);
        //System.out.println( intX + " " + y);
        //System.out.println(decX + " " + sY);
        //System.out.println(x + " " + y);
        return new Coord(x,y);
    }

    public static MazeLinkType ChooseLinkType(int X, int Y)
    {
        //int rand = Math.abs((int)((TheSeed*X)/Y + ((((float)TheSeed)/Y)*1000000))) % 100;
        //int rand = Math.abs((int)(((((X) + (Y))*TheSeed)/((X*Y)+1))*1000000)) % 100;
        
        if(FinishPoint!= null && !FoundFinish && (GetDistance(X,Y,FinishPoint.X,FinishPoint.Y) < FinishRegion))
        {
            FoundFinish = true;
            //System.out.println("Twice?");
            return MazeLinkType.FINISH;
        }
        double num = ((((((double)(X+1)*TheSeed)/(8765423.0*Y+1) + ((double)(Y+1)*TheSeed)/(8396981.0*X+1)))*100000000));
        long rand = (Math.abs((long)num)/10) % 100;

        if(rand<0)rand=40;
        if(rand>=0 && rand <10)
            return MazeLinkType.CROSS;
        else if(rand>=10 && rand <20)
            return MazeLinkType.TEE;
        else if(rand>=20 && rand <40)
            return MazeLinkType.TURNL;
        else if(rand>=40 && rand <60)
            return MazeLinkType.TURNR;
        else if(rand>=60 && rand <=100)
            return MazeLinkType.STRAIGHT;
        else
        {
            return MazeLinkType.STRAIGHT;
        }
    }   
    
    
    public static int GetOppositeDirection(int numDir)
    {
        switch(numDir)
        {
            case NORTH: return SOUTH;
            case SOUTH: return NORTH;
            case WEST: return EAST;
            case EAST: return WEST;
            default: return -2;
        }
    }
    
    public static Coord GetInDirection(int s, int nX, int nY, int dir)
    {
        switch(dir)
        {
            case MazeUtilities.NORTH: nY += s; break;
            case MazeUtilities.SOUTH: nY -= s; break;
            case MazeUtilities.WEST: nX -= s; break;
            case MazeUtilities.EAST: nX += s; break;  
        }
        return Coord.GetCoord(nX, nY);
    }
}
