package com.FRCCompetitionMap.Gui.CustomComponents;

import javax.swing.JPanel;
import javax.swing.UIManager;
import java.awt.*;

public class GradientPanel extends JPanel {
    private Color colorA, colorB;

    public GradientPanel() {
        this(UIManager.getColor("GradientPanel.gradientA"), UIManager.getColor("GradientPanel.gradientB"));
    }

    public GradientPanel(Color a, Color b) {
        super(null);
        this.colorA = a;
        this.colorB = b;
        setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setPaint(new GradientPaint(0, 0, colorA, 0, getHeight(), colorB));
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}
