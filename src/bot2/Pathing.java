package bot2;

import battlecode.common.*;
import java.util.ArrayList;
import java.util.Set;
import bot2.Util.*;

public class Pathing {

  static final int MASS = 5000000;

  static void moveTowards(RobotController rc, LocationType type) throws GameActionException {
    ArrayList<Vec2D> locations = new ArrayList<>();
        /*
    for (Location l : Communication.getItemsByType(rc, type)) {
      locations.add(l.coordinates);
    }
        */

    switch (type) {
      case WELL_AD:
        for (WellInfo w : rc.senseNearbyWells(ResourceType.ADAMANTIUM)) {
 //         if (Communication.setItem(rc, new Well(LocationType.WELL_AD, new Vec2D(w.getMapLocation()), w.isUpgraded())))
            locations.add(new Vec2D(w.getMapLocation()));
        }
        break;
      case WELL_MN:
        for (WellInfo w : rc.senseNearbyWells(ResourceType.MANA)) {
   //       if (Communication.setItem(rc, new Well(LocationType.WELL_AD, new Vec2D(w.getMapLocation()), w.isUpgraded())))
            locations.add(new Vec2D(w.getMapLocation()));
        }
        break;
      case WELL_EX:
        for (WellInfo w : rc.senseNearbyWells(ResourceType.ELIXIR)) {
     //     if (Communication.setItem(rc, new Well(LocationType.WELL_AD, new Vec2D(w.getMapLocation()), w.isUpgraded())))
            locations.add(new Vec2D(w.getMapLocation()));
        }
        break;
      case ISLAND:
        for (int i : rc.senseNearbyIslands()) {
          for (MapLocation l : rc.senseNearbyIslandLocations(i)) {
//            if (Communication.setItem(rc, new Island(LocationType.ISLAND, new Vec2D(l), rc.senseTeamOccupyingIsland(i) == rc.getTeam(), false, false)))
              locations.add(new Vec2D(l));
            break;
          }
        }
        break;
      case HEADQUARTERS:
        for (RobotInfo r : rc.senseNearbyRobots()) {
          if (r.getTeam() == rc.getTeam() && r.getType() == RobotType.HEADQUARTERS) {
//          if (Communication.setItem(rc, new Headquarters(LocationType.HEADQUARTERS, new Vec2D(r.getLocation()))))
            locations.add(new Vec2D(r.getLocation()));
            Communication.hq = new Vec2D(r.getLocation());
            break;
          }
        }
        if (locations.size() == 0) 
            locations.add(Communication.hq);
        System.out.println("HQ location: " + locations.get(0).x + ", " + locations.get(0).y);
        break;
    }

    Vec2D current = new Vec2D(rc.getLocation());
    Vec2D dir = new Vec2D(RobotPlayer.rng.nextInt(1000) - 500, RobotPlayer.rng.nextInt(1000) - 500);

    for (MapInfo m : rc.senseNearbyMapInfos()) {
      if (m.isPassable())
        ; // locations.add(current.scale(2).sub(new Vec2D(m.getMapLocation())));
    }

    for (Vec2D v : locations) {
      Vec2D diff = v.sub(current);
      System.out.println("Calculaing gravity: " + v.x + ", " + v.y);
      int l = diff.length();
      dir = dir.add(diff.scale(MASS / (1 + l * l * l)));
    }

    // System.out.println("Tried dir: " + dir.x + ", "  + dir.y);
    MapLocation to = rc.getLocation().translate(dir.x, dir.y);
    Direction move = rc.getLocation().directionTo(to);
    if (rc.canMove(move))
      rc.move(move);
    else
      RobotPlayer.moveRandom(rc);
  }
}
