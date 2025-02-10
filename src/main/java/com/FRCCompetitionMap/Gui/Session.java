package com.FRCCompetitionMap.Gui;

import com.FRCCompetitionMap.Gui.Themes.ThemeDark;
import com.formdev.flatlaf.FlatLaf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;

public class Session extends JFrame {
    private static final Logger LOGGER = LoggerFactory.getLogger(Session.class);
    public static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();

    public Session() {
        super("FRC Competition Map");

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension((int)(SCREEN_SIZE.getWidth()), (int)(SCREEN_SIZE.getHeight()*0.6)));
        setMaximumSize(SCREEN_SIZE);
        setSize(getMinimumSize());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void startSession() {
        LOGGER.debug("Starting session.");
        try {
            FlatLaf.registerCustomDefaultsSource("themes");
            ThemeDark.setup();
            LOGGER.debug("Flatlaf themes applied.");
        } catch (Exception e) {
            LOGGER.error("Could not install Flatlaf themes.", e);
        }
        LOGGER.debug("Ready to launch session.");
        EventQueue.invokeLater(Session::new);
    }
}
