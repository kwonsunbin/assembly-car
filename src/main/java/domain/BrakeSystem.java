package domain;

public enum BrakeSystem {
    MANDO(1, "MANDO", "Mando"),
    CONTINENTAL(2, "CONTINENTAL", "Continental"),
    BOSCH(3, "BOSCH", "Bosch");

    private final int code;
    private final String label;        // 선택 메시지용 (대문자)
    private final String displayLabel; // 주행 출력용 (혼합대문자)

    BrakeSystem(int code, String label, String displayLabel) {
        this.code = code;
        this.label = label;
        this.displayLabel = displayLabel;
    }

    public String getLabel() { return label; }
    public String getDisplayLabel() { return displayLabel; }

    public static BrakeSystem fromCode(int code) {
        for (BrakeSystem b : values()) {
            if (b.code == code) return b;
        }
        throw new IllegalArgumentException("Unknown BrakeSystem code: " + code);
    }
}
