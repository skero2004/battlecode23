package testElixirBot.util;

import battlecode.common.*;

public class Vec2D {
    public final int x, y, l;

    public Vec2D(int x, int y) {
        this.x = x;
        this.y = y;
        l = (int) Math.sqrt(x * x + y * y); // integer length
    }

    public Vec2D(MapLocation location) {
        this(location.x, location.y);
    }

    public Vec2D add(Vec2D v) {
        return new Vec2D(x + v.x, y + v.y);
    }

    public Vec2D sub(Vec2D v) {
        return new Vec2D(x - v.x, y - v.y);
    }

    public Vec2D scale(int s) {
        return new Vec2D(x * s, y * s);
    }

    public boolean equals(Vec2D v) {
        return x == v.x && y == v.y;
    }
}
