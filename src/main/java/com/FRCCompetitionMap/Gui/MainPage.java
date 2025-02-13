package com.FRCCompetitionMap.Gui;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

public class MainPage extends JPanel implements SessionPage {
    private MainSubpage currentSubpage;

    private final JLabel header = new JLabel("HEADER");
    private final List<MainSubpage> subpages = List.of(
            new LoginSubpage()
    );

    public MainPage() {
        super(null);
        putClientProperty(FlatClientProperties.STYLE, "arc: 10; background: $subpage.background");

        header.setFont(header.getFont().deriveFont(Font.BOLD));

        add(header);
        subpages.forEach((page) -> add((Component) page));
        currentSubpage = subpages.getFirst();
        add(new JButton());

//        putClientProperty(FlatClientProperties.OUTLINE, "");
    }

    private boolean isFirstPage() {
        return currentSubpage.equals(subpages.getFirst());
    }

    private boolean isLastPage() {
        return currentSubpage.equals(subpages.getLast());
    }

    private void nextPage() {
        currentSubpage = subpages.get(subpages.indexOf(currentSubpage) + 1);
    }

    private void prevPage() {
        currentSubpage = subpages.get(subpages.indexOf(currentSubpage) - 1);
    }

    @Override
    public void update() {
        if (!isVisible()) {
            return;
        }

        Container parent = getParent();
        if (parent == null) {
            return;
        }
        setSize(parent.getWidth()/4, parent.getWidth()/4);
        setLocation(SessionUtils.calculateCenterLocation(parent, this));

        if (currentSubpage != null) {
            header.setText(currentSubpage.getHeader());
        }
        header.setHorizontalAlignment(SwingConstants.CENTER);
        header.setLocation(0, 0);
        header.setSize(getWidth(), getHeight()/5);
        header.setFont(header.getFont().deriveFont(header.getWidth() * 0.048f));
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