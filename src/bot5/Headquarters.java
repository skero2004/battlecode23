package bot5;

import battlecode.common.*;

import bot5.util.*;
import bot5.Plan.Mission;

public class Headquarters extends Robot {

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

	void execute(RobotController rc) throws GameActionException {
		Mission mission = Plan.chooseMission(rc);

		rc.setIndicatorString("M: " + mission.missionName + " " + missionCount);
		if (!target(rc, mission)) {
			System.out.println("Failed " + mission.missionName + ": failed target");
			return;
		}

		if (!write(rc, mission)) {
			System.out.println("Failed " + mission.missionName + ": failed write");
			return;
		}

		if (!build(rc, mission)) {
			System.out.println("Failed " + mission.missionName + ": failed build");
			return;
		}

		if (!update(rc, mission)) {
			System.out.println("Failed " + mission.missionName + ": failed update");
			return;
		}

		++missionCount;
	}

	private boolean target(RobotController rc, Mission mission) throws GameActionException {
		switch (mission.missionName) {
			case COLLECT_ADAMANTIUM:
			case COLLECT_MANA:
				mission.target = Communication.readWell(rc, mission.getCollectResourceType());
				return mission.target != null;
			case CAPTURE_ISLAND:
				mission.target = Communication.readIsland(rc, Team.NEUTRAL);
				return mission.target != null;
			case ATTACK_ISLAND:
				if (Communication.readIsland(rc, rc.getTeam().opponent()) != null)
					mission.target = Communication.readIsland(rc, rc.getTeam().opponent());
				else
					mission.target = Communication.readIsland(rc, Team.NEUTRAL);
				return mission.target != null;
			case PROTECT_ISLAND:
				mission.target = Communication.readIsland(rc, rc.getTeam());
				return mission.target != null;
			case ATTACK_HQ:
				mission.target = Map.reflect(rc.getLocation(), Map.SYMMETRY);
				return mission.target != null;
			case FIND_SYMMETRY:
				mission.target = new MapLocation(61, 61);
				return true;
			default:
				mission.target = new MapLocation(Randomize.rng.nextInt(rc.getMapWidth()),
						Randomize.rng.nextInt(rc.getMapHeight()));
				return true;
		}
	}

	private boolean write(RobotController rc, Mission mission) throws GameActionException {
		switch (mission.missionName) {
			case CREATE_ANCHOR:
				return true;
			default:
				Communication.writeMission(rc, mission);
				return true;
		}
	}

	private boolean build(RobotController rc, Mission mission) throws GameActionException {
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

		if (mission.buildAll && !mission.canBuild(rc))
			return false;

		switch (mission.missionName) {
			case CREATE_ANCHOR:
				if (rc.canBuildAnchor(Anchor.STANDARD))
					rc.buildAnchor(Anchor.STANDARD);
				break;
			default:
				break;
		}

		boolean builtOne = false;
		MapLocation cur = rc.getLocation();
		for (int i = 0; i < rt.length; ++i) {
			for (int j = 0; j < rn[i]; ++j) {
				// Pick a direction to build in.
				for (Direction dir : Constants.directions) {
					MapLocation newLoc = cur.add(dir);
					if (rc.canBuildRobot(rt[i], newLoc)) {
						rc.buildRobot(rt[i], newLoc);
						builtOne = true;
						break;
					}
				}
			}
		}

		return !mission.buildOne || builtOne;
	}

	private boolean update(RobotController rc, Mission mission) throws GameActionException {
		Plan.isMissionActive[mission.missionName.ordinal()] = false;
		return true;
	}

}
