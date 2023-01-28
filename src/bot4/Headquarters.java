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

	/**
	 * Responsibilities:
	 * - Choose a new mission from Planning
	 * - Execute the mission 
	 * - Pass it to Communication 
	 * 
	 *
	 * Future plans:
	 * - Retrieve map info from Communication 
	 * - Pass it to Mapping */
	static void run(RobotController rc) throws GameActionException {
		// Choose mission
		Mission mission = Plan.chooseMission(rc);

		// Execute mission
		RobotType[] rt = {
			RobotType.LAUNCHER,
			RobotType.CARRIER,
			//RobotType.DESTABILIZER,
			//RobotType.BOOSTER,
			//RobotType.AMPLIFIER,
		};
		int[] rn = {
			mission.numLauncher,
			mission.numCarrier,
			//mission.numDestabilizer,
			//mission.numBooster,
			//mission.numAmplifier,
		};
		//assert rt.length == rn.length;

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
			//mission.target = loc;
			mission.target = new MapLocation(22,27);
			Communication.writeMission(rc, mission);
		}

		//// TODO how are we planning to distinguish new missions?
		//Mission m2 = Communication.readMission(); 
		//Mapping.addMission(m2);
	}

}
