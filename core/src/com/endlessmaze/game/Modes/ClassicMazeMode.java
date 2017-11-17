package com.endlessmaze.game.Modes;

import com.endlessmaze.game.ControlOverlayPage;
import com.endlessmaze.game.Controls.MenuPage;
import com.endlessmaze.game.Maze.MazeHandler;

/**
 *
 * @author Corey
 */
public class ClassicMazeMode {
    
    public static void Reset()
    {
        if(MazeHandler.IsMazeUnlocked())
        {
            ControlOverlayPage.TriggerFinishUnlocked();
        }
        else
        {
            ControlOverlayPage.SetDisplayText("Grow to " + ((Integer) MazeHandler.GetSizeToComplete()) + " to Unlock Finish");
            ControlOverlayPage.TriggerDisplayText();
        }
    }
    public static void NextLevel()
    {
        ControlOverlayPage.SetDisplayText("Grow to " + ((Integer) MazeHandler.GetSizeToComplete()) + " to Unlock Finish");
        ControlOverlayPage.TriggerDisplayText();
    }

    public static void LevelSelect(MenuPage.LevelSelect values)
    {
        MazeHandler.ResetTimer();
        ControlOverlayPage.ResetOneShot();
        ControlOverlayPage.SetResetMode(false);
        MazeHandler.ResetMazeComplete();
        MazeHandler.ResetMazeUnlocked();
        MazeHandler.SetSizeToComplete(values.Length);
        MazeHandler.SetResetLength(10);
        MazeHandler.SetSeed(values.Seed);
        MazeHandler.SetLevel(values.Level);
        ControlOverlayPage.SetDisplayText("Grow to " + ((Integer) MazeHandler.GetSizeToComplete()) + " to Unlock Finish");
        ControlOverlayPage.TriggerDisplayText();
    }
    public static void OnReturnFromInterstitial()
    {
        if( MazeHandler.IsMazeUnlocked()) {
            ControlOverlayPage.TriggerFinishUnlocked();
        }
        else {
            ControlOverlayPage.SetDisplayText("Grow to " + ((Integer) MazeHandler.GetSizeToComplete()) + " to Unlock Finish");
            ControlOverlayPage.TriggerDisplayText();
        }
    }
    public static void DoneLoading()
    {
        if(MazeHandler.IsMazeUnlocked())                    
        {
            ControlOverlayPage.TriggerFinishUnlocked();

        }
        else
        {
            ControlOverlayPage.SetDisplayText("Grow to " + ((Integer) MazeHandler.GetSizeToComplete()) + " to Unlock Finish");
            ControlOverlayPage.TriggerDisplayText();
        }
    }
}
