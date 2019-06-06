package CostStrategy;

import DataStructures.Cell;

/**
 * DijkstraStrategy
 * Created by Marco
 * Date: 2019/6/4 15:45
 */

public class DijkstraStrategy implements CostStrategy {
    @Override
    public double getScore(Cell cell) {
        return cell.gScore;
    }
}
