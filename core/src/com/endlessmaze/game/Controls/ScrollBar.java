/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.endlessmaze.game.Controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.endlessmaze.game.AssetHandler;

/**
 *
 * @author Corey
 */
public class ScrollBar {
    Texture tScrollBar;
    Sprite Scroller;
    float X, Y, Width, Height;
    float Max, Min;
    float Delta;
    public ScrollBar(float x, float y)
    {
        X=x; Y=y;
        tScrollBar = AssetHandler.GetInstance().GetTexture("data/imgs/ctrls/ScrollBar.png");
        Texture t = AssetHandler.GetInstance().GetTexture("data/imgs/ctrls/Scroll.png");
        Height = t.getHeight()*10;
        Width = t.getWidth();
        Scroller = new Sprite(t);
        Scroller.setPosition(X, Y);
        Max = Y+Height;
        Min = Y;
        Delta = 360/Height;
        
    }
    public Color Scroll(float x, float y)
    {
        if(x>X-20 && x<X+Width+20 && y-32>Y && y-32<Height)
        {  
            Scroller.setPosition(X, y-32);
            
            int f = (int)GetValue();
            return GetRGB(f);
        }
        return new Color(0,0,0,0f);
    }
    public void SetColor(Color c)
    {
        float h = GetH(c);
        if(h<0)h=360f+h;
        Scroller.setY(h/Delta + Min);
    }
    public float GetValue()
    {
        return (Scroller.getY()-Min)*Delta;
    }
    public void draw(SpriteBatch batch)
    {
        batch.draw(tScrollBar, X, Y, Width, Height);
        Scroller.draw(batch);
    }
    
    public Color GetRGB(float H)
    {
        float CX = 1-Math.abs((H/60)%2 - 1);
        float C = 1;
        if(0<=H && H<60) return new Color(C,CX,0,1f);
        else if(60<=H && H<120) return new Color(CX,C,0,1f);
        else if(120<=H && H<180) return new Color(0,C,CX,1f);
        else if(180<=H && H<240) return new Color(0,CX,C,1f);
        else if(240<=H && H<300) return new Color(CX,0,C,1f);
        else if(300<=H && H<360) return new Color(C,0,CX,1f);
        else return new Color(1f,1f,1f,1f);   
    }
    
    public float GetH(Color c)
    {
        float max = Math.max(c.r, Math.max(c.g, c.b));
        float min = Math.min(c.r, Math.min(c.g, c.b));
        float del = max - min;
        
        if(del==0) return 0;
        else if(max==c.r) {return (60*(((c.g-c.b)/del)%6));}
        else if(max==c.g) {return (60*(((c.b-c.r)/del)+2));}
        else if(max==c.b) {return (60*(((c.r-c.g)/del)+4));}
        else return 0;
    }
    
}
