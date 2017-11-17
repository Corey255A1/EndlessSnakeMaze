/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.endlessmaze.game.Maze;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.endlessmaze.game.MazePlayers.MazePlayer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.endlessmaze.game.AssetHandler;
import com.endlessmaze.game.Callbacks.CallbackHandler;
import com.endlessmaze.game.Callbacks.CallbackPayload;
import com.endlessmaze.game.Callbacks.ICallback;
import com.endlessmaze.game.Controls.Timer;
import com.endlessmaze.game.IPlayerModifier;
import java.util.ArrayList;
import com.badlogic.gdx.utils.StringBuilder;
import com.endlessmaze.game.StateHandler;
/**
 *
 * @author Corey
 */
public class MazeHandler {


    private static MazeList TheMazeList;
    static MazePlayer _Player;
    static Sound FinishLevel= null;
    private static MazeLink _CurrentLink;
    public static final int SCALE = 64;
    private static float Zoom = 1.0f;
    private static int MaxDistance = 0;
    private static Coord _FinishLine;
    private static Coord _DiamondPoint;
    private static boolean MazeComplete = false;
    private static boolean FinishUnlockedTrigger = false;
    private static boolean DiamondFound = false;
    private static boolean DoneLoading = false;
    private static boolean PlayerHasMoved = false;
    private static Timer TimerFormatter = new Timer();
    private static int ViewX,ViewY;
    private static StateHandler TheStateHandler;
    
    private static final int KEY_ID  = 2;
    private static final int PICK_ID = 1;
    private static final int BOMB_ID = 0;
    
    private static int MazeMode = MazeUtilities.MAZE_MODE;
    
    
    public static CallbackHandler<Integer> MazeLoadStateCB = new CallbackHandler<Integer>();
    public static CallbackHandler<Integer> FinishUnlockedCB = new CallbackHandler<Integer>();
    public static CallbackHandler<Integer> GemFoundCB = new CallbackHandler<Integer>();
    public static CallbackHandler<Integer> PlayerStateChangedCB = new CallbackHandler<Integer>();
    public static CallbackHandler<Integer> BeginLoadingCB = new CallbackHandler<Integer>();
    public static void ClearStaticCallbacks()
    {
        MazeLoadStateCB.ClearCallbacks();
        FinishUnlockedCB.ClearCallbacks();
        GemFoundCB.ClearCallbacks();
        PlayerStateChangedCB.ClearCallbacks();
    }
    public static void LoadStaticSounds()
    {
        FinishLevel = AssetHandler.GetInstance().GetSound("data/sounds/FinishLevel.ogg");
    }

    public static void LoadSave() {
        _Player = null;
        MazeMode = MazeUtilities.MAZE_MODE;
        TheStateHandler = new StateHandler();
    }

    public static ArrayList<MazeLevelInfo> GetSeeds() {
        return TheStateHandler.StoredSeeds;
    }

    public static void SetSeed(long seed) {
        TheStateHandler.TheSeed = seed;
    }

    public static void SetLevel(int level) {
        TheStateHandler.Level = level;
    }

    public static void SetZoom(float zoom) {
        Zoom = zoom;
        TheMazeList.SetZoom(zoom);
    }
    public static boolean IsDoneLoading()
    {
        return DoneLoading;
    }
    
    private static void CreateClassicMazeComponents()
    {
        _FinishLine = MazeUtilities.GenerateFinishRegion();
        _FinishLine.X+=ViewX/2;
        _FinishLine.Y+=ViewY/2;
        //System.out.println(_FinishLine);
                  
        _DiamondPoint = MazeUtilities.GenerateDiamondPoint(10000);
        _DiamondPoint.X+=ViewX/2;
        _DiamondPoint.Y+=ViewY/2;
        
        MazeObjectFactory.AddNewCoordMazeObjectType(//Diamond
        new MazeObjectCoordType(
            SCALE, //size
            _DiamondPoint.X,//Center Point
            _DiamondPoint.Y,
            500, //Region Size
            128, //TextureScale
            "data/imgs/AllMazePieces.atlas", //texture
            "diamond",
            "data/sounds/DiamondFound.ogg",//audio
            new IPlayerModifier() { // action
                @Override
                public void ActionToPlayer(MazePlayer p) 
                {
                    DiamondFound=true;
                    TheStateHandler.SetDiamondFound();
                    GemFoundCB.DoCallbacks(new CallbackPayload<Integer>(1));
                }
            }));      
    }
    
    private static void CreateMaze(int x, int y, boolean classic)
    {
        MazeUtilities.InitRandomizer(TheStateHandler.TheSeed, TheStateHandler.Level);
        MazeObjectFactory.StartFactory(TheStateHandler.TheSeed);  
        MazeObjectFactory.ClearMazeObjectCoords();   
         
        if(classic) CreateClassicMazeComponents();
        
        //Begin Loading Maze
        MazeLoadStateCB.DoCallbacks(new CallbackPayload<Integer>(1));
        TheMazeList = new MazeList(SCALE,x,y,200,ViewX,ViewY,Zoom,MazeUtilities.GetAColor());
        TheMazeList.CreateLinks(new ICallback<Integer>() {
            @Override
            public void Callback(CallbackPayload<Integer> val) {

                ResetTimer();
                MazeLoadStateCB.DoCallbacks(new CallbackPayload<Integer>(2));
                DoneLoading = true;
            }
        });                      
    }
    
    public static int CurrentMazeMode()
    {
        return MazeMode;
    }
    public static void SetMazeMode(int mode)
    {
        MazeMode = mode;
    }

    public static void SetupMaze(int x, int y, int viewX, int viewY, boolean resetMaze, boolean newPlayer)
    {
        if(MazeMode==MazeUtilities.MAZE_MODE) SetupClassicMaze(x, y, viewX, viewY, resetMaze, newPlayer);
        else  
        {
            _FinishLine = null;
            _DiamondPoint = null;            
            SetupGrowMode(x, y, viewX, viewY, resetMaze, newPlayer);
        }
    }
    
    public static void SetupGrowMode(int x, int y, int viewX, int viewY, boolean resetMaze, boolean newPlayer)
    {
        ResetTimer();
        ViewX = viewX;
        ViewY = viewY;
        DoneLoading = false;
        MazeComplete = false;
        CreateMaze(x, y, false);     
        TheStateHandler.ResetLength = 10;
        PlayerHasMoved = false;
        _Player = new MazePlayer(SCALE,x,y,TheStateHandler.CurrentColor);
        _Player.SetSizeToComplete(TheStateHandler.SizeToComplete);
        _Player.SetPowerLevel(TheStateHandler.ResetLength);
        _Player.PlayerDiedCB.AddCallback(new ICallback<Integer>() {
            @Override
            public void Callback(CallbackPayload<Integer> val) {
                PlayerStateChangedCB.DoCallbacks(new CallbackPayload<Integer>(1));
                long time = PauseTimer(false);
                int length = getPlayerMaxPowerLevel();
                switch(MazeMode)
                {
                   case MazeUtilities.ONE_MODE: TheStateHandler.SaveANewTimedLength(MazeMode,length); break;
                   case MazeUtilities.TWO_MODE: TheStateHandler.SaveANewTimedLength(MazeMode,length); break;
                   case MazeUtilities.FIVE_MODE: TheStateHandler.SaveANewTimedLength(MazeMode,length); break;
                   case MazeUtilities.ENDLESS_MODE: TheStateHandler.SaveNewTimeLength(time, length); break;
                    
                }
            }
        });                 
        AddHammersToPlayer(2);       
        _CurrentLink = TheMazeList.GetCurrentLink();
    }
    public static void SetupClassicMaze(int x, int y, int viewX, int viewY, boolean resetMaze, boolean newPlayer)
    {
        
        ViewX = viewX;
        ViewY = viewY;
        DoneLoading = false;
        MazeComplete = false;        
        
        if(!newPlayer) TheStateHandler.NextLevel();   
        
        TheStateHandler.SaveSeed();        

        if(resetMaze){
            if(newPlayer) ResetTimer();
            CreateMaze(x, y, true);
        }
        else{
            PauseTimer(true);
            TheMazeList.Reset(); 
            DoneLoading=true;
        }        
       
        DiamondFound = TheStateHandler.GetIsDiamondFound();
        
        int hams;
        if(newPlayer)
        {            
            hams=2;
            if(_Player!=null) TheStateHandler.ResetLength = 10;
            
            _Player = new MazePlayer(SCALE,x,y,TheStateHandler.CurrentColor);
            _Player.SetSizeToComplete(TheStateHandler.SizeToComplete);
            _Player.SetPowerLevel(TheStateHandler.ResetLength);
            _Player.PlayerDiedCB.AddCallback(new ICallback<Integer>() {
                @Override
                public void Callback(CallbackPayload<Integer> val) {
                    PlayerStateChangedCB.DoCallbacks(new CallbackPayload<Integer>(1));
                    PauseTimer(false);
                }
            });
            
        }
        else
        {
            hams=1;
            FinishUnlockedTrigger = false;
             _Player.SetSizeToComplete(TheStateHandler.SizeToComplete);
            TheStateHandler.ResetLength = _Player.GetPowerLevel();
            _Player.Reset(x,y);            
        }
        
        TheStateHandler.SaveLevel(_Player.GetPowerLevel());
        PlayerHasMoved = false;
        AddHammersToPlayer(hams);
       
        _CurrentLink = TheMazeList.GetCurrentLink();      
    }
    public static Color GetSnakeColor()
    {
        return TheStateHandler.CurrentColor;
    }
    public static void SetSnakeColor(Color c)
    {
        TheStateHandler.SaveColor(c);
        _Player.SetColor(c);
    }
    public static boolean ShieldActive()
    {
        return _Player.IsShieldUp();
    }
    public static void SetSizeToComplete(int size)
    {
        TheStateHandler.SizeToComplete = size;
    }
    public static void SetResetLength(int size)
    {
        TheStateHandler.ResetLength = size;
    }
    public static int GetSizeToComplete()
    {
        return TheStateHandler.SizeToComplete;
    }
    public static void ResetMazeComplete()
    {
        MazeComplete=false;
    }
    public static boolean IsMazeComplete()
    {
        return MazeComplete;
    }
    public static boolean IsMazeUnlocked()
    {
        return FinishUnlockedTrigger;
    }
    public static void ResetMazeUnlocked()
    {
        FinishUnlockedTrigger=false;
    }
    public static boolean PlayerStarted()
    {
        return PlayerHasMoved;
    }
    
    private static void MazeModeChecks()
    {
        if(MazeMode==MazeUtilities.MAZE_MODE)
        {
            if( _Player.GetPowerLevel()>=TheStateHandler.SizeToComplete)
            {
                if(FinishUnlockedTrigger==false)
                {
                    FinishUnlockedCB.DoCallbacks(new CallbackPayload<Integer>(0));
                }
                FinishUnlockedTrigger = true;
            }
        }
        else if(_Player.GetPowerLevel()>0)
        {
            if(MazeMode==MazeUtilities.ONE_MODE)
            {
                 if(TimerFormatter.GetTimeMs()>=60000)
                {
                    _Player.SetPowerLevel(0);
                }
            }
            else if(MazeMode==MazeUtilities.TWO_MODE)
            {
                 if(TimerFormatter.GetTimeMs()>=120000)
                {
                    _Player.SetPowerLevel(0);
                }
            }
            else if (MazeMode==MazeUtilities.FIVE_MODE)
            {
                if(TimerFormatter.GetTimeMs()>=300000)
                {
                    _Player.SetPowerLevel(0);
                }
            }
        }
    }
    public static void draw(SpriteBatch batch, OrthographicCamera cam)
    {                
        MazeModeChecks();
        
        TheMazeList.draw(batch,_Player.GetX(),_Player.GetY(),FinishUnlockedTrigger);
        
        batch.maxSpritesInBatch = 0;
        
        _Player.draw(batch,cam);
 
        if(_Player.DoneSmoothing() && _CurrentLink!=null)
        {
            if(_CurrentLink._LinkType == MazeLinkType.FINISH)
            {
                if(FinishUnlockedTrigger && !MazeComplete)
                {
                    MazeComplete = true;
                    long Time = PauseTimer(true);
                    //System.out.println(Time);
                    TheStateHandler.SaveNewTime(Time);
                    ResetTimer();
                }
            }
            MazeObject mo = TheMazeList.ObjectAtCurrentLink();
            if(mo != null)
            {
                mo.PerformAction(_Player);
                TheMazeList.RemoveObjectAtCurrentLink();
            }
            
            move(_Player.GetDirection());

        }
        //--
    }    
    public static void dispose()
    {
        TheMazeList.dispose();
    }    
    public static void stop()
    {
        _Player.stop();
    }
    public static long nextLevel()
    {
        TheStateHandler.TheSeed = (_FinishLine.X + _FinishLine.Y + TheStateHandler.TheSeed*10)/3%1061189142;

        return TheStateHandler.TheSeed;
    }
    public static void playNextLevelSound()
    {
        FinishLevel.play();
    }
    public static void move(int direction)
    {
        if(_Player.GetPowerLevel()<=0) return;
        if(direction>=0 && _Player.DoneSmoothing())
        {
            MazeLink m = TheMazeList.move(direction);
            if(m!=null)
            {
                _CurrentLink.MarkPiece();
                _CurrentLink = m;                
                if(_Player.move(direction, (int)m.GetX(), (int)m.GetY()) && MazeMode!=MazeUtilities.MAZE_MODE)
                {
                    _Player.SetPowerLevel(0);
                }
                if(!PlayerHasMoved) {PlayerHasMoved = true; BeginTimer();}
               // _Maze.previewMove(direction); //look ahead to the next link
               int dist = getDistanceFromStart();
               if(dist>MaxDistance) MaxDistance = dist;
            }
            else
            {
                _Player.stop();
            }
        }
        else
        {
            _Player.stop();
        }
    }
    public static int getPlayerPowerLevel() { return _Player.GetPowerLevel(); }
    public static int getPlayerMaxPowerLevel() { return _Player.GetMaxPowerLevel(); }
    public static void lookAhead()
    {
        if(_Player.GetPowerLevel()>10)
        {
            int dir =_Player.GetLastDirection();
            TheMazeList.lookAhead(dir);
            _Player.UsePower(10,dir,false);
        }        
    }
    public static void forgeAhead(boolean bFree)
    {
        if(bFree || _Player.GetPowerLevel()>50)
        {
            int dir =_Player.GetLastDirection();
            TheMazeList.changeAreaPieces((int)_Player.GetX(), (int)_Player.GetY(),2);
            if(bFree) _Player.SetPowerLevel(_Player.GetPowerLevel()+50);
            _Player.UsePower(50,-2,bFree);
        }  
    }
    public static void createBridges()
    {
        TheMazeList.changeAreaPieces((int)_Player.GetX(), (int)_Player.GetY(),0);
        TheMazeList.buildBridge();
    }
    public static void useItem(int item)
    {
        _Player.UseItem(item);
    }
    public static void usePowerItem(int item)
    {
        _Player.UsePowerItem(item);
    }    
    public static MazeObject[] getPlayerItems()
    {
        return _Player.GetItems();
    }
    public static MazeObject[] getPlayerPowerItems()
    {
        return _Player.GetPowerItems();
    }
    public static int getDistanceFromStart()
    {        
        return _Player.GetDistanceFromStart();
    }
    public static int getDistanceToFinish()
    {
        if(_FinishLine==null) return 0;
        
        if(TheMazeList._FoundFinish)
        {
        return MazeUtilities.GetDistance(TheMazeList.FX, TheMazeList.FY, _Player.GetX(), _Player.GetY());
        }
        else
        {
        return MazeUtilities.GetDistance(_FinishLine.X, _FinishLine.Y, _Player.GetX(), _Player.GetY());
        }
    }
    public static float getHeadingToFinish()
    {   if(_FinishLine==null) return 0;
        if(TheMazeList._FoundFinish)
        {
            return (MazeUtilities.GetAngle(TheMazeList.FX, TheMazeList.FY, _Player.GetX(), _Player.GetY())*180.0f)/3.14f+90.0f;
        }
        else
        {
            return (MazeUtilities.GetAngle(_FinishLine.X, _FinishLine.Y, _Player.GetX(), _Player.GetY())*180.0f)/3.14f+90.0f;
        }
    }
    public static int getMaxDistance()
    {
        return MaxDistance;
    }
    public static int getDistanceTravelled()
    {
        return _Player.GetDistanceTravelled();
    }
    public static float getCharacterX()
    {
        return _Player.GetRelX();
    }
    public static float getCharacterY()
    {
        return _Player.GetRelY();
    }
    public static void BeginTimer()
    {
        if(DoneLoading && PlayerHasMoved && _Player.GetPowerLevel()>0)
            TimerFormatter.Start();
    }    
    public static long PauseTimer(boolean stopPlayer)
    {
        if(stopPlayer) PlayerHasMoved = false;
        TimerFormatter.Pause();
        return TimerFormatter.GetTimeMs();
    }
    public static void ResetTimer()
    {
        TimerFormatter.Restart();
    }
    public static StringBuilder GetElapsedTime()
    {
        return TimerFormatter.GetElapsedTime();
    }
    
    public static void AddHammersToPlayer(int amount)
    {
        int it = _Player.AvailableItem();
        for(int j=0;j<amount&&it!=-1;j++)
        {    
            final int hit = it;
            _Player.CollectItem(
            new MazeObject(SCALE,0,0,128,
            "data/imgs/AllMazePieces.atlas", //texture
            "pick",
            "data/sounds/HammerNoise.ogg",
            new IPlayerModifier(){
                @Override
                public void ActionToPlayer(MazePlayer p) {//p.buildBridge()
                    createBridges();
                    p.RemoveItem(hit);                            
                }}));
            it = _Player.AvailableItem();
        } 
    }
    
    public static void AddBombToPlayer()
    {
        int avail = _Player.AvailableItem();
        if(avail==-1)
        {
            MazeObject its[] = _Player.GetItems();
            for(int i=0;i<its.length;i++)
            {
                if(its[i].TextureName.compareToIgnoreCase("pick")==0)
                {
                    _Player.RemoveItem(i);
                    avail = i;
                    break;
                }
            }
        }
        final int it = avail;
        _Player.CollectItem(
                new MazeObject(SCALE, 0, 0, 128,
                        "data/imgs/AllMazePieces.atlas", //texture
                        "bomb",
                        "",
                        new IPlayerModifier() {
                            @Override
                            public void ActionToPlayer(MazePlayer p) {//p.buildBridge()
                                forgeAhead(true);
                                p.RemoveItem(it);
                            }
                        }));
    }
    
    //Maze Objects
    public static void InitObjectFactory() {
        MazeObjectFactory.ClearMazeObjects();
        MazeObjectFactory.AddNewMazeObjectType( //Low Power Object
                new MazeObjectType(
                        SCALE, //size
                        70.0f, //scarity
                        80.0f,
                        -1, //Max Quantity Infinte
                        128,
                        "data/imgs/AllMazePieces.atlas", //texture
                        "item",
                        "data/sounds/Item.ogg",//audio
                        new IPlayerModifier() { // action
                            @Override
                            public void ActionToPlayer(MazePlayer p) {
                                p.IncreasePowerLevel(1, -1,false);
                            }
                        }));
        MazeObjectFactory.AddNewMazeObjectType( //High Power Object
                new MazeObjectType(
                        SCALE, //size
                        80.5f,
                        85.0f,
                        -1, //Max Quantity - Infinte
                        128,
                        "data/imgs/AllMazePieces.atlas", //texture
                        "poweritem",
                        "data/sounds/Item_Plus.ogg",//audio
                        new IPlayerModifier() { // action
                            @Override
                            public void ActionToPlayer(MazePlayer p) {
                                p.IncreasePowerLevel(10, -1,false);
                            }
                        }));
        MazeObjectFactory.AddNewMazeObjectType( //Unlock Finish
                new MazeObjectType(
                        SCALE, //size
                        99.95f, //scarity
                        100.f,
                        1, //Max Quantity
                        128,
                        "data/imgs/AllMazePieces.atlas", //texture
                        "key",
                        "data/sounds/FoundKey.ogg",//audio
                        new IPlayerModifier() { // action
                            @Override
                            public void ActionToPlayer(MazePlayer p) {
                                if(MazeMode!=MazeUtilities.MAZE_MODE)
                                {
                                    p.IncreasePowerLevel(100, -1,false); 
                                }
                                else if (p.GetPowerLevel() < TheStateHandler.SizeToComplete && !MazeComplete) {
                                    p.SetPowerLevel(TheStateHandler.SizeToComplete);
                                } else {
                                    p.IncreasePowerLevel(50, -1,false);
                                }
                            }
                        }));
        MazeObjectFactory.AddNewMazeObjectType( //Pick Object
                new MazeObjectType(
                        SCALE, //size
                        89.7f, //scarity
                        90.0f,
                        -1, //Max Quantity
                        128,
                        "data/imgs/AllMazePieces.atlas", //texture
                        "pick",
                        "data/sounds/HammerNoise.ogg",//audio
                        new IPlayerModifier() { // action
                            @Override
                            public void ActionToPlayer(MazePlayer p) {
                                final int it = p.AvailableItem();
                                p.CollectItem(
                                        new MazeObject(SCALE, 0, 0, 128,
                                                "data/imgs/AllMazePieces.atlas", //texture
                                                "pick",
                                                "data/sounds/HammerNoise.ogg",
                                                new IPlayerModifier() {
                                                    @Override
                                                    public void ActionToPlayer(MazePlayer p) {//p.buildBridge()
                                                        createBridges();
                                                        p.RemoveItem(it);
                                                    }
                                                }));
                            }
                        }));
        MazeObjectFactory.AddNewMazeObjectType( //Bomb Object
                new MazeObjectType(
                        SCALE, //size
                        94.75f, //scarity
                        95.0f,
                        -1, //Max Quantity
                        128,
                        "data/imgs/AllMazePieces.atlas", //texture
                        "bomb",
                        "data/sounds/Bomb.ogg",//audio
                        new IPlayerModifier() { // action
                            @Override
                            public void ActionToPlayer(MazePlayer p) {
                                final int it = p.AvailableItem();
                                p.CollectItem(
                                        new MazeObject(SCALE, 0, 0, 128,
                                                "data/imgs/AllMazePieces.atlas", //texture
                                                "bomb",
                                                "",
                                                new IPlayerModifier() {
                                                    @Override
                                                    public void ActionToPlayer(MazePlayer p) {//p.buildBridge()
                                                        forgeAhead(true);
                                                        p.RemoveItem(it);
                                                    }
                                                }));
                            }
                        }));
        MazeObjectFactory.AddNewMazeObjectType( //Shield
                new MazeObjectType(
                        SCALE, //size
                        69.7f, //scarity,
                        70.0f,
                        -1, //Max Quantity
                        128,
                        "data/imgs/AllMazePieces.atlas", //texture
                        "shield",
                        "data/sounds/Shield.ogg",//audio
                        new IPlayerModifier() { // action
                            @Override
                            public void ActionToPlayer(MazePlayer p) {
                                p.EnableShield();
                            }
                        }));
    } 
}
