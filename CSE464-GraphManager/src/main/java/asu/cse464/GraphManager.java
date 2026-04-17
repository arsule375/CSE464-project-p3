package asu.cse464;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;
import guru.nidi.graphviz.parse.Parser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static guru.nidi.graphviz.model.Factory.*;

/**
 * GraphManager: Parses, manipulates, and outputs DOT-format directed graphs.
 *
 * Features:
 *   1. parseGraph(String filepath)    - Parse a DOT file into a graph
 *   2. addNode(String label)          - Add a node (no duplicates)
 *      addNodes(String[] labels)      - Add multiple nodes
 *      removeNode(String label)        - Remove a node and incident edges
 *      removeNodes(String[] labels)    - Remove multiple nodes
 *   3. addEdge(String src, String dst)- Add an edge (no duplicates)
 *      removeEdge(String src, String dst) - Remove a single edge
 *   5. GraphSearch(Node src, Node dst, Algorithm algo) - Find a path using BFS/DFS
 *   4. outputDOTGraph(String path)    - Write graph to DOT file
 *      outputGraphics(String path, String format) - Write graph to image (png)
 */
public class GraphManager {

    public enum Algorithm {
        BFS,
        DFS
    }

    // Internal representation: adjacency list + node set
    private final LinkedHashSet<String> nodes = new LinkedHashSet<>();
    private final LinkedHashSet<String> edges = new LinkedHashSet<>(); // stored as "src->dst"
    private String graphName = "G";

    // -------------------------------------------------------------------------
    // Feature 1: Parse a DOT graph file
    // -------------------------------------------------------------------------

    /**
     * Parses a DOT graph file and populates internal node/edge structures.
     *
     * @param filepath path to the .dot file
     * @throws IOException if the file cannot be read or parsed
     */
    public void parseGraph(String filepath) throws IOException {
        nodes.clear();
        edges.clear();

        String dotContent = new String(Files.readAllBytes(Paths.get(filepath)));

        // Use nidi parser to get the graph structure
        MutableGraph mg = new Parser().read(dotContent);
        graphName = mg.name().toString().isEmpty() ? "G" : mg.name().toString();

        // Extract nodes and edges from the parsed graph
        for (MutableNode node : mg.nodes()) {
            String nodeName = node.name().toString();
            nodes.add(nodeName);
            // Collect edges from this node
            node.links().forEach(link -> {
                String dst = link.to().name().toString();
                nodes.add(dst);
                edges.add(nodeName + "->" + dst);
            });
        }
    }

    /**
     * Returns a text summary of the graph.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Number of Nodes: ").append(nodes.size()).append("\n");
        sb.append("Node Labels: ").append(String.join(", ", nodes)).append("\n");
        sb.append("Number of Edges: ").append(edges.size()).append("\n");
        sb.append("Edges:\n");
        for (String edge : edges) {
            String[] parts = edge.split("->");
            sb.append("  ").append(parts[0]).append(" -> ").append(parts[1]).append("\n");
        }
        return sb.toString();
    }

    /**
     * Outputs the graph summary to a text file.
     *
     * @param filepath path to the output text file
     * @throws IOException if write fails
     */
    public void outputGraph(String filepath) throws IOException {
        Files.writeString(Paths.get(filepath), this.toString());
    }

    // -------------------------------------------------------------------------
    // Feature 2: Add nodes
    // -------------------------------------------------------------------------

    /**
     * Adds a single node. Ignores duplicates.
     *
     * @param label the node label
     * @return true if added, false if duplicate
     */
    public boolean addNode(String label) {
        if (label == null || label.isEmpty()) return false;
        return nodes.add(label);
    }

    /**
     * Adds multiple nodes. Ignores duplicates.
     *
     * @param labels array of node labels
     */
    public void addNodes(String[] labels) {
        for (String label : labels) {
            addNode(label);
        }
    }

    /**
     * Removes a single node and all edges connected to it.
     *
     * @param label the node label
     * @return true if the node existed and was removed
     * @throws IllegalArgumentException if label is invalid or node does not exist
     */
    public boolean removeNode(String label) {
        if (label == null || label.isEmpty()) {
            throw new IllegalArgumentException("Node label cannot be null or empty");
        }
        boolean removed = nodes.remove(label);
        if (!removed) {
            throw new IllegalArgumentException("Node does not exist: " + label);
        }

        edges.removeIf(edge -> {
            String[] parts = edge.split("->");
            return parts[0].equals(label) || parts[1].equals(label);
        });
        return true;
    }

    /**
     * Removes multiple nodes and their incident edges.
     *
     * @param labels array of node labels
     */
    public void removeNodes(String[] labels) {
        for (String label : labels) {
            removeNode(label);
        }
    }

    // -------------------------------------------------------------------------
    // Feature 3: Add edges
    // -------------------------------------------------------------------------

    /**
     * Adds an edge between two nodes. Creates nodes if they don't exist.
     * Ignores duplicate edges.
     *
     * @param srcLabel source node label
     * @param dstLabel destination node label
     * @return true if added, false if duplicate
     */
    public boolean addEdge(String srcLabel, String dstLabel) {
        if (srcLabel == null || dstLabel == null) return false;
        addNode(srcLabel);
        addNode(dstLabel);
        return edges.add(srcLabel + "->" + dstLabel);
    }

    /**
     * Finds a path from src to dst using the selected graph search algorithm.
     *
     * @param src source node
     * @param dst destination node
     * @param algo BFS or DFS
     * @return path as a Path object, or null if no path exists
     */
    public Path GraphSearch(Node src, Node dst, Algorithm algo) {
        if (src == null || dst == null) return null;
        if (algo == null) return null;

        String srcLabel = src.getLabel();
        String dstLabel = dst.getLabel();

        if (!nodes.contains(srcLabel) || !nodes.contains(dstLabel)) return null;

        if (srcLabel.equals(dstLabel)) {
            return new Path(Collections.singletonList(new Node(srcLabel)));
        }

        Map<String, List<String>> adjacency = new LinkedHashMap<>();
        for (String node : nodes) {
            adjacency.put(node, new ArrayList<>());
        }
        for (String edge : edges) {
            String[] parts = edge.split("->");
            adjacency.get(parts[0]).add(parts[1]);
        }

        if (algo == Algorithm.BFS) {
            return bfsSearch(srcLabel, dstLabel, adjacency);
        }
        return dfsSearch(srcLabel, dstLabel, adjacency);
    }

    /**
     * Backward-compatible overload defaults to BFS.
     */
    public Path GraphSearch(Node src, Node dst) {
        return GraphSearch(src, dst, Algorithm.BFS);
    }

    private Path bfsSearch(String srcLabel, String dstLabel, Map<String, List<String>> adjacency) {
        Queue<String> queue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        Map<String, String> parent = new HashMap<>();

        queue.add(srcLabel);
        visited.add(srcLabel);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            for (String neighbor : adjacency.getOrDefault(current, Collections.emptyList())) {
                if (visited.contains(neighbor)) continue;
                visited.add(neighbor);
                parent.put(neighbor, current);
                if (neighbor.equals(dstLabel)) {
                    List<Node> route = new ArrayList<>();
                    String step = dstLabel;
                    while (step != null) {
                        route.add(new Node(step));
                        step = parent.get(step);
                    }
                    Collections.reverse(route);
                    return new Path(route);
                }
                queue.add(neighbor);
            }
        }

        return null;
    }

    private Path dfsSearch(String srcLabel, String dstLabel, Map<String, List<String>> adjacency) {
        Set<String> visited = new HashSet<>();
        Map<String, String> parent = new HashMap<>();
        boolean found = depthFirstSearch(srcLabel, dstLabel, adjacency, visited, parent);
        if (!found) return null;

        List<Node> route = new ArrayList<>();
        String current = dstLabel;
        while (current != null) {
            route.add(new Node(current));
            current = parent.get(current);
        }
        Collections.reverse(route);
        return new Path(route);
    }

    private boolean depthFirstSearch(String current,
                                     String target,
                                     Map<String, List<String>> adjacency,
                                     Set<String> visited,
                                     Map<String, String> parent) {
        if (current.equals(target)) return true;
        visited.add(current);

        for (String next : adjacency.getOrDefault(current, Collections.emptyList())) {
            if (!visited.contains(next)) {
                parent.put(next, current);
                if (depthFirstSearch(next, target, adjacency, visited, parent)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Removes a single edge if present.
     *
     * @param srcLabel source node label
     * @param dstLabel destination node label
     * @return true if the edge existed and was removed
     * @throws IllegalArgumentException if labels are invalid or edge does not exist
     */
    public boolean removeEdge(String srcLabel, String dstLabel) {
        if (srcLabel == null || dstLabel == null || srcLabel.isEmpty() || dstLabel.isEmpty()) {
            throw new IllegalArgumentException("Source and destination labels cannot be null or empty");
        }

        String edgeKey = srcLabel + "->" + dstLabel;
        if (!edges.contains(edgeKey)) {
            throw new IllegalArgumentException("Edge does not exist: " + edgeKey);
        }
        return edges.remove(edgeKey);
    }

    // -------------------------------------------------------------------------
    // Feature 4: Output graph
    // -------------------------------------------------------------------------

    /**
     * Outputs the graph to a DOT-format file.
     *
     * @param path output file path
     * @throws IOException if write fails
     */
    public void outputDOTGraph(String path) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph ").append(graphName).append(" {\n");
        for (String node : nodes) {
            sb.append("    ").append(node).append(";\n");
        }
        for (String edge : edges) {
            String[] parts = edge.split("->");
            sb.append("    ").append(parts[0]).append(" -> ").append(parts[1]).append(";\n");
        }
        sb.append("}\n");
        Files.writeString(Paths.get(path), sb.toString());
    }

    /**
     * Outputs the graph as an image file.
     *
     * @param path   output file path (e.g., "output.png")
     * @param format image format, currently supports "png"
     * @throws IOException if render or write fails
     */
    public void outputGraphics(String path, String format) throws IOException {
        // Build a DOT string, then render via graphviz-java
        StringBuilder sb = new StringBuilder();
        sb.append("digraph ").append(graphName).append(" {\n");
        for (String node : nodes) {
            sb.append("    ").append(node).append(";\n");
        }
        for (String edge : edges) {
            String[] parts = edge.split("->");
            sb.append("    ").append(parts[0]).append(" -> ").append(parts[1]).append(";\n");
        }
        sb.append("}\n");

        MutableGraph mg = new Parser().read(sb.toString());

        Format gvFormat;
        switch (format.toLowerCase()) {
            case "png":
                gvFormat = Format.PNG;
                break;
            case "svg":
                gvFormat = Format.SVG;
                break;
            default:
                throw new IllegalArgumentException("Unsupported format: " + format + ". Use 'png' or 'svg'.");
        }

        Graphviz.fromGraph(mg).render(gvFormat).toFile(new File(path));
    }

    // -------------------------------------------------------------------------
    // Getters for testing
    // -------------------------------------------------------------------------

    public Set<String> getNodes() {
        return Collections.unmodifiableSet(nodes);
    }

    public Set<String> getEdges() {
        return Collections.unmodifiableSet(edges);
    }

    public int getNodeCount() {
        return nodes.size();
    }

    public int getEdgeCount() {
        return edges.size();
    }
}
