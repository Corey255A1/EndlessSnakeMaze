/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.endlessmaze.game.Callbacks;

/**
 *
 * @author Corey
 */
public interface ICallback<T> {
    public void Callback(CallbackPayload<T> val);
    
}
