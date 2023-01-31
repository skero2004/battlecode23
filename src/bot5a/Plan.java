package bot5a;

import battlecode.common.*;
import bot5a.util.*;

public class Plan {

	private static final int LAUNCHER_MN = 45;
	private static final int CARRIER_AD = 50;
	private static final int AMPLIFIER_AD = 30;
	private static final int AMPLIFIER_MN = 15;

	// These change to true in this file, but false in HQ (once mission is
	// completed)
	static boolean[] isMissionActive = new boolean[MissionName.values().length];

	static class Mission {

		public int startTurn = 0;
		public int numLauncher = 0;
		public int numCarrier = 0;
		public int numAmplifier = 0;
		public boolean buildAll = false;
		public MissionName missionName = null;
		public MapLocation target = null;

		public Mission(MissionName m) {

			startTurn = Robot.turnCount;
			missionName = m;

			// Setup for different missions
			switch (m) {

				case PROTECT_HQ:
					buildAll = true;
					numLauncher = 4;
					break;

				case PROTECT_ISLAND:
					numLauncher = 3;
					break;

				case ATTACK_HQ:
					numLauncher = 3;
					break;

				case CAPTURE_ISLAND:
					numLauncher = 3;
					numCarrier = 1;
					break;

				case ATTACK_ISLAND:
					numLauncher = 3;
					break;

				case COLLECT_ADAMANTIUM:
					numLauncher = 1;
					numCarrier = 2;
					break;

				case COLLECT_MANA:
					numLauncher = 1;
					numCarrier = 2;
					break;

				case SCOUTING:
					numCarrier = 1;
					numLauncher = 1;
					break;

				case SEND_AMPLIFIER:
					numAmplifier = 1;
					break;

				case CREATE_ANCHOR:
					break;

				case FIND_SYMMETRY:
					numAmplifier = 1;

			}

		}

		ResourceType getCollectResourceType() {
			switch (this.missionName) {
				case COLLECT_ADAMANTIUM:
					return ResourceType.ADAMANTIUM;
				case COLLECT_MANA:
					return ResourceType.MANA;
				default:
					throw new IllegalArgumentException("Mission not implemented!");
			}
		}

		boolean canBuild(RobotController rc) {

			RobotType[] rt = {
					RobotType.LAUNCHER,
					RobotType.CARRIER,
					// RobotType.DESTABILIZER,
					// RobotType.BOOSTER,
					RobotType.AMPLIFIER,
			};

			int[] rn = {
					numLauncher,
					numCarrier,
					// mission.numDestabilizer,
					// mission.numBooster,
					numAmplifier,
			};

			int[] mn = {
					LAUNCHER_MN,
					0,
					// ?,
					// ?,
					AMPLIFIER_MN,
			};

			int[] ad = {
					0,
					CARRIER_AD,
					// ?,
					// ?,
					AMPLIFIER_AD,
			};

			int totalAd = 0, totalMn = 0;
			for (int i = 0; i < rt.length; ++i) {
				for (int j = 0; j < rn[i]; ++j) {
					totalAd += ad[i];
					totalMn += mn[i];
				}
			}

			if (missionName == MissionName.CREATE_ANCHOR) {
				totalAd += 80;
				totalMn += 80;
			}

			return rc.getResourceAmount(ResourceType.MANA) >= totalMn
					&& rc.getResourceAmount(ResourceType.ADAMANTIUM) > totalAd;
		}
	}

	public static Mission chooseMission(RobotController rc) throws GameActionException {
		Team OPPONENT = rc.getTeam().opponent();

		if (rc.getType() != RobotType.HEADQUARTERS)
			throw new IllegalArgumentException("RobotType must be Headquarters to choose a mission!");

		// Find number of enemy launchers near HQ
		RobotInfo[] enemies = rc.senseNearbyRobots(-1, OPPONENT);
		int numEnemyLaunchers = 0;
		for (RobotInfo enemy : enemies)
			if (enemy.getType() == RobotType.LAUNCHER)

				// Logic to choose mission (default is loop of scout -> adamantium -> mana)
				if (enemies.length >= 2)
					isMissionActive[MissionName.PROTECT_HQ.ordinal()] = true;

		if (Headquarters.missionCount >= 100
				&& Communication.readIsland(rc, rc.getTeam().opponent()) != null
				&& Headquarters.missionCount % 5 == 0)
			isMissionActive[MissionName.ATTACK_ISLAND.ordinal()] = true;

		if (Headquarters.missionCount >= 100
				&& Headquarters.missionCount % 4 == 0
				&& Communication.readIsland(rc, rc.getTeam()) != null)
			isMissionActive[MissionName.PROTECT_ISLAND.ordinal()] = true;

		if ((rc.getRobotCount() >= 30 || Headquarters.missionCount >= 100)
				&& (Headquarters.missionCount % 43 == 0 || rc.canBuildAnchor(Anchor.STANDARD))
				&& rc.getNumAnchors(Anchor.STANDARD) < 4)
			isMissionActive[MissionName.CREATE_ANCHOR.ordinal()] = true;

		if (rc.getNumAnchors(Anchor.STANDARD) > 0
				&& Communication.readIsland(rc, Team.NEUTRAL) != null)
			isMissionActive[MissionName.CAPTURE_ISLAND.ordinal()] = true;

		if (Headquarters.missionCount >= 50
				&& Headquarters.missionCount % 31 == 0)
			isMissionActive[MissionName.SEND_AMPLIFIER.ordinal()] = true;

		// Return the correct mission. Defaults to rotation between scouting, collect
		// adamantium, and collect mana.
		for (MissionName m : MissionName.values()) {
			if (isMissionActive[m.ordinal()])
				return new Mission(m);
		}

		int mod = 5;
		if (Headquarters.missionCount % mod == 0
				&& Communication.readWell(rc, ResourceType.ADAMANTIUM) != null)
			return new Mission(MissionName.COLLECT_ADAMANTIUM);
		else if (Headquarters.missionCount % mod == 1
				&& Communication.readWell(rc, ResourceType.MANA) != null)
			return new Mission(MissionName.COLLECT_MANA);
		else {

			Mission m = new Mission(MissionName.SCOUTING);
			if (Communication.readWell(rc, ResourceType.ADAMANTIUM) != null
					&& Communication.readWell(rc, ResourceType.MANA) != null)
				m.numCarrier = 0;

			return new Mission(MissionName.SCOUTING);

		}

	}

}
