package org.lexasub.utils.graphDriver.graphiz;

import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Node;
import org.lexasub.utils.graphDriver.GraphDriver;

import java.io.File;
import java.io.IOException;

import static guru.nidi.graphviz.attribute.Rank.RankDir.LEFT_TO_RIGHT;
import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;
import static guru.nidi.graphviz.model.Link.to;

public class graphizDriver implements GraphDriver {
    Graph g;

    public graphizDriver(String skip) {
        g = graph("example1").directed()
                .graphAttr().with(Rank.dir(LEFT_TO_RIGHT))
                .linkAttr().with("class", "link-class");
    }

    @Override
    public void write(String pathname, String fileFormat) {
        try {
            Graphviz.fromGraph(g).height(100).render(Format.valueOf(fileFormat)).toFile(new File(pathname));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addEdge(String s1, String s2, String s) {
        //Label.lines("s2", "s8")
        Node n1 = node(s1).with(Shape.RECORD);
        Node n2 = node(s2).with(Shape.RECORD);
        g = g.with(n1.link(to(n2).with(s.equals("s") ? Style.DOTTED : Style.BOLD)));
    }
}
