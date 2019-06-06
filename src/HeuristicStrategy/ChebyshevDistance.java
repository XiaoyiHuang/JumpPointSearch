package HeuristicStrategy;

import DataStructures.Cell;

/**
 * ChebyshevDistance: Works for diagonal 8-direction movement
 * Created by Marco
 * Date: 2019/6/4 15:58
 *
 * Reference: http://theory.stanford.edu/~amitp/GameProgramming/Heuristics.html
 * Note: Compute the number of steps you take if you can’t take a diagonal
 *       Then subtract the steps you save by using the diagonal
 */

public class ChebyshevDistance implements DistanceAlgorithm {
    @Override
    public double getDistance(Cell from, Cell to) {
        int dx = Math.abs(from.x - to.x);
        int dy = Math.abs(from.y - to.y);
        return (dx + dy) - Math.min(dx, dy);
    }
}
