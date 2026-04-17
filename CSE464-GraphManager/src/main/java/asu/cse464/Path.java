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

    @Override
    public String toString() {
        return nodes.stream().map(Node::getLabel).collect(Collectors.joining(" -> "));
    }
}
