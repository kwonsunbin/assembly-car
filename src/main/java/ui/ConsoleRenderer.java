package ui;

import domain.CarSpec;

public class ConsoleRenderer {
    private static final String CLEAR_SCREEN = "\033[H\033[2J";

    // ── 화면 초기화 ────────────────────────────────────────────────────────────
    public static void clearScreen() {
        System.out.print(CLEAR_SCREEN);
        System.out.flush();
    }

    // ── 메뉴 출력 ──────────────────────────────────────────────────────────────
    public static void showCarTypeMenu() {
        System.out.println("        ______________");
        System.out.println("       /|            |");
        System.out.println("  ____/_|_____________|____");
        System.out.println(" |                      O  |");
        System.out.println(" '-(@)----------------(@)--'");
        System.out.println("===============================");
        System.out.println("어떤 차량 타입을 선택할까요?");
        System.out.println("1. Sedan");
        System.out.println("2. SUV");
        System.out.println("3. Truck");
        System.out.println("===============================");
    }

    public static void showEngineMenu() {
        System.out.println("어떤 엔진을 탑재할까요?");
        System.out.println("0. 뒤로가기");
        System.out.println("1. GM");
        System.out.println("2. TOYOTA");
        System.out.println("3. WIA");
        System.out.println("4. 고장난 엔진");
        System.out.println("===============================");
    }

    public static void showBrakeMenu() {
        System.out.println("어떤 제동장치를 선택할까요?");
        System.out.println("0. 뒤로가기");
        System.out.println("1. MANDO");
        System.out.println("2. CONTINENTAL");
        System.out.println("3. BOSCH");
        System.out.println("===============================");
    }

    public static void showSteeringMenu() {
        System.out.println("어떤 조향장치를 선택할까요?");
        System.out.println("0. 뒤로가기");
        System.out.println("1. BOSCH");
        System.out.println("2. MOBIS");
        System.out.println("===============================");
    }

    public static void showRunTestMenu() {
        System.out.println("멋진 차량이 완성되었습니다.");
        System.out.println("어떤 동작을 할까요?");
        System.out.println("0. 처음 화면으로 돌아가기");
        System.out.println("1. RUN");
        System.out.println("2. Test");
        System.out.println("===============================");
    }

    // ── 입력/오류 출력 ─────────────────────────────────────────────────────────
    public static void showInputPrompt() {
        System.out.print("INPUT > ");
    }

    public static void showInvalidNumberError() {
        System.out.println("ERROR :: 숫자만 입력 가능");
    }

    public static void showRangeError(String message) {
        System.out.println(message);
    }

    public static void showGoodbye() {
        System.out.println("바이바이");
    }

    // ── 선택 확인 출력 ─────────────────────────────────────────────────────────
    public static void showCarTypeSelected(String label) {
        System.out.printf("차량 타입으로 %s을 선택하셨습니다.\n", label);
    }

    public static void showEngineSelected(String label) {
        System.out.printf("%s 엔진을 선택하셨습니다.\n", label);
    }

    public static void showBrakeSystemSelected(String label) {
        System.out.printf("%s 제동장치를 선택하셨습니다.\n", label);
    }

    public static void showSteeringSystemSelected(String label) {
        System.out.printf("%s 조향장치를 선택하셨습니다.\n", label);
    }

    // ── 주행/테스트 결과 출력 ──────────────────────────────────────────────────
    public static void showCannotRun() {
        System.out.println("자동차가 동작되지 않습니다");
    }

    public static void showBrokenEngine() {
        System.out.println("엔진이 고장나있습니다.");
        System.out.println("자동차가 움직이지 않습니다.");
    }

    public static void showCarRunning(CarSpec spec) {
        System.out.printf("Car Type : %s\n", spec.getCarType().getLabel());
        System.out.printf("Engine   : %s\n", spec.getEngine().getLabel());
        System.out.printf("Brake    : %s\n", spec.getBrakeSystem().getDisplayLabel());
        System.out.printf("Steering : %s\n", spec.getSteeringSystem().getDisplayLabel());
        System.out.println("자동차가 동작됩니다.");
    }

    public static void showTestStarting() {
        System.out.println("Test...");
    }

    public static void showTestPass() {
        System.out.println("자동차 부품 조합 테스트 결과 : PASS");
    }

    public static void showTestFail(String reason) {
        System.out.println("자동차 부품 조합 테스트 결과 : FAIL");
        System.out.println(reason);
    }
}
