package asu.cse464;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class GraphSearchTemplate implements SearchStrategy {

    protected boolean isSearchable(String src, String dst, Map<String, List<String>> adjacency) {
        return src != null
            && dst != null
            && adjacency != null
            && adjacency.containsKey(src)
            && adjacency.containsKey(dst);
    }

    protected Path buildPath(List<String> labels) {
        List<Node> nodes = new ArrayList<>();
        for (String label : labels) {
            nodes.add(new Node(label));
        }
        return new Path(nodes);
    }

    protected void printVisit(Path path) {
        System.out.println("visiting " + formatPath(path));
    }

    protected String formatPath(Path path) {
        return "Path{nodes=["
            + path.getNodes().stream()
                .map(node -> "Node{" + node.getLabel() + "}")
                .collect(Collectors.joining(", "))
            + "]}";
    }

    protected List<String> getUnvisitedNeighbors(String node,
                                                 Map<String, List<String>> adjacency,
                                                 Set<String> visited) {
        Set<String> visitedNodes = visited == null ? Collections.emptySet() : visited;
        List<String> unvisited = new ArrayList<>();
        for (String neighbor : adjacency.getOrDefault(node, Collections.emptyList())) {
            if (neighbor == null) {
                continue;
            }
            if (!visitedNodes.contains(neighbor)) {
                unvisited.add(neighbor);
            }
        }
        return unvisited;
    }
}