package HeuristicStrategy;

import DataStructures.Cell;

/**
 * EuclideanDistance: Works ff your units can move at any angle (instead of grid directions)
 * Created by Marco
 * Date: 2019/6/4 16:06
 *
 * Reference: http://theory.stanford.edu/~amitp/GameProgramming/Heuristics.html
 */

public class EuclideanDistance implements DistanceAlgorithm {
    @Override
    public double getDistance(Cell from, Cell to) {
        int dx = Math.abs(from.x - to.x);
        int dy = Math.abs(from.y - to.y);
        return Math.sqrt(dx * dx + dy * dy);
    }
}
