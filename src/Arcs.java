public class Arcs {

    public static double[][] getArray(int[] data, int w, int h) {
        double[][] array = new double[data.length / 2][data.length / 2];

        for (int i = 0; i < data.length / 2; i++) {
            for (int j = 0; j < data.length / 2; j++) {
                array[i][j] = getDistance(data, i, j);
            }
        }

        return array;
    }

    public static double getDistance(int[] data, int p1, int p2) {
        double x1 = data[p1 * 2];
        double y1 = data[p1 * 2 + 1];
        double x2 = data[p2 * 2];
        double y2 = data[p2 * 2 + 1];
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

}
