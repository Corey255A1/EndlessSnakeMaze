
package com.endlessmaze.game.Maze;

import com.badlogic.gdx.audio.Sound;
import com.endlessmaze.game.IPlayerModifier;


/**
 *
 * @author Corey
 */
public class MazeObjectCoordType {
    String TexturePath;
    String TextureName;
    int TexutreRow;
    int ScarcityFactor;
    int Scale;
    int TexScale;
    IPlayerModifier PlayerAction;
    String ObjectSound;
    float X,Y;
    int Range;
    boolean Triggered = false;
    public MazeObjectCoordType(int scale,float x, float y, int range, int texScale, String texture, String textureName, String sound, IPlayerModifier action)
    {
        TexturePath = texture;
        TextureName = textureName;
        X=x;
        Y=y;
        Scale = scale;
        TexScale = texScale;
        PlayerAction = action;
        ObjectSound = sound;
        Range = range/2;
    }
    
    public boolean CoordInRange(float x, float y)
    {
        return !Triggered && (x>(X-Range) && x<(X+Range) && y>(Y-Range) && y<(Y+Range));
    }

    public void Reset()
    {
        Triggered = false;
    }
    public MazeObject CreateNewMazeObject(int x, int y)
    {
        Triggered = true;
        return new MazeObject(Scale, x, y, TexScale, TexturePath,TextureName, ObjectSound, PlayerAction);
    }
}


