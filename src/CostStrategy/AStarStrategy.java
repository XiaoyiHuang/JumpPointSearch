package CostStrategy;

import DataStructures.Cell;

/**
 * AStarStrategy
 * Created by Marco
 * Date: 2019/6/4 15:44
 */

public class AStarStrategy implements CostStrategy {
    @Override
    public double getScore(Cell cell) {
        return cell.gScore + cell.hScore;
    }
}
