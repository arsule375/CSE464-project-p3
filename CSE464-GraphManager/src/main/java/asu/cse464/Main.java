package asu.cse464;

import java.io.IOException;

/**
 * Main entry point — demonstrates all 4 features of GraphManager.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        GraphManager gm = new GraphManager();

        // Feature 1: Parse
        System.out.println("=== Feature 1: Parsing input.dot ===");
        gm.parseGraph("src/main/resources/input.dot");
        System.out.println(gm);
        gm.outputGraph("output_summary.txt");

        // Feature 2: Add nodes
        System.out.println("=== Feature 2: Adding nodes ===");
        boolean added = gm.addNode("z");
        System.out.println("Added 'z': " + added);
        boolean duplicate = gm.addNode("a");
        System.out.println("Added duplicate 'a': " + duplicate);
        gm.addNodes(new String[]{"m", "n"});
        System.out.println("After adding z, m, n:");
        System.out.println(gm);

        // Feature 3: Add edge
        System.out.println("=== Feature 3: Adding edges ===");
        gm.addEdge("z", "m");
        gm.addEdge("m", "n");
        boolean dupEdge = gm.addEdge("a", "b"); // existing edge
        System.out.println("Added duplicate edge a->b: " + dupEdge);
        System.out.println(gm);

        // Feature 4: Output
        System.out.println("=== Feature 4: Outputting graph ===");
        gm.outputDOTGraph("output.dot");
        System.out.println("DOT file written to output.dot");
        gm.outputGraphics("output.png", "png");
        System.out.println("PNG image written to output.png");
    }
}
