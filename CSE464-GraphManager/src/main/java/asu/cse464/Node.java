package asu.cse464;

import java.util.Objects;

/**
 * Node represents a graph vertex by label.
 */
public class Node {
    private final String label;

    public Node(String label) {
        if (label == null || label.isEmpty()) {
            throw new IllegalArgumentException("Node label cannot be null or empty");
        }
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;
        Node node = (Node) o;
        return Objects.equals(label, node.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label);
    }

    @Override
    public String toString() {
        return label;
    }
}
