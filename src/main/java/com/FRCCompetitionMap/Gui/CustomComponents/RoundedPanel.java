package com.FRCCompetitionMap.Gui.CustomComponents;

import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

public class RoundedPanel extends JPanel {
    private int arc;

    public RoundedPanel() {
        this(UIManager.getInt("RoundedPanel.arc"));
    }

    public RoundedPanel(int arc) {
        setArc(arc);
    }

    public void setArc(int arc) {
        this.arc = arc;
//        setBorder(new EmptyBorder(0,arc,arc,arc));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
//        Graphics2D g2d = (Graphics2D) g;
//        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//
////        Color background = getBackground();
////        g2d.setColor(background == null ? Color.BLACK : getBackground());
//
//        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
    }
}
