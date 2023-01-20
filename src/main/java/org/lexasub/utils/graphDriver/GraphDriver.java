package org.lexasub.utils.graphDriver;

public interface GraphDriver {
    void write(String pathname, String fileFormat);

    void addEdge(String s1, String s2, String s);
}
