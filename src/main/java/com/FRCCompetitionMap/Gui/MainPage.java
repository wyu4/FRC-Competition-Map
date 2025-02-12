package com.FRCCompetitionMap.Gui;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Container;

public class MainPage extends JPanel implements SessionPage {
    private MainSubpage currentSubpage;

    private final JLabel header = new JLabel("HEADER");
    private final LoginSubpage loginPage = new LoginSubpage();

    public MainPage() {
        super(new MigLayout());
        putClientProperty(FlatClientProperties.STYLE, "arc: 10; background: $subpage.background");

        add(header);
//        putClientProperty(FlatClientProperties.OUTLINE, "");
    }

    @Override
    public void update() {
        Container parent = getParent();
        if (parent == null) {
            return;
        }
        setSize(parent.getWidth()/4, parent.getWidth()/4);
        setLocation(SessionUtils.calculateCenterLocation(parent, this));

        if (currentSubpage == null) {
            return;
        }
        header.setText(currentSubpage.getHeader());
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

    @Override
    public String getHeader() {
        return "Authentication";
    }
}