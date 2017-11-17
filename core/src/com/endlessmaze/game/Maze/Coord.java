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
 * Damn it Java
 */
public class Coord {
    private static Coord FastCoord = new Coord(0,0);
    public int X, Y;
    public static Coord GetCoord(int x, int y)
    {
        FastCoord.X=x; FastCoord.Y=y;
        return FastCoord;
    }
    public Coord(int x, int y)
    {
        X=x;Y=y;        
    }
    public Coord(double x, double y)
    {
        X=(int)x; Y=(int)y;
    }
    @Override
    public boolean equals(Object c)
    {
        if(c==null) return false;
        
        try{
        //if(!(MazeLink.class).isAssignableFrom((Class)o.getClass())) return false;
        if(!ClassReflection.isAssignableFrom((Coord.class),(Class)c.getClass())) return false;
        }
        catch(Exception e)
        {
            return false;
        }
        Coord m = (Coord)c;
        
        boolean b =((m.X == X)&& (m.Y == Y));

        return b;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + this.X;
        hash = 19 * hash + this.Y;
        return hash;
    }
    
    @Override
    public String toString()
    {
        return "X:" + X + " Y:" + Y;
    }
}
