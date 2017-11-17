/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.endlessmaze.game.Maze;

import com.badlogic.gdx.audio.Sound;
import com.endlessmaze.game.IPlayerModifier;

/**
 *
 * @author Corey
 */
public class MazeObjectType
{
    String TexturePath;
    String TextureName;
    int TextureRow;
    long LowBound;
    long HighBound;
    int Scale;
    IPlayerModifier PlayerAction;
    String ObjectSound;
    int MaxQuantity = -1;
    int QuantityLeft = -1;
    int TexScale;
    public MazeObjectType(int scale,float lowbound, float highbound,int maxQuantity, int texScale, String texture, String textureName, String sound, IPlayerModifier action)
    {
        TexturePath = texture;
        TextureName = textureName;
        HighBound = (int)(highbound*100.0f);
        LowBound = (int)(lowbound*100.0f);
        Scale = scale;
        TexScale = texScale;
        PlayerAction = action;
        ObjectSound = sound;
        MaxQuantity = maxQuantity;
        QuantityLeft = MaxQuantity;
    }
    public boolean CanCreateObject(long val)
    {
        if(MaxQuantity>0)
        {
            return (val>LowBound && val<=HighBound) && QuantityLeft>0;
        }
        else
        {
            return (val>LowBound && val<=HighBound);
        }
    }
    public int ResetQuantity()
    {
        QuantityLeft = MaxQuantity;
        return MaxQuantity;
    }
    public MazeObject CreateNewMazeObject(int x, int y)
    {
        if(QuantityLeft>0) QuantityLeft--;
        return new MazeObject(Scale, x, y, TexScale, TexturePath, TextureName, ObjectSound, PlayerAction);
    }
}
