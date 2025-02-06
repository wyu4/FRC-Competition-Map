package com.FRCCompetitionMap.Nodes;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.util.mxCellRenderer;
import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphModel;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlayoffGraph extends JPanel {
    private final GraphModel model = new DefaultGraphModel();
    private final JGraph graph = new JGraph(model);
    private final List<DefaultGraphCell> cells = new ArrayList<>();

    public PlayoffGraph() {
        graph.setCloneable(false);
        graph.setEditable(false);
    }

    public void addCell(GameNode cell) {
        if (!cells.contains(cell)) {
            return;
        }
        cells.add(cell);
    }

    public void connectCell(GameNode cell1, GameNode cell2) {
        addCell(cell1); addCell(cell2);
    }

    public void render() {
        graph.getGraphLayoutCache().insert(cells);
    }

    public static void main(String[] args) throws IOException {

    }
}
