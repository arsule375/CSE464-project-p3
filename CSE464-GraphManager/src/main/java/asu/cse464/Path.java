package asu.cse464;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Path stores an ordered route of nodes.
 */
public class Path {
    private final List<Node> nodes;

    public Path(List<Node> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            throw new IllegalArgumentException("Path nodes cannot be null or empty");
        }
        this.nodes = Collections.unmodifiableList(new ArrayList<>(nodes));
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void addEdgesToGraph(GraphManager gm) {
        if (nodes.isEmpty()) return;
        if (nodes.size() == 1) {
            gm.addNode(nodes.get(0).getLabel());
            return;
        }
        for (int i = 0; i < nodes.size() - 1; i++) {
            gm.addEdge(nodes.get(i).getLabel(), nodes.get(i + 1).getLabel());
        }
    }

    @Override
    public String toString() {
        return nodes.stream().map(Node::getLabel).collect(Collectors.joining(" -> "));
    }
}
