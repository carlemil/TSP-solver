import javax.swing.SwingUtilities;

public class TSPSolver {

    private final static int WINDOW_SIZE = 900;

    private static long rndSeed;

    public static void main(String[] args) {

        final int[] data = TSPTools.readGraphFromCVSFile("graphs/300_locations.csv");

        final int size = data.length / 2;

        final double[][] arcs = Arcs.getArray(data, size, size);

        final int[] path = new int[size];
        final int[] bestPath = new int[size];

        double globalBest = Integer.MAX_VALUE;
        for (int n = 0; n < 1; n++) {
            rndSeed = System.currentTimeMillis();
            rndSeed = 1;
            TSPTools.getRandomizedStartPath(path, rndSeed);

            double last = Integer.MAX_VALUE - 1;
            double best = Integer.MAX_VALUE;
            while (last < best) {
                best = last;
                last = tryRemoveAndInsert(arcs, path);
                double x = removeXarcs(path, arcs);
                last = last > x ? x : last;
            }
            if (globalBest > best) {
                globalBest = best;
                TSPTools.checkPath(arcs, path);
                for (int i = 0; i < path.length; i++) {
                    bestPath[i] = path[i];
                }

                TSPTools.savePathToFile(bestPath, rndSeed,
                        "result_" + size + "_" + TSPTools.getPathLength(arcs, bestPath) + ".csv");
            }
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                TSPTools.createAndShowGUI(TSPTools.getPolygonForPlotting(data, bestPath, WINDOW_SIZE), WINDOW_SIZE,
                        "TSP path, nodes: " + size + ", length: " + TSPTools.getPathLength(arcs, bestPath));
            }
        });
    }

    private static double tryRemoveAndInsert(double[][] arcs, int[] path) {
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
                    if (n < i) {
                        int v = path[n];
                        for (int j = n; j < i; j++) {
                            path[j] = path[j + 1];
                        }
                        path[i] = v;
                    } else if (n > i) {
                        int v = path[n];
                        for (int j = n; j > i + 1; j--) {
                            path[j] = path[j - 1];
                        }
                        path[i + 1] = v;
                    }
                }
            }
        }
        return TSPTools.getPathLength(arcs, path);
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
                    TSPTools.reversSubSectionOfArray(path, d, b);
                }
            }
        }
        return TSPTools.getPathLength(arcs, path);
    }
}
