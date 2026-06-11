package domain;

public enum CarType {
    SEDAN(1, "Sedan"),
    SUV(2, "SUV"),
    TRUCK(3, "Truck");

    private final int code;
    private final String label;

    CarType(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getLabel() { return label; }

    public static CarType fromCode(int code) {
        for (CarType t : values()) {
            if (t.code == code) return t;
        }
        throw new IllegalArgumentException("Unknown CarType code: " + code);
    }
}
