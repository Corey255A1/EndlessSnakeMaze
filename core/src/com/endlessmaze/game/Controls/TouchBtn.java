/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.endlessmaze.game.Controls;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.endlessmaze.game.AssetHandler;
/**
 *
 * @author Corey
 */
public class TouchBtn {
    
    Sprite Button;
    int ButtonSize = 200;
    float SCALE;
    public TouchBtn(String texPath, int x, int y, float scale, float a)
    {
        SCALE = scale;
        ButtonSize *= scale;
        Texture btnTex = AssetHandler.GetInstance().GetTexture(texPath);
        Button = new Sprite(btnTex,btnTex.getWidth(),btnTex.getHeight());
        Button.setBounds(0, 0, ButtonSize, ButtonSize);
        Button.setOrigin(ButtonSize/2, ButtonSize/2);
        Button.setPosition(x-ButtonSize/2, y);
        Button.setAlpha(a);
    }
    public TouchBtn(TextureRegion tr, int x, int y, float scale, float a)
    {
        SCALE = scale;
        ButtonSize *= scale;
        Button = new Sprite(tr);
        Button.setBounds(0, 0, ButtonSize, ButtonSize);
        Button.setOrigin(ButtonSize/2, ButtonSize/2);
        Button.setPosition(x-ButtonSize/2, y);
        Button.setAlpha(a);
    }
    
    public TouchBtn(TextureRegion tr, int x, int y, float scale)
    {
        this(tr, x, y, scale, 0.5f);
    }
    public void setRotate()
    {
        Button.rotate90(true);
    }
    public void setFlip()
    {
        Button.flip(false,true);
    }
  
    public void draw(SpriteBatch batch)
    {
      Button.draw(batch);
    }
    
    public boolean isPressed(float x, float y)
    {
        return Button.getBoundingRectangle().contains(x, y);
    }
    
}
