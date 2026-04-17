package asu.cse464;

import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for GraphManager covering all 4 features.
 */
public class GraphManagerTest {

    private GraphManager gm;
    private String inputDotPath;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void setUp() throws IOException {
        gm = new GraphManager();
        // Write a temp input.dot for tests
        File dotFile = tempFolder.newFile("input.dot");
        Files.writeString(dotFile.toPath(),
            "digraph G {\n" +
            "    a -> b;\n" +
            "    b -> c;\n" +
            "    c -> d;\n" +
            "    d -> a;\n" +
            "    a -> e;\n" +
            "    e -> f;\n" +
            "    e -> g;\n" +
            "    f -> h;\n" +
            "    g -> h;\n" +
            "}\n"
        );
        inputDotPath = dotFile.getAbsolutePath();
    }

    // =========================================================================
    // Feature 1: parseGraph
    // =========================================================================

    @Test
    public void testParseGraph_NodeCount() throws IOException {
        gm.parseGraph(inputDotPath);
        assertEquals("Should have 8 nodes", 8, gm.getNodeCount());
    }

    @Test
    public void testParseGraph_EdgeCount() throws IOException {
        gm.parseGraph(inputDotPath);
        assertEquals("Should have 9 edges", 9, gm.getEdgeCount());
    }

    @Test
    public void testParseGraph_ContainsExpectedNodes() throws IOException {
        gm.parseGraph(inputDotPath);
        assertTrue(gm.getNodes().contains("a"));
        assertTrue(gm.getNodes().contains("h"));
    }

    @Test
    public void testParseGraph_ContainsExpectedEdge() throws IOException {
        gm.parseGraph(inputDotPath);
        assertTrue(gm.getEdges().contains("a->b"));
        assertTrue(gm.getEdges().contains("g->h"));
    }

    @Test
    public void testToString_ContainsNodeCount() throws IOException {
        gm.parseGraph(inputDotPath);
        String output = gm.toString();
        assertTrue(output.contains("Number of Nodes: 8"));
        assertTrue(output.contains("Number of Edges: 9"));
    }

    @Test
    public void testOutputGraph_WritesFile() throws IOException {
        gm.parseGraph(inputDotPath);
        File outFile = tempFolder.newFile("summary.txt");
        gm.outputGraph(outFile.getAbsolutePath());
        String content = Files.readString(outFile.toPath());
        assertTrue(content.contains("Number of Nodes: 8"));
        assertTrue(content.contains("a -> b"));
    }

    // =========================================================================
    // Feature 2: addNode / addNodes
    // =========================================================================

    @Test
    public void testAddNode_Success() throws IOException {
        gm.parseGraph(inputDotPath);
        boolean result = gm.addNode("z");
        assertTrue("Adding new node should return true", result);
        assertTrue(gm.getNodes().contains("z"));
        assertEquals(9, gm.getNodeCount());
    }

    @Test
    public void testAddNode_Duplicate() throws IOException {
        gm.parseGraph(inputDotPath);
        boolean result = gm.addNode("a"); // already exists
        assertFalse("Duplicate node should return false", result);
        assertEquals("Node count should remain 8", 8, gm.getNodeCount());
    }

    @Test
    public void testAddNodes_MultipleNew() throws IOException {
        gm.parseGraph(inputDotPath);
        gm.addNodes(new String[]{"x", "y", "z"});
        assertEquals(11, gm.getNodeCount());
        assertTrue(gm.getNodes().contains("x"));
        assertTrue(gm.getNodes().contains("y"));
        assertTrue(gm.getNodes().contains("z"));
    }

    @Test
    public void testAddNodes_MixedDuplicates() throws IOException {
        gm.parseGraph(inputDotPath);
        gm.addNodes(new String[]{"a", "newNode"}); // "a" is duplicate
        assertEquals(9, gm.getNodeCount()); // only 1 new node added
    }

    @Test
    public void testRemoveNode_SuccessRemovesIncidentEdges() throws IOException {
        gm.parseGraph(inputDotPath);
        boolean result = gm.removeNode("a");

        assertTrue("Removing existing node should return true", result);
        assertFalse(gm.getNodes().contains("a"));
        assertEquals("Node count should decrease by 1", 7, gm.getNodeCount());
        assertEquals("All incident edges should be removed", 6, gm.getEdgeCount());
        assertFalse(gm.getEdges().contains("a->b"));
        assertFalse(gm.getEdges().contains("a->e"));
        assertFalse(gm.getEdges().contains("d->a"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNode_NotFound() throws IOException {
        gm.parseGraph(inputDotPath);
        gm.removeNode("missing");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNodes_MixedExistingAndMissing() throws IOException {
        gm.parseGraph(inputDotPath);
        gm.removeNodes(new String[]{"a", "h", "missing"});
    }

    @Test
    public void testRemoveNodes_AllExisting() throws IOException {
        gm.parseGraph(inputDotPath);
        gm.removeNodes(new String[]{"a", "h"});

        assertFalse(gm.getNodes().contains("a"));
        assertFalse(gm.getNodes().contains("h"));
        assertEquals(6, gm.getNodeCount());
        assertEquals(4, gm.getEdgeCount());
    }

    // =========================================================================
    // Feature 3: addEdge
    // =========================================================================

    @Test
    public void testAddEdge_Success() throws IOException {
        gm.parseGraph(inputDotPath);
        boolean result = gm.addEdge("h", "a");
        assertTrue("Adding new edge should return true", result);
        assertTrue(gm.getEdges().contains("h->a"));
        assertEquals(10, gm.getEdgeCount());
    }

    @Test
    public void testAddEdge_Duplicate() throws IOException {
        gm.parseGraph(inputDotPath);
        boolean result = gm.addEdge("a", "b"); // already exists
        assertFalse("Duplicate edge should return false", result);
        assertEquals("Edge count should remain 9", 9, gm.getEdgeCount());
    }

    @Test
    public void testAddEdge_CreatesNewNodes() throws IOException {
        gm.parseGraph(inputDotPath);
        gm.addEdge("x", "y"); // new nodes x, y
        assertTrue(gm.getNodes().contains("x"));
        assertTrue(gm.getNodes().contains("y"));
        assertEquals(10, gm.getNodeCount());
    }

    @Test
    public void testRemoveEdge_Success() throws IOException {
        gm.parseGraph(inputDotPath);
        boolean result = gm.removeEdge("a", "b");

        assertTrue("Removing existing edge should return true", result);
        assertFalse(gm.getEdges().contains("a->b"));
        assertEquals(8, gm.getEdgeCount());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveEdge_NotFound() throws IOException {
        gm.parseGraph(inputDotPath);
        gm.removeEdge("h", "a");
    }

    @Test
    public void testGraphSearch_FindsPathWithBFS() throws IOException {
        gm.parseGraph(inputDotPath);

        Path path = gm.GraphSearch(new Node("a"), new Node("h"));
        assertNotNull("Path should exist from a to h", path);

        List<Node> route = path.getNodes();
        assertEquals("BFS path from a to h should use 3 edges", 4, route.size());
        assertEquals("a", route.get(0).getLabel());
        assertEquals("h", route.get(route.size() - 1).getLabel());

        for (int i = 0; i < route.size() - 1; i++) {
            String edge = route.get(i).getLabel() + "->" + route.get(i + 1).getLabel();
            assertTrue("Each step in returned path must be an edge in the graph", gm.getEdges().contains(edge));
        }
    }

    @Test
    public void testGraphSearch_NoPathReturnsNull() throws IOException {
        gm.parseGraph(inputDotPath);

        Path path = gm.GraphSearch(new Node("h"), new Node("a"));
        assertNull("No path should exist from h to a", path);
    }

    @Test
    public void testGraphSearch_MissingNodeReturnsNull() throws IOException {
        gm.parseGraph(inputDotPath);

        Path path = gm.GraphSearch(new Node("a"), new Node("missing"));
        assertNull("Search should return null when destination node is missing", path);
    }

    // =========================================================================
    // Feature 5: GraphSearch (DFS)
    // =========================================================================

    @Test
    public void testGraphSearch_DFSUsesDepthFirstOrder() {
        GraphManager searchGraph = new GraphManager();
        searchGraph.addEdge("s", "b");
        searchGraph.addEdge("s", "a");
        searchGraph.addEdge("a", "t");
        searchGraph.addEdge("b", "c");
        searchGraph.addEdge("c", "t");

        Path path = searchGraph.GraphSearch(new Node("s"), new Node("t"), GraphManager.Algorithm.DFS);

        assertNotNull("Path should be found", path);
        List<Node> route = path.getNodes();
        assertEquals(4, route.size());
        assertEquals("s", route.get(0).getLabel());
        assertEquals("b", route.get(1).getLabel());
        assertEquals("c", route.get(2).getLabel());
        assertEquals("t", route.get(3).getLabel());
    }

    @Test
    public void testGraphSearch_DFSNoPathReturnsNull() {
        GraphManager searchGraph = new GraphManager();
        searchGraph.addEdge("a", "b");
        searchGraph.addNode("z");

        Path path = searchGraph.GraphSearch(new Node("a"), new Node("z"), GraphManager.Algorithm.DFS);
        assertNull("No path should return null", path);
    }

    @Test
    public void testGraphSearch_DFSMissingNodeReturnsNull() throws IOException {
        gm.parseGraph(inputDotPath);

        Path path = gm.GraphSearch(new Node("missing"), new Node("a"), GraphManager.Algorithm.DFS);
        assertNull("Missing source node should return null", path);
    }

    // =========================================================================
    // Feature 4: outputDOTGraph / outputGraphics
    // =========================================================================

    @Test
    public void testOutputDOTGraph_IsValidDOT() throws IOException {
        gm.parseGraph(inputDotPath);
        File outDot = tempFolder.newFile("output.dot");
        gm.outputDOTGraph(outDot.getAbsolutePath());
        String content = Files.readString(outDot.toPath());
        assertTrue(content.startsWith("digraph"));
        assertTrue(content.contains("a -> b;"));
        assertTrue(content.contains("g -> h;"));
    }

    @Test
    public void testOutputDOTGraph_ContainsAllNodes() throws IOException {
        gm.parseGraph(inputDotPath);
        File outDot = tempFolder.newFile("output.dot");
        gm.outputDOTGraph(outDot.getAbsolutePath());
        String content = Files.readString(outDot.toPath());
        for (String node : gm.getNodes()) {
            assertTrue("DOT output should contain node: " + node, content.contains(node + ";"));
        }
    }

    @Test
    public void testOutputDOTGraph_RoundTrip() throws IOException {
        gm.parseGraph(inputDotPath);
        File outDot = tempFolder.newFile("roundtrip.dot");
        gm.outputDOTGraph(outDot.getAbsolutePath());

        // Re-parse the output
        GraphManager gm2 = new GraphManager();
        gm2.parseGraph(outDot.getAbsolutePath());
        assertEquals("Round-trip node count should match", gm.getNodeCount(), gm2.getNodeCount());
        assertEquals("Round-trip edge count should match", gm.getEdgeCount(), gm2.getEdgeCount());
    }

    @Test
    public void testOutputGraphics_PNGCreated() throws IOException {
        gm.parseGraph(inputDotPath);
        File outPng = new File(tempFolder.getRoot(), "output.png");
        gm.outputGraphics(outPng.getAbsolutePath(), "png");
        assertTrue("PNG file should be created", outPng.exists());
        assertTrue("PNG file should not be empty", outPng.length() > 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOutputGraphics_UnsupportedFormat() throws IOException {
        gm.parseGraph(inputDotPath);
        File outFile = new File(tempFolder.getRoot(), "output.jpg");
        gm.outputGraphics(outFile.getAbsolutePath(), "jpg");
    }
}
