package bot5a;

import java.util.ArrayList;
import java.util.Stack;

import battlecode.common.*;

import bot5a.util.*;

public class Paths {

	private static class DFS {
		final MapLocation target;

		private boolean[][] visited;
		private Direction[][] parent;

		DFS(RobotController rc, MapLocation target) {
			this.target = target;
			visited = new boolean[rc.getMapWidth()][rc.getMapHeight()];
			parent = new Direction[rc.getMapWidth()][rc.getMapHeight()];
		}

		Direction nextMove(RobotController rc) throws GameActionException {
			MapLocation current = rc.getLocation();
			if (current.x == target.x && current.y == target.y)
				return Direction.CENTER;

			int bestDistance = Integer.MAX_VALUE;
			MapLocation bestLocation = null;
			for (MapLocation m : neighbors(rc)) {
				if (!rc.canMove(current.directionTo(m))) {
					if (rc.isLocationOccupied(m))
						visited[current.x][current.y] = false;
					continue;
				}

				int dist = m.distanceSquaredTo(target);
				if (dist < bestDistance) {
					bestDistance = dist;
					bestLocation = m;
				}
			}

			if (bestLocation != null) {
				visited[bestLocation.x][bestLocation.y] = true;
				Direction move = current.directionTo(bestLocation);
				parent[bestLocation.x][bestLocation.y] = move.opposite();
				return move;
			}

			if (parent[current.x][current.y] == null)
				return null;

			return parent[current.x][current.y];
		}

		boolean visited(MapLocation m) {
			if (m.x >= 0 && m.x < visited.length && m.y >= 0 && m.y < visited[m.x].length)
				return visited[m.x][m.y];
			else
				return true;
		}

		MapLocation[] neighbors(RobotController rc) {
			MapLocation current = rc.getLocation();
			ArrayList<MapLocation> n = new ArrayList<>();
			for (Direction dir : Constants.directions)
				if (!visited(current.add(dir)))
					n.add(current.add(dir));
			return n.toArray(new MapLocation[0]);
		}
	}

	static DFS currentDFS = null;

	static Direction findMove(RobotController rc, MapLocation target) throws GameActionException {
		if (currentDFS == null || currentDFS.target.x != target.x || currentDFS.target.y != target.y)
			currentDFS = new DFS(rc, target);
		Direction move = currentDFS.nextMove(rc);
		if (move != null)
			return move;
		currentDFS = new DFS(rc, target);
		return Direction.CENTER;
	}

	/*
	 * Calculates a single step using local gravity with provided weights.
	 * (defaults in bot5a.util.Constants).
	 */
	static Direction findMove(RobotController rc, MapLocation target, int MWall, int MEnemyLauncher, int MFriend)
			throws GameActionException {
		MapLocation current = rc.getLocation();
		int dirX = target.x - current.x, dirY = target.y - current.y;

		// walls
		for (MapInfo m : rc.senseNearbyMapInfos())
			if (!m.isPassable()) {
				MapLocation wall = m.getMapLocation();
				int dx = wall.x - current.x, dy = wall.y - current.y;
				dirX += gravity(MWall, dx, dx, dy);
				dirX += gravity(MWall, dy, dx, dy);
			}

		// robots
		if (MEnemyLauncher != 0 || MFriend != 0) {
			for (RobotInfo r : rc.senseNearbyRobots()) {
				MapLocation robot = r.getLocation();
				int dx = current.x - robot.x, dy = current.y - robot.y;
				if (r.getTeam() == rc.getTeam() && r.getType() != RobotType.HEADQUARTERS) {
					dirX += gravity(MFriend, dx, dx, dy);
					dirY += gravity(MFriend, dy, dx, dy);
				} else if (r.getType() == RobotType.LAUNCHER) {
					dirX += gravity(MEnemyLauncher, dx, dx, dy);
					dirY += gravity(MEnemyLauncher, dy, dx, dy);
				}
			}
		}

		MapLocation to = rc.getLocation().translate(dirX, dirY);
		Direction move = rc.getLocation().directionTo(to);
		if (rc.canMove(move))
			return move;
		else
			return Randomize.move(rc);
	}

	private static int gravity(int mass, int s, int dx, int dy) {
		int r = (int) Math.sqrt(dx * dx + dy * dy);
		return s * mass / (r * r * r);
	}
}
