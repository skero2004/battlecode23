package bot5;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;

import battlecode.common.*;

import bot5.util.*;
import bot5.Plan.Mission;

public class Communication {
	private static final int ARRAY_LENGTH = 64;
	private static final int OUTDATED_TURNS = 2000;

	private static class Segment {
		private final int start;
		final int length;

		private static class Update {
			final int t, i, v;

			Update(int i, int v, int t) {
				this.i = i;
				this.v = v;
				this.t = t;
			}
		};

		Queue<Update> queue = new ArrayDeque<Update>();

		Segment(int s, int e) {
			assert s >= 0 && e <= ARRAY_LENGTH;
			start = s;
			length = e - s;
		}

		int read(RobotController rc) throws GameActionException {
			for (int i = 0; i < length; ++i) {
				int index = Randomize.rng.nextInt(length);
				int value = read(rc, index);
				if (value != 0)
					return value;
			}
			return 0;
		}

		int read(RobotController rc, int index) throws GameActionException {
			assert 0 <= index && index < length;
			return rc.readSharedArray(start + index);
		}

		void write(RobotController rc, int value) throws GameActionException {
			for (int i = 0; i < length; ++i)
				if (read(rc, i) == 0) {
					write(rc, i, value);
					return;
				}
			int index = Randomize.rng.nextInt(length);
			write(rc, index, value);
		}

		void write(RobotController rc, int index, int value) throws GameActionException {
			assert 0 <= index && index < length;
			queue.add(new Update(index, value, Robot.turnCount));
			flush(rc);
		}

		boolean flush(RobotController rc) throws GameActionException {
			while (!queue.isEmpty()) {
				if (rc.canWriteSharedArray(0, 0)) {
					Update u = queue.remove();
					if (u.t + OUTDATED_TURNS >= Robot.turnCount)
						rc.writeSharedArray(start + u.i, u.v);
				} else {
					return false;
				}
			}
			return true;
		}
	}

	static Segment missions = new Segment(0, 1); // mission type, mission target
	static Segment adamantium = new Segment(1, 13); // well location
	static Segment mana = new Segment(13, 25); // well location
	static Segment islands = new Segment(25, 64); // island location and type

	static void writeMission(RobotController rc, Mission mission) throws GameActionException {
		int value = mission.missionName.ordinal() + (mission.target.x << 4) + (mission.target.y << 10);
		missions.write(rc, 0, 1 + value);
	}

	static Mission readMission(RobotController rc) throws GameActionException {
		int value = missions.read(rc, 0) - 1;
		if (value < 0)
			return null;
		MissionName name = MissionName.values()[value & 15];
		int x = (value >> 4) & 63, y = (value >> 10) & 63;
		MapLocation target = new MapLocation(x, y);
		Mission m = new Mission(name);
		m.target = target;
		return m;
	}

	static void writeWell(RobotController rc, MapLocation location, ResourceType type) throws GameActionException {
		Segment seg = null;
		switch (type) {
			case ADAMANTIUM:
				seg = adamantium;
				break;
			case MANA:
				seg = mana;
				break;
			default:
				throw new IllegalArgumentException("Cannot write info for well of type " + type);
		}
		seg.write(rc, 1 + location.x + (location.y << 6));
	}

	static MapLocation readWell(RobotController rc, ResourceType type) throws GameActionException {
		Segment seg = null;
		switch (type) {
			case ADAMANTIUM:
				seg = adamantium;
				break;
			case MANA:
				seg = mana;
				break;
			default:
				throw new IllegalArgumentException("Cannot write info for well of type " + type);
		}

		ArrayList<MapLocation> options = new ArrayList<>();
		for (int i = 0; i < seg.length; ++i) {
			int value = seg.read(rc, i) - 1;
			if (value < 0)
				continue;
			int x = value & 63, y = (value >> 6) & 63;
			options.add(new MapLocation(x, y));
		}
		return choice(rc, options.toArray(new MapLocation[0]));
	}

	static void writeIsland(RobotController rc, int index, MapLocation location, Team team)
			throws GameActionException {
		islands.write(rc, index, 1 + location.x + (location.y << 6) + (team.ordinal() << 12));
	}

	static MapLocation readIsland(RobotController rc, Team team) throws GameActionException {
		ArrayList<MapLocation> options = new ArrayList<>();
		for (int i = 0; i < islands.length; ++i) {
			int value = islands.read(rc, i) - 1;
			if (value < 0)
				continue;
			int x = value & 63, y = (value >> 6) & 63;
			if (team.ordinal() == (value >> 12))
				options.add(new MapLocation(x, y));

		}

		return choice(rc, options.toArray(new MapLocation[0]));
	}

	static boolean flush(RobotController rc) throws GameActionException {
		for (Segment seg : new Segment[] { missions, adamantium, mana, islands }) {
			if (!seg.flush(rc))
				return false;
		}
		return true;
	}

	private static MapLocation choice(RobotController rc, MapLocation... locations) {
		if (locations.length == 0)
			return null;
		MapLocation curr = rc.getLocation();
		Arrays.sort(locations, (MapLocation a,
				MapLocation b) -> curr.distanceSquaredTo(a) < curr.distanceSquaredTo(b) ? -1 : 1);

		double mx = 0;
		for (MapLocation m : locations)
			mx += 1.0 / (0.01 + curr.distanceSquaredTo(m));

		double p = Randomize.rng.nextDouble() * mx;

		for (MapLocation m : locations) {
			p -= 1.0 / (0.01 + curr.distanceSquaredTo(m));
			if (p <= 0)
				return m;
		}
		return null;
	}
}
