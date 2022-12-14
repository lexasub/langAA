package org.lexasub.utils;

import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.util.mxCellRenderer;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.lexasub.IR1.IR1;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class IR1IO {

    static boolean jsonize = false;

    public static void dump(IR1 newBlock, boolean deps, boolean parts) {//TODO
        Graph<String, CustomEdge> graph = new DefaultDirectedGraph<>(CustomEdge.class);//DirectedPseudograph//DefaultDirectedGraph
        graph.addVertex(getMyDumpForGraph(newBlock));
        dump(new LinkedList<>(), graph, newBlock, deps, parts);
        /*
           mxgraph.layout.mxCompactTreeLayout;//good
           mxgraph.layout.mxOrganicLayout;//need some modifications
           mxgraph.layout.mxEdgeLabelLayout,mxParallelEdgeLayout,mxStackLayout;//bad
        */
        JGraphXAdapter<String, CustomEdge> jGraphXAdapter = new JGraphXAdapter<>(graph);
        mxIGraphLayout mxIGraphLayout = new mxCompactTreeLayout(jGraphXAdapter);//mxCompactTreeLayout//mxCircleLayout
        mxIGraphLayout.execute(jGraphXAdapter.getDefaultParent());
        BufferedImage bufferedImage = mxCellRenderer.createBufferedImage(jGraphXAdapter, null, 1, Color.WHITE, true, null);
        File newFIle = new File("graph.png");
        try {
            ImageIO.write(bufferedImage, "PNG", newFIle);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void dump(LinkedList<String> visitedNodes, Graph<String, CustomEdge> graph, IR1 newBlock, boolean deps, boolean parts) {
        System.out.println(newBlock.type + ": " + newBlock.blockId);
        if (visitedNodes.contains(newBlock.blockId)) return;//уже обошли
        if (deps) {
            newBlock.nodesOut.forEach(i -> {
                graph.addVertex(getMyDumpForGraph(i));
                graph.addEdge(getMyDumpForGraph(newBlock), getMyDumpForGraph(i), new CustomEdge("s", "s"));
            });
            newBlock.nodesOut.forEach(i -> dump(visitedNodes, graph, i, deps, parts));
        }
        if (parts) {
            newBlock.nodesOutChilds.forEach(i -> {
                graph.addVertex(getMyDumpForGraph(i));
                graph.addEdge(getMyDumpForGraph(newBlock), getMyDumpForGraph(i), new CustomEdge("v", "v"));
            });
            newBlock.nodesOutChilds.forEach(i -> dump(visitedNodes, graph, i, deps, parts));
        }
        /*(newBlock.nodesIn.forEach(i->{
            String myDumpForGraph = getMyDumpForGraph(i);
            if(!graph.containsVertex(myDumpForGraph))
                graph.addVertex(myDumpForGraph);
            if(!graph.containsEdge(myDumpForGraph, getMyDumpForGraph(newBlock)))
                graph.addEdge(myDumpForGraph, getMyDumpForGraph(newBlock),  new CustomEdge("v", "v"));
        });*/
        //вроде все в предыдущих связывается норм
    }

    private static String getMyDumpForGraph(IR1 newBlock) {
        StringBuilder sb = new StringBuilder();
        sb.append(newBlock.blockId + "\n");
        sb.append(newBlock.name + "\n");
        sb.append(newBlock.type + "\n");
        return sb.toString();
    }

    public void serialize(StringBuilder sb1, IR1 newBlock) {
        serialize(sb1, new LinkedList<>(), newBlock);
    }

    private void serialize(StringBuilder sb1, LinkedList<String> visitedNodes, IR1 newBlock) {
        if (visitedNodes.contains(newBlock.blockId)) return;//уже обошли
        sb1.append(newBlock.blockId + "\n");
        sb1.append(newBlock.name + "\n");
        sb1.append(newBlock.type + "\n");
        String nods = newBlock.nodesIn.stream().map(i -> i.blockId + ", ").reduce("", String::concat);
        if (newBlock.nodesIn.size() > 0) sb1.append(nods, 0, nods.length() - 2);
        sb1.append("\n");
        nods = newBlock.nodesOut.stream().map(i -> i.blockId + ", ").reduce("", String::concat);
        if (newBlock.nodesOut.size() > 0) sb1.append(nods, 0, nods.length() - 2);
        sb1.append("\n");
        newBlock.nodesOut.forEach(i -> serialize(sb1, visitedNodes, i));
    }

}
