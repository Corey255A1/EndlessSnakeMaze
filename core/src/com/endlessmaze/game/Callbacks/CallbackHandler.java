/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.endlessmaze.game.Callbacks;
import java.util.ArrayList;

/**
 *
 * @author Corey
 */
public class CallbackHandler<T> {

    ArrayList<ICallback<T>> Callbacks;

    public void AddCallback(ICallback<T> callback)
    {
        if(Callbacks==null)
        {
            Callbacks = new ArrayList<ICallback<T>>();
        }
        Callbacks.add(callback);
    }
    public void DoCallbacks(CallbackPayload<T> val)
    {
        if(Callbacks!=null)
        {
            for(ICallback<T> cb : Callbacks)
            {
                cb.Callback(val);

            }
        }
    }
    public void ClearCallbacks()
    {
        if(Callbacks!=null) Callbacks.clear();
    }

    
}
