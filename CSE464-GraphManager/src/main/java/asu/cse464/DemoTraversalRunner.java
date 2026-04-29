package asu.cse464;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

/**
 * Demo runner that prints traversal histories in TA-friendly format.
 *
 * Uses only input.dot in the project root directory.
 */
public class DemoTraversalRunner {

    private static final String DOT_PATH = "input.dot";
    private static final String SOURCE = "a";
    private static final String TARGET = "h";

    public static void main(String[] args) throws IOException {
        showInputDot();

        GraphManager gm = new GraphManager();
        gm.parseGraph(DOT_PATH);
        Map<String, List<String>> adjacency = buildAlphabeticalAdjacency(gm);

        System.out.println("\nBFS:");
        runBfsWithVisitHistory(SOURCE, TARGET, adjacency);

        System.out.println("\nDFS:");
        runDfsWithVisitHistory(SOURCE, TARGET, adjacency);

        System.out.println("\nRandom Walk (no backtracking, unvisited neighbors only):");
        runRandomWalkDemo(SOURCE, TARGET, adjacency);
    }

    private static void showInputDot() throws IOException {
        System.out.println("Input.dot content:");
        System.out.println(Files.readString(Path.of(DOT_PATH)).trim());
    }

    private static Map<String, List<String>> buildAlphabeticalAdjacency(GraphManager gm) {
        Map<String, List<String>> adjacency = new LinkedHashMap<>();
        for (String node : gm.getNodes()) {
            adjacency.put(node, new ArrayList<>());
        }

        for (String edge : gm.getEdges()) {
            String[] parts = edge.split("->", 2);
            if (parts.length < 2) {
                continue;
            }
            adjacency.computeIfAbsent(parts[0], key -> new ArrayList<>()).add(parts[1]);
            adjacency.computeIfAbsent(parts[1], key -> new ArrayList<>());
        }

        for (List<String> neighbors : adjacency.values()) {
            Collections.sort(neighbors);
        }
        return adjacency;
    }

    private static void runBfsWithVisitHistory(String src,
                                               String dst,
                                               Map<String, List<String>> adjacency) {
        Queue<String> queue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        List<String> visitHistory = new ArrayList<>();

        queue.add(src);
        visited.add(src);
        visitHistory.add(src);
        printVisitHistory(visitHistory);

        if (src.equals(dst)) {
            System.out.println("Found target node: " + dst);
            return;
        }

        while (!queue.isEmpty()) {
            String current = queue.poll();
            for (String neighbor : adjacency.getOrDefault(current, Collections.emptyList())) {
                if (visited.contains(neighbor)) {
                    continue;
                }
                visited.add(neighbor);
                queue.add(neighbor);
                visitHistory.add(neighbor);
                printVisitHistory(visitHistory);

                if (neighbor.equals(dst)) {
                    System.out.println("Found target node: " + dst);
                    return;
                }
            }
        }

        System.out.println("Target node not found: " + dst);
    }

    private static void runDfsWithVisitHistory(String src,
                                               String dst,
                                               Map<String, List<String>> adjacency) {
        Deque<String> stack = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        List<String> visitHistory = new ArrayList<>();

        stack.push(src);
        while (!stack.isEmpty()) {
            String current = stack.pop();
            if (!visited.add(current)) {
                continue;
            }

            visitHistory.add(current);
            printVisitHistory(visitHistory);
            if (current.equals(dst)) {
                System.out.println("Found target node: " + dst);
                return;
            }

            List<String> neighbors = adjacency.getOrDefault(current, Collections.emptyList());
            for (int i = neighbors.size() - 1; i >= 0; i--) {
                String next = neighbors.get(i);
                if (!visited.contains(next)) {
                    stack.push(next);
                }
            }
        }

        System.out.println("Target node not found: " + dst);
    }

    private static void runRandomWalkDemo(String src,
                                          String dst,
                                          Map<String, List<String>> adjacency) {
        int runs = 0;
        int maxRuns = 10;
        Set<String> uniqueSuccessfulPaths = new LinkedHashSet<>();
        long[] seeds = new long[]{1L, 2L, 7L, 11L, 19L, 23L, 31L, 41L, 53L, 67L};

        while (runs < maxRuns) {
            runs++;
            System.out.println("Run " + runs + ":");
            String result = runSingleRandomWalk(src, dst, adjacency, new Random(seeds[runs - 1]));
            if (result != null) {
                uniqueSuccessfulPaths.add(result);
            }

            if (runs >= 5 && uniqueSuccessfulPaths.size() >= 2) {
                break;
            }
        }

        System.out.println("Random Walk runs completed: " + runs);
        System.out.println("Distinct successful paths found: " + uniqueSuccessfulPaths.size());
    }

    /**
     * Recommended random walk: pick a random unvisited neighbor; no backtracking.
     */
    private static String runSingleRandomWalk(String src,
                                              String dst,
                                              Map<String, List<String>> adjacency,
                                              Random random) {
        String current = src;
        Set<String> visited = new HashSet<>();
        List<String> path = new ArrayList<>();

        visited.add(current);
        path.add(current);
        printVisitHistory(path);

        if (src.equals(dst)) {
            System.out.println("Found target node: " + dst);
            return String.join("-", path);
        }

        while (true) {
            List<String> choices = new ArrayList<>();
            for (String neighbor : adjacency.getOrDefault(current, Collections.emptyList())) {
                if (!visited.contains(neighbor)) {
                    choices.add(neighbor);
                }
            }

            if (choices.isEmpty()) {
                System.out.println("Reached dead end at node" + current);
                return null;
            }

            String next = choices.get(random.nextInt(choices.size()));
            visited.add(next);
            path.add(next);
            printVisitHistory(path);

            if (next.equals(dst)) {
                System.out.println("Found target node: " + dst);
                return String.join("-", path);
            }

            current = next;
        }
    }

    private static void printVisitHistory(List<String> history) {
        System.out.println("Visit Node History: " + String.join("-", history));
    }
}