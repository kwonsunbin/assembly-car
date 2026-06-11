package domain.rule;

import domain.BrakeSystem;
import domain.CarSpec;
import domain.SteeringSystem;

import java.util.Optional;

public class BoschBrakeSteeringRule implements CompatibilityRule {
    @Override
    public Optional<String> validate(CarSpec spec) {
        if (spec.getBrakeSystem() == BrakeSystem.BOSCH && spec.getSteeringSystem() != SteeringSystem.BOSCH) {
            return Optional.of("Bosch제동장치에는 Bosch조향장치 이외 사용 불가");
        }
        return Optional.empty();
    }
}
