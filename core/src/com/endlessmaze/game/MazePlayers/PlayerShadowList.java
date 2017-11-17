/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.endlessmaze.game.MazePlayers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.endlessmaze.game.AssetHandler;
import com.endlessmaze.game.Maze.MazeUtilities;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Corey
 */
public class PlayerShadowList
{
    public static final int MAX_SOUND_COUNT = 5;
    public static int ExplodingSoundCount = MAX_SOUND_COUNT;
    public static float ViewportH_half, ViewportV_half;
    public static Color SnakeColor;
    public class PlayerShadow
    {
        Sprite Shadow;
        boolean Exploding = false;
        Sound MySound;
        float vx, vy;
        int ExploadingLife = 100;
       
        public PlayerShadow(TextureRegion t)
        {
            Shadow = new Sprite(t);
            Shadow.setColor(SnakeColor);
            Shadow.setBounds(0, 0, SCALE, SCALE);
            Shadow.setOrigin(SCALE/2, SCALE/2);
            MySound = AssetHandler.GetInstance().GetSound("data/sounds/LosePower.ogg");
        }
        public void SetColor(Color c) {Shadow.setColor(c);}
        public void setPosition(float x, float y) {Shadow.setPosition(x, y);}
        public float getX()
        {
            return Shadow.getX();
        }
        public float getY()
        {
            return Shadow.getY();
        }
        public void draw(float centerX, float centerY, SpriteBatch batch)
        {
            if(Exploding)
            {
                Shadow.translate(vx, vy);
                ExploadingLife--;
            }           
            float x = Shadow.getX();
            float y = Shadow.getY();
            if((x>centerX-ViewportH_half && x<centerX+ViewportH_half) && 
                    (y>centerY-ViewportV_half && y<centerY+ViewportV_half))
            {
            Shadow.draw(batch);
            }
        }
        public boolean dead()
        {
            return ExploadingLife<=0;
        }
        public void explode()
        {
            Random r = new Random();
            vx = 16-(r.nextFloat()*32);
            vy = 16-(r.nextFloat()*32);
            Exploding = true;
            if(ExplodingSoundCount>0){
                ExplodingSoundCount--;
                MySound.play(1,r.nextFloat()+0.9f,0);
            }
        }
        public void launch(int dir)
        {
            Random r = new Random();
            switch(dir)
            {
                case MazeUtilities.NORTH:
                    vy=10;
                    vx = 16-(r.nextFloat()*32);
                    break;
                case MazeUtilities.SOUTH:
                    vy=-10;
                    vx = 16-(r.nextFloat()*32);
                    break;
                case MazeUtilities.WEST:
                    vx=-10;
                    vy = 16-(r.nextFloat()*32);
                    break;
                case MazeUtilities.EAST:
                    vx=10;
                    vy = 16-(r.nextFloat()*32);
                    break;   
                case -1:
                default:
                    vx = 16-(r.nextFloat()*32);
                    vy = 16-(r.nextFloat()*32);
            }
            Exploding = true;
            if(ExplodingSoundCount>0){
                ExplodingSoundCount--;
                MySound.play(1,r.nextFloat()+0.9f,0);
            }
        }
    }
    
    
   ArrayList<PlayerShadow> Shadows = new ArrayList<PlayerShadow>();
   ArrayList<PlayerShadow> ExplodingShadows = new ArrayList<PlayerShadow>();
   int nID;
   TextureRegion _Texture;
   Sprite Tail;
   int SCALE;
   float X, Y;
   
   public PlayerShadowList(int scale, TextureRegion tex, TextureRegion tail, float x, float y, Color c)
   {
    _Texture = tex;
    SCALE = scale;
    SnakeColor = c;
    PlayerShadow shad = new PlayerShadow(_Texture);
    Tail = new Sprite(tail);
    Tail.setBounds(0, 0, SCALE, SCALE);
    Tail.setColor(SnakeColor);
    Tail.setOrigin(SCALE/2, SCALE/2);
    X=x; Y=y;
    Tail.setPosition(x, y);
    shad.setPosition(x, y);
    Shadows.add(shad); 
   }
   public int getCount()
   {
       return Shadows.size();
   }

   public void ResetPositions(float x, float y)
   {
     for(PlayerShadow shad : Shadows)
     {
         shad.setPosition(x, y);
     }
     Tail.setPosition(x, y);
   }
   
   public void SetColor(Color c)
   {
       SnakeColor = c;
       Tail.setColor(SnakeColor);
       for(PlayerShadow shad : Shadows)
       {
        shad.SetColor(SnakeColor);
       }
   }
   
   public void setSize(int count, int launchdir, boolean fromhead)
   {
       if(count>getCount())
       {
           while(getCount()<count) add();
       }
       else
       {
           ExplodingSoundCount=MAX_SOUND_COUNT;
           while(getCount()>count) remove(launchdir, fromhead);
       }
   }
   public void add()
   {
        PlayerShadow shad = new PlayerShadow(_Texture);
        if(Shadows.size()>0)
        {
            int s = Shadows.size();
            shad.setPosition(Shadows.get(s-1).getX(), Shadows.get(s-1).getY());
        }
        Shadows.add(shad);      
   }
   public void remove(int dir, boolean fromhead)
   {
       if(Shadows.size()>0)
       {
         PlayerShadow exp;
         if(!fromhead)
         {
         exp = Shadows.remove(0);
         SetTailPosition(exp.getX(), exp.getY());
         if(dir>=0 || dir==-2) exp.setPosition(X, Y);
         }
         else
         {
             exp = Shadows.remove(Shadows.size()-1);
         }
         exp.launch(dir);       
         ExplodingShadows.add(exp);
         
       }
   }
   public void draw(SpriteBatch batch)
   {

     float x=0,y=0;
     float px=0,py=0;
     for(int i=0;i<Shadows.size();i++)
     {
         PlayerShadow shad = Shadows.get(i);
         x = shad.getX();
         y= shad.getY();
         if(x!=px || y!=py)
         {
             px=x; py=y;
            shad.draw(X,Y,batch);
         }
     }
     for(int i=ExplodingShadows.size()-1;i>=0;i--)
     {
         if(ExplodingShadows.get(i).dead())
         {
             ExplodingShadows.remove(i);
         }
         else
         {
             ExplodingShadows.get(i).draw(X,Y,batch);
         }
     }
     Tail.draw(batch);

   }
   public void setPos(float x, float y)
   {
    float lx =x, ly=y;
    X=x;Y=y;
    for(int i=Shadows.size()-1;i>=0;i--)
    {
        float fx = Shadows.get(i).getX();
        float fy = Shadows.get(i).getY();
        Shadows.get(i).setPosition(lx, ly);
        lx = fx; ly = fy;
    }
    SetTailPosition(lx, ly);
   }
   public void SetTailPosition(float x, float y)
   {
    float tx = Tail.getX();
    float ty = Tail.getY();
    if(x>tx) Tail.setRotation(-90);
    else if(x<tx) Tail.setRotation(90);
    else if(y>ty) Tail.setRotation(0);
    else  Tail.setRotation(180);
    Tail.setPosition(x, y);
   }
   public int check(float x, float y)
   {
    for(int i=Shadows.size()-1;i>=1;i--)
    {
        float fx = Shadows.get(i).getX();
        float fy = Shadows.get(i).getY();
        if(fx==x && fy==y) return i;
    }
    return -1;
   }
}
