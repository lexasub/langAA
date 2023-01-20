package org.lexasub.utils;

import org.lexasub.IR1.IR1;
import org.lexasub.utils.graphiz.graphizDriver;

import java.util.List;

public class GraphDumper {

    static boolean jsonize = false;
    static boolean deps  = false;
    static boolean parts = false;
    static GraphDriver graphDriver;
    public static void dump(IR1 newBlock, boolean deps, boolean parts) {//TODO
        graphDriver = new graphizDriver(getMyDumpForGraph(newBlock));//new jgraphtDriver();
        GraphDumper.deps = deps;
        GraphDumper.parts = parts;
        dump(/*new LinkedList<>(),*/ newBlock);
        graphDriver.write("graph.svg", "SVG");
    }

    private static void dump(/*LinkedList<String> visitedNodes,*/ IR1 newBlock) {
        System.out.println(newBlock.type + ": " + newBlock.blockId);
        //if (visitedNodes.contains(newBlock.blockId)) return;//уже обошли
        String edge = getMyDumpForGraph(newBlock);
        if (deps) dumpChilds(/*visitedNodes,*/ newBlock.nodesOut, edge, "s");
        if (parts) dumpChilds(/*visitedNodes,*/ newBlock.nodesOutChilds, edge, "v");
        /*(newBlock.nodesIn.forEach(i->{
            String edge = getMyDumpForGraph(i);
            if(!graphDriver.containsVertex(edge))
                graphDriver.addVertex(edge);
            if(!graphDriver.containsEdge(edge, getMyDumpForGraph(newBlock)))
                graphDriver.addEdge(edge, getMyDumpForGraph(newBlock), "v");
        });*/
        //вроде все в предыдущих связывается норм
    }

    private static void dumpChilds(/*LinkedList<String> visitedNodes, */List<IR1> childs, String edge, String s) {
        childs.forEach(i -> graphDriver.addEdge(edge, getMyDumpForGraph(i), s));
        childs.forEach(i -> dump(/*visitedNodes,*/ i));
    }

    private static String getMyDumpForGraph(IR1 newBlock) {
        StringBuilder sb = new StringBuilder();
        sb.append(newBlock.blockId + "\n");
        sb.append(newBlock.name + "\n");
        sb.append(newBlock.type + "\n");
        return sb.toString();
    }

}
