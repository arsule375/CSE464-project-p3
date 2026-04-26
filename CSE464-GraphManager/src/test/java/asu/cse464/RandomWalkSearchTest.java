package asu.cse464;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class RandomWalkSearchTest {

    @Test
    public void testRandomWalkSearch_FindsPathAndPrintsVisits() {
        Map<String, List<String>> adjacency = new LinkedHashMap<>();
        adjacency.put("a", Arrays.asList("b"));
        adjacency.put("b", Arrays.asList("c"));
        adjacency.put("c", Collections.emptyList());

        RandomWalkSearch search = new RandomWalkSearch(new Random(0));

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(output));
        try {
            Path path = search.search("a", "c", adjacency);

            assertNotNull(path);
            assertEquals(3, path.getNodes().size());
            assertEquals("a", path.getNodes().get(0).getLabel());
            assertEquals("b", path.getNodes().get(1).getLabel());
            assertEquals("c", path.getNodes().get(2).getLabel());
        } finally {
            System.setOut(originalOut);
        }

        String printed = output.toString();
        assertTrue(printed.contains("visiting Path{nodes=[Node{a}]}"));
        assertTrue(printed.contains("visiting Path{nodes=[Node{a}, Node{b}]}"));
        assertTrue(printed.contains("visiting Path{nodes=[Node{a}, Node{b}, Node{c}]}"));
    }

    @Test
    public void testRandomWalkSearch_DeadEndReturnsNull() {
        Map<String, List<String>> adjacency = new LinkedHashMap<>();
        adjacency.put("a", Arrays.asList("b"));
        adjacency.put("b", Collections.emptyList());
        adjacency.put("c", Collections.emptyList());

        RandomWalkSearch search = new RandomWalkSearch(new Random(0));

        Path path = search.search("a", "c", adjacency);
        assertNull(path);
    }

    @Test
    public void testRandomWalkSearch_InvalidInputsReturnNull() {
        RandomWalkSearch search = new RandomWalkSearch(new Random(0));

        assertNull(search.search(null, "c", Collections.emptyMap()));
        assertNull(search.search("a", "c", null));
        assertNull(search.search("a", "c", Collections.singletonMap("a", Collections.emptyList())));
    }
}