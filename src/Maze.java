import java.util.*;

/**
 * Missions 1 and 2. Building the maze out of the walls.
 * A wall k joins the cells w.a()[k] and w.b()[k] and costs w.cost()[k]. Indexes start from 0,
 * the line number in walls.txt is the index + 1. The cells are connected by DisjointSet.
 */
public class Maze {

    /**
     * Mission 1. Take the walls in the order of the file. Break a wall when its two cells are in different sets.
     * Returns the indexes of the broken walls, in the order of breaking.
     */
    public static int[] breakWalls(Input.Walls w) {
        DisjointSet dsu = new DisjointSet(w.cells());
        int[] broken = new int[Math.max(0, w.cells() - 1)];
        int count = 0;
        for (int k = 0; k < w.count(); k++) {
            if (dsu.union(w.a()[k], w.b()[k])) broken[count++] = k;
        }
        return Arrays.copyOf(broken, count);
    }

    /**
     * Mission 2. Kruskal: the same, but take the walls from the cheapest to the most expensive.
     * Returns the indexes of the broken walls.
     */
    public static int[] cheapestMaze(Input.Walls w) {
        int m = w.count();
        // sort the walls by cost: one long per wall, cost in the high 32 bits, index in the low 32 bits,
        // so sorting the longs sorts the walls by cost (all costs are different) and we still know the index
        long[] order = new long[m];
        for (int k = 0; k < m; k++) order[k] = ((long) w.cost()[k] << 32) | k;
        Arrays.sort(order);

        DisjointSet dsu = new DisjointSet(w.cells());
        int[] broken = new int[Math.max(0, w.cells() - 1)];
        int count = 0;
        for (int i = 0; i < m && count < broken.length; i++) {
            int k = (int) order[i];   // low 32 bits: the index of the wall
            if (dsu.union(w.a()[k], w.b()[k])) broken[count++] = k;
        }
        return Arrays.copyOf(broken, count);
    }

    /**
     * Mission 2. Prim with a priority queue. Returns the total cost of the cheapest maze.
     * It must be equal to the total cost of cheapestMaze.
     */
    public static long primCost(Input.Walls w) {
        int n = w.cells(), m = w.count();
        if (n <= 1) return 0;

        // adjacency lists: for every cell the walls that touch it
        int[] degree = new int[n];
        for (int k = 0; k < m; k++) { degree[w.a()[k]]++; degree[w.b()[k]]++; }
        int[][] adj = new int[n][];
        for (int i = 0; i < n; i++) adj[i] = new int[degree[i]];
        int[] filled = new int[n];
        for (int k = 0; k < m; k++) {
            adj[w.a()[k]][filled[w.a()[k]]++] = k;
            adj[w.b()[k]][filled[w.b()[k]]++] = k;
        }

        // lazy Prim: the queue holds walls on the border of the tree as (cost << 32 | cell outside the tree)
        boolean[] inTree = new boolean[n];
        PriorityQueue<Long> pq = new PriorityQueue<>();
        long total = 0;
        int added = 0;
        int start = 0;
        inTree[start] = true;
        added++;
        for (int k : adj[start]) pq.add(((long) w.cost()[k] << 32) | other(w, k, start));

        while (!pq.isEmpty() && added < n) {
            long top = pq.poll();
            int cell = (int) top;               // low 32 bits: the cell
            if (inTree[cell]) continue;         // old entry: the cell was already taken by a cheaper wall
            inTree[cell] = true;
            added++;
            total += top >>> 32;                // high 32 bits: the cost
            for (int k : adj[cell]) {
                int next = other(w, k, cell);
                if (!inTree[next]) pq.add(((long) w.cost()[k] << 32) | next);
            }
        }
        return total;
    }

    /** The cell on the other side of wall k from the cell 'from'. */
    private static int other(Input.Walls w, int k, int from) {
        return w.a()[k] == from ? w.b()[k] : w.a()[k];
    }
}
