import java.util.ArrayList;
import java.util.List;

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
        // loop over path, keep cluster on index 0 to index clusterLength
        for (int i = 0; i < path.length; i++) {
            // Tools.printPath(apath);

            if (checkCluster(arcs, apath, clusterLength)) {
                break;
            }
            Tools.rotatePathOneStepLeft(apath);
            System.out.println("--- i: " + i);

        }

        int i = 0;
        for (Integer v : apath) {
            path[i++] = v.intValue();
        }
        System.out.println("oarcs: " + arcs[14][19] + ", " + arcs[25][10] + ", " + arcs[13][15] + "   sum "
                + (arcs[14][19] + arcs[25][10] + arcs[13][15]));
        System.out.println("narcs: " + arcs[14][25] + ", " + arcs[15][19] + ", " + arcs[13][10] + "   sum "
                + (arcs[13][10] + arcs[15][19] + arcs[14][25]));
    }

    private static boolean checkCluster(final double[][] arcs, final ArrayList<Integer> path, int cl) {
        int ps = path.size();

        double bestCost = Integer.MAX_VALUE;
        int bestI = -1;
        int bestJ = -1;
        boolean rotateCluster = false;

        // i loopar över path och kollar var vi ska stoppa in klustret,
        // i får inte vara inom kluster Start/End, därav i = clusterLength + 1
        for (int i = cl + 1; i < ps - 1 - cl; i++) {

            double oldCost = arcs[path.get(ps - 1)][path.get(0)] + // -1 -- 0
                    arcs[path.get(cl)][path.get(cl - 1)] + // cl-1 -- cl
                    arcs[path.get(i)][path.get(i + 1)]; // i -- i+1
            if (oldCost < bestCost) {
                bestCost = oldCost;
            }

            // j loopar över kluster och kollar vilken båge i klustret som ska
            // brytas för insättning
            for (int j = 0; j < cl; j++) {
                double newCost = arcs[path.get(ps - 1)][path.get(cl)] + //
                        arcs[path.get(j)][path.get(i)] + //
                        arcs[path.get((j + 1) % cl)][path.get(i + 1)];
                double newCostRotated = arcs[path.get(ps - 1)][path.get(cl)] + //
                        arcs[path.get(j)][path.get(i + 1)] + //
                        arcs[path.get((j + 1) % cl)][path.get(i)];
                if (newCost < bestCost) {
                    bestCost = newCost;
                    bestI = i;
                    bestJ = j;
                    rotateCluster = false;
                } else if (newCostRotated < bestCost) {
                    bestCost = newCostRotated;
                    bestI = i;
                    bestJ = j;
                    rotateCluster = true;
                }
            }
        }
        if (bestI != -1 && bestJ != -1) {
            System.out.println("i: " + path.get(bestI) + " i+1: " + path.get(bestI + 1) + //
                    " j: " + path.get(bestJ) + " j+1: " + path.get((bestJ + 1) % cl) + //
                    " cost " + bestCost);
            System.out.println("bestI: " + bestI + " bestJ: " + bestJ);

            List<Integer> tmp = path.subList(0, cl);
            System.out.println("tmp path");
            Tools.printPath(tmp);
            Tools.printPath(path);

            System.out.println("cl " + cl);

            if (rotateCluster) {
                System.out.println("---REVERSE---");
                Tools.reverseCluster(path, 0, cl);

                System.out.println("bestj " + bestJ);
                Tools.rotateCluster(path, 0, cl, bestJ);
                System.out.println("rotate");
                Tools.printPath(path);
            } else {
                System.out.println("bestj " + bestJ);
                Tools.rotateCluster(path, 0, cl, bestJ);
                System.out.println("rotate");
                Tools.printPath(path);

            }

            tmp = path.subList(0, cl);
            System.out.println("tmp rev path");
            Tools.printPath(tmp);
            Tools.printPath(path);
            Tools.moveCluster(path, 0, cl, bestI - cl);

            return true;
        }
        return false;
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
