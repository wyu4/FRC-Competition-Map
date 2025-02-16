package com.FRCCompetitionMap.Gui;

import com.FRCCompetitionMap.Gui.CustomComponents.GradientPanel;
import com.FRCCompetitionMap.Gui.Themes.ThemeDark;
import com.FRCCompetitionMap.IO.ImageLoader;
import com.formdev.flatlaf.FlatLaf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

public class Session extends JFrame implements ActionListener, WindowListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(Session.class);
    private final List<SessionPage> pages = new ArrayList<>();
    private final MainPage mainPage = new MainPage(() -> LOGGER.debug("Last page!"));
    private final GradientPanel gradientBackground = new GradientPanel();
    private final Attribution attribution = new Attribution(ImageLoader.FRC_LOGO);
    private final Timer runtime;

    public Session() {
        super("FRC Competition Map");
        runtime = new Timer(1000/30, this);

        JFrame.setDefaultLookAndFeelDecorated(false);
        setLayout(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension((int)(SessionUtils.SCREEN_SIZE.getWidth()), (int)(SessionUtils.SCREEN_SIZE.getHeight()*0.75)));
        setMaximumSize(SessionUtils.SCREEN_SIZE);
        setSize(getMinimumSize());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gradientBackground.setLocation(0, 0);

        add(mainPage);
        add(attribution);
        add(gradientBackground);
        addWindowListener(this);

        revalidate();
        repaint();
        setVisible(true);
    }

    @Override
    public Component add(Component comp) {
        if (comp instanceof SessionPage parsed) {
            pages.add(parsed);
        }
        return super.add(comp);
    }

    @Override
    public void remove(Component comp) {
        if (comp instanceof SessionPage page) {
            pages.remove(page);
        }
        super.remove(comp);
    }

    public static void startSession() {
        LOGGER.debug("Starting session.");
        try {
            FlatLaf.registerCustomDefaultsSource("themes");
            ThemeDark.setup();
            LOGGER.debug("Flatlaf themes applied.");
        } catch (Exception e) {
            LOGGER.error("Could not install Flatlaf themes.", e);
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e2) {
                LOGGER.error("Could not setup look & feel to default.", e2);
            }
        }

        LOGGER.debug("Ready to launch session.");
        SwingUtilities.invokeLater(Session::new);
    }

    private void updateAll() {
        pages.forEach(SessionPage::update);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(runtime)) {
            mainPage.setSize(getWidth()/4, getWidth()/4);
            mainPage.setLocation(SessionUtils.calculateCenterLocation(this, mainPage));

            attribution.setSize((int)(mainPage.getWidth()*0.75f), (getHeight()-mainPage.getY()-mainPage.getHeight())/2);
            attribution.setLocation(SessionUtils.calculateCenterLocation(this, attribution).x, mainPage.getY() + mainPage.getHeight());

            gradientBackground.setSize(getSize());

            updateAll();
            repaint();
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {
        EventQueue.invokeLater(runtime::start);
    }

    @Override
    public void windowClosing(WindowEvent e) {
        EventQueue.invokeLater(runtime::stop);
    }

    @Override
    public void windowClosed(WindowEvent e) {}

    @Override
    public void windowIconified(WindowEvent e) {
        EventQueue.invokeLater(runtime::stop);
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        EventQueue.invokeLater(runtime::restart);
    }

    @Override
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowDeactivated(WindowEvent e) {}

    @Override
    public void paintComponents(Graphics g) {
        super.paintComponents(g);
    }
}