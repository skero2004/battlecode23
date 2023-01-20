package bot4;

import java.util.ArrayList;
import java.util.Arrays;

import battlecode.common.*;

import bot4.util.*;

public class Communication {
    private static final int ARRAY_LENGTH = 64;
    private static final int OUTDATED_TURNS = 30;

    private static class Update {
        final int time, value;

        Update(int time, int value) {
            this.time = time;
            this.value = value;
        }
    }

    private static ArrayList<Update> queue = new ArrayList<>();

    static ArrayList<Location> query(RobotController rc, LocationType... locationTypes_)
            throws GameActionException {
        ArrayList<LocationType> locationTypes = new ArrayList<>(Arrays.asList(locationTypes_));
        ArrayList<Location> result = new ArrayList<>();
        for (int i = 0; i < ARRAY_LENGTH; ++i) {
            int bin = rc.readSharedArray(i);
            if (bin == 0)
                break;
            Location location = Location.deserialize(bin);
            if (locationTypes.contains(location.locationType))
                result.add(location);
        }
        return result;
    }

    // returns true if updated, false if the item already exists
    static boolean update(RobotController rc, Location location) throws GameActionException {
        return Communication.update(rc, Location.serialize(location));
    }

    // returns true if successful (i.e in range), false otherwise
    static boolean tryWriteMessages(RobotController rc) throws GameActionException {
        queue.removeIf((update) -> update.time > OUTDATED_TURNS + RobotPlayer.turnCount);

        if (!rc.canWriteSharedArray(0, 0))
            return false;

        while (!queue.isEmpty()) {
            Update update = queue.remove(0);
            Communication.update(rc, update.value);
        }

        return true;
    }

    private static boolean update(RobotController rc, int locationBin) throws GameActionException {
        int i = 0, currentBin = 0;
        for (; i < ARRAY_LENGTH; ++i) {
            currentBin = rc.readSharedArray(i);
            if (currentBin == 0 || currentBin == locationBin)
                break;
        }

        if (i == ARRAY_LENGTH)
            throw new OutOfMemoryError("Cannot write " + locationBin + ": no slots left.");

        if (locationBin != currentBin) {
            if (rc.canWriteSharedArray(i, locationBin))
                rc.writeSharedArray(i, locationBin);
            else
                queue.add(new Update(RobotPlayer.turnCount, locationBin));
        }

        return locationBin == currentBin;
    }
}
