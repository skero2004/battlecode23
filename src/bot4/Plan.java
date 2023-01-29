package bot4;

import battlecode.common.*;

import bot4.util.*;

public class Plan {

	private static final int LAUNCHER_MN = 45;
	private static final int CARRIER_AD = 50;
	private static final int AMPLIFIER_AD = 30;
	private static final int AMPLIFIER_MN = 15;

	private static boolean isCaptureIsland = false;
	private static boolean isProtectIsland = false;
	private static boolean isProtectHQ = false;
	private static boolean isAmplifier = false;
	private static boolean isAttackIsland = false;
	private static boolean isCreateAnchor = false;

	public static class Mission {

		public int startTurn = 0;
		public int numLauncher = 0;
		public int numCarrier = 0;
		public int numAmplifier = 0;
		public MissionName missionName = null;
		public MapLocation target = null;

		public Mission(MissionName m) {

			startTurn = RobotPlayer.turnCount;
			missionName = m;

			// Setup for different missions
			switch (m) {

				case PROTECT_HQ:
					numLauncher = 3;
					break;

				case PROTECT_ISLAND:
					numLauncher = 3;
					break;

				case ATTACK_HQ:
					numLauncher = 3;
					break;

				case CAPTURE_ISLAND:
					numLauncher = 2;
					numCarrier = 1;
					break;

				case AMBUSH:
					numLauncher = 3;
					break;

				case ATTACK_ISLAND:
					numLauncher = 3;

				case CREATE_ELIXIR_WELL:
					numLauncher = 3;
					numCarrier = 3;
					break;

				case UPGRADE_ADAMANTIUM_WELL:
					numLauncher = 3;
					numCarrier = 3;
					break;

				case UPGRADE_MANA_WELL:
					numLauncher = 3;
					numCarrier = 3;
					break;

				case COLLECT_ADAMANTIUM:
					numLauncher = 1;
					numCarrier = 2;
					break;

				case COLLECT_MANA:
					numLauncher = 1;
					numCarrier = 2;
					break;

				case COLLECT_ELIXIR:
					numLauncher = 2;
					numCarrier = 2;
					break;

				case SCOUTING:
					numLauncher = 2;
					numCarrier = 2;
					break;

				case SEND_AMPLIFIER:
					numAmplifier = 1;

				case CREATE_ANCHOR:
					break;

			}

		}


		boolean isValidCollectMission() {
			return this.missionName == MissionName.COLLECT_ADAMANTIUM
				|| this.missionName == MissionName.COLLECT_MANA;
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
				numEnemyLaunchers++;

		// Logic to choose mission (default is loop of scout -> adamantium -> mana)
		if (numEnemyLaunchers > 3) isProtectIsland = true;
		if (rc.getRoundNum() % 100 == 0) isAmplifier = true;
		if (rc.getRoundNum() % 150 == 0) isAttackIsland = true;
		if (rc.getRoundNum() > 500 && rc.getRoundNum() % 100 == 0 && rc.getNumAnchors(Anchor.STANDARD) < 4) isCreateAnchor = true;
		if (rc.getRoundNum() > 500 && rc.getRoundNum() % 200 == 0 && rc.getNumAnchors(Anchor.STANDARD) > 0) isCaptureIsland = true;
		// TODO: Protect island???

		// Return the correct mission. Defaults to rotation between scouting, collect adamantium, and collect mana.
		if (!isCaptureIsland && !isProtectHQ && !isProtectIsland && !isAmplifier) {
			switch (rc.getRoundNum() % 3) {
				case 0:
					return new Mission(MissionName.SCOUTING);
				case 1:
					return new Mission(MissionName.COLLECT_ADAMANTIUM);
				default:
					return new Mission(MissionName.COLLECT_MANA);
			}

		} else if (isProtectHQ) {
			return new Mission(MissionName.PROTECT_HQ);
		} else if (isProtectIsland) {
			isProtectIsland = false;
			return new Mission(MissionName.PROTECT_ISLAND);
		} else if (isCaptureIsland) {
			isCaptureIsland = false;
			return new Mission(MissionName.CAPTURE_ISLAND);
		} else if (isAttackIsland) {
			isAttackIsland = false;
			return new Mission(MissionName.ATTACK_ISLAND);
		} else if (isCreateAnchor) {
			isCreateAnchor = false;
			return new Mission(MissionName.CREATE_ANCHOR);
		} else if (isCaptureIsland) {
			isCaptureIsland = false;
			return new Mission(MissionName.CAPTURE_ISLAND);
		} else {
			isAmplifier = false;
			return new Mission(MissionName.SEND_AMPLIFIER);
		}

	}

}
