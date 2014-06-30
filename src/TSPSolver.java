import java.util.ArrayList;
import javax.swing.SwingUtilities;

public class TSPSolver {

    private final static int WINDOW_SIZE = 500;

    private static long rndSeed;

    public static void main(String[] args) {

        final int[] data = Tools.readGraphFromCVSFile("graphs/30_locations.csv");

        final int size = data.length / 2;

        final double[][] arcs = Arcs.getArray(data, size, size);

        final int[] path = new int[] {
                24, 5, 17, 4, 12, 27, 26, 6, 23, 9, 16, 0, 18, 2, 8, 20, 7, 21, 28, 14, 19, 11, 10, 25, 3, 15, 13, 29, 22, 1,
        // 24, 5, 17, 4, 12, 27, 26, 6, 23, 9, 16, 0, 18, 2, 8, 20, 7, 21, 28,
        // 14, 11, 10, 19, 25, 3, 15, 13, 29, 22, 1,
        // 24, 5, 17, 4, 12, 27, 26, 6, 23, 9, 16, 0, 18, 2, 8, 20, 7, 21, 28,
        // 14, 25, 3, 15, 19, 11, 10, 13, 29, 22, 1,
        };
        // final int[] path = new int[size];
        final int[] bestPath = new int[size];

        double globalBest = Integer.MAX_VALUE;
        for (int n = 0; n < 1; n++) {
            rndSeed = System.currentTimeMillis() + n;
            rndSeed = 9;
            // Tools.getRandomizedStartPath(path, rndSeed);

            double bestN3 = n3optimizations(arcs, path);

            if (globalBest > bestN3) {
                globalBest = bestN3;
                Tools.checkPath(arcs, path);
                for (int i = 0; i < path.length; i++) {
                    bestPath[i] = path[i];
                }

                // Tools.savePathToFile(bestPath, rndSeed, "result_" + size +
                // "_" + Tools.getPathLength(arcs, bestPath) + ".csv");
            }
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Tools.createAndShowGUI(Tools.getPolygonForPlotting(data, bestPath, WINDOW_SIZE), path, WINDOW_SIZE,
                        "TSP path, nodes: " + size + ", length: " + Tools.getPathLength(arcs, bestPath));
            }
        });
    }

    private static double n3optimizations(final double[][] arcs, final int[] path) {
        double bestN2 = 500;// n2optimizations(arcs, path);
        Tools.checkPath(arcs, path);
        lookForClustersToMove(arcs, path, 3);
        return bestN2;
    }

    private static void lookForClustersToMove(final double[][] arcs, final int[] path, final int clusterLength) {
        ArrayList<Integer> apath = new ArrayList<Integer>();
        for (int n : path) {
            apath.add(n);
        }
        for (int i = 0; i < path.length - clusterLength; i++) {
            checkCluster(arcs, apath, i, i + clusterLength);
        }

        int i = 0;
        for (Integer v : apath) {
            path[i++] = v.intValue();
        }
    }

    private static void checkCluster(final double[][] arcs, final ArrayList<Integer> path, int clusterStart, int clusterEnd) {

        // kan nog skippa många på rad beroende på hur många gånger större new
        // är än old, dvs hur långt bort vi råkar vara

        // [clusterStart - 1][clusterStart] + [clusterEnd][clusterEnd + 1]
        int oldCost = 0;

        // [i][clusterStart] + [clusterEnd][i+1]
        // [i+1][clusterStart] + [clusterEnd][i] (reverse is needed)
        int newCost = 1;
        // steps = i;
        int steps = 0;


        if (clusterStart == 20) {
            Tools.rotateCluster(path, clusterStart, clusterEnd, steps);

            int move = 3;
            Tools.moveCluster(path, clusterStart, clusterEnd, move);

            System.out.println("cluster start: " + clusterStart + ", cluster end: " + clusterEnd + ", cluster move: " + move);
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
                    Tools.moveNodeFromAToB(path, n, i);
                }
            }
        }
        return Tools.getPathLength(arcs, path);
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
