package com.FRCCompetitionMap.Gui;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

public class SessionUtils {
    public static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
    private static final Desktop DESKTOP = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;

    public static Point calculateCenterLocation(Container container, Component component) {
        return new Point(
                Math.round(container.getWidth()/2.0f - component.getWidth()/2.0f),
                Math.round(container.getHeight()/2.0f - component.getHeight()/2.0f)
        );
    }

    public static void openLink(String link) throws IOException {
        if (DESKTOP == null) {
            throw new IOException("Desktop is not supported.");
        }
        if (!DESKTOP.isSupported(Desktop.Action.BROWSE)) {
            throw new IOException("Desktop does not allow browsing.");
        }
        DESKTOP.browse(URI.create(link));
    }
}
