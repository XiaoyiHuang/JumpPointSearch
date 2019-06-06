package DataStructures;

/**
 * Represents direction of a movement
 * Created by Marco
 * Date: 2019/6/4 15:48
 */

public enum Direction {
    LEFT(-1, 0),
    TOP_LEFT(-1, 1),
    TOP(0, 1),
    TOP_RIGHT(1, 1),
    RIGHT(1, 0),
    BOTTOM_RIGHT(1, -1),
    BOTTOM(0, -1),
    BOTTOM_LEFT(-1, -1);

    public int xOffset, yOffset;
    static Direction[][] offsetToDirection;

    Direction(int xOffset, int yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    Direction(int[] offsets) {
        if (offsets.length < 2) {
            return;
        }

        this.xOffset = offsets[0];
        this.yOffset = offsets[1];
    }

    /**
     * Caching up the correlation between offsets and Directions
     */
    static {
        offsetToDirection = new Direction[3][3];

        offsetToDirection[0][0] = BOTTOM_LEFT;
        offsetToDirection[0][1] = LEFT;
        offsetToDirection[0][2] = TOP_LEFT;

        offsetToDirection[1][0] = BOTTOM;
        offsetToDirection[1][2] = TOP;

        offsetToDirection[2][0] = BOTTOM_RIGHT;
        offsetToDirection[2][1] = RIGHT;
        offsetToDirection[2][2] = TOP_RIGHT;
    }

    public static boolean isDiagonal(int xOffset, int yOffset) {
        return xOffset != 0 && yOffset != 0;
    }

    public static Direction findDirectionWithOffsets(int xOffset, int yOffset) {
        return offsetToDirection[xOffset + 1][yOffset + 1];
    }

    public Direction getXSubDirection() {
        return findDirectionWithOffsets(xOffset, 0);
    }

    public Direction getYSubDirection() {
        return findDirectionWithOffsets(0, yOffset);
    }

    @Override
    public String toString() {
        return this.name();
    }
}
