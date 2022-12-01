package org.lexasub.IR1.utils;

import com.mxgraph.layout.*;
import com.mxgraph.util.mxCellRenderer;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DirectedPseudograph;
import org.lexasub.frontend.utils.FrontendBaseBlock;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public class IR1BaseBlock {
    private String name;
    private String code;
    private String blockId;
    private FrontendBaseBlock.TYPE type;
    LinkedList<IR1BaseBlock> nodesIn = new LinkedList<>();
    LinkedList<IR1BaseBlock> nodesOut = new LinkedList<>();

    static boolean jsonize = false;
    public static IR1BaseBlock makeFromFrontendBaseBlock(FrontendBaseBlock frontendBlock) {
        return makeFromFrontendBaseBlock(frontendBlock, new HashMap<>());
    }
    private static IR1BaseBlock makeFromFrontendBaseBlock(FrontendBaseBlock frontendBlock, HashMap<String, IR1BaseBlock> _decls) {
        //clone for cancel local modification(mb scopes)(ex local variables)//TODO check
        IR1BaseBlock ir1BB = new IR1BaseBlock();
        getIr1BaseBlock(frontendBlock, _decls, ir1BB).forEach(ir1BB::connectTo);
        return ir1BB;
    }

    private static LinkedList<IR1BaseBlock> getIr1BaseBlock(FrontendBaseBlock frontendBlock, HashMap<String, IR1BaseBlock> _decls, IR1BaseBlock ir1BB) {
        HashMap<String, IR1BaseBlock> decls = (HashMap<String, IR1BaseBlock>) _decls.clone();
        ir1BB.name = frontendBlock.name;
        ir1BB.code = frontendBlock.code;
        ir1BB.type = frontendBlock.type;
        ir1BB.blockId = frontendBlock.blockId;
        // name -> addSome
        LinkedList<FrontendBaseBlock> fbChilds = frontendBlock.childs;
        int cnt = addFuncArgs(ir1BB, decls, fbChilds.iterator());
        autoDeclare(ir1BB, decls);
        Stream<IR1BaseBlock> ir1BaseBlockStream = fbChilds.stream().skip(cnt) //skip funcArgs
                .map(i -> {
                    IR1BaseBlock ir1BaseBlock = makeFromFrontendBaseBlock(i, decls);
                    if (!Objects.equals(i.type, FrontendBaseBlock.TYPE.BLOCK))
                        decls.put("res_" + i.blockId, ir1BaseBlock);
                    return ir1BaseBlock;
                });
        //may be last expr - it's return if blockId != ""
        return new LinkedList<>(ir1BaseBlockStream.toList());
    }

    private static void autoDeclare(IR1BaseBlock ir1BB, HashMap<String, IR1BaseBlock> decls) {
        if(!Objects.equals(ir1BB.name, ""))
            decls.put(ir1BB.name, ir1BB);
        else if (!Objects.equals(ir1BB.code, "")) {
            //TODO
            //extract from code ids and maybe add to nodesIn, nodesOut
            Arrays.stream(ir1BB.code.split(" |\\(|\\)")).forEach(i->{
                IR1BaseBlock ir1BaseBlock = decls.get(i);
                if(ir1BaseBlock==null)
                    return;
                //ok on read variable, on write to variable-it's wrong
                //if == "res_...." -> вроде всегда получаем read.
                // write на данном этапе в дереве не будет.(т.к.) auto-return(ex return last expr) not applyed
                ir1BaseBlock.connectTo(ir1BB);//mb_swap
            });
        }
    }

    private void connectTo(IR1BaseBlock ir1BaseBlock) {
        nodesOut.add(ir1BaseBlock);
        ir1BaseBlock.nodesIn.add(this);
    }

    private static int addFuncArgs(IR1BaseBlock ir1BB, HashMap<String, IR1BaseBlock> decls, Iterator<FrontendBaseBlock> childsIt) {
        int cnt = 0;
        if(ir1BB.type == FrontendBaseBlock.TYPE.FUNC) {
            while (childsIt.hasNext()){
                FrontendBaseBlock v = childsIt.next();
                if(v.type != FrontendBaseBlock.TYPE.ID) break;
                ++cnt;
                IR1BaseBlock vv = new IR1BaseBlock();
                ir1BB.connectTo(vv);
                vv.name = v.name;
                vv.code = v.code;
                vv.type = v.type;
                vv.blockId = v.blockId;
                decls.put(v.name, vv);
                //childs должен при type=ID быть пустым
            }
        }
        return cnt;
    }
    public void dump() {//TODO
        Graph<String, CustomEdge> graph = new DefaultDirectedGraph<>(CustomEdge.class);//DirectedPseudograph//DefaultDirectedGraph
        graph.addVertex(this.getMyDumpForGraph());
        dump(new LinkedList<>(), graph);
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

    private void dump(LinkedList<String> visitedNodes, Graph<String, CustomEdge> graph) {
        if(visitedNodes.contains(blockId)) return;//уже обошли
        nodesOut.forEach(i->{
            graph.addVertex(i.getMyDumpForGraph());
            graph.addEdge(this.getMyDumpForGraph(), i.getMyDumpForGraph(), new CustomEdge("s", "s"));
        });
        /*nodesIn.forEach(i->{
            String myDumpForGraph = i.getMyDumpForGraph();
            if(!graph.containsVertex(myDumpForGraph))
                graph.addVertex(myDumpForGraph);
            if(!graph.containsEdge(myDumpForGraph, this.getMyDumpForGraph()))
                graph.addEdge(myDumpForGraph, this.getMyDumpForGraph(),  new CustomEdge("v", "v"));
        });*///вроде все в предыдущих связывается норм
        nodesOut.forEach(i->i.dump(visitedNodes, graph));
    }

    public void serialize(StringBuilder sb1) {
        serialize(sb1, new LinkedList<>());
    }

    private String getMyDumpForGraph() {
        StringBuilder sb = new StringBuilder();
        sb.append(blockId + "\n");
        sb.append(name + "\n");
        sb.append(code + "\n");
        sb.append(type + "\n");
        return sb.toString();
    }

    private void serialize(StringBuilder sb1, LinkedList<String> visitedNodes) {
        if(visitedNodes.contains(blockId)) return;//уже обошли
        sb1.append(blockId + "\n");
        sb1.append(name + "\n");
        sb1.append(code + "\n");
        sb1.append(type + "\n");
        String nods = nodesIn.stream().map(i -> i.blockId + ", ").reduce("", String::concat);
        if(nodesIn.size() > 0) sb1.append(nods, 0, nods.length() - 2);
        sb1.append("\n");
        nods = nodesOut.stream().map(i -> i.blockId + ", ").reduce("", String::concat);
        if(nodesOut.size() > 0) sb1.append(nods, 0, nods.length() - 2);
        sb1.append("\n");
        nodesOut.forEach(i->i.serialize(sb1, visitedNodes));
    }

}
