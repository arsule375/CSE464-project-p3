package asu.cse464;

import java.util.List;
import java.util.Map;

public interface SearchStrategy {
    Path search(String src, String dst, Map<String, List<String>> adjacency);
}