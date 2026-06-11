package domain;

public enum SteeringSystem {
    BOSCH(1, "BOSCH", "Bosch"),
    MOBIS(2, "MOBIS", "Mobis");

    private final int code;
    private final String label;        // 선택 메시지용 (대문자)
    private final String displayLabel; // 주행 출력용 (혼합대문자)

    SteeringSystem(int code, String label, String displayLabel) {
        this.code = code;
        this.label = label;
        this.displayLabel = displayLabel;
    }

    public String getLabel() { return label; }
    public String getDisplayLabel() { return displayLabel; }

    public static SteeringSystem fromCode(int code) {
        for (SteeringSystem s : values()) {
            if (s.code == code) return s;
        }
        throw new IllegalArgumentException("Unknown SteeringSystem code: " + code);
    }
}
