# CSE464-GraphManager

**CSE 464: Software QA and Testing — Project Part #1**

A Java Maven project that parses, manipulates, and outputs directed graphs in DOT format.

---

## Requirements

- Java JDK 11+
- Maven 3.6+
- (Optional for native rendering) [Graphviz](https://graphviz.org/download/) installed on PATH

---

## Build & Test

```bash
# Build and run all tests
mvn package

# Run tests only
mvn test

# Run the demo main class (after building)
java -cp target/CSE464-GraphManager-1.0-SNAPSHOT.jar asu.cse464.Main
```

---

## Features

### Feature 1 — Parse DOT Graph

```java
GraphManager gm = new GraphManager();
gm.parseGraph("src/main/resources/input.dot");
System.out.println(gm.toString());
gm.outputGraph("summary.txt");
```

**Output:**
```
Number of Nodes: 8
Node Labels: a, b, c, d, e, f, g, h
Number of Edges: 9
Edges:
  a -> b
  b -> c
  ...
```

---

### Feature 2 — Add Nodes

```java
gm.addNode("z");              // Add single node (returns false if duplicate)
gm.addNodes(new String[]{"m", "n", "o"}); // Add multiple nodes
```

---

### Feature 3 — Add Edges

```java
gm.addEdge("z", "m");  // Adds edge and creates nodes if missing
gm.addEdge("a", "b");  // Returns false — duplicate edge
```

---

### Feature 4 — Output Graph

```java
// Output as DOT file
gm.outputDOTGraph("output.dot");

// Output as PNG image
gm.outputGraphics("output.png", "png");
```

---

## Project Structure

```
CSE464-GraphManager/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/asu/cse464/
│   │   │   ├── GraphManager.java   ← Core graph logic (all 4 features)
│   │   │   └── Main.java           ← Demo entry point
│   │   └── resources/
│   │       └── input.dot           ← Sample DOT input
│   └── test/
│       └── java/asu/cse464/
│           └── GraphManagerTest.java ← JUnit 4 tests
└── README.md
```

---

## API Reference

| Method | Description |
|--------|-------------|
| `parseGraph(String filepath)` | Parse DOT file, populate internal graph |
| `toString()` | Print node/edge summary |
| `outputGraph(String filepath)` | Write summary to text file |
| `addNode(String label)` | Add node; returns false if duplicate |
| `addNodes(String[] labels)` | Add multiple nodes |
| `addEdge(String src, String dst)` | Add directed edge; returns false if duplicate |
| `outputDOTGraph(String path)` | Write graph to .dot file |
| `outputGraphics(String path, String format)` | Write graph to PNG image |

---

## GitHub Commit Strategy

Each feature is committed separately per the project requirements:

1. **Commit 1** — Feature 1: `parseGraph`, `toString`, `outputGraph`
2. **Commit 2** — Feature 2: `addNode`, `addNodes`
3. **Commit 3** — Feature 3: `addEdge`
4. **Commit 4** — Feature 4: `outputDOTGraph`, `outputGraphics`
5. **Commit 5** — Unit tests + Maven setup
