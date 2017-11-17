/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.endlessmaze.game.MazePlayers;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.endlessmaze.game.AssetHandler;
import com.endlessmaze.game.Callbacks.CallbackHandler;
import com.endlessmaze.game.Callbacks.CallbackPayload;
import com.endlessmaze.game.IPlayerModifier;
import com.endlessmaze.game.Maze.MazeObject;
import com.endlessmaze.game.Maze.MazeUtilities;
import java.util.ArrayList;

/**
 *
 * @author Corey
 */
public class MazePlayer { 
    
    
    Sprite _Player;
    Sprite PlayerEyes;
    Sound  LoseShield;
    final int MAX_ITEMS = 4;
    final int MAX_POWERS = 1;
    MazeObject[] _Items = new MazeObject[MAX_ITEMS];
    MazeObject[] _PowerItems = new MazeObject[MAX_POWERS];
    PlayerShadowList _MainShadow;
    //TextureRegion _PlayerTexture;
    final int SCALE;   

    float STARTX, STARTY;
    float _playerX, _playerY;
    float _prevplayerX, _prevplayerY;
    float _velX, _velY;
    int _Distance = 0;
    int _DistanceTravelled=0;
    boolean _bSmoothing;
    int _SizeToComplete = 20;
    int _PowerLevel = 10;
    int _LastPowerLevel = _PowerLevel;
    int _PowerLevelMax = _PowerLevel;
    int _Direction = -1;
    int _LastDirection = -1;
    
    int _ItemCount = 0;
    int _AvailableItem = 0;
    int _PowerItem =0;
    int _PowerItemCount = 0;
    public final int SnakeRegionSize = 128;
    int _SnakeSpeed = 2;
    
    Color SnakeColor;
    
    boolean _ShieldUp = false;
    
    public CallbackHandler<Integer> PlayerDiedCB = new CallbackHandler<Integer>();
    
    public MazePlayer(int scale, int startX, int startY, Color c)
    {
        STARTX= _playerX=_prevplayerX = startX;
        STARTY=_playerY=_prevplayerY = startY;        
        SCALE = scale;    
        SnakeColor = c;
        LoseShield = AssetHandler.GetInstance().GetSound("data/sounds/RevShield.ogg");

        _MainShadow = new PlayerShadowList(SCALE,
                AssetHandler.GetInstance().GetTextureRegion("data/imgs/PlayerRegion.png", SnakeRegionSize,2,0),
                AssetHandler.GetInstance().GetTextureRegion("data/imgs/PlayerRegion.png", SnakeRegionSize,1,0), startX, startY, SnakeColor);
        _MainShadow.setSize(_PowerLevel,-1,false);
        TextureRegion t = AssetHandler.GetInstance().GetTextureRegion("data/imgs/PlayerRegion.png", SnakeRegionSize,3,0);
        PlayerEyes = new Sprite(t);
        PlayerEyes.setBounds(0, 0, SCALE, SCALE);
        PlayerEyes.setOrigin(SCALE/2, SCALE/2);
        PlayerEyes.setPosition(startX, startY);
        
        t = AssetHandler.GetInstance().GetTextureRegion("data/imgs/PlayerRegion.png", SnakeRegionSize,0,0);
        _Player = new Sprite(t);
        _Player.setBounds(0, 0, SCALE, SCALE);
        _Player.setOrigin(SCALE/2, SCALE/2);
        _Player.setPosition(startX, startY);
        _bSmoothing = false;

        _Player.setColor(SnakeColor);
        _MainShadow.SetColor(SnakeColor);
        
    }
    
    public void SetColor(Color c)
    {
        SnakeColor = c;
        _Player.setColor(SnakeColor);
        _MainShadow.SetColor(SnakeColor);
    }

    public int IncreaseSizeToComplete(int amount)
    {
        _SizeToComplete += amount;
        return _SizeToComplete;
    }
    public int GetSizeToComplete()
    {
        return _SizeToComplete;
    }
    public void SetSizeToComplete(int s)
    {
        _SizeToComplete = s;
    }
    public int GetDistanceFromStart()
    {
        _Distance = MazeUtilities.GetDistance(_Player.getX(), _Player.getY(), STARTX, STARTY);
        return _Distance;
    }
    public void EnableShield()
    {
        _ShieldUp = true;
    }
    public boolean IsShieldUp()
    {
        return _ShieldUp;
    }
    
    public int GetDistanceTravelled()
    {
        return  _DistanceTravelled;
    }
    
    public void UseItem(int item)
    {
        if(item<MAX_ITEMS)
        {
            if(_Items[item]!=null) _Items[item].PerformAction(this);
        }
    }
    public void UsePowerItem(int item)
    {
        if(item<2)
        {
            if(_PowerItems[item]!=null) _PowerItems[item].PerformAction(this);
        }
    }
    public void CollectItem(MazeObject mo)
    {
        if(_ItemCount<MAX_ITEMS)
        {
            _Items[_AvailableItem] = mo;
            if((++_ItemCount) < MAX_ITEMS)
            {
                for(int i=0; i<_Items.length;i++)
                {
                    if(_Items[i] == null)
                    {
                        _AvailableItem =i;
                        break;
                    }
                }
            }
            else
            {
                _AvailableItem = -1;
            }
        }
    }
    
    public void AddPowerItem(MazeObject mo)
    {
        _PowerItems[_PowerItem] = mo;
        if((++_PowerItemCount) < MAX_POWERS)
        {
            for(int i=0; i<_PowerItems.length;i++)
            {
                if(_PowerItems[i] == null)
                {
                    _PowerItem =i;
                    break;
                }
            }
        }
    }
    public int AvailableItem()
    {
        return _AvailableItem;
    }
    public MazeObject[] GetItems()
    {
        return _Items;
    }
    public MazeObject[] GetPowerItems()
    {
        return _PowerItems;
    }
    public void RemoveItem(int it)
    {
        _Items[it] = null;
        _ItemCount--;
        _AvailableItem = it;
        
    }  
    public void UsePower(int powerdeduction, int launchDir, boolean fromhead)
    {
        IncreasePowerLevel(-powerdeduction,launchDir, fromhead); 
    }
    public void IncreasePowerLevel(int amount, int launchDir, boolean fromhead) 
    {
        _PowerLevel += amount;
        if(_PowerLevel>=0)
        {
           if(_PowerLevelMax<_PowerLevel)
            { _PowerLevelMax = _PowerLevel;}
           
           _MainShadow.setSize(_PowerLevel,launchDir, fromhead);
           if(_PowerLevel==0)
           {
               PlayerDiedCB.DoCallbacks(new CallbackPayload<Integer>(0));
           }
        }
    }
    public void SetPowerLevel(int amount) 
    {
        _PowerLevel = amount;
        _MainShadow.setSize(_PowerLevel,-1,false);
        if(_PowerLevel==0)
        {
            PlayerDiedCB.DoCallbacks(new CallbackPayload<Integer>(0));
        }
    }
    public int GetPowerLevel() { return _PowerLevel; }
    public int GetLastPowerLevel() { return _LastPowerLevel; }
    public int GetMaxPowerLevel() { return _PowerLevelMax; }
    public void Reset(int x, int y)
    {
        _bSmoothing = false; 
        _playerX =x;
        _playerY = y;
        _velX = x;
        _velY = y;
        _prevplayerX = x;
        _prevplayerY = y;
        _Player.setPosition(x, y);
        PlayerEyes.setPosition(x, y);        
        _MainShadow.ResetPositions(x, y);
        _LastDirection = -1;
        _Direction = -1;
        STARTX = x;
        STARTY= y;
    }
    public void draw(SpriteBatch batch, OrthographicCamera cam)
    {   
        _MainShadow.draw(batch);
        _Player.draw(batch);
        PlayerEyes.draw(batch);
        if(_bSmoothing)
        {           
             cam.translate(_velX, _velY);
             cam.update();
            _Player.setX(_prevplayerX + _velX);
            _Player.setY(_prevplayerY + _velY);
            PlayerEyes.setX(_prevplayerX + _velX);
            PlayerEyes.setY(_prevplayerY + _velY);
            _MainShadow.setPos(_prevplayerX, _prevplayerY);
            _prevplayerX =_Player.getX();
            _prevplayerY = _Player.getY();
            if(_prevplayerX==_playerX && _prevplayerY==_playerY)
            { _bSmoothing = false; }
        }

    }
    
    public boolean DoneSmoothing()
    {
        return !_bSmoothing;
    }
    
    public void stop()
    {
        _Direction = -1;
    }
    
    public int GetDirection()
    {
        return _Direction;
    }
    public int GetLastDirection()
    {
        return _LastDirection;
    }
    
    public boolean move(int direction, int x, int y)
    {       
        int tailCollide;
        boolean CollisionOccured = false;
        _playerX = x;
        _playerY = y;
        if(_LastDirection == MazeUtilities.GetOppositeDirection(direction))
        {            
            tailCollide = _MainShadow.getCount();
        }
        else
        {
          tailCollide = _MainShadow.check(_playerX,_playerY);
        }
        if(tailCollide>=0)
        {
            if(!_ShieldUp)
            {
                _LastPowerLevel = _PowerLevel;
                IncreasePowerLevel(-(tailCollide),-1,false);
                CollisionOccured = true;
            }
            else
            {
                LoseShield.play();
                _ShieldUp = false;
            }
        }
        if(direction!=-1)
        {
         _Player.setRotation(direction*-90);
         PlayerEyes.setRotation(direction*-90);
        }
        _LastDirection = direction;
        _Direction = direction;

        _prevplayerX =_Player.getX();
        _prevplayerY = _Player.getY();

        _velX =(_playerX-_prevplayerX)/(_SnakeSpeed<<2);
        _velY =(_playerY-_prevplayerY)/(_SnakeSpeed<<2);
        _DistanceTravelled+=SCALE;
        _bSmoothing = true;
        return CollisionOccured;
    }
    
    public float GetX()
    {
        return _Player.getX();
    }
    
    public float GetY()
    {
        return _Player.getY();
    }
    
    public float GetRelX()
    {
        return _Player.getX()-STARTX;
    }
    
    public float GetRelY()
    {
        return _Player.getY()-STARTY;
    }
    
    public float GetStartX()
    {
        return STARTX;
    }
    
    public float GetStartY()
    {
        return STARTY;
    }    
}
