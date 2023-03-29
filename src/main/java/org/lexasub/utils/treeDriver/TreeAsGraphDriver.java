package org.lexasub.utils.treeDriver;

import org.lexasub.utils.graphDriver.GraphDriver;

public class TreeAsGraphDriver implements TreeDriver {
    GraphDriver graph;
    String content;

    public TreeAsGraphDriver(GraphDriver graph, String content) {
        this.graph = graph;
        this.content = content;
    }

    @Override
    public void addChild(String s) {
        graph.addEdge(content, s, "s");//addChild
    }
}
