package com.FRCCompetitionMap.Nodes;

import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

/**
 * The class that represents alliances as nodes in a graph.
 */
public class GameNode extends DefaultGraphCell {

    public GameNode(String name) {
        super(name);
    }

    public void setBounds(double x, double y, double w, double h) {
        GraphConstants.setBounds(getAttributes(),
                new Rectangle2D.Double(x, y, w, h));
    }

    public Rectangle2D getBounds() {
        return GraphConstants.getBounds(getAttributes());
    }

    public void setBackground(Color bg) {
        GraphConstants.setGradientColor(getAttributes(), bg);
    }

    public void setOpaque(boolean opaque) {
        GraphConstants.setOpaque(getAttributes(), opaque);
    }

    public DefaultEdge createConnection(String name, DefaultGraphCell cell) {
        addPort();
        DefaultEdge edge = new DefaultEdge(name);
        edge.setSource(this);
        edge.setTarget(cell);
        return edge;
    }

    public void setArrowStyle(int style) {
        GraphConstants.setLineEnd(getAttributes(), style);
    }

    public void setEndFill(boolean filled) {
        GraphConstants.setEndFill(getAttributes(), filled);
    }
}
