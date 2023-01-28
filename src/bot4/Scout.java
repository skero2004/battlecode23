package bot4;

import battlecode.common.*;

import bot4.util.*;

public class Scout {
	private static MapLocation prev = null;
	private static boolean[][] vis = null;

	static void move(RobotController rc) throws GameActionException {
		if (prev == null)
			prev = rc.getLocation();

		if (vis == null)
			vis = new boolean[rc.getMapWidth()][rc.getMapHeight()];

		Direction move = Constants.directions[(int) Math.sqrt(RobotPlayer.turnCount) % 8];
		if (rc.canMove(move))
			rc.move(move);
		else
			Randomize.move(rc);
	}

	static void updateInfos(RobotController rc) throws GameActionException {
		if (prev == null)
			prev = rc.getLocation();
		for (WellInfo w : rc.senseNearbyWells()) {
			MapLocation l = w.getMapLocation();
			if (isVisited(l))
				continue;
			visit(l);
			// TODO: Communication.writeScout();
			prev = w.getMapLocation();
		}
	}

	private static boolean isVisited(MapLocation l) {
		return vis != null && vis[l.x][l.y];
	}

	private static void visit(MapLocation l) {
		vis[l.x][l.y] = true;
	}
}
