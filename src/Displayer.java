import javax.swing.SwingUtilities;

public class Displayer {

    // Size of the window where we draw the resulting path
    private final static int WINDOW_SIZE = 500;

    public static void main(String[] args) {

        // Read the x and y values for all nodes from a csv file.
        final int[] data = Tools.readGraphFromCVSFile("graphs/100_locations.csv");
        final int[] path = Tools.readPathFromCVSFile("result_100_786.8340220921709.csv");

        // How long should the path be / how many nodes are we dealing with.
        final int size = data.length / 2;

        // Get the distances between all nodes
        final double[][] arcs = Arcs.getArray(data, size, size);

        // Show the resulting path in a window, useful for debugging and
        // improving the optimizing algorithms.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Tools.createAndShowGUI(Tools.getPolygonForPlotting(data, path, WINDOW_SIZE), WINDOW_SIZE, "TSP path, nodes: "
                        + size + ", length: " + Tools.getPathLength(arcs, path));
            }
        });
    }

}
