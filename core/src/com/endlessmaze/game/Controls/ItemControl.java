/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.endlessmaze.game.Controls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.endlessmaze.game.AssetHandler;
import com.endlessmaze.game.Maze.MazeObject;

/**
 *
 * @author Corey
 */
public class ItemControl {
    Sprite[] Containers;
    int ContainerCount = 3;
    float SizeRatio = 1.0f;
    float actualHeight;
    float actualWidth;
    Texture[] Items;
    float X, Y;
    boolean Horizontal = false;
    Animation[] PoofAnis;
    float[] elapsedTimes;
    boolean[] PoofFlags;
    boolean[] HadAnObject;
    public ItemControl(float x, float y, float s, int count, boolean horizontal)
    {
        TextureRegion contain  = AssetHandler.GetInstance().GetTextureRegion("data/imgs/ctrls/CtrlRegion.png",512,2,0);
        SizeRatio = s;
        X=x; Y=y;
        int Height = 512;
        int Width = 512;
        Horizontal = horizontal;
        actualHeight = Height*SizeRatio;
        actualWidth = Width*SizeRatio;
        ContainerCount = count;
        Containers = new Sprite[ContainerCount];
        PoofAnis = new Animation[ContainerCount];
        elapsedTimes = new float[ContainerCount];
        PoofFlags = new boolean[ContainerCount];
        HadAnObject = new boolean[ContainerCount];
        for(int i=0;i<ContainerCount;i++)
        {
            Containers[i] = new Sprite(contain);
            Containers[i].setBounds(0, 0,actualWidth,actualHeight);
            Containers[i].setOrigin(actualWidth/2, actualHeight);
            Containers[i].setPosition(x, y);
            if(horizontal) x+=actualWidth;
            else y+=actualHeight;
            Containers[i].setAlpha(0.7f);
            PoofAnis[i] = new Animation(1f/30f,AssetHandler.GetInstance().GetTextureAtlas("data/imgs/ani/PoofAni.atlas").getRegions());
            PoofAnis[i].setPlayMode(Animation.PlayMode.NORMAL);
            PoofFlags[i] = false;
            elapsedTimes[i] = 0f;
            
        }
        Items = new Texture[ContainerCount];

    }
    public void draw(SpriteBatch batch)
    {           
        for(int i=0;i<ContainerCount;i++)
        {
            float tx,ty;
            if(Horizontal){                   
               tx = actualWidth*i+X+16; ty = Y+16; 
            }
            else
            {
                tx = X+16; ty = actualHeight*i+Y+16;
                
            } 
            Containers[i].draw(batch);            
        }
    }
    public void drawObjects(SpriteBatch batch, MazeObject[] objects)
    {
        for(int i=0;i<ContainerCount;i++)
        {
            float tx,ty;
            if(Horizontal){                   
               tx = actualWidth*i+X+16; ty = Y+16; 
            }
            else
            {
                tx = X+16; ty = actualHeight*i+Y+16;
                
            }            
            if(objects[i]!=null)
            {
                HadAnObject[i]=true;
                batch.draw(objects[i].getTexture(), tx, ty, 164, 164);
            }
            if(HadAnObject[i] && PoofFlags[i])
            {
                elapsedTimes[i] += Gdx.graphics.getDeltaTime();
                
                batch.draw(PoofAnis[i].getKeyFrame(elapsedTimes[i]),tx,ty,164,164);
                if(PoofAnis[i].isAnimationFinished(elapsedTimes[i]))
                {
                    PoofFlags[i] = false;
                    HadAnObject[i] = false;
                    elapsedTimes[i] = 0;
                }
            }
            else if(!HadAnObject[i])
            {
                PoofFlags[i]=false;
            }
            
        }
    }
    public int ItemSelected(float x, float y)
    {
        for(int i=0;i<ContainerCount;i++)
        {
            if(Containers[i].getBoundingRectangle().contains(x, y))
            {   
                PoofFlags[i] = true;
                return i;
            }
        }
        return -1;
    }
}
