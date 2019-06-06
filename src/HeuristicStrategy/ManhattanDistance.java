package HeuristicStrategy;

import DataStructures.Cell;

/**
 * ManhattanDistance: Suitable for 4-direction movement
 * Created by Marco
 * Date: 2019/6/4 16:05
 *
 * Reference: http://theory.stanford.edu/~amitp/GameProgramming/Heuristics.html (Manhattan distance for diagonal movement)
 */

public class ManhattanDistance implements DistanceAlgorithm {
    @Override
    public double getDistance(Cell from, Cell to) {
        int dx = Math.abs(from.x - to.x);
        int dy = Math.abs(from.y - to.y);
        return dx + dy;
    }
}
