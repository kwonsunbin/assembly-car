import domain.BrakeSystem;
import domain.CarSpec;
import domain.CarType;
import domain.Engine;
import domain.SteeringSystem;
import org.junit.jupiter.api.*;

import java.io.*;
import java.lang.reflect.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Characterization test suite for Assemble.java (legacy code).
 * All private static methods are accessed via reflection.
 * Static field `spec` (CarSpec) is reset before each test for isolation.
 *
 * Note: 4 structural dead branches remain in main() and cannot be covered:
 *   - switch(step) default (step outside 0–4): unreachable through normal flow
 *   - else if (step > CarType_Q) false-path: isValidRange blocks answer==0 at step==0
 *   - else if (answer == 2) false-path at Run_Test: only 1 or 2 reach this point
 * These are pre-existing dead code in the legacy code, not a test gap.
 */
@SuppressWarnings("JavaReflectionInvocation")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AssembleTest {

    // ── step 상수 (isValidRange 테스트용) ─────────────────────────────────────
    private static final int CarType_Q        = 0;
    private static final int Engine_Q         = 1;
    private static final int BrakeSystem_Q    = 2;
    private static final int SteeringSystem_Q = 3;
    private static final int Run_Test         = 4;

    // ── I/O 캡처 ──────────────────────────────────────────────────────────────
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream  originalIn  = System.in;

    @BeforeEach
    void setUp() throws Exception {
        System.setOut(new PrintStream(outContent));
        resetSpec();
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    // ── 헬퍼 ─────────────────────────────────────────────────────────────────

    private void resetSpec() throws Exception {
        Field f = Assemble.class.getDeclaredField("spec");
        f.setAccessible(true);
        f.set(null, new CarSpec());
    }

    private void setSpec(CarType carType, Engine engine,
                         BrakeSystem brakeSystem, SteeringSystem steeringSystem) throws Exception {
        Field f = Assemble.class.getDeclaredField("spec");
        f.setAccessible(true);
        CarSpec s = new CarSpec();
        s.setCarType(carType);
        s.setEngine(engine);
        s.setBrakeSystem(brakeSystem);
        s.setSteeringSystem(steeringSystem);
        f.set(null, s);
    }

    private Object call(String name, Class<?>[] types, Object... args) throws Exception {
        Method m = Assemble.class.getDeclaredMethod(name, types);
        m.setAccessible(true);
        return m.invoke(null, args);
    }

    private boolean isValidRange(int step, int ans) throws Exception {
        return (boolean) call("isValidRange", new Class[]{int.class, int.class}, step, ans);
    }

    private boolean isValidCheck() throws Exception {
        return (boolean) call("isValidCheck", new Class[]{});
    }

    private String out() {
        return outContent.toString();
    }

    private void runMain(String input) throws Exception {
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Assemble.main(new String[]{});
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Constructor — utility class 이지만 default constructor 커버
    // ═══════════════════════════════════════════════════════════════════════════

    @Test @Order(0)
    void constructor_canBeInstantiated() throws Exception {
        Constructor<Assemble> c = Assemble.class.getDeclaredConstructor();
        c.setAccessible(true);
        assertNotNull(c.newInstance());
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Show-menu tests  (각 메뉴가 핵심 텍스트를 출력하는지 확인)
    // ═══════════════════════════════════════════════════════════════════════════

    @Test @Order(1)
    void showCarTypeMenu_containsAllCarTypes() throws Exception {
        call("showCarTypeMenu", new Class[]{});
        assertTrue(out().contains("Sedan"));
        assertTrue(out().contains("SUV"));
        assertTrue(out().contains("Truck"));
    }

    @Test @Order(2)
    void showEngineMenu_containsAllEngines() throws Exception {
        call("showEngineMenu", new Class[]{});
        String o = out();
        assertTrue(o.contains("GM"));
        assertTrue(o.contains("TOYOTA"));
        assertTrue(o.contains("WIA"));
        assertTrue(o.contains("고장난 엔진"));
    }

    @Test @Order(3)
    void showBrakeMenu_containsAllBrakeSystems() throws Exception {
        call("showBrakeMenu", new Class[]{});
        String o = out();
        assertTrue(o.contains("MANDO"));
        assertTrue(o.contains("CONTINENTAL"));
        assertTrue(o.contains("BOSCH"));
    }

    @Test @Order(4)
    void showSteeringMenu_containsAllSteeringSystems() throws Exception {
        call("showSteeringMenu", new Class[]{});
        String o = out();
        assertTrue(o.contains("BOSCH"));
        assertTrue(o.contains("MOBIS"));
    }

    @Test @Order(5)
    void showRunTestMenu_containsRunAndTest() throws Exception {
        call("showRunTestMenu", new Class[]{});
        String o = out();
        assertTrue(o.contains("RUN"));
        assertTrue(o.contains("Test"));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // isValidRange — CarType_Q (step = 0)
    // ═══════════════════════════════════════════════════════════════════════════

    @Test @Order(10)
    void isValidRange_carType_belowMin_returnsFalse() throws Exception {
        assertFalse(isValidRange(CarType_Q, 0));
        assertTrue(out().contains("ERROR"));
    }

    @Test @Order(11)
    void isValidRange_carType_aboveMax_returnsFalse() throws Exception {
        assertFalse(isValidRange(CarType_Q, 4));
        assertTrue(out().contains("ERROR"));
    }

    @Test @Order(12)
    void isValidRange_carType_lowerBound_returnsTrue() throws Exception {
        assertTrue(isValidRange(CarType_Q, 1));
    }

    @Test @Order(13)
    void isValidRange_carType_upperBound_returnsTrue() throws Exception {
        assertTrue(isValidRange(CarType_Q, 3));
    }

    @Test @Order(14)
    void isValidRange_unknownStep_fallthroughReturnsTrue() throws Exception {
        // switch default 경로: step이 0~4 범위 밖이면 어떤 case도 해당 없어 true 반환
        assertTrue(isValidRange(99, 0));
    }

    // ── Engine_Q (step = 1) ──────────────────────────────────────────────────

    @Test @Order(20)
    void isValidRange_engine_belowMin_returnsFalse() throws Exception {
        assertFalse(isValidRange(Engine_Q, -1));
        assertTrue(out().contains("ERROR"));
    }

    @Test @Order(21)
    void isValidRange_engine_aboveMax_returnsFalse() throws Exception {
        assertFalse(isValidRange(Engine_Q, 5));
        assertTrue(out().contains("ERROR"));
    }

    @Test @Order(22)
    void isValidRange_engine_zero_returnsTrue() throws Exception {
        assertTrue(isValidRange(Engine_Q, 0));
    }

    @Test @Order(23)
    void isValidRange_engine_upperBound_returnsTrue() throws Exception {
        assertTrue(isValidRange(Engine_Q, 4));
    }

    // ── BrakeSystem_Q (step = 2) ─────────────────────────────────────────────

    @Test @Order(30)
    void isValidRange_brake_belowMin_returnsFalse() throws Exception {
        assertFalse(isValidRange(BrakeSystem_Q, -1));
        assertTrue(out().contains("ERROR"));
    }

    @Test @Order(31)
    void isValidRange_brake_aboveMax_returnsFalse() throws Exception {
        assertFalse(isValidRange(BrakeSystem_Q, 4));
        assertTrue(out().contains("ERROR"));
    }

    @Test @Order(32)
    void isValidRange_brake_zero_returnsTrue() throws Exception {
        assertTrue(isValidRange(BrakeSystem_Q, 0));
    }

    @Test @Order(33)
    void isValidRange_brake_upperBound_returnsTrue() throws Exception {
        assertTrue(isValidRange(BrakeSystem_Q, 3));
    }

    // ── SteeringSystem_Q (step = 3) ──────────────────────────────────────────

    @Test @Order(40)
    void isValidRange_steering_belowMin_returnsFalse() throws Exception {
        assertFalse(isValidRange(SteeringSystem_Q, -1));
        assertTrue(out().contains("ERROR"));
    }

    @Test @Order(41)
    void isValidRange_steering_aboveMax_returnsFalse() throws Exception {
        assertFalse(isValidRange(SteeringSystem_Q, 3));
        assertTrue(out().contains("ERROR"));
    }

    @Test @Order(42)
    void isValidRange_steering_zero_returnsTrue() throws Exception {
        assertTrue(isValidRange(SteeringSystem_Q, 0));
    }

    @Test @Order(43)
    void isValidRange_steering_upperBound_returnsTrue() throws Exception {
        assertTrue(isValidRange(SteeringSystem_Q, 2));
    }

    // ── Run_Test (step = 4) ──────────────────────────────────────────────────

    @Test @Order(50)
    void isValidRange_runTest_belowMin_returnsFalse() throws Exception {
        assertFalse(isValidRange(Run_Test, -1));
        assertTrue(out().contains("ERROR"));
    }

    @Test @Order(51)
    void isValidRange_runTest_aboveMax_returnsFalse() throws Exception {
        assertFalse(isValidRange(Run_Test, 3));
        assertTrue(out().contains("ERROR"));
    }

    @Test @Order(52)
    void isValidRange_runTest_zero_returnsTrue() throws Exception {
        assertTrue(isValidRange(Run_Test, 0));
    }

    @Test @Order(53)
    void isValidRange_runTest_upperBound_returnsTrue() throws Exception {
        assertTrue(isValidRange(Run_Test, 2));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // selectXxx — 선택 시 spec에 값이 저장되고 메시지가 출력되는지 확인
    // ═══════════════════════════════════════════════════════════════════════════

    @Test @Order(60)
    void selectCarType_sedan_printsSedan() throws Exception {
        call("selectCarType", new Class[]{int.class}, 1);
        assertTrue(out().contains("Sedan"));
    }

    @Test @Order(61)
    void selectCarType_suv_printsSuv() throws Exception {
        call("selectCarType", new Class[]{int.class}, 2);
        assertTrue(out().contains("SUV"));
    }

    @Test @Order(62)
    void selectCarType_truck_printsTruck() throws Exception {
        call("selectCarType", new Class[]{int.class}, 3);
        assertTrue(out().contains("Truck"));
    }

    @Test @Order(63)
    void selectEngine_gm_printsGm() throws Exception {
        call("selectEngine", new Class[]{int.class}, 1);
        assertTrue(out().contains("GM"));
    }

    @Test @Order(64)
    void selectEngine_toyota_printsToyota() throws Exception {
        call("selectEngine", new Class[]{int.class}, 2);
        assertTrue(out().contains("TOYOTA"));
    }

    @Test @Order(65)
    void selectEngine_wia_printsWia() throws Exception {
        call("selectEngine", new Class[]{int.class}, 3);
        assertTrue(out().contains("WIA"));
    }

    @Test @Order(66)
    void selectEngine_broken_printsBrokenEngine() throws Exception {
        call("selectEngine", new Class[]{int.class}, 4);
        assertTrue(out().contains("고장난 엔진"));
    }

    @Test @Order(67)
    void selectBrakeSystem_mando_printsMando() throws Exception {
        call("selectBrakeSystem", new Class[]{int.class}, 1);
        assertTrue(out().contains("MANDO"));
    }

    @Test @Order(68)
    void selectBrakeSystem_continental_printsContinental() throws Exception {
        call("selectBrakeSystem", new Class[]{int.class}, 2);
        assertTrue(out().contains("CONTINENTAL"));
    }

    @Test @Order(69)
    void selectBrakeSystem_bosch_printsBosch() throws Exception {
        call("selectBrakeSystem", new Class[]{int.class}, 3);
        assertTrue(out().contains("BOSCH"));
    }

    @Test @Order(70)
    void selectSteeringSystem_bosch_printsBosch() throws Exception {
        call("selectSteeringSystem", new Class[]{int.class}, 1);
        assertTrue(out().contains("BOSCH"));
    }

    @Test @Order(71)
    void selectSteeringSystem_mobis_printsMobis() throws Exception {
        call("selectSteeringSystem", new Class[]{int.class}, 2);
        assertTrue(out().contains("MOBIS"));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // isValidCheck — 부품 호환 규칙 5가지 + && 단락평가 분기
    // ═══════════════════════════════════════════════════════════════════════════

    @Test @Order(80)
    void isValidCheck_sedanWithContinental_returnsFalse() throws Exception {
        setSpec(CarType.SEDAN, Engine.GM, BrakeSystem.CONTINENTAL, SteeringSystem.BOSCH);
        assertFalse(isValidCheck());
    }

    @Test @Order(81)
    void isValidCheck_suvWithToyota_returnsFalse() throws Exception {
        setSpec(CarType.SUV, Engine.TOYOTA, BrakeSystem.MANDO, SteeringSystem.BOSCH);
        assertFalse(isValidCheck());
    }

    @Test @Order(82)
    void isValidCheck_truckWithWia_returnsFalse() throws Exception {
        setSpec(CarType.TRUCK, Engine.WIA, BrakeSystem.CONTINENTAL, SteeringSystem.BOSCH);
        assertFalse(isValidCheck());
    }

    @Test @Order(83)
    void isValidCheck_truckWithMando_returnsFalse() throws Exception {
        setSpec(CarType.TRUCK, Engine.GM, BrakeSystem.MANDO, SteeringSystem.BOSCH);
        assertFalse(isValidCheck());
    }

    @Test @Order(84)
    void isValidCheck_boschBrakeWithMobisSteering_returnsFalse() throws Exception {
        setSpec(CarType.SEDAN, Engine.GM, BrakeSystem.BOSCH, SteeringSystem.MOBIS);
        assertFalse(isValidCheck());
    }

    @Test @Order(85)
    void isValidCheck_validCombination_returnsTrue() throws Exception {
        setSpec(CarType.SEDAN, Engine.GM, BrakeSystem.MANDO, SteeringSystem.BOSCH);
        assertTrue(isValidCheck());
    }

    // 누락 branch: && 단락평가에서 첫 조건 true, 두 번째 조건 false 경로들
    @Test @Order(86)
    void isValidCheck_suvWithNonToyota_returnsTrue() throws Exception {
        setSpec(CarType.SUV, Engine.GM, BrakeSystem.MANDO, SteeringSystem.BOSCH);
        assertTrue(isValidCheck());
    }

    @Test @Order(87)
    void isValidCheck_truckWithNonMandoNonWia_returnsTrue() throws Exception {
        setSpec(CarType.TRUCK, Engine.GM, BrakeSystem.CONTINENTAL, SteeringSystem.BOSCH);
        assertTrue(isValidCheck());
    }

    @Test @Order(88)
    void isValidCheck_boschBrakeWithBoschSteering_returnsTrue() throws Exception {
        setSpec(CarType.SEDAN, Engine.GM, BrakeSystem.BOSCH, SteeringSystem.BOSCH);
        assertTrue(isValidCheck());
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // runProducedCar — 3가지 분기 + 출력 ternary 분기
    // ═══════════════════════════════════════════════════════════════════════════

    @Test @Order(90)
    void runProducedCar_invalidCombo_printsCannotRun() throws Exception {
        setSpec(CarType.SEDAN, Engine.GM, BrakeSystem.CONTINENTAL, SteeringSystem.BOSCH);
        call("runProducedCar", new Class[]{});
        assertTrue(out().contains("동작되지 않습니다"));
    }

    @Test @Order(91)
    void runProducedCar_brokenEngine_printsBrokenMessage() throws Exception {
        setSpec(CarType.SEDAN, Engine.BROKEN, BrakeSystem.MANDO, SteeringSystem.BOSCH);
        call("runProducedCar", new Class[]{});
        assertTrue(out().contains("고장나있습니다"));
    }

    @Test @Order(92)
    void runProducedCar_validCombo_mandoBrake_printsCarDetails() throws Exception {
        setSpec(CarType.SEDAN, Engine.GM, BrakeSystem.MANDO, SteeringSystem.BOSCH);
        call("runProducedCar", new Class[]{});
        assertTrue(out().contains("동작됩니다"));
        assertTrue(out().contains("Mando"));
    }

    @Test @Order(93)
    void runProducedCar_continentalBrake_printsContinental() throws Exception {
        setSpec(CarType.SUV, Engine.GM, BrakeSystem.CONTINENTAL, SteeringSystem.BOSCH);
        call("runProducedCar", new Class[]{});
        assertTrue(out().contains("Continental"));
    }

    @Test @Order(94)
    void runProducedCar_boschBrake_printsBosch() throws Exception {
        setSpec(CarType.SEDAN, Engine.GM, BrakeSystem.BOSCH, SteeringSystem.BOSCH);
        call("runProducedCar", new Class[]{});
        assertTrue(out().contains("Bosch"));
    }

    @Test @Order(95)
    void runProducedCar_mobisSteering_printsMobis() throws Exception {
        setSpec(CarType.SEDAN, Engine.GM, BrakeSystem.MANDO, SteeringSystem.MOBIS);
        call("runProducedCar", new Class[]{});
        assertTrue(out().contains("Mobis"));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // testProducedCar — 5가지 실패 케이스 + && 분기 포함 PASS 케이스
    // ═══════════════════════════════════════════════════════════════════════════

    @Test @Order(100)
    void testProducedCar_sedanWithContinental_printsFail() throws Exception {
        setSpec(CarType.SEDAN, Engine.GM, BrakeSystem.CONTINENTAL, SteeringSystem.BOSCH);
        call("testProducedCar", new Class[]{});
        assertTrue(out().contains("FAIL"));
        assertTrue(out().contains("Continental"));
    }

    @Test @Order(101)
    void testProducedCar_suvWithToyota_printsFail() throws Exception {
        setSpec(CarType.SUV, Engine.TOYOTA, BrakeSystem.MANDO, SteeringSystem.BOSCH);
        call("testProducedCar", new Class[]{});
        assertTrue(out().contains("FAIL"));
        assertTrue(out().contains("TOYOTA"));
    }

    @Test @Order(102)
    void testProducedCar_truckWithWia_printsFail() throws Exception {
        setSpec(CarType.TRUCK, Engine.WIA, BrakeSystem.CONTINENTAL, SteeringSystem.BOSCH);
        call("testProducedCar", new Class[]{});
        assertTrue(out().contains("FAIL"));
        assertTrue(out().contains("WIA"));
    }

    @Test @Order(103)
    void testProducedCar_truckWithMando_printsFail() throws Exception {
        setSpec(CarType.TRUCK, Engine.GM, BrakeSystem.MANDO, SteeringSystem.BOSCH);
        call("testProducedCar", new Class[]{});
        assertTrue(out().contains("FAIL"));
        assertTrue(out().contains("Mando"));
    }

    @Test @Order(104)
    void testProducedCar_boschBrakeWithMobisSteering_printsFail() throws Exception {
        setSpec(CarType.SEDAN, Engine.GM, BrakeSystem.BOSCH, SteeringSystem.MOBIS);
        call("testProducedCar", new Class[]{});
        assertTrue(out().contains("FAIL"));
        assertTrue(out().contains("Bosch"));
    }

    @Test @Order(105)
    void testProducedCar_validCombo_printsPass() throws Exception {
        setSpec(CarType.SEDAN, Engine.GM, BrakeSystem.MANDO, SteeringSystem.BOSCH);
        call("testProducedCar", new Class[]{});
        assertTrue(out().contains("PASS"));
    }

    // 누락 branch: if/else if 에서 첫 조건 true, 두 번째 조건 false → else 진행
    @Test @Order(106)
    void testProducedCar_suvWithNonToyota_printsPass() throws Exception {
        setSpec(CarType.SUV, Engine.GM, BrakeSystem.MANDO, SteeringSystem.BOSCH);
        call("testProducedCar", new Class[]{});
        assertTrue(out().contains("PASS"));
    }

    @Test @Order(107)
    void testProducedCar_truckWithNonMandoNonWia_printsPass() throws Exception {
        setSpec(CarType.TRUCK, Engine.GM, BrakeSystem.CONTINENTAL, SteeringSystem.BOSCH);
        call("testProducedCar", new Class[]{});
        assertTrue(out().contains("PASS"));
    }

    @Test @Order(108)
    void testProducedCar_boschBrakeWithBoschSteering_printsPass() throws Exception {
        setSpec(CarType.SEDAN, Engine.GM, BrakeSystem.BOSCH, SteeringSystem.BOSCH);
        call("testProducedCar", new Class[]{});
        assertTrue(out().contains("PASS"));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // fail — 직접 호출 확인
    // ═══════════════════════════════════════════════════════════════════════════

    @Test @Order(110)
    void fail_printsFailAndMessage() throws Exception {
        call("fail", new Class[]{String.class}, "테스트 실패 원인");
        String o = out();
        assertTrue(o.contains("FAIL"));
        assertTrue(o.contains("테스트 실패 원인"));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // delay — 정상 실행 + InterruptedException catch 경로
    // ═══════════════════════════════════════════════════════════════════════════

    @Test @Order(120)
    void delay_doesNotThrow() {
        assertDoesNotThrow(() -> call("delay", new Class[]{int.class}, 1));
    }

    @Test @Order(121)
    void delay_interrupted_catchesInterruptedException() throws Exception {
        // catch(InterruptedException ignored) {} 블록 커버
        Thread t = new Thread(() -> {
            try {
                Method m = Assemble.class.getDeclaredMethod("delay", int.class);
                m.setAccessible(true);
                m.invoke(null, 30000);
            } catch (Exception ignored) {}
        });
        t.start();
        Thread.sleep(50);
        t.interrupt();
        t.join(500);
        assertFalse(t.isAlive());
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // main() 통합 테스트 — 루프 제어 흐름 커버
    // ═══════════════════════════════════════════════════════════════════════════

    @Test @Order(200)
    void main_exitInput_exitsImmediately() throws Exception {
        assertDoesNotThrow(() -> runMain("exit\n"));
        assertTrue(out().contains("바이바이"));
    }

    @Test @Order(201)
    void main_nonNumberInput_showsErrorAndContinues() throws Exception {
        assertDoesNotThrow(() -> runMain("abc\nexit\n"));
        assertTrue(out().contains("ERROR"));
    }

    @Test @Order(202)
    void main_outOfRangeAtCarType_showsErrorAndContinues() throws Exception {
        assertDoesNotThrow(() -> runMain("0\nexit\n"));
        assertTrue(out().contains("ERROR"));
    }

    @Test @Order(203)
    void main_backAtEngineStep_returnsToCarType() throws Exception {
        assertDoesNotThrow(() -> runMain("1\n0\nexit\n"));
    }

    @Test @Order(204)
    void main_backAtRunTest_returnsToCarType() throws Exception {
        assertDoesNotThrow(() -> runMain("1\n1\n1\n1\n0\nexit\n"));
    }

    @Test @Order(205)
    void main_fullFlowRun_printsCarRunning() throws Exception {
        assertDoesNotThrow(() -> runMain("1\n1\n1\n1\n1\nexit\n"));
        assertTrue(out().contains("동작됩니다"));
    }

    @Test @Order(206)
    void main_fullFlowTest_printsPassResult() throws Exception {
        assertDoesNotThrow(() -> runMain("1\n1\n1\n1\n2\nexit\n"));
        assertTrue(out().contains("PASS"));
    }
}
