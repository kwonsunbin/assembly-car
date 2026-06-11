package domain.rule;

import domain.BrakeSystem;
import domain.CarSpec;
import domain.CarType;

import java.util.Optional;

public class TruckMandoRule implements CompatibilityRule {
    @Override
    public Optional<String> validate(CarSpec spec) {
        if (spec.getCarType() == CarType.TRUCK && spec.getBrakeSystem() == BrakeSystem.MANDO) {
            return Optional.of("Truck에는 Mando제동장치 사용 불가");
        }
        return Optional.empty();
    }
}
