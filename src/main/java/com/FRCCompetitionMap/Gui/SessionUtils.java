package com.FRCCompetitionMap.Gui;

import java.awt.*;

public class SessionUtils {
    public static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();

    public static Point calculateCenterLocation(Container container, Component component) {
        return new Point(
                Math.round(container.getWidth()/2.0f - component.getWidth()/2.0f),
                Math.round(container.getHeight()/2.0f - component.getHeight()/2.0f)
        );
    }
}
