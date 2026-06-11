import domain.BrakeSystem;
import domain.CarSpec;
import domain.CarType;
import domain.CompatibilityValidator;
import domain.Engine;
import domain.SteeringSystem;
import ui.ConsoleRenderer;

import java.util.Scanner;

public class Assemble {
    private static final int CarType_Q        = 0;
    private static final int Engine_Q         = 1;
    private static final int BrakeSystem_Q    = 2;
    private static final int SteeringSystem_Q = 3;
    private static final int Run_Test         = 4;

    private static final CompatibilityValidator VALIDATOR = new CompatibilityValidator();
    private static CarSpec spec = new CarSpec();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int step = CarType_Q;

        while (true) {
            ConsoleRenderer.clearScreen();

            switch (step) {
                case CarType_Q:        ConsoleRenderer.showCarTypeMenu();   break;
                case Engine_Q:         ConsoleRenderer.showEngineMenu();    break;
                case BrakeSystem_Q:    ConsoleRenderer.showBrakeMenu();     break;
                case SteeringSystem_Q: ConsoleRenderer.showSteeringMenu();  break;
                case Run_Test:         ConsoleRenderer.showRunTestMenu();   break;
            }

            ConsoleRenderer.showInputPrompt();
            String buf = sc.nextLine().trim();

            if (buf.equalsIgnoreCase("exit")) {
                ConsoleRenderer.showGoodbye();
                break;
            }

            int answer;
            try {
                answer = Integer.parseInt(buf);
            } catch (NumberFormatException e) {
                ConsoleRenderer.showInvalidNumberError();
                delay(800);
                continue;
            }

            if (!isValidRange(step, answer)) {
                delay(800);
                continue;
            }

            if (answer == 0) {
                if (step == Run_Test) {
                    step = CarType_Q;
                } else if (step > CarType_Q) {
                    step--;
                }
                continue;
            }

            switch (step) {
                case CarType_Q:
                    selectCarType(answer);
                    delay(800);
                    step = Engine_Q;
                    break;
                case Engine_Q:
                    selectEngine(answer);
                    delay(800);
                    step = BrakeSystem_Q;
                    break;
                case BrakeSystem_Q:
                    selectBrakeSystem(answer);
                    delay(800);
                    step = SteeringSystem_Q;
                    break;
                case SteeringSystem_Q:
                    selectSteeringSystem(answer);
                    delay(800);
                    step = Run_Test;
                    break;
                case Run_Test:
                    if (answer == 1) {
                        runProducedCar();
                        delay(2000);
                    } else if (answer == 2) {
                        ConsoleRenderer.showTestStarting();
                        delay(1500);
                        testProducedCar();
                        delay(2000);
                    }
                    break;
            }
        }

        sc.close();
    }

    private static boolean isValidRange(int step, int ans) {
        switch (step) {
            case CarType_Q:
                if (ans < 1 || ans > 3) {
                    ConsoleRenderer.showRangeError("ERROR :: 차량 타입은 1 ~ 3 범위만 선택 가능");
                    return false;
                }
                break;
            case Engine_Q:
                if (ans < 0 || ans > 4) {
                    ConsoleRenderer.showRangeError("ERROR :: 엔진은 1 ~ 4 범위만 선택 가능");
                    return false;
                }
                break;
            case BrakeSystem_Q:
                if (ans < 0 || ans > 3) {
                    ConsoleRenderer.showRangeError("ERROR :: 제동장치는 1 ~ 3 범위만 선택 가능");
                    return false;
                }
                break;
            case SteeringSystem_Q:
                if (ans < 0 || ans > 2) {
                    ConsoleRenderer.showRangeError("ERROR :: 조향장치는 1 ~ 2 범위만 선택 가능");
                    return false;
                }
                break;
            case Run_Test:
                if (ans < 0 || ans > 2) {
                    ConsoleRenderer.showRangeError("ERROR :: Run 또는 Test 중 하나를 선택 필요");
                    return false;
                }
                break;
        }
        return true;
    }

    private static void selectCarType(int a) {
        spec.setCarType(CarType.fromCode(a));
        ConsoleRenderer.showCarTypeSelected(spec.getCarType().getLabel());
    }

    private static void selectEngine(int a) {
        spec.setEngine(Engine.fromCode(a));
        ConsoleRenderer.showEngineSelected(spec.getEngine().getLabel());
    }

    private static void selectBrakeSystem(int a) {
        spec.setBrakeSystem(BrakeSystem.fromCode(a));
        ConsoleRenderer.showBrakeSystemSelected(spec.getBrakeSystem().getLabel());
    }

    private static void selectSteeringSystem(int a) {
        spec.setSteeringSystem(SteeringSystem.fromCode(a));
        ConsoleRenderer.showSteeringSystemSelected(spec.getSteeringSystem().getLabel());
    }

    private static void runProducedCar() {
        if (!VALIDATOR.isValid(spec)) {
            ConsoleRenderer.showCannotRun();
            return;
        }
        if (spec.getEngine() == Engine.BROKEN) {
            ConsoleRenderer.showBrokenEngine();
            return;
        }
        ConsoleRenderer.showCarRunning(spec);
    }

    private static void testProducedCar() {
        VALIDATOR.findViolation(spec)
                .ifPresentOrElse(ConsoleRenderer::showTestFail, ConsoleRenderer::showTestPass);
    }

    private static void delay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {}
    }
}
