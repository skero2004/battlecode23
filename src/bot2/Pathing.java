package bot2;

import battlecode.common.*;
import java.util.ArrayList;
import java.util.Set;
import bot2.Util.*;

public class Pathing {

  static final int MASS = 5000000;

  static void moveTowards(RobotController rc, LocationType type) throws GameActionException {
    ArrayList<Vec2D> locations = new ArrayList<>();
    for (Location l : Communication.getItemsByType(rc, type)) {
      locations.add(l.coordinates);
    }

    switch (type) {
      case WELL_AD:
        for (WellInfo w : rc.senseNearbyWells(ResourceType.ADAMANTIUM)) {
          if (Communication.setItem(rc, new Well()))
            locations.add(new Vec2D(w.getMapLocation()));
        }
        break;
      case WELL_MN:
        for (WellInfo w : rc.senseNearbyWells(ResourceType.MANA)) {
          if (Communication.setItem(rc, new Well()))
            locations.add(new Vec2D(w.getMapLocation()));
        }
        break;
      case WELL_EX:
        for (WellInfo w : rc.senseNearbyWells(ResourceType.ELIXIR)) {
          if (Communication.setItem(rc, new Well()))
            locations.add(new Vec2D(w.getMapLocation()));
        }
        break;
      case ISLAND:
        for (int i : rc.senseNearbyIslands()) {
          for (MapLocation l : rc.senseNearbyIslandLocations(i)) {
            if (Communication.setItem(rc, new Island()))
              locations.add(new Vec2D(l));
            break;
          }
        }
        break;
      case HEADQUARTERS:
        for (RobotInfo r : rc.senseNearbyRobots()) {
          if (r.getTeam() == rc.getTeam() && r.getType() == RobotType.HEADQUARTERS) {
            locations.add(new Vec2D(r.getLocation()));
          }
        }
        break;
    }

    Vec2D current = new Vec2D(rc.getLocation());
    Vec2D dir = new Vec2D(RobotPlayer.rng.nextInt, 0);

    for (MapInfo m : rc.senseNearbyMapInfos()) {
      if (m.isPassable())
        locations.add(current.scale(2).sub(new Vec2D(m.getMapLocation())));
    }

    for (Vec2D v : locations) {
      Vec2D diff = v.sub(current);
      int l = diff.length();
      dir = dir.add(diff.scale(MASS / (l * l * l)));
    }

    rc.move(rc.getLocation().directionTo(rc.getLocation().translate(dir.x, dir.y)));
  }
}
