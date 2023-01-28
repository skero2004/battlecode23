package bot4;

import javax.lang.model.util.ElementScanner6;

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

		int sign = RobotPlayer.turnCount / 300;

		Direction move = Constants.directions[(int) Math.sqrt(RobotPlayer.turnCount - 300 * sign) % 8];

		if (sign % 2 == 1)
			move = Constants.directions[(int) (7 - Math.sqrt(300 - (RobotPlayer.turnCount - 300 * sign)) % 8)];
		
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
