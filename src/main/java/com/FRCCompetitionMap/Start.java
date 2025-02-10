package com.FRCCompetitionMap;

import com.FRCCompetitionMap.Gui.Session;

import java.awt.EventQueue;

public class Start {
    public static void main(String[] args) {
        EventQueue.invokeLater(Session::startSession);
    }
}
