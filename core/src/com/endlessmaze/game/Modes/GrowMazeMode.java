/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.endlessmaze.game.Modes;

import com.endlessmaze.game.ControlOverlayPage;
import com.endlessmaze.game.Controls.MenuPage;
import com.endlessmaze.game.Maze.MazeHandler;

/**
 *
 * @author Corey
 */
public class GrowMazeMode {
        public static void Reset()
    {
        ControlOverlayPage.SetDisplayText("Grow To The Longest Snake!");
        ControlOverlayPage.TriggerDisplayText();
    }
    public static void NextLevel()
    {
        ControlOverlayPage.SetDisplayText("Grow To The Longest Snake!");
        ControlOverlayPage.TriggerDisplayText();
    }

    public static void LevelSelect(MenuPage.LevelSelect values)
    {
        MazeHandler.ResetTimer();
        ControlOverlayPage.ResetOneShot();
        ControlOverlayPage.SetResetMode(false);
        MazeHandler.SetResetLength(10);
        MazeHandler.SetSeed(values.Seed);
        MazeHandler.SetLevel(values.Level);
        ControlOverlayPage.SetDisplayText("Grow To The Longest Snake!");
        ControlOverlayPage.TriggerDisplayText();
    }
    public static void OnReturnFromInterstitial()
    {
        ControlOverlayPage.SetDisplayText("Grow To The Longest Snake!");
        ControlOverlayPage.TriggerDisplayText();
    }
    public static void DoneLoading()
    {
        ControlOverlayPage.SetDisplayText("Grow To The Longest Snake!");
        ControlOverlayPage.TriggerDisplayText();
    }
}
