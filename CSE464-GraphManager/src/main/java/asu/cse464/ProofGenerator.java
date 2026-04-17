package asu.cse464;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Generates visual proof images for remove operations and BFS/DFS path search.
 */
public class ProofGenerator {
    public static void main(String[] args) throws IOException {
        Path outDir = Path.of("evidence");
        Files.createDirectories(outDir);

        GraphManager gm = new GraphManager();
        gm.parseGraph("src/main/resources/input.dot");

        // Remove method proofs.
        gm.outputGraphics(outDir.resolve("remove_before.png").toString(), "png");

        gm.removeNode("e");
        gm.outputGraphics(outDir.resolve("remove_after_removeNode_e.png").toString(), "png");

        gm.parseGraph("src/main/resources/input.dot");
        gm.removeEdge("a", "e");
        gm.outputGraphics(outDir.resolve("remove_after_removeEdge_a_e.png").toString(), "png");

        // Build a graph where BFS and DFS return different valid paths.
        GraphManager searchGraph = new GraphManager();
        searchGraph.addEdge("s", "b");
        searchGraph.addEdge("s", "a");
        searchGraph.addEdge("a", "t");
        searchGraph.addEdge("b", "c");
        searchGraph.addEdge("c", "t");
        searchGraph.outputGraphics(outDir.resolve("search_graph.png").toString(), "png");

        Path bfsPathFile = outDir.resolve("bfs_path.png");
        Path dfsPathFile = outDir.resolve("dfs_path.png");

        PathResult bfs = renderPath(searchGraph.GraphSearch(new Node("s"), new Node("t"), GraphManager.Algorithm.BFS), bfsPathFile.toString());
        PathResult dfs = renderPath(searchGraph.GraphSearch(new Node("s"), new Node("t"), GraphManager.Algorithm.DFS), dfsPathFile.toString());

        String summary = "BFS Path: " + bfs.pathText + System.lineSeparator()
            + "DFS Path: " + dfs.pathText + System.lineSeparator();
        Files.writeString(outDir.resolve("path_results.txt"), summary);
    }

    private static PathResult renderPath(asu.cse464.Path path, String outputFile) throws IOException {
        if (path == null) {
            return new PathResult("(no path)");
        }

        List<Node> route = path.getNodes();
        GraphManager pathGraph = new GraphManager();
        for (int i = 0; i < route.size() - 1; i++) {
            pathGraph.addEdge(route.get(i).getLabel(), route.get(i + 1).getLabel());
        }

        // Handle single-node route.
        if (route.size() == 1) {
            pathGraph.addNode(route.get(0).getLabel());
        }

        pathGraph.outputGraphics(outputFile, "png");
        return new PathResult(path.toString());
    }

    private static class PathResult {
        private final String pathText;

        private PathResult(String pathText) {
            this.pathText = pathText;
        }
    }
}
