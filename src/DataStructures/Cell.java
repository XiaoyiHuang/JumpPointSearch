package DataStructures;

/**
 * Represent a coordinate on the map
 * Created by Marco
 * Date: 2019/6/4 15:46
 */

public class Cell {
    public int x;
    public int y;

    public double gScore = 0.0;
    public double hScore = 0.0;

    public Cell parent = null;

    public boolean isObstacle = false;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Cell initFromCGCoordinate(int x, int y, int height) {
        return new Cell(y, height - x - 1);
    }

    /**
     * Translate coordinates from CG coordinate system, which has its origin located at the upper-left corner,
     * extending eastwards (x-axis) and southwards(y-axis), to the classic Cartesian coordinate system
     *
     * @param height
     * @return
     */
    public Cell CGCoordinateToCartesianCoordinate(int height) {
        return new Cell(y, height - x - 1);
    }

    /**
     * Translate coordinates from the classic coordinate system to the classic Cartesian coordinate system,
     * which has its origin located at the upper-left corner, extending eastwards(x-axis) and southwards(y-axis)
     *
     * @param height
     * @return
     */
    public Cell CartesianCoordinateToCGCoordinate(int height) {
        return new Cell(height - y - 1, x);
    }

    /**
     * Clear all stored from previous path-finding
     */
    public void reset() {
        this.gScore = 0.0;
        this.hScore = 0.0;
        this.parent = null;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Cell)) {
            return false;
        }
        return this.x == ((Cell)obj).x && this.y == ((Cell)obj).y;
    }

    @Override
    public int hashCode() {
        return (String.valueOf(x) + y).hashCode();
    }

    @Override
    public String toString() {
        return new StringBuilder("[")
                .append(x)
                .append(", ")
                .append(y)
                .append("]")
                .toString();
    }
}
