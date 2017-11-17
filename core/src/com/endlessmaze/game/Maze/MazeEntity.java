/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.endlessmaze.game.Maze;

import com.endlessmaze.game.IPlayerModifier;

/**
 *
 * @author Corey
 */
public class MazeEntity extends MazeObject {
    
    public MazeEntity(int scale, int x, int y, int texScale, String texturePath, String textureName, IPlayerModifier action) {
        super(scale, x, y, texScale, texturePath, textureName, null, action);
    }
    
    
}
