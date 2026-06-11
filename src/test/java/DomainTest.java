import domain.BrakeSystem;
import domain.CarType;
import domain.Engine;
import domain.SteeringSystem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for domain enum fromCode() methods.
 * Covers the invalid-code exception path not reachable through Assemble.java flow.
 */
class DomainTest {

    @Test
    void carType_fromCode_validCodes_returnCorrectEnum() {
        assertEquals(CarType.SEDAN, CarType.fromCode(1));
        assertEquals(CarType.SUV,   CarType.fromCode(2));
        assertEquals(CarType.TRUCK, CarType.fromCode(3));
    }

    @Test
    void carType_fromCode_invalidCode_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> CarType.fromCode(99));
    }

    @Test
    void engine_fromCode_validCodes_returnCorrectEnum() {
        assertEquals(Engine.GM,     Engine.fromCode(1));
        assertEquals(Engine.TOYOTA, Engine.fromCode(2));
        assertEquals(Engine.WIA,    Engine.fromCode(3));
        assertEquals(Engine.BROKEN, Engine.fromCode(4));
    }

    @Test
    void engine_fromCode_invalidCode_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> Engine.fromCode(99));
    }

    @Test
    void brakeSystem_fromCode_validCodes_returnCorrectEnum() {
        assertEquals(BrakeSystem.MANDO,       BrakeSystem.fromCode(1));
        assertEquals(BrakeSystem.CONTINENTAL, BrakeSystem.fromCode(2));
        assertEquals(BrakeSystem.BOSCH,       BrakeSystem.fromCode(3));
    }

    @Test
    void brakeSystem_fromCode_invalidCode_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> BrakeSystem.fromCode(99));
    }

    @Test
    void steeringSystem_fromCode_validCodes_returnCorrectEnum() {
        assertEquals(SteeringSystem.BOSCH, SteeringSystem.fromCode(1));
        assertEquals(SteeringSystem.MOBIS, SteeringSystem.fromCode(2));
    }

    @Test
    void steeringSystem_fromCode_invalidCode_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> SteeringSystem.fromCode(99));
    }
}
