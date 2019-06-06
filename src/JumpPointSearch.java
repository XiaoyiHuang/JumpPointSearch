import CostStrategy.AStarStrategy;
import CostStrategy.CostStrategy;
import DataStructures.Cell;
import DataStructures.Direction;
import DataStructures.Map;
import HeuristicStrategy.DistanceAlgorithm;
import HeuristicStrategy.EuclideanDistance;
import HeuristicStrategy.OctileDistance;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * JumpPointSearch.JumpPointSearch: A Java version implementation of the Jump-Point-Search algorithm (JPS)
 * Created by Marco
 * Date: 2019/6/3 10:30
 *
 * References:
 *   1) https://zerowidth.com/2013/05/05/jump-point-search-explained.html
 *   2) https://github.com/zerowidth/jps-explained
 *   3) http://users.cecs.anu.edu.au/~dharabor/data/papers/harabor-grastien-aaai11.pdf
 *
 * For details of the algorithm itself, see:
 *   http://users.cecs.anu.edu.au/~dharabor/data/papers/harabor-grastien-aaai11.pdf
 */

public class JumpPointSearch {

    private static JumpPointSearch jps;

    /** Properties of the map */
    private Map map;
    private Cell start, goal;
    private CostStrategy costStrategy;
    private DistanceAlgorithm heuristicStrategy;
    private DistanceAlgorithm distanceAlgorithm;

    PriorityQueue<Cell> openSet = new PriorityQueue<>(Comparator.comparingDouble(c -> costStrategy.getScore(c)));
    Set<Cell> closedSet = new HashSet<>();

    private JumpPointSearch() {}

    public JumpPointSearch initMap(Map map) {
        this.map = map;
        openSet.clear();
        closedSet.clear();
        return this;
    }

    public JumpPointSearch initStartPoint(Cell startPos) {
        this.start = startPos;
        return this;
    }

    public JumpPointSearch initGoalPoint(Cell goalPos) {
        this.goal = goalPos;
        return this;
    }

    // Init algorithm for calculating f score
    public JumpPointSearch initCostStrategy(CostStrategy costStrategy) {
        this.costStrategy = costStrategy;
        return this;
    }

    // Init algorithm for calculating h score
    public JumpPointSearch initHeuristicStrategy(DistanceAlgorithm heuristicStrategy) {
        this.heuristicStrategy = heuristicStrategy;
        return this;
    }

    // Init algorithm for calculating g score
    public JumpPointSearch initDistanceAlgorithm(DistanceAlgorithm distanceAlgorithm) {
        this.distanceAlgorithm = distanceAlgorithm;
        return this;
    }

    public static JumpPointSearch initPathFinding(Map map, Cell startPos, Cell goalPos, CostStrategy costStrategy,
                                                  DistanceAlgorithm distanceAlgorithm, DistanceAlgorithm heuristicStrategy) {
        if (jps != null) {
            return jps;
        }
        jps = new JumpPointSearch()
                .initMap(map)
                .initStartPoint(startPos)
                .initGoalPoint(goalPos)
                .initCostStrategy(costStrategy)
                .initDistanceAlgorithm(distanceAlgorithm)
                .initHeuristicStrategy(heuristicStrategy);
        return jps;
    }


    /**
     * Find forced neighbors of given position
     * @param pos
     * @param direction
     * @return
     */
    private List<Cell> getForcedNeighbors(Cell pos, Direction direction) {
        List<Cell> forcedNeighbors = new ArrayList<>();

        if (direction.yOffset == 0) {                                                           // Move horizontally
            if (map.reachable(pos.x, pos.y, pos.x + direction.xOffset, pos.y - 1) &&
                    map.isObstacleAt(pos.x, pos.y - 1)) {
                forcedNeighbors.add(map.getCellAt(pos.x + direction.xOffset, pos.y - 1));
            }
            if (map.reachable(pos.x, pos.y, pos.x + direction.xOffset, pos.y + 1) &&
                    map.isObstacleAt(pos.x, pos.y + 1)) {
                forcedNeighbors.add(map.getCellAt(pos.x + direction.xOffset, pos.y + 1));
            }
        } else if (direction.xOffset == 0) {                                                    // Move vertically
            if (map.reachable(pos.x, pos.y, pos.x - 1, pos.y + direction.yOffset) &&
                    map.isObstacleAt(pos.x - 1, pos.y)) {
                forcedNeighbors.add(map.getCellAt(pos.x - 1, pos.y + direction.yOffset));
            }
            if (map.reachable(pos.x, pos.y, pos.x + 1, pos.y + direction.yOffset) &&
                    map.isObstacleAt(pos.x + 1, pos.y)) {
                forcedNeighbors.add(map.getCellAt(pos.x + 1, pos.y + direction.yOffset));
            }
        } else {                                                                                // Move diagonally
            if (map.reachable(pos.x, pos.y, pos.x - direction.xOffset, pos.y + direction.yOffset) &&
                    map.isObstacleAt(pos.x - direction.xOffset, pos.y)) {
                forcedNeighbors.add(map.getCellAt(pos.x - direction.xOffset, pos.y + direction.yOffset));
            }
            if (map.reachable(pos.x, pos.y, pos.x + direction.xOffset, pos.y - direction.yOffset) &&
                    map.isObstacleAt(pos.x, pos.y - direction.yOffset)) {
                forcedNeighbors.add(map.getCellAt(pos.x + direction.xOffset, pos.y - direction.yOffset));
            }
        }

        return forcedNeighbors;
    }

    /**
     * Obtain all valid neighbors of current position that requires checking
     * @param pos
     * @return
     */
    private List<Cell> getNeighbors(Cell pos) {
        List<Cell> neighbors = new ArrayList<>();
        Cell prevPos = pos.parent;

        // If current node is the starting point, expand in all directions
        if (prevPos == null) {
            Direction[] directions = Direction.values();

            for (Direction direction : directions) {
                neighbors.add(map.getCellAt(pos.x + direction.xOffset, pos.y + direction.yOffset));
            }
        }
        else {
            int directionXOffset = pos.x - prevPos.x;
            int directionYOffset = pos.y - prevPos.y;

            // Clamp the direction vector to between -1 and 1
            directionXOffset = directionXOffset > 1 ? 1 : (directionXOffset < -1 ? -1 : directionXOffset);
            directionYOffset = directionYOffset > 1 ? 1 : (directionYOffset < -1 ? -1 : directionYOffset);

            // If previous movement is either horizontal or vertical
            if (!Direction.isDiagonal(directionXOffset, directionYOffset)) {
                if (map.reachable(pos.x, pos.y, pos.x + directionXOffset, pos.y + directionYOffset)) {
                    neighbors.add(map.getCellAt(pos.x + directionXOffset, pos.y + directionYOffset));
                }
            }
            // If previous movement is diagonal
            else {
                if (map.reachable(pos.x, pos.y, pos.x + directionXOffset, pos.y)) {
                    // Move horizontally
                    neighbors.add(map.getCellAt(pos.x + directionXOffset, pos.y));
                }

                if (map.reachable(pos.x, pos.y, pos.x, pos.y + directionYOffset)) {
                    // Move vertically
                    neighbors.add(map.getCellAt(pos.x, pos.y + directionYOffset));
                }

                // Move diagonally
                if (map.reachable(pos.x, pos.y, pos.x + directionXOffset, pos.y + directionYOffset)) {
                    neighbors.add(map.getCellAt(pos.x + directionXOffset, pos.y + directionYOffset));
                }
            }

            // Get forced neighbors
            List<Cell> forcedNeighbors = getForcedNeighbors(pos,
                    Direction.findDirectionWithOffsets(directionXOffset, directionYOffset));

            // Merge neighbors with forced neighbors
            neighbors.addAll(forcedNeighbors);
        }

        return neighbors;
    }

    /**
     * Obtain the next jump point on given direction of movement
     * @param curr
     * @param direction
     * @return
     */
    private Cell getNextJumpPoint(Cell curr, Direction direction) {
        int neighborCoordX = curr.x + direction.xOffset;
        int neighborCoordY = curr.y + direction.yOffset;

        Cell neighbor = map.getCellAt(neighborCoordX, neighborCoordY);

        if (!map.isPositionWalkable(neighborCoordX, neighborCoordY) ||
                !map.reachable(curr.x, curr.y, neighbor.x, neighbor.y)) {
            return null;
        }

        if (neighbor.equals(goal)) {
            return goal;
        }

        if(getForcedNeighbors(neighbor, direction).size() > 0) {
            return neighbor;
        }

        // Next moves of diagonal direction include horizontal, vertical and diagonal moves
        // Here we perform checking on horizontal and vertical movements first
        if (Direction.isDiagonal(direction.xOffset, direction.yOffset)) {

            // Check on sub-direction X
            if (getNextJumpPoint(neighbor, direction.getXSubDirection()) != null) {
                return neighbor;
            }

            // Check on sub-direction Y
            if (getNextJumpPoint(neighbor, direction.getYSubDirection()) != null) {
                return neighbor;
            }
        }

        // Recursively find next jump point
        return getNextJumpPoint(neighbor, direction);
    }

    /**
     * Obtain all eligible successors (jump points) starting from current position
     * @param curr
     * @return
     */
    private List<Cell> getSuccessors(Cell curr) {
        List<Cell> jumpPoints = new ArrayList<>();
        List<Cell> neighbors = getNeighbors(curr);

        for (Cell neighbor : neighbors) {
            Direction direction = Direction.findDirectionWithOffsets(neighbor.x - curr.x, neighbor.y - curr.y);
            Cell jumpPoint = getNextJumpPoint(curr, direction);

            if (jumpPoint != null) {
                jumpPoints.add(jumpPoint);
            }
        }

        return jumpPoints;
    }

    /**
     * Finding the minimum-cost path from starting point to the goal point
     * @return
     */
    private List<Cell> findPath() {
        LinkedList<Cell> path = new LinkedList<>();

        // Add starting point to the openSet
        openSet.add(start);

        while (!openSet.isEmpty()) {
            Cell currentPos = openSet.poll();
            closedSet.add(currentPos);

            if (currentPos.equals(goal)) {
                // Trace all the way back to identify the path
                while (currentPos.parent != null) {
                    path.addFirst(currentPos);
                    currentPos = currentPos.parent;
                }
                break;
            }

            List<Cell> successors = getSuccessors(currentPos);

            for (Cell successor : successors) {
                double updatedGScore = currentPos.gScore + distanceAlgorithm.getDistance(currentPos, successor);

                if (closedSet.contains(successor)) {
                    continue;
                }

                if (openSet.contains(successor)) {
                    if (updatedGScore < successor.gScore) {
                        successor.gScore = updatedGScore;
                        successor.parent = currentPos;
                    }
                } else {
                    // Clear previous path-finding results, if current successor is first-time-visited
                    successor.reset();

                    successor.parent = currentPos;
                    successor.gScore = updatedGScore;
                    successor.hScore = updatedGScore + heuristicStrategy.getDistance(successor, goal);
                    openSet.offer(successor);
                }
            }
        }

        return path;
    }

    /**
     * Compare our path-finding results with correct results we obtained from other sources
     * @param path
     * @param correctResults
     */
    public void benchmark(List<Cell> path, int[][] correctResults) {
        System.out.println("LENGTH OF OUR PATH: " + path.size());

        for (Cell pathCell : path) {
            System.out.print(pathCell.CartesianCoordinateToCGCoordinate(map.getHeight()) + " ");
        }

        System.out.println();

        // Compare results
        if (correctResults.length != path.size()) {
            System.out.println("PATH LENGTH NOT MATCH! CORRECT RESULTS: " + correctResults.length + ", PATH: "
                    + path.size());
        }
        for (int i = 0; i < correctResults.length; i++) {
            Cell cell = path.get(i).CartesianCoordinateToCGCoordinate(map.getHeight());
            if (correctResults[i][0] != cell.x || correctResults[i][1] != cell.y) {
                System.out.println("JUMPING POINT NOT MATCHED! CORRECT RESULTS: [" + correctResults[i][0] + ", "
                        + correctResults[i][1] + "], PATH: " + cell);
                break;
            }
        }

    }

    public static void main(String[] args) {
        Map map = Map.initMap("./mapFiles/maze-100-1.map");

        Cell startPos = Cell.initFromCGCoordinate(1,39, map.getHeight());
        Cell goalPos = Cell.initFromCGCoordinate(99,65, map.getHeight());
        JumpPointSearch jps = JumpPointSearch.initPathFinding(map, startPos, goalPos,
                new AStarStrategy(), new EuclideanDistance(), new OctileDistance());

        long curr = System.currentTimeMillis();
        List<Cell> path = jps.findPath();
        System.out.println("Consumed time：" + (System.currentTimeMillis() - curr) + " ms");

        // Correct result for reference
        int[][] correctResults = {{1, 46}, {2, 47}, {4, 47}, {6, 45}, {7, 46}, {8, 47}, {10, 47}, {11, 46}, {12, 45},
                {14, 47}, {15, 46}, {15, 40}, {16, 39}, {17, 40}, {17, 46}, {18, 47}, {19, 46}, {19, 42}, {20, 41},
                {21, 42}, {21, 46}, {22, 47}, {23, 46}, {23, 44}, {24, 43}, {28, 43}, {29, 42}, {30, 41}, {32, 41},
                {34, 41}, {36, 41}, {39, 38}, {39, 36}, {40, 35}, {42, 37}, {43, 36}, {44, 35}, {46, 35}, {48, 33},
                {50, 33}, {52, 35}, {53, 36}, {53, 40}, {54, 41}, {56, 41}, {57, 40}, {57, 38}, {58, 37}, {60, 37},
                {61, 38}, {61, 40}, {63, 42}, {61, 44}, {61, 48}, {62, 49}, {63, 50}, {63, 52}, {65, 54}, {65, 56},
                {65, 58}, {65, 60}, {66, 61}, {68, 59}, {72, 59}, {74, 59}, {76, 61}, {80, 61}, {82, 61}, {83, 60},
                {83, 58}, {84, 57}, {86, 59}, {88, 59}, {89, 60}, {89, 62}, {90, 63}, {98, 63}, {99, 64}, {99, 65}};

        jps.benchmark(path, correctResults);
    }
}
