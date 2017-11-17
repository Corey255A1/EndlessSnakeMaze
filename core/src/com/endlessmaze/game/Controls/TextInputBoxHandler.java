/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.endlessmaze.game.Controls;

import com.badlogic.gdx.Input;

/**
 *
 * @author Corey
 */
public class TextInputBoxHandler implements Input.TextInputListener {

    String Resp = "";
    @Override
    public void input(String text) {
        Resp = text;
    }

    @Override
    public void canceled() {
        Resp = "";
    }
    
    public String GetResponse()
    {
        return Resp;
    }
    
}
