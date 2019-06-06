package HeuristicStrategy;

import DataStructures.Cell;

/**
 * OctileDistance: Works for diagonal 8-direction movement
 * Created by Marco
 * Date: 2019/6/5 14:32
 *
 * Reference: http://theory.stanford.edu/~amitp/GameProgramming/Heuristics.html
 * Note: Compute the number of steps you take if you can’t take a diagonal,
 *       then subtract the steps you save by using the diagonal
 */

public class OctileDistance implements DistanceAlgorithm {
    @Override
    public double getDistance(Cell from, Cell to) {
        int dx = Math.abs(from.x - to.x);
        int dy = Math.abs(from.y - to.y);
        return (dx + dy) + (Math.sqrt(2) - 2) * Math.min(dx, dy);
    }
}
