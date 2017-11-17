/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.endlessmaze.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 *
 * @author Corey
 */
public abstract class GraphicsPage {
    protected SpriteBatch theBatch;
    protected OrthographicCamera theCamera;
    protected int ViewPortX, ViewPortY;
    protected boolean Disposed = false;
    public void create(SpriteBatch batch, int vPortW, int vPortH)
    {
        theBatch = batch;
        theCamera = new OrthographicCamera(vPortW,vPortH);
        ViewPortX = vPortW;
        ViewPortY = vPortH;
    }
    public abstract void reset();
    public void render()
    {
        if(Disposed) return;
        theBatch.setProjectionMatrix(theCamera.combined);
        theBatch.begin();
        this.draw();
        theBatch.end();
    }
    public abstract void draw();
    public void dispose()
    {
        ///theBatch.dispose();
    }

    
}
