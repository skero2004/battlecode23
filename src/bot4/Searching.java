package bot4;

import java.util.Arrays;
import java.util.ArrayList;

import battlecode.common.*;

import bot4.util.*;

public class Searching {

  static final int UPDATE_FREQ = 20;
  static final int TEMPERATURE = 5;

  private static ArrayList<ArrayList<Vec2D>> map = null;

  private static WellInfo[] adamantiumCache = null;
  private static WellInfo[] manaCache = null;
  private static WellInfo[] elixirCache = null;
  private static MapInfo[] mapCache = null;
  private static RobotInfo[] robotCache = null;
  private static int[] islandCache = null;

  private static Direction currentDir = Direction.NORTH;

  static void moveTowards(RobotController rc, LocationType... targets_) throws GameActionException {
    Vec2D cur = new Vec2D(rc.getLocation());
    Vec2D dir = new Vec2D(RobotPlayer.rng.nextInt(2 * TEMPERATURE) - TEMPERATURE,
        RobotPlayer.rng.nextInt(2 * TEMPERATURE) - TEMPERATURE);

    ArrayList<LocationType> targets = new ArrayList<>(Arrays.asList(targets_));
    targets.add(LocationType.CLOUD);
    targets.add(LocationType.CURRENT);
    targets.add(LocationType.WALL);

    for (LocationType target : targets) {
      for (Vec2D to : search(rc, target)) {
        Vec2D d = to.sub(cur);
        if (d.l == 0)
          if (mass(target) > 0)
            return;
          else
            continue;
        dir = dir.add(d.scale(mass(target) / (d.l * d.l * d.l)));
      }
    }

    MapLocation to = rc.getLocation().translate(dir.x, dir.y);
    Direction move = rc.getLocation().directionTo(to);
    if (rc.canMove(move) && move != Direction.CENTER)
      rc.move(move);
    else
      RobotPlayer.moveRandom(rc);

    adamantiumCache = null;
    manaCache = null;
    elixirCache = null;
    robotCache = null;
    mapCache = null;
    islandCache = null;
  }

  private static ArrayList<Vec2D> search(RobotController rc, LocationType locationType)
      throws GameActionException {
    if (map == null) {
      map = new ArrayList<ArrayList<Vec2D>>();
      while (map.size() < LocationType.values().length)
        map.add(new ArrayList<>());
      sync(rc);
    } else if (RobotPlayer.turnCount % UPDATE_FREQ == 0)
      sync(rc);

    ArrayList<Vec2D> locations = local(rc, locationType);

    for (Vec2D v : map.get(locationType.ordinal()))
      locations.add(v);

    return locations;
  }

  private static void sync(RobotController rc) throws GameActionException {
    map = new ArrayList<ArrayList<Vec2D>>();
    while (map.size() < LocationType.values().length)
      map.add(new ArrayList<>());
    for (LocationType locationType : LocationType.values())
      if (mass(locationType) > 0)
        communicate(rc, locationType);
      else
        for (Vec2D v : local(rc, locationType))
          map.get(locationType.ordinal()).add(v);

    for (Location location : Communication.query(rc, LocationType.values()))
      map.get(location.locationType.ordinal()).add(location.coordinates);
  }

  private static void communicate(RobotController rc, LocationType locationType) throws GameActionException {
    for (Vec2D v : local(rc, locationType))
      Communication.update(rc, new Location(locationType, v));
  }

  private static ArrayList<Vec2D> local(RobotController rc, LocationType locationType)
      throws GameActionException {
    ArrayList<Vec2D> locations = new ArrayList<Vec2D>();
    switch (locationType) {
      case WELL_ADAMANTIUM:
        if (adamantiumCache == null)
          adamantiumCache = rc.senseNearbyWells(ResourceType.ADAMANTIUM);
        for (WellInfo w : adamantiumCache)
          locations.add(new Vec2D(w.getMapLocation()));
        break;
      case WELL_MANA:
        if (manaCache == null)
          manaCache = rc.senseNearbyWells(ResourceType.MANA);
        for (WellInfo w : rc.senseNearbyWells(ResourceType.MANA))
          locations.add(new Vec2D(w.getMapLocation()));
        break;
      case WELL_ELIXIR:
        if (elixirCache == null)
          elixirCache = rc.senseNearbyWells(ResourceType.ELIXIR);
        for (WellInfo w : elixirCache)
          locations.add(new Vec2D(w.getMapLocation()));
        break;

      case ISLAND_FRIENDS:
        if (islandCache == null)
          islandCache = rc.senseNearbyIslands();
        for (int i : islandCache)
          if (rc.senseTeamOccupyingIsland(i) == rc.getTeam())
            for (MapLocation l : rc.senseNearbyIslandLocations(i))
              locations.add(new Vec2D(l));
        break;
      case ISLAND_NEUTRAL:
        if (islandCache == null)
          islandCache = rc.senseNearbyIslands();
        for (int i : islandCache)
          if (rc.senseTeamOccupyingIsland(i) == Team.NEUTRAL)
            for (MapLocation l : rc.senseNearbyIslandLocations(i))
              locations.add(new Vec2D(l));
        break;
      case ISLAND_ENEMIES:
        if (islandCache == null)
          islandCache = rc.senseNearbyIslands();
        for (int i : islandCache) {
          Team islandTeam = rc.senseTeamOccupyingIsland(i);
          if (islandTeam == rc.getTeam().opponent())
            for (MapLocation l : rc.senseNearbyIslandLocations(i))
              locations.add(new Vec2D(l));
        }
        break;

      case HEADQUARTERS:
        if (robotCache == null)
          robotCache = rc.senseNearbyRobots();
        for (RobotInfo r : robotCache)
          if (r.getTeam() == rc.getTeam() && r.getType() == RobotType.HEADQUARTERS)
            locations.add(new Vec2D(r.getLocation()));
        break;

      case WALL:
        if (mapCache == null)
          mapCache = rc.senseNearbyMapInfos();
        for (MapInfo m : mapCache)
          if (!m.isPassable())
            locations.add(new Vec2D(m.getMapLocation()));
        break;
      case CLOUD:
        if (mapCache == null)
          mapCache = rc.senseNearbyMapInfos();
        for (MapInfo m : mapCache)
          if (m.hasCloud())
            locations.add(new Vec2D(m.getMapLocation()));
        break;
      case CURRENT:
        if (mapCache == null)
          mapCache = rc.senseNearbyMapInfos();
        for (MapInfo m : mapCache)
          if (m.getCurrentDirection() != Direction.CENTER)
            locations.add(new Vec2D(m.getMapLocation()));
        break;
    }

    return locations;
  }

  private static int mass(LocationType locationType) {
    switch (locationType) {
      case WELL_MANA:
        return 5000000;
      case WELL_ADAMANTIUM:
        return 4000000;
      case WELL_ELIXIR:
        return 7000000;

      case ISLAND_FRIENDS:
        return 5000000;
      case ISLAND_ENEMIES:
        return 5000000;
      case ISLAND_NEUTRAL:
        return 8000000;

      case HEADQUARTERS:
        return 9000000;

      case WALL:
        return -50000;
      case CLOUD:
        return 0;
      case CURRENT:
        return 100;
    }

    throw new IllegalArgumentException("Mass unimplemented for LocationType: " + locationType);
  }
}
