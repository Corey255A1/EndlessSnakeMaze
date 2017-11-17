/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.endlessmaze.game.Controls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.endlessmaze.game.AssetHandler;
import com.endlessmaze.game.Callbacks.CallbackHandler;
import com.endlessmaze.game.Callbacks.CallbackPayload;
import com.endlessmaze.game.GraphicsPage;
import com.endlessmaze.game.Maze.MazeLevelInfo;
import com.endlessmaze.game.Maze.MazeUtilities;
import java.util.ArrayList;

/**
 *
 * @author Corey
 */
public class MenuPage extends GraphicsPage  implements InputProcessor {

    public class LevelSelect
    {
        public int Level;
        public long Seed;
        public int Length;
        public int Mode;
        public LevelSelect(int level, long seed, int mode)
        {
            Length = level * 20;
            Seed = seed;
            Level = level;
            Mode = mode;
        }
    }
    
    TouchBtn CloseBtn;
    TouchBtn ModeArrow;
    TouchBtn ModeArrowRev;
    ArrayList<MenuItem> Items = new ArrayList<MenuItem>();
    public CallbackHandler<Integer> CloseBtnCB = new CallbackHandler<Integer>();
    public CallbackHandler<LevelSelect> LevelSelectCB = new CallbackHandler<LevelSelect>();

    final int MinYOffset = -250;
    final int BottomOffset = 100;
    
    int YOffset = MinYOffset;
    int LastY = -1;
    int AdditionOffset=0;
    int ItemCount = 0;
    boolean TouchDown = false;
    BitmapFont LevelText;
    Texture TxtBackground;
    Rectangle TxtBackgroundRect;
    Texture ItemText;
    Sprite SnakeColor;
    int Mode = 0;
    
    ScrollBar Scroller;
    @Override
    public void reset() {
        return;
    }

    @Override
    public void draw() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        CloseBtn.draw(theBatch);
        for(MenuItem m: Items)
        {
            m.SetYOffset(YOffset);
            if(m.GetY()+m.GetHeight()>0 && m.GetY()<ViewPortY)
            {
                m.draw(theBatch,Mode);
            }
        }
        theBatch.draw(TxtBackground, 100, ViewPortY-225, 1050,600);
        switch(Mode)
        {
            case MazeUtilities.MAZE_MODE: LevelText.draw(theBatch, "Maze Mode", 440, ViewPortY-150); break;
            case MazeUtilities.ONE_MODE: LevelText.draw(theBatch, "1min Mode", 440, ViewPortY-150); break;
            case MazeUtilities.TWO_MODE: LevelText.draw(theBatch, "2min Mode", 440, ViewPortY-150); break;
            case MazeUtilities.FIVE_MODE: LevelText.draw(theBatch, "5min Mode", 440, ViewPortY-150); break;
            case MazeUtilities.ENDLESS_MODE: LevelText.draw(theBatch, "Endless Mode", 400, ViewPortY-150); break;                
        }       
        LevelText.draw(theBatch, "Level Selection", 375, ViewPortY-50);
        
        LevelText.draw(theBatch, "Snake Color", ViewPortX/2+325, ViewPortY-300);
        ModeArrow.draw(theBatch);
        ModeArrowRev.draw(theBatch);
        Scroller.draw(theBatch);
        SnakeColor.draw(theBatch);
        
    }
    @Override
    public void create(SpriteBatch batch,int vPortW, int vPortH)
    {
        super.create(batch,vPortW,vPortH);
        theCamera.translate(ViewPortX/2,ViewPortY/2);
        theCamera.update();
        AdditionOffset = ViewPortY;
        TextureRegion tr  = AssetHandler.GetInstance().GetTextureRegion("data/imgs/ctrls/CtrlRegion.png",512,0,0);
        TextureRegion texArrow = AssetHandler.GetInstance().GetTextureRegion("data/imgs/ctrls/CtrlRegion.png",512,3,0);
        CloseBtn  = new TouchBtn(tr,ViewPortX-90, ViewPortY-155, .7f);
        LevelText = new BitmapFont(Gdx.files.internal("data/fonts/liberation.fnt"),false);
        TxtBackground = AssetHandler.GetInstance().GetTexture("data/imgs/ctrls/TextMessageBackground.png");
        ItemText = AssetHandler.GetInstance().GetTexture("data/imgs/ctrls/MenuItem.png");
        Scroller = new ScrollBar(ViewPortX-400, 50);
        SnakeColor = new Sprite(AssetHandler.GetInstance().GetTexture("data/imgs/ctrls/SnakeColor.png"));
        ModeArrow = new TouchBtn(texArrow, ViewPortX/2-80, ViewPortY-220, 0.5f);
        ModeArrow.setRotate();
        ModeArrowRev = new TouchBtn(texArrow, ViewPortX/2-620, ViewPortY-220, 0.5f);
        ModeArrowRev.setRotate();
        ModeArrowRev.setFlip();
        SnakeColor.setPosition(ViewPortX-500, 100);
        SnakeColor.setColor(.2f,.2f,1f,1f);
        
        TxtBackgroundRect = new Rectangle(100, ViewPortY-225, 1050,600);
    }
    
    public void SetClassicMode(int mode)
    {
        Mode = mode;
    }
    
    public void ClearItems()
    {
        Items.clear();
        ItemCount=0;
        AdditionOffset = ViewPortY;
        YOffset = MinYOffset;
    }
    public void SetSnakeColor(Color c)
    {
        SnakeColor.setColor(c);
        Scroller.SetColor(c);        
    }
    public Color GetSnakeColor()
    {
        return SnakeColor.getColor();
    }
    public void AddItems(ArrayList<MazeLevelInfo> items)
    {
        //for(MazeLevelInfo l: items)
        for(int it=items.size()-1;it>=0;it--)
        {
            MazeLevelInfo l = items.get(it);
            MenuItem m = new MenuItem(ItemText, LevelText, items.size()-(ItemCount++),l.Seed,l.Time,115,AdditionOffset,l.DiamondFound);
            m.SetOneTime(l.OneTime);
            m.SetTwoTime(l.TwoTime);
            m.SetFiveTime(l.FiveTime);
            m.SetEndlessTime(l.TimeLen,l.Length);
            //System.out.println(l.Seed + " " + AdditionOffset);
            AdditionOffset -= (int)m.GetHeight();
            Items.add(m);
        }
        //YOffset = -(AdditionOffset-BottomOffset);
    }/*
    public void AddItem(Long item, boolean diamondFound)
    {
        MenuItem m = new MenuItem(++ItemCount,item,0,AdditionOffset,diamondFound);
        AdditionOffset -= (int)m.GetHeight();
        Items.add(m);
        YOffset = (AdditionOffset-BottomOffset);
    }*/
    @Override
    public boolean keyDown(int keycode) {
        return false;
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
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        TouchDown = true;
        LastY = screenY;
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if(TouchDown)
        {
            TouchDown = false;
            float scaleX = (float)screenX/Gdx.graphics.getWidth()*ViewPortX;
            float scaleY = (1-(float)screenY/Gdx.graphics.getHeight())*ViewPortY;
            if(CloseBtn.isPressed(scaleX, scaleY))
            {
                CloseBtnCB.DoCallbacks(null);
                return true;
            }
            else
            {
                if(TxtBackgroundRect.contains(scaleX, scaleY))
                {
                    if(ModeArrow.isPressed(scaleX, scaleY))
                    {
                        Mode = (Mode+1)%MazeUtilities.MAZE_MODE_COUNT;
                    }
                    else if (ModeArrowRev.isPressed(scaleX, scaleY))
                    {
                        Mode = (Mode-1)>=0 ? Mode-1 : MazeUtilities.MAZE_MODE_COUNT-1;
                    }
                }
                else
                {
                    for(MenuItem mi : Items)
                    {
                        if(mi.Selected(scaleX, scaleY))
                        {
                            System.out.println(mi.Level + " " + mi.Seed);
                            LevelSelectCB.DoCallbacks(new CallbackPayload<LevelSelect>(
                                new LevelSelect(mi.Level,mi.Seed, Mode)
                            ));
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        float scaleX = (float)screenX/Gdx.graphics.getWidth()*ViewPortX;
        float scaleY = (float)screenY/Gdx.graphics.getHeight()*ViewPortY;
        if(scaleX>115 && scaleX<155+ItemText.getWidth())
        {        
            int diff = LastY-screenY;
            if(YOffset+diff>MinYOffset && YOffset+diff<-(AdditionOffset-BottomOffset))
            {
                YOffset+=diff;
            }
            TouchDown = TouchDown && (Math.abs(diff)<5);
            LastY=screenY;

            return true;
        }
        else
        {
           Color c = Scroller.Scroll(scaleX,ViewPortY-scaleY);
           if(c.a==1f) SnakeColor.setColor(c);
        }
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
