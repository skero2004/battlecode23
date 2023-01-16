package bot2;

import battlecode.common.*;

public class Util {
    static class Vec2D {
        final int x, y;

        Vec2D(MapLocation location) {
            x = location.x;
            y = location.y;
        }

        Vec2D(int x, int y) {
            this.x = x;
            this.y = y;
        }

        Vec2D add(Vec2D v) {
            return new Vec2D(x + v.x, y + v.y);
        }

        Vec2D sub(Vec2D v) {
            return new Vec2D(x + v.x, y + v.y);
        }

        Vec2D scale(int s) {
            return new Vec2D(x * s, y * s);
        }

        int length() {
            return (int) Math.sqrt(x * x + y * y);
        }
    }

    static enum LocationType {
        WELL_AD, WELL_MN, WELL_EX, ISLAND, HEADQUARTERS
    }

    static LocationType[] lookup = { LocationType.WELL_AD, LocationType.WELL_MN, LocationType.WELL_EX };

    static class Coordinates {
        final int x, y;

        Coordinates(int x, int y) {
            this.x = x;
            this.y = y;
        }

        static int serialize(Coordinates coord) {
            return coord.x | (coord.y << 6);
        }

        static Coordinates deserialize(int bin) {
            return new Coordinates(bin & 63, (bin >> 6));
        }
    }

    abstract static class Location {
        final LocationType typ;
        final Coordinates coordinates;

        Location(LocationType typ, Coordinates coordinates) {
            this.typ = typ;
            this.coordinates = coordinates;
        }

        abstract int serialize();
    }

    static class Island extends Location {
        final boolean isNotCaptured;
        final boolean needsLauncher; // launchers # < ??
        final boolean isAnchorDying; // HP below ?? %

        Island(LocationType typ, Coordinates coord, boolean isNotCaptured, boolean needsLauncher,
                boolean isAnchorDying) {
            super(typ, coord);
            this.isNotCaptured = isNotCaptured;
            this.needsLauncher = needsLauncher;
            this.isAnchorDying = isAnchorDying;
        }

        Island(int bin) {
            this(
                    LocationType.ISLAND,
                    Coordinates.deserialize(bin >> 4),
                    ((bin >> 1) & 1) == 1,
                    ((bin >> 2) & 1) == 1,
                    ((bin >> 3) & 1) == 1);
            int typ = bin & 1;
            if (typ != 1)
                throw new IllegalArgumentException();
        }

        int serialize() {
            int res = 1;
            res |= ((isNotCaptured ? 1 : 0) << 1);
            res |= ((needsLauncher ? 1 : 0) << 2);
            res |= ((isAnchorDying ? 1 : 0) << 3);
            res |= (Coordinates.serialize(coordinates) << 4);
            return res;
        }
    }

    static class Well extends Location {
        final boolean isUpgraded;

        Well(LocationType typ, Coordinates coordinates, boolean isUpgraded) {
            super(typ, coordinates);
            this.isUpgraded = isUpgraded;
        }

        Well(int bin) {
            this(
                    lookup[(bin >> 1) & 3],
                    Coordinates.deserialize(bin >> 4),
                    ((bin >> 3) & 1) == 1);
            int typ = bin & 1;
            if (typ != 0)
                throw new IllegalArgumentException();
        }

        int serialize() {
            int idx = typ.ordinal() - LocationType.WELL_AD.ordinal();
            int res = 0;
            res |= (idx << 1);
            res |= ((isUpgraded ? 1 : 0) << 3);
            res |= (Coordinates.serialize(coordinates) << 4);
            return res;
        }
    }

    static class Headquarters extends Location {
        Headquarters(LocationType typ, Coordinates coord) {
            super(typ, coord);
        }

        Headquarters(int bin) {
            this(LocationType.HEADQUARTERS, Coordinates.deserialize(bin >> 4));
            int typ = bin & 1;
            if (typ != 0)
                throw new IllegalArgumentException();
            if (typ != 3)
                throw new IllegalArgumentException();
        }

        int serialize() {
            int res = 1;
            res |= (3 << 1);
            res |= (Coordinates.serialize(coordinates) << 4);
            return res;
        }
    }
}
