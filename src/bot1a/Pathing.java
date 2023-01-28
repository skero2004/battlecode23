package bot1a;

import battlecode.common.*;
import java.util.ArrayList;

public class Pathing {

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
			if (m.x < visited.length && m.y < visited[m.x].length)
				return visited[m.x][m.y];
			else
				return true;
		}

		MapLocation[] neighbors(RobotController rc) {
			MapLocation current = rc.getLocation();
			ArrayList<MapLocation> n = new ArrayList<>();
			for (Direction dir : RobotPlayer.directions)
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

	static void moveTowards(RobotController rc, MapLocation target) throws GameActionException {
		Direction move = findMove(rc, target);
		if (rc.canMove(move))
			rc.move(move);
	}
}
