package com.FRCCompetitionMap.Nodes;

import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

public class GameNode extends DefaultGraphCell {

    public GameNode(String name) {
        super(name);
    }

    public void setBounds(double x, double y, double w, double h) {
        GraphConstants.setBounds(getAttributes(),
                new Rectangle2D.Double(x, y, w, h));
    }

    public void setBackground(Color bg) {
        GraphConstants.setGradientColor(getAttributes(), bg);
    }

    public void setOpaque(boolean opaque) {
        GraphConstants.setOpaque(getAttributes(), opaque);
    }
}
