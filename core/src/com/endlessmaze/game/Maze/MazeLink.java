package com.endlessmaze.game.Maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.endlessmaze.game.AssetHandler;

import java.util.ArrayList;

/**
 *
 * @author Corey
 */
public class MazeLink
{
    public final int SCALE;
    public final int TEXTURE_SCALE=128;
    public final int _Rotation;
    public int AVAILABLE_CONNECTIONS;
    public MazeLinkType _LinkType;
    public int X,Y;
    public static MazeLink EmptyMazeLink = new MazeLink(0,MazeLinkType.NULL);
 
    static final float Overlap=1.0f;//1.0725f; //60/64
   
    MazeObject _LocalObject;
    MazeLink[] _MazeConnections;
    
    float _Alpha;
    int _ConnectionsLeft;
    TextureRegion MyTexture;
    Sprite MySprite;
    Texture ModifierTexture;
    Color MyColor = Color.GREEN;
    static Color MarkingColor = Color.SKY;
    
    // ----- MEMBER FUNCTIONS -----
    public MazeLink(int scale, MazeLinkType type)
    {
        this(scale, type, MazeUtilities.NORTH);
    }
    public MazeLink(int scale, MazeLinkType type, int rotation)
    {
        this(scale, type, rotation, 0, 0, Color.GREEN);
    }
    public MazeLink(int scale, MazeLinkType type, int rotation, float x, float y, Color c)
    {
        _Rotation = rotation;
        _LinkType = type;
        _Alpha = 0.0f;
        SCALE = scale;
        X = (int)x;
        Y = (int)y;
        MyColor = new Color(c);
        if(_LinkType!=MazeLinkType.NULL)
        {
            SetLinkType(_LinkType);
        }
    }
    public Coord GetPoint()
    {
        return new Coord((int)X,(int)Y);
    }
    
    private void SetLinkType(MazeLinkType type)
    {
        _LinkType = type;
        String linkTex = "";
        switch(type)
        {
            case STRAIGHT:
                linkTex = "straight";
                _ConnectionsLeft = AVAILABLE_CONNECTIONS = 2;
                _MazeConnections = new MazeLink[AVAILABLE_CONNECTIONS];
                break;
            case TURNL:
            case TURNR:
                linkTex = "turn";
                _ConnectionsLeft = AVAILABLE_CONNECTIONS = 2;
                _MazeConnections = new MazeLink[AVAILABLE_CONNECTIONS];
                break;
            case TEE:
                linkTex = "t";
                _ConnectionsLeft = AVAILABLE_CONNECTIONS = 3;
                _MazeConnections = new MazeLink[AVAILABLE_CONNECTIONS];
                break;
            case CROSS:
                linkTex = "cross";
                _ConnectionsLeft = AVAILABLE_CONNECTIONS = 4;
                _MazeConnections = new MazeLink[AVAILABLE_CONNECTIONS];
                break;   
            case START:
                linkTex = "start";
                _ConnectionsLeft = AVAILABLE_CONNECTIONS = 4;
                _MazeConnections = new MazeLink[AVAILABLE_CONNECTIONS];
                break;    
            case FINISH:
                linkTex = "finish";
                ModifierTexture = AssetHandler.GetInstance().GetTexture("data/imgs/FinishClosed.png");
                _ConnectionsLeft = AVAILABLE_CONNECTIONS = 4;
                _MazeConnections = new MazeLink[AVAILABLE_CONNECTIONS];
                break;     
            default:
                _ConnectionsLeft = AVAILABLE_CONNECTIONS = 0;
                break;             
        }
        
        if(!linkTex.isEmpty())
        {
            MyTexture = AssetHandler.GetInstance().GetRegionFromAtlas("data/imgs/AllMazePieces.atlas",linkTex);
            MySprite = new Sprite(MyTexture);
            MySprite.setBounds(0, 0, SCALE, SCALE);
            MySprite.setScale(Overlap);
            MySprite.setOrigin((SCALE)/2f, (SCALE)/2f);
            MySprite.setRotation((int)MazeUtilities.RotationToDegrees(_Rotation));
        
            if(_LinkType != MazeLinkType.START && _LinkType != MazeLinkType.FINISH)
            {MySprite.setColor(MyColor);}
            if(_LinkType == MazeLinkType.TURNR) MySprite.flip(true,false);
            MySprite.setPosition((int)X, (int)Y);    
        }
    }
    
    public void MarkPiece()
    {
        MySprite.setColor(MarkingColor);
    }
    public Color GetColor()
    {
        return MyColor;
    }

    public int GetDirectionOfConnection(int connection)
    {
        //Messy AF!
        switch(_LinkType)
        {
            case START:
            case FINISH:
            case CROSS:
                return (_Rotation+connection-1)%4;
            case TEE:
                switch(connection)
                {
                    case 1: return (_Rotation+3)%4;
                    case 2:  return (_Rotation+1)%4;
                    case 3:  return (_Rotation+2)%4;
                }break;
            case TURNL: 
                return connection==1 ? (_Rotation+3)%4 : (_Rotation+2)%4;
            case TURNR: return connection==1 ? (_Rotation+1)%4  : (_Rotation+2)%4;
            case STRAIGHT: return connection==1 ? _Rotation : (_Rotation+2)%4; 
            default: 
                return _Rotation;
        }
        return _Rotation;
    }
    private MazeLink FilterCapper(int connection)
    {
        if(_MazeConnections[connection] == null)
        {            
            return null;
        }
        else
        {
            return _MazeConnections[connection];
        }
    }
    public MazeLink GetConnectionFromDirection(int direction)
    {
        int adjusted = (direction + (4-_Rotation)) % 4;
        //System.out.println(_LinkType);
        //System.out.println("adjusted " + adjusted);
        //System.out.println(this._MySprite.getX() + " " + this._MySprite.getY());
        //System.out.println(this.ConnectionsLeft());
        switch(_LinkType)
        {
            case START:                
            case FINISH:
            case CROSS:
                if(adjusted==0) return FilterCapper(0);
                if(adjusted==1) return FilterCapper(1);
                if(adjusted==2) return FilterCapper(2);
                if(adjusted==3) return FilterCapper(3);
                break;
            case TEE:
                if(adjusted==1) return FilterCapper(1);
                if(adjusted==2) return FilterCapper(2);
                if(adjusted==3) return FilterCapper(0);
                break;
            case TURNL:
                if(adjusted==2) return FilterCapper(1);
                if(adjusted==3) return FilterCapper(0);
                break;
            case TURNR:
                if(adjusted==1) return FilterCapper(0);
                if(adjusted==2) return FilterCapper(1);
                break;
            case STRAIGHT:
                if(adjusted==0) return FilterCapper(0);
                if(adjusted==2) return FilterCapper(1);
                break;
            default:
                return EmptyMazeLink;
        }
        return EmptyMazeLink;
    }
    
    public void IncreaseLinks()
    { 
        MazeLink[] oMl = _MazeConnections;
        switch(_LinkType)
        {
            
            case TURNL:
            {
                _MazeConnections = new MazeLink[3];
                AVAILABLE_CONNECTIONS = 3;
                _MazeConnections[0] = oMl[0];
                _LinkType = MazeLinkType.TEE;                
                MyTexture= AssetHandler.GetInstance().GetRegionFromAtlas("data/imgs/AllMazePieces.atlas","t");

                MySprite = new Sprite(MyTexture);
                MySprite.setColor(MyColor);
                MySprite.setBounds(0, 0, SCALE, SCALE);
                MySprite.setScale(Overlap);
                MySprite.setOrigin(SCALE/2f, SCALE/2f);
                MySprite.setRotation((int)MazeUtilities.RotationToDegrees(_Rotation));
                MySprite.setPosition((int)X, (int)Y);
                //MarkPiece();
                break;
            }
            /*
            case TURNR:
            {
                System.out.println("TEE");
                _MazeConnections = new MazeLink[3];
                AVAILABLE_CONNECTIONS = 3;
                _MazeConnections[1] = oMl[0];
                _LinkType = MazeLinkType.TEE;
                MyTexture = AssetHandler.GetInstance().GetTextureRegion("data/imgs/MazePiecesRegion.png",SCALE,2,0);
               MySprite = new Sprite(MyTexture);
                MySprite.setColor(MyColor);
                MySprite.setBounds(0, 0, SCALE, SCALE);
                MySprite.setOrigin(SCALE/2, SCALE/2);
                MySprite.setRotation(MazeUtilities.RotationToDegrees(_Rotation));
                MySprite.setPosition(X, Y);
                MarkPiece();
                break;
            }*/
            case STRAIGHT:
            {
                _MazeConnections = new MazeLink[4];
                _LinkType = MazeLinkType.CROSS;
                AVAILABLE_CONNECTIONS = 4;
                //_ConnectionsLeft++;
                _MazeConnections[2] = oMl[1];
                _MazeConnections[0] = oMl[0];
                MyTexture = AssetHandler.GetInstance().GetRegionFromAtlas("data/imgs/AllMazePieces.atlas","cross");

                MySprite = new Sprite(MyTexture);
                MySprite.setColor(MyColor);
                MySprite.setBounds(0, 0, SCALE, SCALE);
                MySprite.setScale(Overlap);
                MySprite.setOrigin(SCALE/2f, SCALE/2f);
                MySprite.setRotation((int)MazeUtilities.RotationToDegrees(_Rotation));
                MySprite.setPosition((int)X, (int)Y); 
                break;
            }
        }
        
    }
    
    public void SetConnection(int connection, MazeLink linkToSet)
    {
        if(connection-1<AVAILABLE_CONNECTIONS)
            _MazeConnections[connection-1] = linkToSet;
    }
    
    public void SetConnectionAtDirection(int direction, MazeLink linkToSet)
    {
        int adjusted = (direction + (4-_Rotation)) % 4;
        //System.out.println(_LinkType);
        //System.out.println("adjusted " + adjusted);
        //System.out.println(this._MySprite.getX() + " " + this._MySprite.getY());
        //System.out.println(this.ConnectionsLeft());
        switch(_LinkType)
        {
            case START:                
            case CROSS:
            case FINISH:
                if(adjusted==0) _MazeConnections[0] = linkToSet;                 
                if(adjusted==1) _MazeConnections[1] = linkToSet;
                if(adjusted==2) _MazeConnections[2] = linkToSet; 
                if(adjusted==3) _MazeConnections[3] = linkToSet;
                 break;
            case TEE:
                if(adjusted==3) _MazeConnections[0] = linkToSet;
                if(adjusted==1) _MazeConnections[1] = linkToSet;
                if(adjusted==2) _MazeConnections[2] = linkToSet; 
                 break;
            case TURNL:
                if(adjusted==3) _MazeConnections[0] = linkToSet;
                if(adjusted==2) _MazeConnections[1] = linkToSet; 
                 break;
            case TURNR:
                if(adjusted==1) _MazeConnections[0] = linkToSet;
                if(adjusted==2) _MazeConnections[1] = linkToSet; 
                 break;
            case STRAIGHT:
                if(adjusted==0) _MazeConnections[0] = linkToSet;
                if(adjusted==2) _MazeConnections[1] = linkToSet; 
                 break;
        }
    }
    
    public boolean ConnectionsLeft()
    {
        return NullConnections()>0;
    }
    public int NullConnections()
    {
        int c =0;
        for(int i=0; i<AVAILABLE_CONNECTIONS; i++)
        {
            if(_MazeConnections[i]==null)
            {
                c++;
            }
        }
        return c;
    }
    
    public MazeLink GetParent()
    {
        switch(_LinkType)
        {
            case START:                
            case CROSS:
            case FINISH:
            case TEE:
                return _MazeConnections[2];
            case TURNL:
            case TURNR:
            case STRAIGHT:
                return _MazeConnections[1];
            default:
                return null;
        }
    }
    public void SetParent(MazeLink p)
    {
        switch(_LinkType)
        {
            case START:                
            case CROSS:
            case FINISH:
            case TEE:
                _MazeConnections[2] = p; break;
            case TURNL:
            case TURNR:
            case STRAIGHT:
                _MazeConnections[1] = p; break;
        }
    }
    
    public MazeLink[] GetChildren()
    {
        return _MazeConnections;
    }
    public MazeLink GetLink(int connection)
    {
        if(connection<=AVAILABLE_CONNECTIONS)
        {
            if(_MazeConnections[connection-1] != null)
            {
                return _MazeConnections[connection-1];
            }
        }
        return null;
    }
    
    public boolean GetConnectionIsAvailable(int i)
    {
        return (i<AVAILABLE_CONNECTIONS && _MazeConnections[i]==null);
    }
    
    public float GetX()
    {
        return X;
    }
    public float GetY()
    {
        return Y;
    }
    
    public boolean CanCreateNewLink(int connection)
    {
        return MySprite!=null && 
               connection<=AVAILABLE_CONNECTIONS && 
                _MazeConnections[connection-1] == null;
    }
        
    @Override
    public boolean equals(Object o)
    {
        if(o==null) return false;
        
        try{
        if(!ClassReflection.isAssignableFrom((MazeLink.class),(Class)o.getClass())) return false;
        }
        catch(Exception e)
        {
            return false;
        }
        MazeLink m = (MazeLink)o;
        
        boolean b =(((int)m.GetX() == (int)this.GetX())&&
                ((int)m.GetY() == (int)this.GetY()));

        return b;
    }
    
    public void setPosition(float x, float y)
    {
        if(MySprite!=null)
            MySprite.setPosition(x, y);
    }
    
    public void draw(SpriteBatch batch, boolean modifier)
    {
        if(MySprite!=null)
        {
            if(_Alpha<1f)
            {
                MySprite.setAlpha(_Alpha);
                _Alpha+=0.02f;
            }            
            MySprite.draw(batch);

        }
        if(modifier && ModifierTexture!=null)
        {
            batch.draw(ModifierTexture,X, Y, SCALE, SCALE);
        }
    }
    
    public void dispose()
    {
        if(MyTexture!=null)
        {
            //_MyTexture.dispose();
        }
    }

}
