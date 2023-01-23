package bot4;

import battlecode.common.*;

import bot4.util.*;

public class Plan {

    public class Mission {

        public int startTurn = 0;
        public int numLauncher = 0;
        public int numCarrier = 0;
        public int numDestabilizer = 0;
        public int numBooster = 0;
        public int numAmplifier = 0;

        public Mission(MissionName m) { this(false, m); }
        public Mission(boolean isAdv, MissionName m) {

            // Setup for different missions
            switch(m) {

                case START_UP:
                    numLauncher = 2;
                    numCarrier = 2;
                    break;

                case PROTECT_HQ:
                    numLauncher = 4;
                    numDestabilizer = isAdv ? 1 : 0;
                    numBooster = isAdv ? 1 : 0;
                    break;

                case PROTECT_ISLAND:
                    numLauncher = 4;
                    numDestabilizer = isAdv ? 1 : 0;
                    numBooster = isAdv ? 1 : 0;
                    break;

                case ATTACK_HQ:
                    numLauncher = 5;
                    numDestabilizer = isAdv ? 1 : 0;
                    numBooster = isAdv ? 1 : 0;
                    break;

                case CAPTURE_ISLAND:
                    numLauncher = 5;
                    numDestabilizer = isAdv ? 1 : 0;
                    numBooster = isAdv ? 1 : 0;
                    break;

                case AMBUSH:
                    numLauncher = 3;
                    numDestabilizer = isAdv ? 1 : 0;
                    break;

                case CREATE_ELIXIR_WELL:
                    numLauncher = 3;
                    numCarrier = 3;
                    numDestabilizer = isAdv ? 1 : 0;
                    break;

                case SPEED_UP_HQ:
                    numBooster = 1;
                    break;

                case UPGRADE_ADAMANTIUM_WELL:
                    numLauncher = 3;
                    numCarrier = 3;
                    numDestabilizer = isAdv ? 1 : 0;
                    break;

                case UPGRADE_MANA_WELL:
                    numLauncher = 3;
                    numCarrier = 3;
                    numDestabilizer = isAdv ? 1 : 0;
                    break;

                case COLLECT_ADAMANTIUM:
                    numLauncher = 2;
                    numCarrier = 2;
                    break;

                case COLLECT_MANA:
                    numLauncher = 2;
                    numCarrier = 2;
                    break;

                case COLLECT_ELIXIR:
                    numLauncher = 2;
                    numCarrier = 2;
                    break;

                case SCOUTING:
                    numCarrier = 2;
                    numAmplifier = 1;
                    break;

            }

        }

    }

    public MissionName chooseMission(RobotController rc) {

        Team OPPONENT = rc.getTeam().opponent();

        if (rc.getType() != RobotType.HEADQUARTERS)
            throw new IllegalArgumentException("RobotType must be Headquarters to choose a mission!");

        // Information from HQ
        int amountMn = rc.getResourceAmount(ResourceType.MANA);
        int amountAd = rc.getResourceAmount(ResourceType.ADAMANTIUM);
        int amountEx = rc.getResourceAmount(ResourceType.ELIXIR);

        return MissionName.COLLECT_ADAMANTIUM;

    }

}
