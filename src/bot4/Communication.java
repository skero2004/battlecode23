package bot4;

import java.util.ArrayDeque;
import java.util.Queue;

import battlecode.common.*;

import bot4.util.*;
import bot4.Plan.Mission;

public class Communication {
	private static final int ARRAY_LENGTH = 64;
	private static final int OUTDATED_TURNS = 2000;

	private static class Segment {
		private final int start, end;

		private static class Update {
			int i, v;

			Update(int i, int v) {
				this.i = i;
				this.v = v;
			}
		};

		Queue<Update> queue = new ArrayDeque<Update>();

		Segment(int s, int e) {
			start = s;
			end = e;
		}

		int read(RobotController rc) throws GameActionException {
			for (int i = 0; i < end - start; ++i) {
				int v = read(rc, -1);
				if (v > 0)
					return v;
			}
			return 0;
		}

		int read(RobotController rc, int index) throws GameActionException {
			if (0 > index || index >= end - start)
				index = Math.abs(Randomize.rng.nextInt()) % (end - start);
			return rc.readSharedArray(start + index);
		}

		void write(RobotController rc, int index, int value) throws GameActionException {
			if (0 > index || index >= end - start)
				index = Math.abs(Randomize.rng.nextInt()) % (end - start);

			queue.add(new Update(start + index, value));

			flush(rc);
		}

		void write(RobotController rc, int value) throws GameActionException {
			write(rc, -1, value);
		}

		void flush(RobotController rc) throws GameActionException {
			while (!queue.isEmpty()) {
				Update u = queue.remove();
				if (rc.canWriteSharedArray(0, 0))
					rc.writeSharedArray(u.i, u.v);
			}
		}
	}

	static Segment missions = new Segment(0, 1); // mission type, mission target
	static Segment adamantium = new Segment(1, 4); // well location
	static Segment mana = new Segment(4, 7); // well location
	static Segment islands = new Segment(7, 42);

	static void writeMission(RobotController rc, Mission mission) throws GameActionException {
		int value = mission.missionName.ordinal() + (mission.target.x << 4) + (mission.target.y << 10);
		missions.write(rc, 0, value);
	}

	static Mission readMission(RobotController rc) throws GameActionException {
		int value = missions.read(rc, 0);
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
		seg.write(rc, location.x + (location.y << 6));
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

		int value = seg.read(rc);
		int x = value & 63, y = value >> 6;
		return new MapLocation(x, y);
	}

	static void writeIsland(RobotController rc, int index, MapLocation location, Team team)
			throws GameActionException {
		islands.write(rc, index, location.x + (location.y << 6) + (team.ordinal() << 12));
	}

	static MapLocation readIsland(RobotController rc, Team team) throws GameActionException {
		throw new UnsupportedOperationException("readIsland is unimplemented");
	}
}
