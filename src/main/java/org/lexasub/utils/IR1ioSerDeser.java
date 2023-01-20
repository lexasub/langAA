package org.lexasub.utils;

public class IR1ioSerDeser {
    /*
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
*/
}
