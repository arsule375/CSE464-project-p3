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
        if (bfsPath != null) {
            bfsPath.addEdgesToGraph(bfsPathGraph);
        }
        bfsPathGraph.outputGraphics(evidenceDir.resolve("bfs-path.png").toString(), "png");

        GraphManager dfsPathGraph = new GraphManager();
        if (dfsPath != null) {
            dfsPath.addEdgesToGraph(dfsPathGraph);
        }
        dfsPathGraph.outputGraphics(evidenceDir.resolve("dfs-path.png").toString(), "png");

        String bfsSummary = bfsPath == null ? "no path found" : bfsPath.toString();
        String dfsSummary = dfsPath == null ? "no path found" : dfsPath.toString();
        String summary = "BFS path: " + bfsSummary + System.lineSeparator()
            + "DFS path: " + dfsSummary + System.lineSeparator();
        Files.writeString(evidenceDir.resolve("search-paths.txt"), summary);
    }
}
