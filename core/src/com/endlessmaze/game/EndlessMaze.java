//The Endless Maze
// Corey Wunderlich


package com.endlessmaze.game;

import com.endlessmaze.game.Maze.MazeHandler;
import com.endlessmaze.game.Maze.MazeGraphicsPage;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
//import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.endlessmaze.game.Callbacks.CallbackHandler;

import com.endlessmaze.game.Callbacks.CallbackPayload;
import com.endlessmaze.game.Callbacks.ICallback;
import com.endlessmaze.game.Controls.MenuPage;
import com.endlessmaze.game.Maze.MazeUtilities;
import com.endlessmaze.game.Modes.ClassicMazeMode;
import com.endlessmaze.game.Modes.GrowMazeMode;
public class EndlessMaze extends ApplicationAdapter {

    SpriteBatch TheBatch;
    OrthographicCamera HudCam;
    AssetHandler GameAssets;
    float CamX, CamY;

    boolean InAd = false;
    Integer MaxDistance = 0;
    int ViewPortX = 1920;
    int ViewPortY = 1080;

    MazeGraphicsPage mazePage = new MazeGraphicsPage();
    ControlOverlayPage controlsPage = new ControlOverlayPage();
    MenuPage menuPage = new MenuPage();
    TitlePage titlePage = new TitlePage();
    
    CallbackHandler<Integer> TitleScreenCB = new CallbackHandler<Integer>();
    
    enum GameStates {TITLE, DISPOSETITLE, INGAME, MENU};

    GameStates theGameState = GameStates.TITLE;

    @Override
    public void create() {
        //GLProfiler.enable();
        TheBatch = new SpriteBatch();    
        GameAssets = new AssetHandler();
        //Load all Textures Upfront
        GameAssets.LoadInitialAssets();
        MazeHandler.ResetTimer();
        
        MazeHandler.LoadStaticSounds();
        MazeHandler.InitObjectFactory();
        MazeHandler.LoadSave();
        mazePage.create(TheBatch, ViewPortX, ViewPortY);
        controlsPage.create(TheBatch, ViewPortX, ViewPortY);
        titlePage.create(TheBatch, ViewPortX, ViewPortY);
        menuPage.create(TheBatch, ViewPortX, ViewPortY);
        
        AddCallbacks();
    }

    public void Reset() {
        MazeHandler.dispose();
        mazePage.reset(controlsPage.GetResetSeed());
        controlsPage.reset();
        ClassicMazeMode.Reset();
    }

    public void NextLevel() {
        MazeHandler.dispose();
        mazePage.nextLevel();
        controlsPage.reset();
        ClassicMazeMode.NextLevel();
    }

    @Override
    public void render() {
        //GLProfiler.reset();
        switch (theGameState) {
            case TITLE:
                Gdx.gl.glClearColor(0, 0, 0, 1);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                titlePage.render();
                break;
            case DISPOSETITLE:
                titlePage.dispose();
                theGameState = GameStates.INGAME;
                renderMainGame();
                break;
            case INGAME:
                renderMainGame();
                break;
            case MENU:
                menuPage.render();
                break;
        }
        //System.out.println("DC " + GLProfiler.drawCalls);
        //System.out.println("TB " + GLProfiler.textureBindings);
    }


    public void renderMainGame() {
        Integer dist = MazeHandler.getDistanceFromStart();

        float x = MazeHandler.getCharacterX();
        float y = MazeHandler.getCharacterY();
        float r = ((float) (x % 5000)) / 10000.0f;
        float g = ((float) (y % 6000)) / 12000.0f;
        float b = (((float) dist % 7000)) / 14000.0f;

        Gdx.gl.glClearColor(r, g, b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mazePage.render();
        controlsPage.render();
        if (MazeHandler.IsMazeComplete()) {
            NextLevel();
        }

    }

    @Override
    public void dispose() {
        mazePage.dispose();
        controlsPage.dispose();
        GameAssets.dispose();
    }


    public void AddCallbacks() {
        MazeHandler.ClearStaticCallbacks();
        MazeHandler.MazeLoadStateCB.AddCallback(new ICallback<Integer>() {
            @Override
            public void Callback(CallbackPayload<Integer> val) {
                switch(val.Value)
                {
                    case 1: ControlOverlayPage.TriggerLoading(); break;
                    case 2:
                    default:
                    {                
                        ControlOverlayPage.DoneLoading();
                        if(MazeHandler.CurrentMazeMode()==MazeUtilities.MAZE_MODE) ClassicMazeMode.DoneLoading();
                        else GrowMazeMode.DoneLoading();
                    }break;
                }
            }
        });
        MazeHandler.FinishUnlockedCB.AddCallback(new ICallback<Integer>() {
            @Override
            public void Callback(CallbackPayload<Integer> val) {
                ControlOverlayPage.TriggerFinishUnlocked();
            }
        });
        MazeHandler.PlayerStateChangedCB.AddCallback(new ICallback<Integer>() {
            @Override
            public void Callback(CallbackPayload<Integer> val) {
                switch(val.Value)
                {
                    case 1:
                    default:
                    {
                        if(MazeHandler.CurrentMazeMode()!=MazeUtilities.MAZE_MODE)
                        {
                            ControlOverlayPage.SetDisplayText("Length! " + MazeHandler.getPlayerMaxPowerLevel());
                            ControlOverlayPage.TriggerDisplayText();
                        }
                        ControlOverlayPage.SetResetMode(true);
                    }break;
                }
            }
        });
        MazeHandler.GemFoundCB.AddCallback(new ICallback<Integer>() {
            @Override
            public void Callback(CallbackPayload<Integer> val) {
                switch(val.Value)
                {
                    case 1:
                    default:
                    {
                        ControlOverlayPage.SetDisplayText("Diamond Found!");
                        ControlOverlayPage.TriggerDisplayText();
                    }break;
                }
            }
        });                    
        
        controlsPage.ArrowBtnCB.AddCallback(new ICallback<Integer>() {
            @Override
            public void Callback(CallbackPayload<Integer> val) {
                if(val.Value>=0)
                    MazeHandler.move(val.Value);
                else
                    MazeHandler.stop();
            }
        });

        controlsPage.ResetBtnCB.AddCallback(new ICallback<Integer>() {
            @Override
            public void Callback(CallbackPayload<Integer> val) {
                MazeHandler.dispose();
                mazePage.reset();
                if(MazeHandler.CurrentMazeMode()==MazeUtilities.MAZE_MODE) ClassicMazeMode.Reset();
                else GrowMazeMode.Reset();
                ControlOverlayPage.SetResetMode(false);
            }
        });

        controlsPage.UseItemCB.AddCallback(new ICallback<Integer>() {
            @Override
            public void Callback(CallbackPayload<Integer> val) {
                MazeHandler.useItem(val.Value);
            }
        });

        controlsPage.UsePowerItemCB.AddCallback(new ICallback<Integer>() {
            @Override
            public void Callback(CallbackPayload<Integer> val) {
                MazeHandler.PauseTimer(true);
            }
        });

        menuPage.LevelSelectCB.AddCallback(new ICallback<MenuPage.LevelSelect>() {
            @Override
            public void Callback(CallbackPayload<MenuPage.LevelSelect> val) {
                MazeHandler.dispose();
                MazeHandler.SetSnakeColor(menuPage.GetSnakeColor());
                if(val.Value.Mode == MazeUtilities.MAZE_MODE) ClassicMazeMode.LevelSelect(val.Value);
                else GrowMazeMode.LevelSelect(val.Value);
                MazeHandler.SetMazeMode(val.Value.Mode);
                mazePage.reset(true, true);    
                theGameState = GameStates.INGAME;
                controlsPage.reset();
                controlsPage.SetAsInputProcessor();
            }
        });

        menuPage.CloseBtnCB.AddCallback(new ICallback<Integer>() {
            @Override
            public void Callback(CallbackPayload<Integer> val) {
                theGameState = GameStates.INGAME;
                controlsPage.SetAsInputProcessor();
                MazeHandler.SetSnakeColor(menuPage.GetSnakeColor());
                MazeHandler.BeginTimer();
            }
        });
        controlsPage.MenuBtnCB.AddCallback(new ICallback<Integer>() {
            @Override
            public void Callback(CallbackPayload<Integer> val) {
                theGameState = GameStates.MENU;
                MazeHandler.PauseTimer(false);
                menuPage.ClearItems();
                menuPage.SetClassicMode(MazeHandler.CurrentMazeMode());
                menuPage.SetSnakeColor(MazeHandler.GetSnakeColor());
                menuPage.AddItems(MazeHandler.GetSeeds());
                Gdx.input.setInputProcessor(menuPage);
            }
        });
        controlsPage.ZoomCB.AddCallback(new ICallback<Float>() {
            @Override
            public void Callback(CallbackPayload<Float> val) {
                mazePage.SetZoom(val.Value);
            }
        });
        titlePage.InputReceived.AddCallback(new ICallback<Integer>() {
            @Override
            public void Callback(CallbackPayload<Integer> val) {
                //titlePage.dispose();
                MazeHandler.ResetTimer();
                TitleScreenCB.DoCallbacks(val);
                controlsPage.SetAsInputProcessor();
                theGameState = GameStates.DISPOSETITLE;
                if(MazeHandler.IsDoneLoading())
                {
                    if(MazeHandler.CurrentMazeMode()==MazeUtilities.MAZE_MODE) ClassicMazeMode.Reset();
                    else GrowMazeMode.Reset();
                }
                else
                {
                    ControlOverlayPage.TriggerLoading();
                }                
            }
        });

        Gdx.input.setInputProcessor(titlePage);
    }

    public void AddMenuCallback(ICallback<Integer> cb)
    {
        controlsPage.MenuBtnCB.AddCallback(cb);
    }
    public void AddGameStartedCallback(ICallback<Integer> cb)
    {
        TitleScreenCB.AddCallback(cb);
    }
    public void AddMenuClosed(ICallback<Integer> cb)
    {
        menuPage.CloseBtnCB.AddCallback(cb);
    }

    public void PauseTimer()
    {
        MazeHandler.PauseTimer(true);
    }
    public void ResumeTimer()
    {
        MazeHandler.BeginTimer();
    }

    public void SetInAd()
    {
        InAd = true;
    }
    public boolean InMenu(){ return theGameState==GameStates.MENU; }
    public void AddResetCallback(ICallback<Integer> cb)
    {
        controlsPage.ResetBtnCB.AddCallback(cb);
    }

    public void AddLevelSelectCallback(ICallback<MenuPage.LevelSelect> cb) {
        menuPage.LevelSelectCB.AddCallback(cb);
    }
    
    public void AddActionItemCB(ICallback<Integer> cb)
    {
        controlsPage.UsePowerItemCB.AddCallback(cb);
    }
    public void OnReturnFromRewardAd(int bRewarded)
    {
        ControlOverlayPage.DoneLoading();
        if(bRewarded==1) {
            for(int i=0;i<4;i++) {
                MazeHandler.AddBombToPlayer();
            }
        }
        else if(bRewarded==2) {
            ControlOverlayPage.SetDisplayText("Could Not Load Ad. Check Network.");
            ControlOverlayPage.TriggerDisplayText();
        }
        InAd = false;
        MazeHandler.BeginTimer();
    }
    
    public void OnReturnFromInterstitial()
    {
        if(MazeHandler.CurrentMazeMode()==MazeUtilities.MAZE_MODE) ClassicMazeMode.OnReturnFromInterstitial();
        else GrowMazeMode.OnReturnFromInterstitial();
        InAd = false;
    }

}
