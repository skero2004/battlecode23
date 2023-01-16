package bot2;

import battlecode.common.*;

public class Util {
    public static class Vec2D {
        final int x, y;

        public Vec2D(MapLocation location) {
            x = location.x;
            y = location.y;
        }

        public Vec2D(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Vec2D add(Vec2D v) {
            return new Vec2D(x + v.x, y + v.y);
        }

        public Vec2D sub(Vec2D v) {
            return new Vec2D(x + v.x, y + v.y);
        }

        public Vec2D scale(int s) {
            return new Vec2D(x * s, y * s);
        }

        public int length() {
            return (int) Math.sqrt(x * x + y * y);
        }
    }

    public enum LocationType {
        WELL_AD, WELL_MN, WELL_EX, ISLAND, HEADQUARTERS
    }

}
