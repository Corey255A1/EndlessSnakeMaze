/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.endlessmaze.game.Controls;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.endlessmaze.game.AssetHandler;

/**
 *
 * @author Corey
 */
public class Compass {
    Sprite CompassRose;
    float Rotation = 0.0f;
    public Compass(int x, int y, float s, float r)
    {
        Texture t = AssetHandler.GetInstance().GetTexture("data/imgs/ctrls/Compass.png");
        CompassRose = new Sprite(t);
        CompassRose.setBounds(0, 0, 150, 150);
        CompassRose.setOrigin(150/2, 150/2);
        
        
        CompassRose.setPosition(x, y);
        Rotation = r;
        CompassRose.setRotation(Rotation);
    }
    public void draw(SpriteBatch batch)
    {
        CompassRose.draw(batch);
    }
    public void SetOrientation(float heading)
    {
        Rotation = heading;
        CompassRose.setRotation(Rotation);
    }
}
