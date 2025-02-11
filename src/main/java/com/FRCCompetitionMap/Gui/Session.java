package com.FRCCompetitionMap.Gui;

import com.FRCCompetitionMap.Gui.Themes.ThemeDark;
import com.formdev.flatlaf.FlatLaf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Session extends JFrame implements ActionListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(Session.class);
    private final List<SessionPage> pages = new ArrayList<>();
    private final MainPage mainPage = new MainPage();
    private final Timer runtime;

    public Session() {
        super("FRC Competition Map");
        runtime = new Timer(1, this);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension((int)(SessionUtils.SCREEN_SIZE.getWidth()), (int)(SessionUtils.SCREEN_SIZE.getHeight()*0.6)));
        setMaximumSize(SessionUtils.SCREEN_SIZE);
        setSize(getMinimumSize());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        add(mainPage);

        revalidate();
        repaint();
        setVisible(true);

        runtime.start();
    }

    @Override
    public Component add(Component comp) {
        if (comp instanceof SessionPage parsed) {
            pages.add(parsed);
        }
        return super.add(comp);
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

    private void updateAll() {
        pages.forEach(SessionPage::update);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(runtime)) {
            updateAll();
        }
    }
}
