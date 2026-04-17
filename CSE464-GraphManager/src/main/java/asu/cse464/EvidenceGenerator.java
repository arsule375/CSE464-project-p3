package asu.cse464;

import java.io.IOException;
import java.nio.file.Files;

/**
 * Generates visual evidence images for remove operations and graph search.
 */
public class EvidenceGenerator {
    public static void main(String[] args) throws IOException {
        java.nio.file.Path evidenceDir = java.nio.file.Path.of("evidence");
        Files.createDirectories(evidenceDir);

        generateRemoveNodeEvidence(evidenceDir);
        generateRemoveEdgeEvidence(evidenceDir);
        generateSearchEvidence(evidenceDir);
    }

    private static void generateRemoveNodeEvidence(java.nio.file.Path evidenceDir) throws IOException {
        GraphManager before = new GraphManager();
        before.parseGraph("src/main/resources/input.dot");
        before.outputGraphics(evidenceDir.resolve("remove-node-before.png").toString(), "png");

        GraphManager after = new GraphManager();
        after.parseGraph("src/main/resources/input.dot");
        after.removeNode("a");
        after.outputGraphics(evidenceDir.resolve("remove-node-after.png").toString(), "png");
    }

    private static void generateRemoveEdgeEvidence(java.nio.file.Path evidenceDir) throws IOException {
        GraphManager before = new GraphManager();
        before.parseGraph("src/main/resources/input.dot");
        before.outputGraphics(evidenceDir.resolve("remove-edge-before.png").toString(), "png");

        GraphManager after = new GraphManager();
        after.parseGraph("src/main/resources/input.dot");
        after.removeEdge("a", "e");
        after.outputGraphics(evidenceDir.resolve("remove-edge-after.png").toString(), "png");
    }

    private static void generateSearchEvidence(java.nio.file.Path evidenceDir) throws IOException {
        GraphManager searchGraph = new GraphManager();
        searchGraph.addEdge("s", "b");
        searchGraph.addEdge("s", "a");
        searchGraph.addEdge("a", "t");
        searchGraph.addEdge("b", "c");
        searchGraph.addEdge("c", "t");

        // Full graph used for both BFS and DFS test-case paths.
        searchGraph.outputGraphics(evidenceDir.resolve("search-test-graph.png").toString(), "png");

        Path bfsPath = searchGraph.GraphSearch(
            new Node("s"),
            new Node("t"),
            GraphManager.Algorithm.BFS
        );
        Path dfsPath = searchGraph.GraphSearch(
            new Node("s"),
            new Node("t"),
            GraphManager.Algorithm.DFS
        );

        GraphManager bfsPathGraph = new GraphManager();
        addPathEdges(bfsPathGraph, bfsPath);
        bfsPathGraph.outputGraphics(evidenceDir.resolve("bfs-path.png").toString(), "png");

        GraphManager dfsPathGraph = new GraphManager();
        addPathEdges(dfsPathGraph, dfsPath);
        dfsPathGraph.outputGraphics(evidenceDir.resolve("dfs-path.png").toString(), "png");

        String summary = "BFS path: " + bfsPath + System.lineSeparator()
            + "DFS path: " + dfsPath + System.lineSeparator();
        Files.writeString(evidenceDir.resolve("search-paths.txt"), summary);
    }

    private static void addPathEdges(GraphManager gm, Path path) {
        if (path == null || path.getNodes().isEmpty()) {
            return;
        }

        if (path.getNodes().size() == 1) {
            gm.addNode(path.getNodes().get(0).getLabel());
            return;
        }

        for (int i = 0; i < path.getNodes().size() - 1; i++) {
            gm.addEdge(path.getNodes().get(i).getLabel(), path.getNodes().get(i + 1).getLabel());
        }
    }
}
