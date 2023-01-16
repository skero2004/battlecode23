package bot2;

import java.util.ArrayList;

import bot2.Util.*;

import battlecode.common.*;

public class Communication {
    static final int ARRAY_LENGTH = 64;
    static final int ITEM_BITS = 16;

    static Location deserialize(int bin, LocationType typ) {
        Location loc;
        switch (typ) {
            case WELL_AD:
            case WELL_MN:
            case WELL_EX:
                loc = new Well(bin);
                break;
            case ISLAND:
                loc = new Island(bin);
            case HEADQUARTERS:
                loc = new Headquarters(bin);
            default:
                throw new IllegalArgumentException();
        }
        return loc;
    }

    static int serialize(Location loc) {
        switch (loc.typ) {
            case WELL_AD:
            case WELL_MN:
            case WELL_EX:
                return ((Well) loc).serialize();
            case ISLAND:
                return ((Island) loc).serialize();
            case HEADQUARTERS:
                return ((Headquarters) loc).serialize();
            default:
                throw new IllegalArgumentException();
        }
    }

    static ArrayList<Location> getItemsByType(RobotController rc, LocationType typ) throws GameActionException {
        ArrayList<Location> result = new ArrayList<>();
        for (int i = 0; i < ARRAY_LENGTH; ++i) {
            int bin = rc.readSharedArray(i);
            if (bin == 0)
                continue;
            Location loc = deserialize(bin, typ);
            if (loc.typ == typ) {
                result.add(loc);
            }
        }
        return result;
    }

    // GameActionException
    // returns true if updated, false if the item already exists
    static boolean setItem(RobotController rc, Location loc) throws GameActionException {
        int idx = -1;
        for (int i = 0; i < ARRAY_LENGTH; ++i) {
            int bin = rc.readSharedArray(i);
            if (bin == 0) {
                if (idx == -1)
                    idx = i;
            } else {
                Location cur = deserialize(bin, loc.typ);
                if (cur.coordinates == loc.coordinates) {
                    if (cur.equals(loc))
                        return false;
                    idx = i;
                    break;
                }
            }
        }

        int val = serialize(loc);
        if (idx != -1) {
            if (rc.canWriteSharedArray(idx, val)) {
                rc.writeSharedArray(idx, val);
                return true;
            } else {
                throw new IllegalArgumentException();
            }
        } else
            throw new IllegalArgumentException();
    }
}
