/**
 * Missions 4 and 5. The slides: a directed graph. Cells are 0 .. n-1, slide i goes from s.from()[i] to s.to()[i]
 * and takes s.time()[i]. There are no slides from a cell to itself, and the exit can always be reached from the start.
 *
 * Ready for you: Graphs.slidesFrom(s)[u] is the list of the slides that leave cell u, and Graphs.slidesInto(s)[v] is the list
 * of the slides that enter cell v (reversed graph). The lists hold slide numbers i; the cell at the other end is s.to()[i]
 * or s.from()[i].
 */
public class Zones {

    /**
     * Mission 4. Kosaraju. Returns zoneOf: zoneOf[cell] = number of the zone, numbers 0 .. zones-1.
     * Number the zones in the order in which the second pass finds them. That order is a topological order
     * of the zone graph: every slide between two zones goes from a smaller number to a larger number.
     *
     * Plan: 1) DFS on the slides, write the cells in the order in which they are finished (use a stack, not recursion);
     * 2) go through the cells from the last finished to the first; if a cell has no zone yet, DFS from it on the reversed
     * slides (slidesInto) and give every cell it reaches the next zone number.
     * Is DAG in Main is 1 when the number of zones equals the number of cells (no cycle), so you do not need a separate check.
     */
    public static int[] kosaraju(Input.Slides s) {
        int n = s.n();
        int[][] out = Graphs.slidesFrom(s);
        int[][] in = Graphs.slidesInto(s);

        // Pass 1: DFS on the slides, write down the cells in the order in which they are finished.
        // Own stack instead of recursion: next[u] is how many slides of u are already tried.
        boolean[] visited = new boolean[n];
        int[] next = new int[n];
        int[] stack = new int[n];
        int[] finished = new int[n];
        int done = 0;
        for (int start = 0; start < n; start++) {
            if (visited[start]) continue;
            int top = 0;
            stack[0] = start;
            visited[start] = true;
            while (top >= 0) {
                int u = stack[top];
                if (next[u] < out[u].length) {
                    int v = s.to()[out[u][next[u]++]];
                    if (!visited[v]) { visited[v] = true; stack[++top] = v; }
                } else {
                    finished[done++] = u;   // all slides of u are tried: u is finished
                    top--;
                }
            }
        }

        // Pass 2: from the last finished cell to the first, DFS on the reversed slides.
        // Every cell reached that has no zone yet gets the current zone number.
        int[] zoneOf = new int[n];
        java.util.Arrays.fill(zoneOf, -1);
        int zones = 0;
        for (int i = n - 1; i >= 0; i--) {
            int root = finished[i];
            if (zoneOf[root] >= 0) continue;
            int top = 0;
            stack[0] = root;
            zoneOf[root] = zones;
            while (top >= 0) {
                int v = stack[top--];
                for (int k : in[v]) {
                    int u = s.from()[k];
                    if (zoneOf[u] < 0) { zoneOf[u] = zones; stack[++top] = u; }
                }
            }
            zones++;
        }
        return zoneOf;
    }

    /**
     * Mission 5. The graph of zones: the edge X to Y costs the cheapest slide from X to Y, walking inside a zone is free.
     * Go through the zones in the order of their numbers (no priority queue). Returns two numbers:
     * [0] flow stages: the number of zones on the longest chain of zones;
     * [1] the cost of the cheapest route from the zone of cell 0 to the zone of the last cell.
     */
    public static long[] routes(Input.Slides s, int[] zoneOf, int zoneCount) {
        // TODO
        return new long[]{0, 0};
    }
}
