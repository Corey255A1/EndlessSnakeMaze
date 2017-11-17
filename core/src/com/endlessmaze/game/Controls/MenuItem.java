/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.endlessmaze.game.Controls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.endlessmaze.game.AssetHandler;
import com.endlessmaze.game.Maze.MazeUtilities;

/**
 *
 * @author Corey
 */
public class MenuItem {
    Sprite Item;
    Sprite Diamond;
    BitmapFont DistText;
    CharSequence TxtMsg;
    CharSequence Time;
    CharSequence TimeLen;
    CharSequence OneTime;
    CharSequence TwoTime;
    CharSequence FiveTime;
    
    int initX=0, initY=0;
    int Level;
    long Seed;
    public MenuItem(Texture t, BitmapFont f, int level, long seed, long time,  int x, int y, boolean diamond)
    {
        float h = t.getHeight()/2;
        Item = new Sprite(t,t.getWidth(),t.getHeight());
        Item.setPosition(x, y-h);
        Item.setSize(t.getWidth(), h);
        TextureRegion tr = AssetHandler.GetInstance().GetRegionFromAtlas("data/imgs/AllMazePieces.atlas","diamond");
        Diamond = new Sprite(tr);
        Diamond.setPosition(x+50, y-h+50);
        Diamond.setSize(64, 64);
        if(!diamond)
        {
            Diamond.setColor(Color.DARK_GRAY);
        }
        
        initX=(int)Item.getX();
        initY=(int)Item.getY();
        DistText = f; 
        //DistText.getData().setScale(2);
        Level = level;
        Seed = seed;
        TxtMsg = "Level:"+Level+" - Seed:"+Seed;
        //System.out.println(time);
        Time = Timer.GetTimeFormat(time).toString();
        TimeLen = "";
        OneTime = "";
        TwoTime = "";
        FiveTime = "";
    }
    public void SetEndlessTime(long timelen, int length)
    {
        TimeLen = Timer.GetTimeFormat(timelen).toString();
        TimeLen = length + ": " + TimeLen;   
    }
    public void SetOneTime(int length)
    {
        OneTime = Integer.toString(length) + ": 01:00:0";
    }
    public void SetTwoTime(int length)
    {
        TwoTime = Integer.toString(length) + ": 02:00:0";
    }
    public void SetFiveTime(int length)
    {
        FiveTime = Integer.toString(length) + ": 05:00:0";
    }
    
    public float GetHeight()
    {
        return Item.getHeight();
    }
    public float GetY()
    {
        return Item.getY();
    }
    public void draw(SpriteBatch batch, int mode)
    {
        Item.draw(batch);
        DistText.draw(batch, TxtMsg, Item.getX()+50, Item.getY()+Item.getHeight()-50);
        switch(mode)
        {
            case MazeUtilities.MAZE_MODE:
            {
            DistText.draw(batch, Time, Item.getX()+150, Item.getY()+Item.getHeight()-150);
            Diamond.draw(batch);
            }break;
            case MazeUtilities.ONE_MODE:
            {
                DistText.draw(batch, OneTime, Item.getX()+50, Item.getY()+Item.getHeight()-150); 
            }break;            
            case MazeUtilities.TWO_MODE:
            {
                DistText.draw(batch, TwoTime, Item.getX()+50, Item.getY()+Item.getHeight()-150); 
            }break;
            case MazeUtilities.FIVE_MODE:
            {
                DistText.draw(batch, FiveTime, Item.getX()+50, Item.getY()+Item.getHeight()-150); 
            }break;
            case MazeUtilities.ENDLESS_MODE:
            {
                DistText.draw(batch, TimeLen, Item.getX()+50, Item.getY()+Item.getHeight()-150); 
            }break;
        }
    }
    
    public void SetYOffset(int y)
    {
        Item.setY(initY+y);
        Diamond.setY(Item.getY()+50);
    }
    
    public boolean Selected(float x, float y)
    {
        return Item.getBoundingRectangle().contains(x, y);
    }
}
