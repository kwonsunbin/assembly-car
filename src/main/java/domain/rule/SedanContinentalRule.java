package domain.rule;

import domain.BrakeSystem;
import domain.CarSpec;
import domain.CarType;

import java.util.Optional;

public class SedanContinentalRule implements CompatibilityRule {
    @Override
    public Optional<String> validate(CarSpec spec) {
        if (spec.getCarType() == CarType.SEDAN && spec.getBrakeSystem() == BrakeSystem.CONTINENTAL) {
            return Optional.of("Sedan에는 Continental제동장치 사용 불가");
        }
        return Optional.empty();
    }
}
