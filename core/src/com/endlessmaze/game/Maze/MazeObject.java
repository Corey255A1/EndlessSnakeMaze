/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.endlessmaze.game.Maze;

import com.badlogic.gdx.audio.Sound;
import com.endlessmaze.game.MazePlayers.MazePlayer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.endlessmaze.game.AssetHandler;
import com.endlessmaze.game.IPlayerModifier;

/**
 *
 * @author Corey
 */

public class MazeObject {  
    Sprite _ObjSprite;
    TextureRegion _ObjTexture;
    Sound   _ObjSound;
    int X,Y;
    public int getX() { return (int)_ObjSprite.getX(); }
    public int getY() { return (int)_ObjSprite.getY(); }
    
    IPlayerModifier PlayerAction;
    public String TextureName;
    public MazeObject(int scale, int x, int y, int texScale, String texturePath, String textureName, String sound, IPlayerModifier action)
    {
        this.X = x;
        this.Y = y;
        TextureName = textureName;
        _ObjTexture = AssetHandler.GetInstance().GetRegionFromAtlas(texturePath,textureName);
        _ObjSprite = new Sprite(_ObjTexture);
        _ObjSprite.setBounds(0, 0, scale, scale);
        _ObjSprite.setOrigin(scale/2, scale/2);
        _ObjSprite.setPosition(x, y);
        if(!sound.isEmpty()) _ObjSound = AssetHandler.GetInstance().GetSound(sound);
        PlayerAction = action;
    }
    public TextureRegion getTexture()
    {
        return _ObjTexture;
    }
    public void draw(SpriteBatch batch)
    {
        _ObjSprite.draw(batch);
    }
    
    public void PerformAction(MazePlayer player)
    {
        if(_ObjSound!=null) _ObjSound.play();
        PlayerAction.ActionToPlayer(player);
    }
    
    
}
