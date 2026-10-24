/**
 * Checks of Mission 0. Run:  java -cp out DisjointSetTest
 * Add your own checks below: a large chain, a union of a set with itself, count after many unions.
 */
public class DisjointSetTest {

    static int failed = 0;

    static void check(boolean ok, String what) {
        System.out.println((ok ? "ok    " : "FAIL  ") + what);
        if (!ok) failed++;
    }

    public static void main(String[] args) {
        DisjointSet d = new DisjointSet(6);
        check(d.count() == 6, "6 sets at the start");
        check(!d.connected(0, 1), "0 and 1 are apart at the start");
        check(d.union(0, 1), "union(0, 1) joins two sets");
        check(d.connected(0, 1), "0 and 1 are together");
        check(!d.union(1, 0), "union(1, 0) again returns false");
        check(d.count() == 5, "5 sets after one union");
        d.union(2, 3); d.union(1, 3);
        check(d.connected(0, 2), "0 and 2 are together through 1 and 3");
        check(d.count() == 3, "3 sets: {0,1,2,3} {4} {5}");
        check(d.find(4) == 4 && d.find(5) == 5, "a single cell is its own root");

        // my own checks
        check(!d.union(4, 4), "union of a set with itself returns false");
        check(d.count() == 3, "count does not change after union(4, 4)");
        check(d.connected(3, 3), "a cell is connected to itself");
        check(!d.connected(0, 5), "0 and 5 are still apart");
        d.union(4, 5);
        check(d.connected(4, 5) && d.count() == 2, "2 sets after union(4, 5)");
        d.union(5, 0);
        check(d.count() == 1 && d.connected(4, 2), "everything in one set");
        check(d.find(0) == d.find(5), "one root for the whole set");

        // a large chain: 1 000 000 cells joined one after another, find must not overflow the stack
        int n = 1_000_000;
        DisjointSet big = new DisjointSet(n);
        boolean allJoined = true;
        for (int i = 1; i < n; i++) allJoined &= big.union(i - 1, i);
        check(allJoined, "every union in the chain joins two different sets");
        check(big.count() == 1, "count is 1 after n - 1 unions");
        check(big.connected(0, n - 1), "the ends of the chain are connected");
        check(!big.union(0, n - 1), "union of the two ends returns false");

        // count after many unions: join the pairs (2i, 2i+1), then n/2 sets stay
        DisjointSet pairs = new DisjointSet(1000);
        for (int i = 0; i < 1000; i += 2) pairs.union(i, i + 1);
        check(pairs.count() == 500, "500 sets after joining 500 pairs");
        check(!pairs.connected(1, 2), "different pairs stay apart");

        System.out.println(failed == 0 ? "All checks passed." : failed + " checks failed.");
    }
}