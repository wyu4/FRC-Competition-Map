package com.FRCCompetitionMap.Gui;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Container;

public class MainPage extends JPanel implements SessionPage {
    private final LoginSubpage loginPage = new LoginSubpage();

    public MainPage() {
    }

    @Override
    public void update() {
        System.out.println("update mainpage");
        Container parent = getParent();
        if (parent == null) {
            return;
        }
        setSize(parent.getWidth()/3, parent.getWidth()/3);
        setLocation(SessionUtils.calculateCenterLocation(parent, this));
        setBackground(Color.WHITE);
    }
}

interface MainSubpage {
    default String getHeader() {
        return "Unnamed";
    }
}

class LoginSubpage extends JPanel implements MainSubpage {
    public LoginSubpage() {

    }
}