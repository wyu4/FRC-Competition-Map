package com.FRCCompetitionMap.Nodes;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphLayoutCache;

import javax.swing.JFrame;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlayoffGraph extends DefaultGraphModel {
    private final JGraph graph;
    private final List<DefaultGraphCell> cells = new ArrayList<>();

    public PlayoffGraph() {
        graph = new JGraph(this);
        graph.setCloneable(false);
        graph.setEditable(false);
    }

    public void addCell(GameNode cell) {
        if (!cells.contains(cell)) {
            return;
        }
        cells.add(cell);
    }

    public DefaultGraphCell[] getCells() {
        return cells.toArray(DefaultGraphCell[]::new);
    }

    public void render() {
        graph.getGraphLayoutCache().insert(cells);
    }

    public JGraph getGraph() {
        return graph;
    }

    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("JGraph Test");
        frame.setLayout(new GridLayout(1, 1));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        PlayoffGraph graph = new PlayoffGraph();

        GameNode testNode1 = new GameNode("Snowberry rocks");
        testNode1.setBounds(100, 100, 100, 100);
        graph.addCell(testNode1);
        graph.render();

        frame.add(graph.getGraph());
        frame.setVisible(true);
        frame.revalidate();
        frame.repaint();
    }
}
