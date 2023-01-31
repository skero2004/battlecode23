package bot5;

import java.util.HashMap;

import battlecode.common.*;

import bot5.util.*;

public class Scout {

	private static boolean[][] vis = new boolean[GameConstants.MAP_MAX_WIDTH][GameConstants.MAP_MAX_HEIGHT];
	private static boolean isReturn = false;
	private static HashMap<WellInfo, MapLocation> wellMemory = new HashMap<>();
	private static HashMap<Integer, MapLocation> islandLocs = new HashMap<>();
	private static HashMap<Integer, Team> islandTeams = new HashMap<>();

	static void move(RobotController rc) throws GameActionException {
		move(rc, null);
	}

	static void move(RobotController rc, RobotInfo myHq) throws GameActionException {
		int sign = Robot.turnCount / 300 + rc.getID();

		if (isReturn && myHq != null) {

			MapLocation target = myHq.location;
			Direction dir = Paths.findMove(rc, target);
			if (rc.canMove(dir))
				rc.move(dir);

		} else {

			Direction move = Constants.directions[(int) Math.sqrt(Robot.turnCount - 300 * sign) % 8];

			if (sign % 2 == 1)
				move = Constants.directions[(int) (7
						- Math.sqrt(300 - (Robot.turnCount - 300 * sign)) % 8)];

			if (rc.canMove(move))
				rc.move(move);
			else
				Randomize.move(rc);

		}
	}

	static void updateInfos(RobotController rc) throws GameActionException {

		WellInfo[] wells = rc.senseNearbyWells();
		for (WellInfo w : wells) {
			MapLocation l = w.getMapLocation();
			if (isVisited(l))
				continue;
			vis[l.x][l.y] = true;
			wellMemory.put(w, l);
			// Communication.writeWell(rc, l, w.getResourceType());
		}

		int[] islands = rc.senseNearbyIslands();
		for (int i : islands) {
			MapLocation l = rc.senseNearbyIslandLocations(i)[0];
			Team team = rc.senseTeamOccupyingIsland(i);
			islandLocs.put(i, l);
			islandTeams.put(i, team);
			// Communication.writeIsland(rc, i, l, team);
		}

		// Only write if it won't commit suicide
		RobotInfo[] enemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
		if (rc.canWriteSharedArray(0, 0) && enemies.length <= 2) {

			for (WellInfo w : wellMemory.keySet())
				Communication.writeWell(rc, wellMemory.get(w), w.getResourceType());
			wellMemory.clear();

			for (Integer i : islandTeams.keySet())
				Communication.writeIsland(rc, i, islandLocs.get(i), islandTeams.get(i));
			islandLocs.clear();
			islandTeams.clear();

			if (isReturn)
				isReturn = false;

		}

		if (wellMemory.size() + islandTeams.size() >= 2)
			isReturn = true;

	}

	private static boolean isVisited(MapLocation l) {
		return vis != null && vis[l.x][l.y];
	}

}
