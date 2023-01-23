package bot4.util;

public enum MissionName {

    START_UP(0),

    PROTECT_HQ(1),
    ATTACK_HQ(2),
    PROTECT_ISLAND(3),
    CAPTURE_ISLAND(4),
    AMBUSH(5),
    CREATE_ELIXIR_WELL(6),
    UPGRADE_ADAMANTIUM_WELL(7),
    UPGRADE_MANA_WELL(8),
    COLLECT_ADAMANTIUM(9),
    COLLECT_MANA(10),
    COLLECT_ELIXIR(11),

    SPEED_UP_HQ(12),
    SCOUTING(13);

    public final int id;

    private MissionName(int id) {
        this.id = id;
    }

}
