import javax.swing.SwingUtilities;

public class TSPSolver {

    private final static int WINDOW_SIZE = 900;

    private static long rndSeed;

    public static void main(String[] args) {

        final int[] data = Tools.readGraphFromCVSFile("graphs/300_locations.csv");

        final int size = data.length / 2;

        final double[][] arcs = Arcs.getArray(data, size, size);

        final int[] path = new int[size];
        final int[] bestPath = new int[size];

        double globalBest = Integer.MAX_VALUE;
        for (int n = 0; n < 1; n++) {
            rndSeed = System.currentTimeMillis() + n;
            rndSeed = 7;
            Tools.getRandomizedStartPath(path, rndSeed);

            double bestN3 = n3optimizations(arcs, path);

            if (globalBest > bestN3) {
                globalBest = bestN3;
                Tools.checkPath(arcs, path);
                for (int i = 0; i < path.length; i++) {
                    bestPath[i] = path[i];
                }

                Tools.savePathToFile(bestPath, rndSeed, "result_" + size + "_" + Tools.getPathLength(arcs, bestPath) + ".csv");
            }
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Tools.createAndShowGUI(Tools.getPolygonForPlotting(data, bestPath, WINDOW_SIZE), WINDOW_SIZE, "TSP path, nodes: "
                        + size + ", length: " + Tools.getPathLength(arcs, bestPath));
            }
        });
    }

    private static double n3optimizations(final double[][] arcs, final int[] path) {
        double bestN2 = n2optimizations(arcs, path);
        // moveClusters(arcs, path);
        return bestN2;
    }

    private static void moveClusters(final double[][] arcs, final int[] path) {
        int pl = path.length;

        // cluster length
        int cl = 2;
        int[] c = new int[cl];

        // cluster starts at node n
        for (int n = 0; n < pl - cl; n++) {

            // copy cluster to its own array
            for (int cc = 0; cc < cl; cc++) {
                c[cc] = path[n + cc];
            }

            // loop over locations to insert path
            for (int i = 0; i < pl; i++) {
                // loop over cluster and try to insert using different start/end nodes

            }
        }
    }

    private static double n2optimizations(final double[][] arcs, final int[] path) {
        double last = Integer.MAX_VALUE - 1;
        double best = Integer.MAX_VALUE;
        while (last < best) {
            best = last;
            last = tryRemoveAndInsertOneNode(arcs, path);
            double x = removeXarcs(path, arcs);
            last = last > x ? x : last;
        }
        return best;
    }

    private static double tryRemoveAndInsertOneNode(double[][] arcs, int[] path) {
        int l = path.length;

        for (int n = 0; n < l; n++) {
            for (int i = 0; i < l; i++) {
                int pna = path[(n - 1 + l) % l];
                int pnb = path[n];
                int pnc = path[(n + 1) % l];
                int pib = path[i];
                int pic = path[(i + 1) % l];
                double distOrg = arcs[pna][pnb] + arcs[pnb][pnc] + arcs[pib][pic];
                double distNew = arcs[pnb][pib] + arcs[pnb][pic] + arcs[pna][pnc];
                if (distOrg > distNew) {
                    moveNodeFromAToB(path, n, i);
                }
            }
        }
        return Tools.getPathLength(arcs, path);
    }

    private static void moveNodeFromAToB(int[] path, int a, int b) {
        if (a < b) {
            int node = path[a];
            for (int j = a; j < b; j++) {
                path[j] = path[j + 1];
            }
            path[b] = node;
        } else if (a > b) {
            int node = path[a];
            for (int j = a; j > b + 1; j--) {
                path[j] = path[j - 1];
            }
            path[b + 1] = node;

        }
    }

    private static double removeXarcs(int[] path, double[][] arcs) {
        int l = path.length;
        for (int i = 0; i < l; i++) {
            for (int j = i + 2; j < l; j++) {
                int a = i;
                int d = (i + 1) % l;
                int b = j;
                int c = (j + 1) % l;
                double a1 = arcs[path[a]][path[d]] + arcs[path[b]][path[c]];
                double a2 = arcs[path[a]][path[b]] + arcs[path[d]][path[c]];
                if (a1 > a2) {
                    Tools.reverseSubSectionOfArray(path, d, b);
                }
            }
        }
        return Tools.getPathLength(arcs, path);
    }
}
