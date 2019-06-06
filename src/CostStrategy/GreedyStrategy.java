package CostStrategy;

import DataStructures.Cell;

/**
 * GreedyStrategy
 * Created by Marco
 * Date: 2019/6/4 15:45
 */

public class GreedyStrategy implements CostStrategy {
    @Override
    public double getScore(Cell cell) {
        return cell.hScore;
    }
}
