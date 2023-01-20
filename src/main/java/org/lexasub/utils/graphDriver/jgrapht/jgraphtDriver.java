package org.lexasub.utils.graphDriver.jgrapht;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.util.mxCellRenderer;
import org.jgrapht.ext.JGraphXAdapter;
import org.lexasub.utils.graphDriver.GraphDriver;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class jgraphtDriver implements GraphDriver {
    Graph<String, CustomEdge> graph;

    public jgraphtDriver(String s) {
        graph = new DefaultDirectedGraph<>(CustomEdge.class);//DirectedPseudograph//DefaultDirectedGraph
        addVertex(s);
    }

    private void addVertex(String text) {
        graph.addVertex(text);
    }

    @Override
    public void write(String pathname, String fileFormat) {
        JGraphXAdapter<String, CustomEdge> jGraphXAdapter = new JGraphXAdapter<>(graph);
        mxIGraphLayout mxIGraphLayout = new mxCompactTreeLayout(jGraphXAdapter);//mxCompactTreeLayout//mxCircleLayout
        /*
           mxgraph.layout.mxCompactTreeLayout;//good
           mxgraph.layout.mxOrganicLayout;//need some modifications
           mxgraph.layout.mxEdgeLabelLayout,mxParallelEdgeLayout,mxStackLayout;//bad
        */
        mxIGraphLayout.execute(jGraphXAdapter.getDefaultParent());
        BufferedImage bufferedImage = mxCellRenderer.createBufferedImage(jGraphXAdapter, null, 1, Color.WHITE, true, null);
        File newFIle = new File(pathname);
        try {
            ImageIO.write(bufferedImage, fileFormat, newFIle);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addEdge(String s1, String s2, String s) {
        addVertex(s2);
        graph.addEdge(s1, s2, new CustomEdge(s, s));
    }
}
