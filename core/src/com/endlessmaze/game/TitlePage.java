/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.endlessmaze.game;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.endlessmaze.game.Callbacks.CallbackHandler;

/**
 *
 * @author Corey
 */
public class TitlePage extends GraphicsPage  implements InputProcessor {
    
    Sprite _TitleImage;
    Sprite _TapToBegin;
    Music TitleMusic;
    float _Alpha = 0.0f;
    float _TapAlpha = 0.0f;
    boolean bFade = true;
    public CallbackHandler<Integer> InputReceived = new CallbackHandler<Integer>();
    public TitlePage()
    {            
    }
    @Override
    public void reset() {
    }
    @Override
    public void create(SpriteBatch batch,int vPortW, int vPortH)
    {
        super.create(batch, vPortW, vPortH);
         _TitleImage = new Sprite(AssetHandler.GetInstance().GetTexture("data/imgs/TitleScreen.png"),vPortW,vPortH);
         Texture t = AssetHandler.GetInstance().GetTexture("data/imgs/TapToBegin.png");
         _TapToBegin = new Sprite(t,t.getWidth(),t.getHeight());
         theCamera.translate(vPortW/2, vPortH/2);
         theCamera.update();
         TitleMusic = AssetHandler.GetInstance().GetMusic("data/sounds/SnakeTitle.ogg");
         TitleMusic.setLooping(true);
         TitleMusic.play();
    }
    public void Enable()
    {
        if(!TitleMusic.isPlaying())
        {
            TitleMusic.play();
        }
    }
    public void Disable()
    {
        if(TitleMusic.isPlaying())
        {
            TitleMusic.stop();
        }
    }
    @Override
    public void draw() {
        if(_Alpha<1.0f)
        {
            _TitleImage.setAlpha(_Alpha);
            _Alpha+=0.01f;
        }
        _TitleImage.draw(theBatch);
        if(bFade && _TapAlpha<.98f)
        {
            _TapAlpha+=0.02f;
        }
        else if(_TapAlpha>0.02)
        {
            bFade=false;
            _TapAlpha-=0.02f;
        }
        else
        {
            bFade = true;
        }
        _TapToBegin.setAlpha(_TapAlpha);
        _TapToBegin.draw(theBatch);
    }
    
    @Override
    public void dispose()
    {
        TitleMusic.stop();
        TitleMusic.dispose();
        super.dispose();
    }
    
    @Override
    public boolean keyDown(int keycode) {
       InputReceived.DoCallbacks(null);
       return true;
    }
    @Override
    public boolean keyUp(int keycode) {
       return false;
    }

    @Override
    public boolean keyTyped(char character) {
       return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) 
    {
        InputReceived.DoCallbacks(null);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    } 

    
}
