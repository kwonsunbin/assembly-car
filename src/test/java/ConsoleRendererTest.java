import domain.BrakeSystem;
import domain.CarSpec;
import domain.CarType;
import domain.Engine;
import domain.SteeringSystem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.ConsoleRenderer;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class ConsoleRendererTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    private String out() {
        return outContent.toString();
    }

    // ── 생성자 커버 ───────────────────────────────────────────────────────────

    @Test
    void constructor_exists() {
        assertNotNull(new ConsoleRenderer());
    }

    // ── clearScreen ───────────────────────────────────────────────────────────

    @Test
    void clearScreen_doesNotThrow() {
        assertDoesNotThrow(ConsoleRenderer::clearScreen);
    }

    // ── 메뉴 출력 ─────────────────────────────────────────────────────────────

    @Test
    void showCarTypeMenu_containsAllTypes() {
        ConsoleRenderer.showCarTypeMenu();
        String o = out();
        assertTrue(o.contains("Sedan"));
        assertTrue(o.contains("SUV"));
        assertTrue(o.contains("Truck"));
    }

    @Test
    void showEngineMenu_containsAllEngines() {
        ConsoleRenderer.showEngineMenu();
        String o = out();
        assertTrue(o.contains("GM"));
        assertTrue(o.contains("TOYOTA"));
        assertTrue(o.contains("WIA"));
        assertTrue(o.contains("고장난 엔진"));
    }

    @Test
    void showBrakeMenu_containsAllBrakeSystems() {
        ConsoleRenderer.showBrakeMenu();
        String o = out();
        assertTrue(o.contains("MANDO"));
        assertTrue(o.contains("CONTINENTAL"));
        assertTrue(o.contains("BOSCH"));
    }

    @Test
    void showSteeringMenu_containsAllSteeringSystems() {
        ConsoleRenderer.showSteeringMenu();
        String o = out();
        assertTrue(o.contains("BOSCH"));
        assertTrue(o.contains("MOBIS"));
    }

    @Test
    void showRunTestMenu_containsRunAndTest() {
        ConsoleRenderer.showRunTestMenu();
        String o = out();
        assertTrue(o.contains("RUN"));
        assertTrue(o.contains("Test"));
    }

    // ── 입력/오류 출력 ────────────────────────────────────────────────────────

    @Test
    void showInputPrompt_printsPrompt() {
        ConsoleRenderer.showInputPrompt();
        assertTrue(out().contains("INPUT"));
    }

    @Test
    void showInvalidNumberError_printsError() {
        ConsoleRenderer.showInvalidNumberError();
        String o = out();
        assertTrue(o.contains("ERROR"));
        assertTrue(o.contains("숫자"));
    }

    @Test
    void showRangeError_printsGivenMessage() {
        ConsoleRenderer.showRangeError("ERROR :: 테스트 오류 메시지");
        assertTrue(out().contains("테스트 오류 메시지"));
    }

    @Test
    void showGoodbye_printsFarewell() {
        ConsoleRenderer.showGoodbye();
        assertTrue(out().contains("바이바이"));
    }

    // ── 선택 확인 출력 ────────────────────────────────────────────────────────

    @Test
    void showCarTypeSelected_printsLabel() {
        ConsoleRenderer.showCarTypeSelected("Sedan");
        assertTrue(out().contains("Sedan"));
    }

    @Test
    void showEngineSelected_printsLabel() {
        ConsoleRenderer.showEngineSelected("GM");
        assertTrue(out().contains("GM"));
    }

    @Test
    void showBrakeSystemSelected_printsLabel() {
        ConsoleRenderer.showBrakeSystemSelected("MANDO");
        assertTrue(out().contains("MANDO"));
    }

    @Test
    void showSteeringSystemSelected_printsLabel() {
        ConsoleRenderer.showSteeringSystemSelected("BOSCH");
        assertTrue(out().contains("BOSCH"));
    }

    // ── 주행/테스트 결과 출력 ─────────────────────────────────────────────────

    @Test
    void showCannotRun_printsCannotRunMessage() {
        ConsoleRenderer.showCannotRun();
        assertTrue(out().contains("동작되지 않습니다"));
    }

    @Test
    void showBrokenEngine_printsBrokenMessages() {
        ConsoleRenderer.showBrokenEngine();
        String o = out();
        assertTrue(o.contains("고장나있습니다"));
        assertTrue(o.contains("움직이지 않습니다"));
    }

    @Test
    void showCarRunning_printsAllCarDetails() {
        CarSpec spec = new CarSpec();
        spec.setCarType(CarType.SEDAN);
        spec.setEngine(Engine.GM);
        spec.setBrakeSystem(BrakeSystem.MANDO);
        spec.setSteeringSystem(SteeringSystem.BOSCH);

        ConsoleRenderer.showCarRunning(spec);

        String o = out();
        assertTrue(o.contains("Sedan"));
        assertTrue(o.contains("GM"));
        assertTrue(o.contains("Mando"));
        assertTrue(o.contains("Bosch"));
        assertTrue(o.contains("동작됩니다"));
    }

    @Test
    void showTestStarting_printsTestMessage() {
        ConsoleRenderer.showTestStarting();
        assertTrue(out().contains("Test"));
    }

    @Test
    void showTestPass_printsPassResult() {
        ConsoleRenderer.showTestPass();
        assertTrue(out().contains("PASS"));
    }

    @Test
    void showTestFail_printsFailAndReason() {
        ConsoleRenderer.showTestFail("테스트 실패 원인");
        String o = out();
        assertTrue(o.contains("FAIL"));
        assertTrue(o.contains("테스트 실패 원인"));
    }
}
