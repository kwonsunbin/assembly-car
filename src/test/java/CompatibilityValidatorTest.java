import domain.*;
import domain.rule.*;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CompatibilityValidatorTest {

    private CarSpec spec(CarType ct, Engine e, BrakeSystem bs, SteeringSystem ss) {
        CarSpec s = new CarSpec();
        s.setCarType(ct);
        s.setEngine(e);
        s.setBrakeSystem(bs);
        s.setSteeringSystem(ss);
        return s;
    }

    // ── SedanContinentalRule ─────────────────────────────────────────────────

    @Test
    void sedanContinentalRule_nonSedan_returnsEmpty() {
        SedanContinentalRule rule = new SedanContinentalRule();
        // 첫 번째 조건(SEDAN) false → empty
        Optional<String> result = rule.validate(spec(CarType.SUV, Engine.GM, BrakeSystem.CONTINENTAL, SteeringSystem.BOSCH));
        assertTrue(result.isEmpty());
    }

    @Test
    void sedanContinentalRule_sedanNonContinental_returnsEmpty() {
        SedanContinentalRule rule = new SedanContinentalRule();
        // 첫 번째 조건 true, 두 번째 조건(CONTINENTAL) false → empty
        Optional<String> result = rule.validate(spec(CarType.SEDAN, Engine.GM, BrakeSystem.MANDO, SteeringSystem.BOSCH));
        assertTrue(result.isEmpty());
    }

    @Test
    void sedanContinentalRule_sedanWithContinental_returnsViolation() {
        SedanContinentalRule rule = new SedanContinentalRule();
        Optional<String> result = rule.validate(spec(CarType.SEDAN, Engine.GM, BrakeSystem.CONTINENTAL, SteeringSystem.BOSCH));
        assertTrue(result.isPresent());
        assertTrue(result.get().contains("Continental"));
    }

    // ── SuvToyotaRule ────────────────────────────────────────────────────────

    @Test
    void suvToyotaRule_nonSuv_returnsEmpty() {
        SuvToyotaRule rule = new SuvToyotaRule();
        Optional<String> result = rule.validate(spec(CarType.SEDAN, Engine.TOYOTA, BrakeSystem.MANDO, SteeringSystem.BOSCH));
        assertTrue(result.isEmpty());
    }

    @Test
    void suvToyotaRule_suvNonToyota_returnsEmpty() {
        SuvToyotaRule rule = new SuvToyotaRule();
        Optional<String> result = rule.validate(spec(CarType.SUV, Engine.GM, BrakeSystem.MANDO, SteeringSystem.BOSCH));
        assertTrue(result.isEmpty());
    }

    @Test
    void suvToyotaRule_suvWithToyota_returnsViolation() {
        SuvToyotaRule rule = new SuvToyotaRule();
        Optional<String> result = rule.validate(spec(CarType.SUV, Engine.TOYOTA, BrakeSystem.MANDO, SteeringSystem.BOSCH));
        assertTrue(result.isPresent());
        assertTrue(result.get().contains("TOYOTA"));
    }

    // ── TruckWiaRule ─────────────────────────────────────────────────────────

    @Test
    void truckWiaRule_nonTruck_returnsEmpty() {
        TruckWiaRule rule = new TruckWiaRule();
        Optional<String> result = rule.validate(spec(CarType.SEDAN, Engine.WIA, BrakeSystem.MANDO, SteeringSystem.BOSCH));
        assertTrue(result.isEmpty());
    }

    @Test
    void truckWiaRule_truckNonWia_returnsEmpty() {
        TruckWiaRule rule = new TruckWiaRule();
        Optional<String> result = rule.validate(spec(CarType.TRUCK, Engine.GM, BrakeSystem.CONTINENTAL, SteeringSystem.BOSCH));
        assertTrue(result.isEmpty());
    }

    @Test
    void truckWiaRule_truckWithWia_returnsViolation() {
        TruckWiaRule rule = new TruckWiaRule();
        Optional<String> result = rule.validate(spec(CarType.TRUCK, Engine.WIA, BrakeSystem.CONTINENTAL, SteeringSystem.BOSCH));
        assertTrue(result.isPresent());
        assertTrue(result.get().contains("WIA"));
    }

    // ── TruckMandoRule ───────────────────────────────────────────────────────

    @Test
    void truckMandoRule_nonTruck_returnsEmpty() {
        TruckMandoRule rule = new TruckMandoRule();
        Optional<String> result = rule.validate(spec(CarType.SEDAN, Engine.GM, BrakeSystem.MANDO, SteeringSystem.BOSCH));
        assertTrue(result.isEmpty());
    }

    @Test
    void truckMandoRule_truckNonMando_returnsEmpty() {
        TruckMandoRule rule = new TruckMandoRule();
        Optional<String> result = rule.validate(spec(CarType.TRUCK, Engine.GM, BrakeSystem.CONTINENTAL, SteeringSystem.BOSCH));
        assertTrue(result.isEmpty());
    }

    @Test
    void truckMandoRule_truckWithMando_returnsViolation() {
        TruckMandoRule rule = new TruckMandoRule();
        Optional<String> result = rule.validate(spec(CarType.TRUCK, Engine.GM, BrakeSystem.MANDO, SteeringSystem.BOSCH));
        assertTrue(result.isPresent());
        assertTrue(result.get().contains("Mando"));
    }

    // ── BoschBrakeSteeringRule ───────────────────────────────────────────────

    @Test
    void boschBrakeRule_nonBoschBrake_returnsEmpty() {
        BoschBrakeSteeringRule rule = new BoschBrakeSteeringRule();
        // 첫 번째 조건(BOSCH brake) false → empty
        Optional<String> result = rule.validate(spec(CarType.SEDAN, Engine.GM, BrakeSystem.MANDO, SteeringSystem.MOBIS));
        assertTrue(result.isEmpty());
    }

    @Test
    void boschBrakeRule_boschBrakeWithBoschSteering_returnsEmpty() {
        BoschBrakeSteeringRule rule = new BoschBrakeSteeringRule();
        // 첫 번째 조건 true, 두 번째 조건(!=BOSCH) false → empty
        Optional<String> result = rule.validate(spec(CarType.SEDAN, Engine.GM, BrakeSystem.BOSCH, SteeringSystem.BOSCH));
        assertTrue(result.isEmpty());
    }

    @Test
    void boschBrakeRule_boschBrakeWithMobisSteering_returnsViolation() {
        BoschBrakeSteeringRule rule = new BoschBrakeSteeringRule();
        Optional<String> result = rule.validate(spec(CarType.SEDAN, Engine.GM, BrakeSystem.BOSCH, SteeringSystem.MOBIS));
        assertTrue(result.isPresent());
        assertTrue(result.get().contains("Bosch"));
    }

    // ── CompatibilityValidator ───────────────────────────────────────────────

    @Test
    void isValid_allRulesPass_returnsTrue() {
        CompatibilityValidator validator = new CompatibilityValidator();
        assertTrue(validator.isValid(spec(CarType.SEDAN, Engine.GM, BrakeSystem.MANDO, SteeringSystem.BOSCH)));
    }

    @Test
    void isValid_violationExists_returnsFalse() {
        CompatibilityValidator validator = new CompatibilityValidator();
        assertFalse(validator.isValid(spec(CarType.SEDAN, Engine.GM, BrakeSystem.CONTINENTAL, SteeringSystem.BOSCH)));
    }

    @Test
    void findViolation_allRulesPass_returnsEmpty() {
        CompatibilityValidator validator = new CompatibilityValidator();
        assertTrue(validator.findViolation(spec(CarType.SEDAN, Engine.GM, BrakeSystem.MANDO, SteeringSystem.BOSCH)).isEmpty());
    }

    @Test
    void findViolation_violationExists_returnsMessage() {
        CompatibilityValidator validator = new CompatibilityValidator();
        Optional<String> violation = validator.findViolation(spec(CarType.SEDAN, Engine.GM, BrakeSystem.CONTINENTAL, SteeringSystem.BOSCH));
        assertTrue(violation.isPresent());
        assertTrue(violation.get().contains("Continental"));
    }
}
