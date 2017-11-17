/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.endlessmaze.game.Maze;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.endlessmaze.game.GraphicsPage;
import com.endlessmaze.game.MazePlayers.PlayerShadowList;

/**
 *
 * @author Corey
 */
public class MazeGraphicsPage extends GraphicsPage{
    //long TheSeed = 69500;

    public void create(SpriteBatch batch, int vPortW, int vPortH, int Seed)
    {
        //TheSeed = Seed;
        create(batch,vPortW,vPortH);
        
    }
    
    @Override
    public void create(SpriteBatch batch, int vPortW, int vPortH)
    {
        super.create(batch, vPortW, vPortH);
        PlayerShadowList.ViewportH_half = ViewPortX/2;
        PlayerShadowList.ViewportV_half = ViewPortY/2;
        theCamera.setToOrtho(false, ViewPortX, ViewPortY);
        
        reset(true,true);        
    }
    public void reset(long seed)
    {              
        reset();
    }
    public void reset(boolean resetMaze, boolean newPlayer)
    {
        theCamera.position.x = ViewPortX/2; 
        theCamera.position.y = ViewPortY/2;
        theCamera.update();
        MazeHandler.SetupMaze(ViewPortX/2,ViewPortY/2,ViewPortX,ViewPortY,resetMaze,newPlayer);
    }
    public void SetZoom(float zoom)
    {
        theCamera.zoom = zoom;
        PlayerShadowList.ViewportH_half = (int)(ViewPortX*zoom/2.0f);
        PlayerShadowList.ViewportV_half = (int)(ViewPortY*zoom/2.0f);
        MazeHandler.SetZoom(zoom);
        theCamera.update();
    }
    public void nextLevel()
    {
        MazeHandler.nextLevel();
        MazeHandler.playNextLevelSound();
        reset(true, false);
    }

    @Override
    public void reset() {
        reset(false, true);
    }
     
    @Override
    public void draw()
    {
        MazeHandler.draw(theBatch,theCamera);
        
    }


    
    
}
