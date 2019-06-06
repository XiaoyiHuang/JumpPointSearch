package DataStructures;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Map for path-finding
 * Map data: https://github.com/SteveRabin/JPSPlusWithGoalBounding/blob/master/JPSPlusGoalBounding/Maps/maze-100-1.map
 * Created by Marco
 * Date: 2019/6/4 15:49
 *
 * Note: The coordinate system used here is the classic Cartesian coordinate system, which has its origin at
 *       the lower-left corner of the map, with its x-axis extending eastwards, and y-axis going northwards.
 *       Yet, as most coordinate systems applied in Computer Science problems place their origins at the
 *       upper-left corner, {Cell#CartesianCoordinateToCGCoordinate} and {Cell#CGCoordinateToCartesianCoordinate}
 *       methods are offered to perform necessary coordinate translation between different coordinate systems.
 */

public class Map {
    private Cell topLeft, topRight, bottomLeft, bottomRight;
    private Set<Cell> obstacles = new HashSet<>();
    private int width = -1, height = -1;
    private Cell[][] cells = null;

    private static Map mapInstance;

    private Map(){}

    public static Map getMap() {
        return mapInstance == null ? initMap(new Cell(0, 0), 5, 5) : mapInstance;
    }

    /**
     * Init map based on the coordinates of the four corners
     * @param topLeft
     * @param topRight
     * @param bottomLeft
     * @param bottomRight
     * @return
     */
    public static Map initMap(Cell topLeft, Cell topRight, Cell bottomLeft, Cell bottomRight) {
        int width = topRight.x - topLeft.x + 1;
        int height = topLeft.y - bottomLeft.y + 1;

        return initMap(bottomLeft, width, height);
    }

    /**
     * Init map with coordinate of the origin, along with the width & height of the map
     * @param bottomLeft
     * @param width
     * @param height
     * @return
     */
    public static Map initMap(Cell bottomLeft, int width, int height) {
        mapInstance = new Map();
        mapInstance.topLeft = new Cell(bottomLeft.x, bottomLeft.y + height - 1);
        mapInstance.topRight = new Cell(bottomLeft.x + width - 1, bottomLeft.y + height - 1);
        mapInstance.bottomLeft = bottomLeft;
        mapInstance.bottomRight = new Cell(bottomLeft.x + width - 1, bottomLeft.y);

        mapInstance.width = width;
        mapInstance.height = height;

        mapInstance.cells = new Cell[width][height];

        return mapInstance;
    }

    /**
     * Init map from a file
     * @param filePath
     * @return
     */
    public static Map initMap(String filePath) {
        mapInstance = new Map();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line = null;
            char[] lineChars = null;
            int rowIdx = 0;
            boolean hasStartedParsingMapData = false;

            while ((line = reader.readLine()) != null) {
                if (!hasStartedParsingMapData) {
                    if (line.startsWith("height")) {
                        mapInstance.height = Integer.valueOf(line.split(" ")[1]);
                        rowIdx = mapInstance.height - 1;

                        mapInstance.bottomLeft = new Cell(0, 0);
                        mapInstance.topLeft = new Cell(0, rowIdx);
                    }
                    else if (line.startsWith("width")) {
                        mapInstance.width = Integer.valueOf(line.split(" ")[1]);
                        mapInstance.bottomRight = new Cell(mapInstance.width - 1, 0);
                        mapInstance.topRight = new Cell(mapInstance.width - 1, rowIdx);
                    }
                    else if (line.startsWith("map")) {
                        hasStartedParsingMapData = true;
                    }

                    if (mapInstance.height != -1 && mapInstance.width != -1 && mapInstance.cells == null) {
                        mapInstance.cells = new Cell[mapInstance.width][mapInstance.height];
                    }
                }
                else {
                    lineChars = line.toCharArray();

                    for (int colIdx = 0; colIdx < lineChars.length; colIdx ++) {
                        if (lineChars[colIdx] == '@') {
                            mapInstance.addObstacle(colIdx, rowIdx);
                        }
                        else {
                            mapInstance.addCell(colIdx, rowIdx);
                        }
                    }

                    rowIdx -= 1;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mapInstance;
    }

    /**
     * Check if given coordinate position on the map is an obstacle
     * @param x
     * @param y
     * @return
     */
    public boolean isObstacleAt(int x, int y) {
        Cell cell = getCellAt(x, y);
        return cell == null || cell.isObstacle;
    }

    /**
     * Check if given coordinate position is beyond the boundaries of the map
     * @param x
     * @param y
     * @return
     */
    public boolean isOutsideMap(int x, int y) {
        return x < topLeft.x || x > topRight.x ||
                y < bottomLeft.y || y > topLeft.y;
    }

    /**
     * Check if the given coordinate position is eligible to walk over
     * @param x
     * @param y
     * @return
     */
    public boolean isPositionWalkable(int x, int y) {
        return !isOutsideMap(x, y) && !isObstacleAt(x, y);
    }

    /**
     * Check if a given coordinate position, specified by (toX, toY)
     * can be reached from a neighboring position (fromX, fromY)
     *
     * Condition 1: If the two positions are diagonally aligned, we need
     * to avoid the wall-walk-through situation, as illustrated below:
     *
     * |OBSTACLE|   TO   |
     * |--------|--------|
     * |  FROM  |OBSTACLE|
     *
     * Condition 2: If the two positions are either horizontally or
     * vertically aligned, we only need to check if the destination position
     * is walkable
     *
     * @param fromX
     * @param fromY
     * @param toX
     * @param toY
     * @return
     */
    public boolean reachable(int fromX, int fromY, int toX, int toY) {
        int subDirX = toX - fromX;
        int subDirY = toY - fromY;

        return isPositionWalkable(toX, toY) && ((subDirX == 0 || subDirY == 0) ||
                (isPositionWalkable(fromX, toY) || isPositionWalkable(toX, fromY)));
    }

    /** ---------- Setters ---------- */

    public void setBottomLeft(Cell bottomLeft) {
        mapInstance.bottomLeft = bottomLeft;
    }

    public void setBottomRight(Cell bottomRight) {
        mapInstance.bottomRight = bottomRight;
    }

    public void setTopLeft(Cell topLeft) {
        mapInstance.topLeft = topLeft;
    }

    public void setTopRight(Cell topRight) {
        mapInstance.topRight = topRight;
    }

    /** ---------- Getters ---------- */

    public Cell getBottomLeft() {
        return mapInstance.bottomLeft;
    }

    public Cell getBottomRight() {
        return mapInstance.bottomRight;
    }

    public Cell getTopLeft() {
        return mapInstance.topLeft;
    }

    public Cell getTopRight() {
        return mapInstance.topRight;
    }

    public int getHeight() {
        return mapInstance.height;
    }

    public int getWidth() {
        return mapInstance.width;
    }

    public Cell[][] getCells() {
        return mapInstance.getCells();
    }

    public Cell getCellAt(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return null;
        }

        if (mapInstance.cells[x][y] == null) {
            mapInstance.cells[x][y] = new Cell(x, y);
        }

        return mapInstance.cells[x][y];
    }

    public Cell getCellAt(Cell cell) {
        return getCellAt(cell.x, cell.y);
    }

    /**
     * Add a Cell instance at given coordinate
     * @param x
     * @param y
     * @return True if a Cell instance is created, false if a Cell instance already exists at the given coordinate
     */
    public boolean addCell(int x, int y) {
        if (mapInstance.cells[x][y] == null) {
            mapInstance.cells[x][y] = new Cell(x, y);
            return true;
        } else {
            return false;
        }
    }

    /** Add an obstacle at given coordinate
     * @param x
     * @param y
     */
    public void addObstacle(int x, int y) {
        mapInstance.obstacles.add(new Cell(x, y));

        getCellAt(x, y).isObstacle = true;
    }
}
