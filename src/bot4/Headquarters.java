package bot4;

import battlecode.common.*;

import bot4.util.*;
import bot4.Plan.Mission;

import java.util.Random;

public class Headquarters {
	static final Random rng = new Random();
	static final Direction[] directions = {
			Direction.NORTH,
			Direction.NORTHEAST,
			Direction.EAST,
			Direction.SOUTHEAST,
			Direction.SOUTH,
			Direction.SOUTHWEST,
			Direction.WEST,
			Direction.NORTHWEST,
	};

	static boolean init = true;
	static Team myTeam = Team.NEUTRAL;
	static void init(RobotController rc) throws GameActionException {
		if (!init) return;
		myTeam = rc.getTeam();

		init = false;
	}

	/**
	 * Responsibilities:
	 * - Choose a new mission from Planning
	 * - Execute the mission
	 * - Pass it to Communication
	 * 
	 *
	 * Future plans:
	 * - Retrieve map info from Communication
	 * - Pass it to Mapping
	 */
	static void run(RobotController rc) throws GameActionException {
		init(rc);

		// Choose mission
		Mission mission = Plan.chooseMission(rc);
		rc.setIndicatorString("T: " + mission.missionName);

		// Execute mission
		RobotType[] rt = {
				RobotType.LAUNCHER,
				RobotType.CARRIER,
				// RobotType.DESTABILIZER,
				// RobotType.BOOSTER,
				RobotType.AMPLIFIER,
		};
		int[] rn = {
				mission.numLauncher,
				mission.numCarrier,
				// mission.numDestabilizer,
				// mission.numBooster,
				mission.numAmplifier,
		};
		// assert rt.length == rn.length;
		// Communicate mission
		if (mission.isValidCollectMission()) {
			ResourceType type = mission.getCollectResourceType();
			MapLocation loc = Communication.readWell(rc, type);
			if (loc != null)
				mission.target = loc;
		}
		if (mission.target == null)
			mission.target = new MapLocation(0, 0); // dummy
		System.out.println("sending mission " + mission.missionName);
		Communication.writeMission(rc, mission);

		MapLocation cur = rc.getLocation();
		for (int i = 0; i < rt.length; ++i) {
			for (int j = 0; j < rn[i]; ++j) {
				// Pick a direction to build in.
				Direction dir = directions[rng.nextInt(directions.length)];
				MapLocation newLoc = cur.add(dir);
				if (rc.canBuildRobot(rt[i], newLoc)) {
					rc.buildRobot(rt[i], newLoc);
				}
			}
		}

		// Communicate mission
		if (mission.isValidCollectMission()) {
			ResourceType type = mission.getCollectResourceType();
			MapLocation loc = Communication.readWell(rc, type);
			mission.target = loc;
		} else if (mission.missionName == MissionName.CAPTURE_ISLAND ||
				   mission.missionName == MissionName.ATTACK_ISLAND) {
			MapLocation loc = Communication.readIsland(rc, Team.NEUTRAL);
			if (loc == null) {
				Team enemy = myTeam == Team.A ? Team.B : Team.A;
				loc = Communication.readIsland(rc, enemy);
			}
			mission.target = loc;
		} else mission.target = new MapLocation(0,0); // dummy

		if (mission.target == null) {
			System.out.println("skipping mission " + mission.missionName + " because no target specified");
		} else {
			System.out.println("sending mission " + mission.missionName);
			Communication.writeMission(rc, mission);
		}
	}

}
