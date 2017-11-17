/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.endlessmaze.game.Controls;
import com.endlessmaze.game.Callbacks.ICallback;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.endlessmaze.game.AssetHandler;
import java.util.ArrayList;
/**
 *
 * @author Corey
 */
public class TouchControl {
    
    Sprite[] Arrows = new Sprite[4];
    int ArrowSize = 150;
    float SCALE; 
    public TouchControl(int x, int y, float scale)
    {
        SCALE = scale;
        ArrowSize *= SCALE;
        TextureRegion texArrow = AssetHandler.GetInstance().GetTextureRegion("data/imgs/ctrls/CtrlRegion.png",512,3,0);
        Arrows[0] = new Sprite(texArrow);
        Arrows[0].setBounds(0, 0, ArrowSize, ArrowSize);
        Arrows[0].setOrigin(ArrowSize/2, ArrowSize/2);
        Arrows[0].setPosition(x, y+ArrowSize);
        Arrows[0].setAlpha(0.5f);
        
        Arrows[1] = new Sprite(texArrow);
        Arrows[1].setBounds(0, 0, ArrowSize, ArrowSize);
        Arrows[1].setOrigin(ArrowSize/2, ArrowSize/2);
        Arrows[1].setPosition(x+ArrowSize, y);
        Arrows[1].rotate90(true);
        Arrows[1].setAlpha(0.5f);
        
        Arrows[2] = new Sprite(texArrow);
        Arrows[2].setBounds(0, 0, ArrowSize, ArrowSize);
        Arrows[2].setOrigin(ArrowSize/2, ArrowSize/2);
        Arrows[2].setPosition(x, y-ArrowSize);
        Arrows[2].flip(false,true);
        Arrows[2].setAlpha(0.5f);
             
        Arrows[3] = new Sprite(texArrow);
        Arrows[3].setBounds(0, 0, ArrowSize, ArrowSize);
        Arrows[3].setOrigin(ArrowSize/2, ArrowSize/2);
        Arrows[3].setPosition(x-ArrowSize, y);
        Arrows[3].rotate90(false);
        Arrows[3].setAlpha(0.5f);
 
    }
  
    public void draw(SpriteBatch batch)
    {
        for(Sprite s: Arrows)
        {
            s.draw((batch));
        }
    }
    
    public int getDirection(float x, float y)
    {
        for(int i=0; i<4; i++)
        {
            if(Arrows[i].getBoundingRectangle().contains(x, y))
            {
                return i;
            }
        }
        return -1;
    }
    
}
