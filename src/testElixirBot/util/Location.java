package testElixirBot.util;

public class Location {
    public final LocationType locationType;
    public final Vec2D coordinates;

    private static final LocationType[] locationTypes = LocationType.values();

    public Location(LocationType locationType, Vec2D coordinates) {
        this.locationType = locationType;
        this.coordinates = coordinates;
    }

    public static int serialize(Location location) {
        // 0 means empty, so add 1
        return 1 + (location.coordinates.x | location.coordinates.y << 6 | location.locationType.ordinal() << 12);
    }

    public static Location deserialize(int bin) {
        --bin;

        if (bin < 0 || bin >= (1 << 16))
            throw new IllegalArgumentException("Could not deserialize `" + bin + "`: out of bounds.");
        if ((bin >> 12) >= locationTypes.length)
            throw new IllegalArgumentException("Could not deserialize `" + bin + "`: no such locationType.");

        int x = (bin >> 0) & ((1 << 6) - 1);
        int y = (bin >> 6) & ((1 << 6) - 1);
        LocationType locationType = locationTypes[bin >> 12];

        return new Location(locationType, new Vec2D(x, y));
    }

    public boolean equals(Location location) {
        return coordinates.equals(location.coordinates) && locationType == location.locationType;
    }
}
