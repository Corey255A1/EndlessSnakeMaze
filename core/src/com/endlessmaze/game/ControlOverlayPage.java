/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.endlessmaze.game;

import com.endlessmaze.game.Callbacks.CallbackHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.endlessmaze.game.Callbacks.CallbackPayload;
import com.endlessmaze.game.Controls.*;
import com.endlessmaze.game.Maze.MazeHandler;
import com.endlessmaze.game.Maze.MazeObject;
import com.endlessmaze.game.Maze.MazeUtilities;
import com.endlessmaze.game.MazePlayers.MazePlayer;

/**
 *
 * @author Corey
 */
public class ControlOverlayPage extends GraphicsPage implements InputProcessor, GestureDetector.GestureListener{
    TouchControl CtrlTouch;
   // TouchBtn PowerBtn;    
    static boolean ResetMode = false;
    boolean GetMoreBombs = true;
    long ResetSeed = 6700;
    TouchBtn ResetBtn;
    TouchBtn MenuBtn;
    Compass CompassCtrl;
    static boolean CompassEnabled = true;
    BitmapFont DistText; 
    
    ItemControl ItemController;
    ItemControl ActionItemController;
    
    static CharSequence DisplayText;
    BitmapFont MyFont;
    Sprite FinishUnlocked;
    Sprite Loading;
    float LoadingAlpha = 0.0f;
    boolean LoadingFade = true;
    static long StartTime;
    static long EndTime;
    Texture TxtBackground;
    TextureRegion TxtDisplayBackground;
    TextureRegion ShieldIndicator;
    Texture NoBombs;
    
    static final int DrawDisplayText = 1;
    static final int DrawFinishUnlocked = 2;
    static final int DrawLoading = 3;
    static int DrawTextState = 0;
    
    static boolean bOneShotUnlock = false;
    
    float ZoomLevel = 1.0f;
    float TempZoomLevel = 1.0f;
    com.badlogic.gdx.utils.StringBuilder LineBuilder = new com.badlogic.gdx.utils.StringBuilder(100);
    
  
    MazeObject[] HardMazeObjs = new MazeObject[1];
        
    InputMultiplexer Multi = new InputMultiplexer();
    
    public static void SetResetMode(boolean bShow)
    {
        ResetMode = bShow;
    }
    
    @Override
    public void reset()
    {
        ResetMode = false;
        bOneShotUnlock = false;
        GetMoreBombs = true;
    }
    public static void ResetOneShot() {bOneShotUnlock=false;}

    public long GetResetSeed(){return ResetSeed;}
    
    public static void SetDisplayText(String str)
    {
        DisplayText = str;
    }
    public static void TriggerDisplayText()
    {
        if(DrawTextState!=DrawLoading)
        {
            StartTime = System.currentTimeMillis();
            EndTime = StartTime + 5000;
            DrawTextState = DrawDisplayText;
        }
    }
    public static void TriggerFinishUnlocked()
    {
        if(DrawTextState!=DrawLoading && !bOneShotUnlock)
        {
            StartTime = System.currentTimeMillis();
            EndTime = StartTime + 5000;
            DrawTextState = DrawFinishUnlocked;  
            bOneShotUnlock = true;
        }
    }
    
    public void SetAsInputProcessor()
    {
        Gdx.input.setInputProcessor(Multi);
    }
    
    @Override
    public void create(SpriteBatch batch,int vPortW, int vPortH)
    {
        
        ResetMode = false;
        bOneShotUnlock = false;
        GetMoreBombs = true;
        
        super.create(batch, vPortW, vPortH);
        theCamera.translate(ViewPortX/2,ViewPortY/2);
        theCamera.update();
        Multi.addProcessor(new GestureDetector(this));
        Multi.addProcessor(this);
        CompassEnabled = true;
        MyFont = new BitmapFont(Gdx.files.internal("data/fonts/liberation.fnt"),false);
        MyFont.getData().setScale(1.5f);
        FinishUnlocked  = new Sprite(AssetHandler.GetInstance().GetTexture("data/imgs/FinishUnlocked.png"));
        FinishUnlocked.setPosition(0, ViewPortY/2);
        Loading = new Sprite(AssetHandler.GetInstance().GetTexture("data/imgs/ctrls/Loading.png"));
        Loading.setPosition(0, ViewPortY/2);
        
        CompassCtrl = new Compass(ViewPortX-440, 260,1f,0);
        CtrlTouch = new TouchControl(240,240,1.5f); 
       // PowerBtn = new TouchBtn("data/imgs/ctrls/RoundBtn.png",ViewPortX-350,250, 1.5f);
        
        ResetBtn = new TouchBtn("data/imgs/ctrls/Reset.png",ViewPortX/2,ViewPortY/2, 1.5f,0.8f);
        //SeedBtn = new TouchBtn("data/imgs/ctrls/RoundBtn.png",ViewPortX/2,50, 1.5f);
        DistText = new BitmapFont(Gdx.files.internal("data/fonts/liberation.fnt"),false);
        DistText.getData().setScale(0.5f);
        TxtBackground = AssetHandler.GetInstance().GetTexture("data/imgs/ctrls/TextMessageBackground.png");
        TxtDisplayBackground  = AssetHandler.GetInstance().GetTextureRegion("data/imgs/ctrls/CtrlRegion.png",512,2,0);
        ShieldIndicator = AssetHandler.GetInstance().GetRegionFromAtlas("data/imgs/AllMazePieces.atlas","shield");
        NoBombs = AssetHandler.GetInstance().GetTexture("data/imgs/FinishClosed.png");
        TextureRegion tr  = AssetHandler.GetInstance().GetTextureRegion("data/imgs/ctrls/CtrlRegion.png",512,1,0);
        MenuBtn = new TouchBtn(tr,ViewPortX-90, ViewPortY-155, .7f,.9f);
        ItemController = new ItemControl(ViewPortX-270, 30,.4f,4,false);
        ActionItemController = new ItemControl(ViewPortX/2-200, 30,.4f,1,true);
        HardMazeObjs[0] = new MazeObject(128,0,0,128,"data/imgs/AllMazePieces.atlas","watchad","",
                    new IPlayerModifier(){
                        @Override
                        public void ActionToPlayer(MazePlayer p) {
                        }});
    }
    
    @Override
    public void draw()
    {
         
        int dist = MazeHandler.getDistanceFromStart();
        int distFin = MazeHandler.getDistanceToFinish();
        int powerLevel = MazeHandler.getPlayerPowerLevel();
        int maxPl = MazeHandler.getPlayerMaxPowerLevel();
        
        theBatch.draw(TxtDisplayBackground, ViewPortX/2, 30, 690,205);
        
        LineBuilder.setLength(0);
        LineBuilder.append("Distance From Start: ");
        LineBuilder.append(dist);
        DistText.draw(theBatch, LineBuilder, ViewPortX/2+95, 66);
        if(MazeHandler.CurrentMazeMode()==MazeUtilities.MAZE_MODE)
        {
        LineBuilder.setLength(0);
        LineBuilder.append("Distance To Finish: ");
        LineBuilder.append(distFin);
        DistText.draw(theBatch, LineBuilder, ViewPortX/2+95, 104);
        }
        LineBuilder.setLength(0);
        LineBuilder.append(MazeHandler.getCharacterX());
        LineBuilder.append(':');
        LineBuilder.append(MazeHandler.getCharacterY());
        DistText.draw(theBatch,LineBuilder, ViewPortX/2+95,142);
        LineBuilder.setLength(0);
        LineBuilder.append("Snake Length:");
        LineBuilder.append(powerLevel);
        if(MazeHandler.CurrentMazeMode()==MazeUtilities.MAZE_MODE)
        {
        LineBuilder.append('/');
        LineBuilder.append(MazeHandler.GetSizeToComplete());
        }
        DistText.draw(theBatch, LineBuilder, ViewPortX/2+95, 180);        
        DistText.draw(theBatch,MazeHandler.GetElapsedTime(), ViewPortX/2+95, 218);
        //GLProfiler.reset();
        CtrlTouch.draw(theBatch);
        MenuBtn.draw(theBatch);        
        ItemController.draw(theBatch);
        ActionItemController.draw(theBatch);
        
        if(MazeHandler.CurrentMazeMode()==MazeUtilities.MAZE_MODE)
        {
            CompassCtrl.SetOrientation(MazeHandler.getHeadingToFinish());
            CompassCtrl.draw(theBatch);
        }
        //Split to reduce Context Switches
        ItemController.drawObjects(theBatch,MazeHandler.getPlayerItems());
        ActionItemController.drawObjects(theBatch,HardMazeObjs);
        if(!GetMoreBombs) theBatch.draw(NoBombs, ViewPortX/2-200, 30, 200,200);
        if(ResetMode==true){ ResetBtn.draw(theBatch);}
        if(MazeHandler.ShieldActive()) theBatch.draw(ShieldIndicator, ViewPortX/2+25, 140,64,64);
        
        switch(DrawTextState)
        {
            case DrawLoading:
            {
                if(LoadingFade && LoadingAlpha<.98f)
                {
                    LoadingAlpha+=0.02f;
                }
                else if(LoadingAlpha>0.02)
                {
                    LoadingFade=false;
                    LoadingAlpha-=0.02f;
                }
                else
                {
                    LoadingFade = true;
                }
                Loading.setAlpha(LoadingAlpha);
                Loading.draw(theBatch);
                break;
            }            
            case DrawDisplayText:
                if(System.currentTimeMillis()<EndTime)
                {
                    theBatch.draw(TxtBackground, 0, ViewPortY-400);
                    GlyphLayout gl = new GlyphLayout(MyFont,DisplayText);
                    MyFont.draw(theBatch, gl, (ViewPortX-gl.width)/2, ViewPortY-300);                    
                }
                else
                {
                    DrawTextState = 0;
                }
                break;
            case DrawFinishUnlocked:
                if(System.currentTimeMillis()<EndTime)
                {
                   theBatch.draw(TxtBackground, 0, ViewPortY-400);
                   FinishUnlocked.draw(theBatch);
                }
                else
                {
                    DrawTextState = 0;
                }
                break;
        }
        //System.out.println("DC " + GLProfiler.drawCalls);
        //System.out.println("TB " + GLProfiler.textureBindings);
    }
    
    public static void TriggerLoading()
    {
        DrawTextState = DrawLoading;
    }

    public static void DoneLoading()
    {
        DrawTextState = 0;
    }
    public static void ClearTime()
    {
        EndTime=0;
    }
    public static void EnableCompass(boolean bEnable)
    {
        CompassEnabled = bEnable;
    }
    
    public CallbackHandler<Integer> ArrowBtnCB = new CallbackHandler<Integer>();
    public CallbackHandler<Integer> ResetBtnCB = new CallbackHandler<Integer>();
    public CallbackHandler<Integer> MenuBtnCB = new CallbackHandler<Integer>();
   // public CallbackHandler<Integer> PowerBtnCB = new CallbackHandler<Integer>();
    public CallbackHandler<Integer> UseItemCB = new CallbackHandler<Integer>();
    public CallbackHandler<Integer> UsePowerItemCB = new CallbackHandler<Integer>();
    public CallbackHandler<Float> ZoomCB = new CallbackHandler<Float>();
    //public CallbackHandler<Integer> SeedBtnCB = new CallbackHandler<Integer>();
    
    
    @Override
    public boolean keyDown(int keycode)
    {
        boolean handled = false;
        switch(keycode)
        {
            case Input.Keys.LEFT: DoMove(3); handled = true; break;
            case Input.Keys.RIGHT: DoMove(1); handled = true; break;
            case Input.Keys.UP: DoMove(0); handled = true; break;
            case Input.Keys.DOWN: DoMove(2); handled = true; break;
        }
        return handled;
    }
    
    private void DoMove(int dir)
    {
        if(DrawTextState!=DrawLoading)
        {
        ArrowBtnCB.DoCallbacks(new CallbackPayload<Integer>(dir));
        }
    }

    @Override
    public boolean keyUp(int keycode) {
        if(DrawTextState!=DrawLoading)
        {
            ArrowBtnCB.DoCallbacks(new CallbackPayload<Integer>(-1));
        }
       return false;
    }

    @Override
    public boolean keyTyped(char character) {
       return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) 
    {        
        boolean handled = false;
        if(DrawTextState==DrawLoading) return false;
        
        float scaleX = (float)screenX/Gdx.graphics.getWidth()*ViewPortX;
        float scaleY = (1-(float)screenY/Gdx.graphics.getHeight())*ViewPortY;

        int dir = CtrlTouch.getDirection(scaleX, scaleY);
        int item=-1;
        if(!ResetMode && dir>=0)
        {            
            ArrowBtnCB.DoCallbacks(new CallbackPayload<Integer>(dir));
            return true;
        }
        else if(MenuBtn.isPressed(scaleX, scaleY))
        {
            MenuBtnCB.DoCallbacks(null);
            return true;
        }
        else if(ResetMode && ResetBtn.isPressed(scaleX, scaleY))
        {
            ResetBtnCB.DoCallbacks(null);            
            this.reset();
            return true;
        }
        else if(!ResetMode && (item=ItemController.ItemSelected(scaleX, scaleY))>-1)
        {            
            UseItemCB.DoCallbacks(new CallbackPayload<Integer>(item));
            return true;
        }        
        else if(!ResetMode && (item=ActionItemController.ItemSelected(scaleX, scaleY))>-1)
        {
            if(GetMoreBombs)
            {
                GetMoreBombs=false;
                DisplayText = "Please Wait...";
                DrawTextState = DrawDisplayText;
                EndTime = System.currentTimeMillis()+300000;            
                UsePowerItemCB.DoCallbacks(new CallbackPayload<Integer>(item));            
            }
            return true;
        }
        
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if(DrawTextState!=DrawLoading)
            ArrowBtnCB.DoCallbacks(new CallbackPayload<Integer>(-1));
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
        TempZoomLevel=ZoomLevel+=(0.2f*ZoomLevel*amount);
        if(TempZoomLevel>5.0f)
        {
            TempZoomLevel = 5.0f;            
        }
        else if(TempZoomLevel<0.2f)
        {
            TempZoomLevel = 0.2f;
        }
        ZoomCB.DoCallbacks(new CallbackPayload(TempZoomLevel)); 
        return true;
    }
    
    
    

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        ZoomLevel = TempZoomLevel;
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {

        TempZoomLevel=ZoomLevel*(initialDistance/distance);
        if(TempZoomLevel>5.0f)
        {
            TempZoomLevel = 5.0f;            
        }
        else if(TempZoomLevel<0.2f)
        {
            TempZoomLevel = 0.2f;
        }
        ZoomCB.DoCallbacks(new CallbackPayload(TempZoomLevel));    
            
        return true;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }
    
}
