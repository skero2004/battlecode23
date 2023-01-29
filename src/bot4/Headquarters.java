package bot4;

import battlecode.common.*;

import bot4.util.*;
import bot4.Plan.Mission;

public class Headquarters {

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
	static int missionCount = 0;

	static void run(RobotController rc) throws GameActionException {
		Mission mission = Plan.chooseMission(rc);

		rc.setIndicatorString("M: " + mission.missionName);
		if (!target(rc, mission)) {
			System.out.println("Skipped " + mission.missionName + ": failed target");
			return;
		}

		if (!write(rc, mission)) {
			System.out.println("Skipped " + mission.missionName + ": failed write");
			return;
		}

		if (!build(rc, mission)) {
			System.out.println("Skipped " + mission.missionName + ": failed build");
			return;
		}

		++missionCount;
	}

	private static boolean target(RobotController rc, Mission mission) throws GameActionException {
		switch (mission.missionName) {
			case COLLECT_ADAMANTIUM:
			case COLLECT_MANA:
				mission.target = Communication.readWell(rc, mission.getCollectResourceType());
				return mission.target != null;
			case CAPTURE_ISLAND:
				mission.target = Communication.readIsland(rc, Team.NEUTRAL);
				return mission.target != null;
			case ATTACK_ISLAND:
				mission.target = Communication.readIsland(rc, rc.getTeam().opponent());
				return mission.target != null;
			default:
				mission.target = new MapLocation(Randomize.rng.nextInt(rc.getMapWidth()),
						Randomize.rng.nextInt(rc.getMapHeight()));
				return true;
		}
	}

	private static boolean write(RobotController rc, Mission mission) throws GameActionException {
		switch (mission.missionName) {
			case CREATE_ANCHOR:
				return true;
			default:
				Communication.writeMission(rc, mission);
				return true;
		}
	}

	private static boolean build(RobotController rc, Mission mission) throws GameActionException {
		RobotType[] rt = {
				RobotType.LAUNCHER,
				RobotType.CARRIER,
				// RobotType.DESTABILIZER,
				// RobotType.BOOSTER,
				//RobotType.AMPLIFIER,
		};

		int[] rn = {
				mission.numLauncher,
				mission.numCarrier,
				// mission.numDestabilizer,
				// mission.numBooster,
				//mission.numAmplifier,
		};

		switch (mission.missionName) {
			case CREATE_ANCHOR:
				if (rc.canBuildAnchor(Anchor.STANDARD)) {
					Plan.isCreateAnchor = false;
					rc.buildAnchor(Anchor.STANDARD);
				} else {
					return false;
				}
				break;
			default:
				break;
		}

		MapLocation cur = rc.getLocation();
		for (int i = 0; i < rt.length; ++i) {
			for (int j = 0; j < rn[i]; ++j) {
				// Pick a direction to build in.
				Direction dir = Constants.directions[Randomize.rng.nextInt(Constants.directions.length)];
				MapLocation newLoc = cur.add(dir);
				if (rc.canBuildRobot(rt[i], newLoc)) {
					rc.buildRobot(rt[i], newLoc);
				}
			}
		}

		return true;
	}

}
