import org.junit.jupiter.api.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("JavaReflectionInvocation")

/**
 * Characterization test suite for Assemble.java (legacy code).
 * All private static methods are accessed via reflection.
 * Static field `stack` is reset before each test to ensure isolation.
 *
 * Note: one structural dead branch exists in main() —
 *   `else if (step > CarType_Q)` false-path when step==CarType_Q with answer==0
 *   is unreachable because isValidRange() blocks answer==0 at CarType_Q.
 *   This is a pre-existing bug in the legacy code, not a test gap.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AssembleTest {

    // ── mirrors of private constants ──────────────────────────────────────────
    private static final int CarType_Q       = 0;
    private static final int Engine_Q        = 1;
    private static final int BrakeSystem_Q   = 2;
    private static final int SteeringSystem_Q = 3;
    private static final int Run_Test        = 4;

    private static final int SEDAN = 1, SUV = 2, TRUCK = 3;
    private static final int GM = 1, TOYOTA = 2, WIA = 3;
    private static final int MANDO = 1, CONTINENTAL = 2, BOSCH_B = 3;
    private static final int BOSCH_S = 1, MOBIS = 2;

    // ── I/O capture ──────────────────────────────────────────────────────────
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream  originalIn  = System.in;

    @BeforeEach
    void setUp() throws Exception {
        System.setOut(new PrintStream(outContent));
        resetStack();
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private void resetStack() throws Exception {
        Field f = Assemble.class.getDeclaredField("stack");
        f.setAccessible(true);
        Arrays.fill((int[]) f.get(null), 0);
    }

    private void setStack(int carType, int engine, int brake, int steering) throws Exception {
        Field f = Assemble.class.getDeclaredField("stack");
        f.setAccessible(true);
        int[] s = (int[]) f.get(null);
        s[0] = carType;
        s[1] = engine;
        s[2] = brake;
        s[3] = steering;
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
    // selectXxx — 선택 시 stack에 값이 저장되고 메시지가 출력되는지 확인
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
    // isValidCheck — 부품 호환 규칙 5가지 + 정상 조합
    // ═══════════════════════════════════════════════════════════════════════════

    @Test @Order(80)
    void isValidCheck_sedanWithContinental_returnsFalse() throws Exception {
        setStack(SEDAN, GM, CONTINENTAL, BOSCH_S);
        assertFalse(isValidCheck());
    }

    @Test @Order(81)
    void isValidCheck_suvWithToyota_returnsFalse() throws Exception {
        setStack(SUV, TOYOTA, MANDO, BOSCH_S);
        assertFalse(isValidCheck());
    }

    @Test @Order(82)
    void isValidCheck_truckWithWia_returnsFalse() throws Exception {
        setStack(TRUCK, WIA, CONTINENTAL, BOSCH_S);
        assertFalse(isValidCheck());
    }

    @Test @Order(83)
    void isValidCheck_truckWithMando_returnsFalse() throws Exception {
        setStack(TRUCK, GM, MANDO, BOSCH_S);
        assertFalse(isValidCheck());
    }

    @Test @Order(84)
    void isValidCheck_boschBrakeWithMobisSteering_returnsFalse() throws Exception {
        setStack(SEDAN, GM, BOSCH_B, MOBIS);
        assertFalse(isValidCheck());
    }

    @Test @Order(85)
    void isValidCheck_validCombination_returnsTrue() throws Exception {
        setStack(SEDAN, GM, MANDO, BOSCH_S);
        assertTrue(isValidCheck());
    }

    // 누락 branch: && 단락평가에서 첫 조건 true, 두 번째 조건 false 경로들
    @Test @Order(86)
    void isValidCheck_suvWithNonToyota_returnsTrue() throws Exception {
        // SUV(true) + GM(≠TOYOTA → false): line 214 두 번째 조건 false 경로
        setStack(SUV, GM, MANDO, BOSCH_S);
        assertTrue(isValidCheck());
    }

    @Test @Order(87)
    void isValidCheck_truckWithNonMandoNonWia_returnsTrue() throws Exception {
        // TRUCK(true) + CONTINENTAL(≠MANDO → false): line 216 두 번째 조건 false 경로
        // WIA가 아닌 engine 사용으로 line 215도 통과
        setStack(TRUCK, GM, CONTINENTAL, BOSCH_S);
        assertTrue(isValidCheck());
    }

    @Test @Order(88)
    void isValidCheck_boschBrakeWithBoschSteering_returnsTrue() throws Exception {
        // BOSCH_B(true) + BOSCH_S(1!=1 → false): line 217 두 번째 조건 false 경로
        setStack(SEDAN, GM, BOSCH_B, BOSCH_S);
        assertTrue(isValidCheck());
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // runProducedCar — 3가지 분기 + 출력 ternary 분기
    // ═══════════════════════════════════════════════════════════════════════════

    @Test @Order(90)
    void runProducedCar_invalidCombo_printsCannotRun() throws Exception {
        setStack(SEDAN, GM, CONTINENTAL, BOSCH_S); // isValidCheck() = false
        call("runProducedCar", new Class[]{});
        assertTrue(out().contains("동작되지 않습니다"));
    }

    @Test @Order(91)
    void runProducedCar_brokenEngine_printsBrokenMessage() throws Exception {
        // engine=4(broken), 나머지는 valid combo
        setStack(SEDAN, 4, MANDO, BOSCH_S);
        call("runProducedCar", new Class[]{});
        assertTrue(out().contains("고장나있습니다"));
    }

    @Test @Order(92)
    void runProducedCar_validCombo_mandobrake_printsCarDetails() throws Exception {
        setStack(SEDAN, GM, MANDO, BOSCH_S);
        call("runProducedCar", new Class[]{});
        assertTrue(out().contains("동작됩니다"));
        assertTrue(out().contains("Mando"));
    }

    @Test @Order(93)
    void runProducedCar_continentalBrake_printsContinental() throws Exception {
        // SUV + GM + CONTINENTAL 은 isValidCheck() 통과 가능
        setStack(SUV, GM, CONTINENTAL, BOSCH_S);
        call("runProducedCar", new Class[]{});
        assertTrue(out().contains("Continental"));
    }

    @Test @Order(94)
    void runProducedCar_boschBrake_printsBosch() throws Exception {
        // BOSCH_B + BOSCH_S 조합은 유효 (line 217: BOSCH_B=true, BOSCH_S=BOSCH_S → false)
        setStack(SEDAN, GM, BOSCH_B, BOSCH_S);
        call("runProducedCar", new Class[]{});
        assertTrue(out().contains("Bosch"));
    }

    @Test @Order(95)
    void runProducedCar_mobisSteering_printsMobis() throws Exception {
        // MANDO brake + MOBIS steering 은 유효 (BOSCH_B 아니므로 line 217 false)
        setStack(SEDAN, GM, MANDO, MOBIS);
        call("runProducedCar", new Class[]{});
        assertTrue(out().contains("Mobis"));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // testProducedCar — 5가지 실패 케이스 + PASS
    // ═══════════════════════════════════════════════════════════════════════════

    @Test @Order(100)
    void testProducedCar_sedanWithContinental_printsFail() throws Exception {
        setStack(SEDAN, GM, CONTINENTAL, BOSCH_S);
        call("testProducedCar", new Class[]{});
        assertTrue(out().contains("FAIL"));
        assertTrue(out().contains("Continental"));
    }

    @Test @Order(101)
    void testProducedCar_suvWithToyota_printsFail() throws Exception {
        setStack(SUV, TOYOTA, MANDO, BOSCH_S);
        call("testProducedCar", new Class[]{});
        assertTrue(out().contains("FAIL"));
        assertTrue(out().contains("TOYOTA"));
    }

    @Test @Order(102)
    void testProducedCar_truckWithWia_printsFail() throws Exception {
        setStack(TRUCK, WIA, CONTINENTAL, BOSCH_S);
        call("testProducedCar", new Class[]{});
        assertTrue(out().contains("FAIL"));
        assertTrue(out().contains("WIA"));
    }

    @Test @Order(103)
    void testProducedCar_truckWithMando_printsFail() throws Exception {
        // WIA 조건이 먼저 걸리지 않도록 engine=GM
        setStack(TRUCK, GM, MANDO, BOSCH_S);
        call("testProducedCar", new Class[]{});
        assertTrue(out().contains("FAIL"));
        assertTrue(out().contains("Mando"));
    }

    @Test @Order(104)
    void testProducedCar_boschBrakeWithMobisSteering_printsFail() throws Exception {
        // 앞선 조건들이 걸리지 않도록 SEDAN + GM 사용
        setStack(SEDAN, GM, BOSCH_B, MOBIS);
        call("testProducedCar", new Class[]{});
        assertTrue(out().contains("FAIL"));
        assertTrue(out().contains("Bosch"));
    }

    @Test @Order(105)
    void testProducedCar_validCombo_printsPass() throws Exception {
        setStack(SEDAN, GM, MANDO, BOSCH_S);
        call("testProducedCar", new Class[]{});
        assertTrue(out().contains("PASS"));
    }

    // 누락 branch: if/else if 에서 첫 조건 true, 두 번째 조건 false → else 진행
    @Test @Order(106)
    void testProducedCar_suvWithNonToyota_printsPass() throws Exception {
        // SUV(true) + GM(≠TOYOTA → false): line 247 두 번째 조건 false → PASS
        setStack(SUV, GM, MANDO, BOSCH_S);
        call("testProducedCar", new Class[]{});
        assertTrue(out().contains("PASS"));
    }

    @Test @Order(107)
    void testProducedCar_truckWithNonMandoNonWia_printsPass() throws Exception {
        // TRUCK+GM(≠WIA → false), TRUCK+CONTINENTAL(≠MANDO → false): line 251 두 번째 조건 false → PASS
        setStack(TRUCK, GM, CONTINENTAL, BOSCH_S);
        call("testProducedCar", new Class[]{});
        assertTrue(out().contains("PASS"));
    }

    @Test @Order(108)
    void testProducedCar_boschBrakeWithBoschSteering_printsPass() throws Exception {
        // BOSCH_B(true) + BOSCH_S(1!=1 → false): line 253 두 번째 조건 false → PASS
        setStack(SEDAN, GM, BOSCH_B, BOSCH_S);
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
    // delay — 예외 없이 완료되는지 확인
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
                m.invoke(null, 30000); // 긴 sleep → 인터럽트로 깨움
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
        // 0 은 CarType_Q 에서 유효하지 않음 (1~3 만 허용)
        assertDoesNotThrow(() -> runMain("0\nexit\n"));
        assertTrue(out().contains("ERROR"));
    }

    @Test @Order(203)
    void main_backAtEngineStep_returnsToCarType() throws Exception {
        // 1(Sedan 선택) → step 0→1, 0(뒤로가기) → step 1→0, exit
        assertDoesNotThrow(() -> runMain("1\n0\nexit\n"));
    }

    @Test @Order(204)
    void main_backAtRunTest_returnsToCarType() throws Exception {
        // 전체 선택 후 RunTest 진입 → 0(뒤로가기) → CarType 으로 돌아감 → exit
        assertDoesNotThrow(() -> runMain("1\n1\n1\n1\n0\nexit\n"));
    }

    @Test @Order(205)
    void main_fullFlowRun_printsCarRunning() throws Exception {
        // Sedan + GM + Mando + Bosch → RUN(1)
        assertDoesNotThrow(() -> runMain("1\n1\n1\n1\n1\nexit\n"));
        assertTrue(out().contains("동작됩니다"));
    }

    @Test @Order(206)
    void main_fullFlowTest_printsPassResult() throws Exception {
        // Sedan + GM + Mando + Bosch → Test(2) → PASS
        assertDoesNotThrow(() -> runMain("1\n1\n1\n1\n2\nexit\n"));
        assertTrue(out().contains("PASS"));
    }
}
