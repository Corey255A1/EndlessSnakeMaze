/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.endlessmaze.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.HashMap;

/**
 *
 * @author Corey
 */
public class AssetHandler {
    AssetManager Manager = new AssetManager();
    //HashMap<String, Texture> TextureMap = new HashMap<String, Texture>();
    //HashMap<String, TextureRegion> TextureRegionMap = new HashMap<String, TextureRegion>();
    //HashMap<String, Sound> SoundMap = new HashMap<String, Sound>();
    HashMap<String, TextureRegion> LoadedRegionMap = new HashMap<String, TextureRegion>();
    static AssetHandler Singleton;
    TextureParameter TParam = new TextureParameter();
    TextureParameter MParam = new TextureParameter();
    
    public AssetHandler()
    {
        Singleton = this;
        TParam.genMipMaps = false;
        TParam.magFilter = Texture.TextureFilter.Nearest;
        TParam.minFilter = Texture.TextureFilter.Nearest;
        
        MParam.genMipMaps = false;
        MParam.magFilter = Texture.TextureFilter.Linear;
        MParam.minFilter = Texture.TextureFilter.Linear;
    }
    public static AssetHandler GetInstance()
    {
        return Singleton;
    }
    
    public void LoadInitialAssets()
    {
        this.LoadTexture("data/imgs/FinishUnlocked.png");
        this.LoadTexture("data/imgs/FinishClosed.png");
        this.LoadTexture("data/imgs/ctrls/MenuItem.png");
        this.LoadTexture("data/imgs/ctrls/Compass.png");
        this.LoadTexture("data/imgs/ctrls/TextMessageBackground.png");
        this.LoadTexture("data/imgs/TitleScreen.png");
        this.LoadTexture("data/imgs/TapToBegin.png");
        this.LoadTexture("data/imgs/PlayerRegion.png");
        this.LoadTexture("data/imgs/ctrls/CtrlRegion.png");
        this.LoadTexture("data/imgs/ctrls/Reset.png");
        this.LoadTexture("data/imgs/ctrls/Loading.png");      
        this.LoadTexture("data/imgs/ctrls/ScrollBar.png");
        this.LoadTexture("data/imgs/ctrls/Scroll.png");
        this.LoadTexture("data/imgs/ctrls/SnakeColor.png");
        this.LoadSound("data/sounds/FinishLevel.ogg");
        this.LoadSound("data/sounds/RevShield.ogg");
        this.LoadSound("data/sounds/LosePower.ogg");
        this.LoadSound("data/sounds/HammerNoise.ogg");
        this.LoadSound("data/sounds/FoundKey.ogg");
        this.LoadSound("data/sounds/Item_Plus.ogg");
        this.LoadSound("data/sounds/Item.ogg");
        this.LoadSound("data/sounds/Bomb.ogg");
        this.LoadSound("data/sounds/Shield.ogg");
        this.LoadSound("data/sounds/DiamondFound.ogg");
        this.LoadMusic("data/sounds/SnakeTitle.ogg");
        this.LoadTextureAtlas("data/imgs/ani/PoofAni.atlas");
        this.LoadTextureAtlas("data/imgs/AllMazePieces.atlas");
        this.FinishLoading();
    }
    public void LoadTexture(String texture)
    {
        LoadTexture(texture, false);
    }
    public void LoadTexture(String texture, boolean blending)
    {
        Manager.load(texture, Texture.class);
        if(blending) Manager.load(texture, Texture.class, TParam);
        else Manager.load(texture, Texture.class, MParam);
    }
    public void FinishLoading()
    {
        Manager.finishLoading();
    }
    
    public Texture GetTexture(String texture)
    {        
        Texture t = Manager.get(texture,Texture.class);
        return t;
    }    
    public TextureRegion GetTextureRegion(String texture, int size, int x, int y)
    {
        Texture t = Manager.get(texture,Texture.class);
        TextureRegion[][] regions = TextureRegion.split(t, size, size);
        return regions[y][x];
    }
    public void LoadSound(String sound)
    {
        Manager.load(sound,Sound.class);
    }
    public Sound GetSound(String sound)
    {
        return Manager.get(sound,Sound.class);
    }
    public void LoadMusic(String audio)
    {
        Manager.load(audio, Music.class);
    }
    public Music GetMusic(String audio)
    {
        return Manager.get(audio,Music.class);
    }
    public void LoadTextureAtlas(String path)
    {
        Manager.load(path, TextureAtlas.class);
    }
    public TextureAtlas GetTextureAtlas(String path)
    {
        return Manager.get(path, TextureAtlas.class);
    }
    public TextureRegion GetRegionFromAtlas(String path, String name)
    {
        if(LoadedRegionMap.containsKey(name))
        {
            return LoadedRegionMap.get(name);
        }
        else
        {
            TextureAtlas ta = Manager.get(path, TextureAtlas.class);
            TextureRegion s = ta.findRegion(name);
            LoadedRegionMap.put(name,s);
            return s;
        }
    }
    public void dispose()
    {
        Manager.dispose();

    }
}
