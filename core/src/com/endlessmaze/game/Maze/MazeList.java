package com.endlessmaze.game.Maze;
//import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.graphics.profiling.GLProfiler;
//import com.endlessmaze.game.Callbacks.CallbackHandler;
//import com.endlessmaze.game.Callbacks.CallbackPayload;
import com.endlessmaze.game.Callbacks.ICallback;
import java.util.ArrayList;

import java.util.HashMap;

/**
 *
 * @author Corey Wunderlich
 */


public class MazeList {

    final int MAX_LEVEL = 6;
    int ViewPortX_half = 1280;
    int ViewPortY_half = 720;
    int ViewPortX = 1280;
    int ViewPortY=720;
    int STARTX, STARTY;
    int XSCALEOFFSET,YSCALEOFFSET;
    float Zoom = 1.0f;
    int ITERATIONS = 0;
    //ArrayList<MazeLink> _MazeLinks = new ArrayList<MazeLink>();
    HashMap<Coord,MazeLink> _MazeLinks = new HashMap<Coord, MazeLink>();
    HashMap<Coord,MazeObject> _MazeObjects = new HashMap<Coord, MazeObject>();
    ArrayList<Sprite> BlendList = new ArrayList<Sprite>();
    static int SCALE;
    MazeLink _CurrentLink;
    MazeLink FurthestLink;
    int Distance;
    boolean _Loading = false;
    boolean _Drawing = false;
    boolean _FoundFinish = false;
    int FX = 0;
    int FY = 0;
    public MazeList(int scale, int x, int y, int initialDepth, int viewX, int viewY, float zoom, Color mazeColor)
    {       
        SCALE=scale;
        ViewPortX_half = (int)(viewX*zoom/2.0f);
        ViewPortY_half = (int)(viewY*zoom/2.0f);
        STARTX=x;
        STARTY=y;
        XSCALEOFFSET=STARTX%SCALE;
        YSCALEOFFSET=STARTY%SCALE;
        ViewPortX = viewX;
        ViewPortY = viewY;
        Zoom = zoom;
        _CurrentLink = new MazeLink(SCALE,MazeLinkType.START, MazeUtilities.NORTH, x,y, mazeColor);
        _MazeLinks.put(_CurrentLink.GetPoint(),_CurrentLink);
        FurthestLink = _CurrentLink;
        Distance=0;


    }
    public void CreateLinks(final ICallback<Integer> doneLoading)
    {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                _Loading = true;
                CreateAllLinks(_CurrentLink,150,true,5);
                //changePiece(FurthestLink, true);       
                CreateFillerLinks(_CurrentLink.GetPoint(),SCALE*50,SCALE*50, true);
                if(doneLoading!=null) doneLoading.Callback(null);
                _Loading = false;
            }
        });
        t.start(); 
    }
    public void Reset()
    {
       _CurrentLink = _MazeLinks.get(new Coord(ViewPortX/2,ViewPortY/2));
    }
    public MazeLink GetCurrentLink()
    {
        return _CurrentLink;
    }
    public MazeObject ObjectAtCurrentLink()
    {
        if(_MazeObjects.containsKey(_CurrentLink.GetPoint()))
        {
            return _MazeObjects.get(_CurrentLink.GetPoint());
        }
        return null;
    }
    public void RemoveObjectAtCurrentLink()
    {
        if(_MazeObjects.containsKey(_CurrentLink.GetPoint()))
        {
            _MazeObjects.remove(_CurrentLink.GetPoint());
        }
    }
    
    private void CreateFillerLinks(Coord c, int XThick, int YThick, boolean punchthru)
    {       
        //int SCALELEN = SCALE*100;
        //int halfScaleX=(((int)ViewPortX_half/SCALE)*SCALE);        
       // int halfScaleY=(((int)ViewPortY_half/SCALE)*SCALE);
        int sX = c.X;
        int sY = c.Y;
        final int lBoundx = sX-XThick;
        final int uboundx = sX+XThick;
        final int lBoundy = sY-YThick;
        final int uboundy = sY+YThick;
        for(int x=lBoundx;x<uboundx;x+=SCALE)
        {
           for(int y=lBoundy;y<uboundy;y+=SCALE)
           {
               if(!_MazeLinks.containsKey(Coord.GetCoord(x,y)))
               {
                   MazeLinkType mlt = MazeUtilities.ChooseLinkType(x, y);
                   MazeLink nLink = new MazeLink(SCALE,mlt, MazeUtilities.NORTH, x,y, _CurrentLink.GetColor());
                   AddToMap(nLink);
                   ConnectSurroundingPieces(nLink);
                   CreateAllLinks(nLink, 10, punchthru);
               }
           }
        }
    }
    
    private void AddToMap(MazeLink ml)
    {
       _MazeLinks.put(ml.GetPoint(),ml);
    }
    private void AddToObjectMap(Coord c, MazeObject mo)
    {
        _MazeObjects.put(c, mo);       
    }
    private void CreateAllLinks(MazeLink parent, int level, boolean punchThru)
    {
        CreateAllLinks(parent,level,punchThru,0);
    }
    private void CreateAllLinks(MazeLink parent, int level, boolean punchThru, int objProbs)
    {
        if(parent!=null)
        {
            //System.out.println("not null");
            if(level>0)
            {
                //System.out.println("Link Level " + level);
                
                for(int l=0; l<4; l++)
                {
                    if(!parent.GetConnectionIsAvailable(l)) continue;
                    int i=l+1;
                    //System.out.println(parent._LinkType + " " + parent._ConnectionsLeft);
                   //System.out.println("Link Level " + level);
                    if(parent.CanCreateNewLink(i))
                    {
                        //System.out.println("Creating");
                        int conDir = parent.GetDirectionOfConnection(i);
                        Coord pLoc= MazeUtilities.GetInDirection(SCALE, (int)parent.X, (int)parent.Y, conDir);
                        if(!_MazeLinks.containsKey(pLoc))
                        {
                            MazeLink child = new MazeLink(SCALE,MazeUtilities.ChooseLinkType(pLoc.X,pLoc.Y),conDir, pLoc.X, pLoc.Y, parent.GetColor());
                            if(child._LinkType == MazeLinkType.FINISH)
                            {
                                //System.out.println("Found Finish");
                                _FoundFinish = true;
                                FX = (int)child.X;
                                FY = (int)child.Y;
                            }
                            child.SetParent(parent);
                            parent.SetConnection(i, child);
                            
                            //float ptDist = MazeUtilities.GetDistance(ml.GetX(), ml.GetY(), ViewPortX_half, ViewPortY_half);
                            if(child._LinkType != MazeLinkType.FINISH)
                            {
                                MazeObject mo = MazeObjectFactory.RandomlyGetNewObject((int)child.GetX(), (int)child.GetY(), objProbs);                           
                                if(mo!=null) AddToObjectMap(child.GetPoint(), mo);
                            }
                            AddToMap(child);
                            //int d = MazeUtilities.GetDistance(child.X, child.Y, STARTX,STARTY);
                            ConnectSurroundingPieces(child);
                            CreateAllLinks(child, level-1,punchThru,objProbs);
                           //if(d>Distance)
                            //{
                            //    FurthestLink = child;
                            //    Distance =d;
                                //System.out.println(d);
                            //}
                        }
                        else
                        {
                            MazeLink ml = _MazeLinks.get(pLoc);
                            if(ml.ConnectionsLeft())
                            {
                              ConnectSurroundingPieces(ml);
                            }                  
                            else if(punchThru && parent._LinkType != MazeLinkType.CROSS)
                            {  
                             // System.out.println((ml==null) + " " + pLoc.toString());
                              ml.IncreaseLinks();
                              //ml.MarkPiece();
                              ConnectSurroundingPieces(ml);
                              CreateAllLinks(ml, level-1,punchThru,objProbs);
                            }
                        }
                                            
                    }
                }
            }
        }
    }
    
    public MazeLink move(int direction)
    {
        MazeLink nextLink = _CurrentLink.GetConnectionFromDirection(direction);
        if(nextLink!=null && nextLink!=MazeLink.EmptyMazeLink)
        {            
            _CurrentLink = nextLink;
            int YOffset = 0;
            int XOffset = 0;
            int XScale = SCALE;
            int YScale = SCALE;
            switch(direction)
            {
                case MazeUtilities.NORTH: YOffset = SCALE*25; XScale=SCALE*50; break;
                case MazeUtilities.SOUTH: YOffset = -SCALE*25; XScale=SCALE*50; break;
                case MazeUtilities.EAST: XOffset = SCALE*25; YScale=SCALE*50; break;
                case MazeUtilities.WEST: XOffset = -SCALE*25; YScale=SCALE*50; break;
            }
            CreateFillerLinks(Coord.GetCoord((int)_CurrentLink.X+XOffset, (int)_CurrentLink.Y+YOffset), XScale, YScale, true);
            return nextLink;
        }
        
        return null;
    }
    
    public void previewMove(int direction)
    {
       previewMove(direction,true);
    }
    
    public void previewMove(int direction, boolean createLinks)
    {
       //MazeLink nextLink = _CurrentLink.GetConnectionFromDirection(direction);
       //if(createLinks) CreateAllLinks(_MazeLinks, _CurrentLink, 5, false, true, false);
    }
    
    public void ConnectSurroundingPieces(MazeLink newLink)
    {
        for(int i=0; i<4; i++)
        {
            if(!newLink.GetConnectionIsAvailable(i)) continue;
            int nlNew=i+1;
            int bdir = newLink.GetDirectionOfConnection(nlNew);
            Coord bpLoc = MazeUtilities.GetInDirection(SCALE, (int)newLink.X, (int)newLink.Y, bdir);
            if(_MazeLinks.containsKey(bpLoc))
            {
                //System.out.println("Setting New Link\n");
                MazeLink nchild = _MazeLinks.get(bpLoc);
                //nchild.MarkPiece();
                MazeLink opDir = nchild.GetConnectionFromDirection(MazeUtilities.GetOppositeDirection(bdir));
                //System.out.println((opDir==null) + " " + bdir);
                if(MazeLink.EmptyMazeLink != opDir)
                {
                    nchild.SetConnectionAtDirection(MazeUtilities.GetOppositeDirection(bdir), newLink);
                    newLink.SetConnectionAtDirection(bdir, nchild);
                }
            }
        } 
    }  
    
    private MazeLink changePiece(MazeLink ml, boolean finish)
    {
        if(ml._LinkType == MazeLinkType.START ||
                ml._LinkType == MazeLinkType.FINISH)
        {return null;}
        _MazeLinks.remove(ml.GetPoint());
        MazeLink newLink = new MazeLink(SCALE,finish?MazeLinkType.FINISH:MazeLinkType.CROSS,
                ml._Rotation,
                ml.GetX(),
                ml.GetY(),
                ml.GetColor());

        if(finish)
        {
            _FoundFinish = true;
            FX = (int)ml.GetX();
            FY = (int)ml.GetY();
        }
        ConnectSurroundingPieces(newLink);
        _MazeLinks.put(newLink.GetPoint(), newLink);
        if(ml==_CurrentLink) _CurrentLink = newLink;
        //_CurrentLink.MarkPiece();

        return newLink;
    }
    
    public boolean changePiece()
    {
        MazeLink changed = changePiece(_CurrentLink,false);
        return changed!=null;        
    }
    public void changeDirectionalPieces(int mX, int mY)
    {
        Coord c[] = new Coord[5];        
        c[0] = new Coord(mX+SCALE, mY);
        c[1] = new Coord(mX-SCALE, mY);
        c[2] = new Coord(mX, mY+SCALE);
        c[3] = new Coord(mX, mY-SCALE);
        c[4] = new Coord(mX, mY);
        for(Coord nc : c)
        {
            MazeLink ml = _MazeLinks.get(nc);
            if(ml!=null && ml._LinkType!=MazeLinkType.CROSS)
            {
                changePiece(ml,false);
            }
        }
        for(Coord nc : c)
        {
            MazeLink ml = _MazeLinks.get(nc);
            if(ml!=null && ml._LinkType!=MazeLinkType.CROSS)
            {
                connectPieces(ml);
            }
        }

    }
    
    public void changeAreaPieces(int mX, int mY, int count)
    {
        int SCALELEN = SCALE*count;        
        for(int x=mX-SCALELEN;x<mX+SCALELEN+SCALE;x+=SCALE)
        {
           for(int y=mY-SCALELEN;y<mY+SCALELEN+SCALE;y+=SCALE)
           {
               MazeLink ml = _MazeLinks.get(Coord.GetCoord(x,y));
               if(ml!=null && ml._LinkType!=MazeLinkType.CROSS)
               {
                   changePiece(ml,false);
                   
               }
           }
        }
        for(int x=mX-SCALELEN;x<mX+SCALELEN+SCALE;x+=SCALE)
        {
           for(int y=mY-SCALELEN;y<mY+SCALELEN+SCALE;y+=SCALE)
           {
               MazeLink ml = _MazeLinks.get(Coord.GetCoord(x,y));
               if(ml!=null && ml._LinkType!=MazeLinkType.CROSS)
               {
                   connectPieces(ml);                   
               }
           }
        }
    }
    
    
    private boolean connectPieces(MazeLink PieceToConnect)
    {
        for(int i=0; i<4; i++)
        {
            if(!PieceToConnect.GetConnectionIsAvailable(i)) continue;
            int ml=i+1;
            int dir = PieceToConnect.GetDirectionOfConnection(ml);
            Coord pLoc = MazeUtilities.GetInDirection(SCALE, (int)PieceToConnect.X, (int)PieceToConnect.Y, dir);
            MazeLink blocker = _MazeLinks.get(pLoc);
            if(blocker!=null)
            {                
                if(blocker._LinkType!=MazeLinkType.FINISH && 
                   blocker._LinkType!=MazeLinkType.START)
                {
                //Remove the Piece that is changing
                 _MazeLinks.remove(pLoc);
                //Create a new cross piece
                MazeLink newLink = new MazeLink(SCALE,MazeLinkType.CROSS,
                        blocker._Rotation,
                        blocker.GetX(),
                        blocker.GetY(),
                        blocker.GetColor());
                
                ConnectSurroundingPieces(newLink);                
                _MazeLinks.put(newLink.GetPoint(),newLink);
                }
            }
            
        }
        return true;
    }
    public boolean buildBridge()
    {
        return connectPieces(_CurrentLink);
    }
    
    public boolean lookAhead(int dir)
    {
        /*
        System.out.println("DIR " + dir);
        for(int i=0;i<4;i++)
        {
            System.out.println("I " + i);
            if(i==MazeUtilities.GetOppositeDirection(dir)) continue;
            MazeLink nextLink = _CurrentLink.GetConnectionFromDirection(i);
            if(nextLink!=null && nextLink!=MazeLink.EmptyMazeLink)
            {
                System.out.println("Looking " + i);
                CreateAllLinks(_MazeLinks, nextLink, 12, false, true, true);
            }
        }*/
        return true;
    }

    public void SetZoom(float zoom)
    {
        ViewPortX_half = (int)((float)ViewPortX*zoom/2.0f);
        ViewPortY_half = (int)((float)ViewPortY*zoom/2.0f);
    }
    public void draw(SpriteBatch batch, float centerX, float centerY, boolean finishUnlocked)
    {
        //System.out.println("List " + System.currentTimeMillis());
       //GLProfiler.reset();      
       //BlendList.clear();
       // batch.disableBlending();
        //int w = Gdx.graphics.getWidth();
        //int h = Gdx.graphics.getHeight();
        //System.out.println(w + " " + h);
        if(!_Loading)
        {
            _Drawing = true;
            int halfScaleX=(((int)ViewPortX_half/SCALE)*SCALE);        
            int halfScaleY=(((int)ViewPortY_half/SCALE)*SCALE);
            int centerXS = (((int)centerX/SCALE)*SCALE)+XSCALEOFFSET;
            int centerYS = (((int)centerY/SCALE)*SCALE)+YSCALEOFFSET; 
            for(int x=((int)centerXS-halfScaleX)-SCALE; x<((int)centerXS+halfScaleX)+SCALE; x+=SCALE)
            {
                 for(int y=((int)centerYS-halfScaleY)-SCALE; y<((int)centerYS+halfScaleY)+SCALE; y+=SCALE)
                 {
                     Coord c = Coord.GetCoord(x,y);
                    MazeLink ml = _MazeLinks.get(c);
                    if(ml!=null)
                    {
                        ml.draw(batch,(ml._LinkType==MazeLinkType.FINISH && !finishUnlocked));
                    }
                    MazeObject mo = _MazeObjects.get(c);
                    if(mo!=null)
                    {
                        mo.draw(batch);
                    }
                 }
            }
        }
         //System.out.println("DC " + GLProfiler.drawCalls);
        // System.out.println("TB " + GLProfiler.textureBindings);
        // System.out.println("Sprites" + batch.maxSpritesInBatch);
         
    }
    
    public void dispose()
    {
        for(MazeLink m : _MazeLinks.values())
        {
            m.dispose();
        }
    }
}
