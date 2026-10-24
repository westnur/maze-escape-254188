import java.util.Arrays;

/**
 * Mission 3. Escape: DFS from cell 0 to the last cell (rows * cols - 1) in the maze of Mission 1.
 * Two neighbouring cells are connected when the wall between them is broken.
 * The neighbours are tried in this order: up, right, down, left (cell - cols, cell + 1, cell + cols, cell - 1).
 * The ready method Graphs.open(w, broken) gives you the open neighbours of every cell, in no particular order:
 * to visit them in the required order, go through the four cells above one by one and take those that are in the list.
 * The program must not overflow the stack on a maze of 1000 by 1000 cells.
 */
public class Escape {

    /**
     * path: the cells of the way out, from 0 to the last cell.
     * visited: how many cells are marked as visited at the moment the exit is marked (the start and the exit are counted).
     * deadEnds: how many cells of the maze have exactly one open neighbour.
     */
    public record Result(int[] path, int visited, int deadEnds) {}

    public static Result escape(Input.Walls w, int[] broken) {
        int n = w.cells(), cols = w.cols();
        int exit = n - 1;
        int[][] open = Graphs.open(w, broken);

        // dead ends: cells with exactly one open door
        int deadEnds = 0;
        for (int c = 0; c < n; c++) if (open[c].length == 1) deadEnds++;

        // DFS with our own stack instead of recursion, so a 1000 x 1000 maze cannot overflow the call stack.
        // stack[0 .. top] is the current way from the start; dir[cell] is the next direction to try from the cell
        // (0 up, 1 right, 2 down, 3 left), this is what a recursive call would remember in its local variables.
        boolean[] visited = new boolean[n];
        int[] dir = new int[n];
        int[] stack = new int[n];
        int top = 0;
        stack[0] = 0;
        visited[0] = true;
        int marked = 1;

        while (top >= 0 && stack[top] != exit) {
            int cell = stack[top];
            int next = -1;
            while (dir[cell] < 4 && next < 0) {
                int t = neighbour(cell, dir[cell], w.rows(), cols);
                dir[cell]++;
                if (t >= 0 && !visited[t] && isOpen(open[cell], t)) next = t;
            }
            if (next < 0) {
                top--;                  // every direction tried: go back
            } else {
                visited[next] = true;   // mark the cell when DFS enters it
                marked++;
                stack[++top] = next;
            }
        }

        if (top < 0) return new Result(new int[0], marked, deadEnds);   // the exit cannot be reached
        // the stack now holds the way from the start to the exit
        return new Result(Arrays.copyOf(stack, top + 1), marked, deadEnds);
    }

    /** The neighbour of the cell in direction d (0 up, 1 right, 2 down, 3 left), or -1 outside the maze. */
    private static int neighbour(int cell, int d, int rows, int cols) {
        int r = cell / cols, c = cell % cols;
        switch (d) {
            case 0: return r > 0 ? cell - cols : -1;
            case 1: return c + 1 < cols ? cell + 1 : -1;
            case 2: return r + 1 < rows ? cell + cols : -1;
            default: return c > 0 ? cell - 1 : -1;
        }
    }

    /** True if t is in the list of open neighbours (the list has at most 4 cells). */
    private static boolean isOpen(int[] list, int t) {
        for (int x : list) if (x == t) return true;
        return false;
    }
}