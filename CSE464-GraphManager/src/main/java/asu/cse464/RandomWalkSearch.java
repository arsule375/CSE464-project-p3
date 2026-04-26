package asu.cse464;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RandomWalkSearch extends GraphSearchTemplate implements SearchStrategy {
    private final Random random;

    public RandomWalkSearch() {
        this(new Random());
    }

    public RandomWalkSearch(Random random) {
        this.random = random == null ? new Random() : random;
    }

    @Override
    public Path search(String src, String dst, Map<String, List<String>> adjacency) {
        if (!isSearchable(src, dst, adjacency)) {
            return null;
        }

        List<String> route = new ArrayList<>();
        route.add(src);

        Path currentPath = buildPath(route);
        printVisit(currentPath);

        if (src.equals(dst)) {
            return currentPath;
        }

        String current = src;
        while (true) {
            List<String> unvisitedNeighbors = getUnvisitedNeighbors(current, adjacency, route);
            if (unvisitedNeighbors.isEmpty()) {
                return null;
            }

            String next = unvisitedNeighbors.get(random.nextInt(unvisitedNeighbors.size()));
            route.add(next);
            currentPath = buildPath(route);
            printVisit(currentPath);

            if (next.equals(dst)) {
                return currentPath;
            }

            current = next;
        }
    }
}