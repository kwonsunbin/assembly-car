package domain.rule;

import domain.CarSpec;
import domain.CarType;
import domain.Engine;

import java.util.Optional;

public class TruckWiaRule implements CompatibilityRule {
    @Override
    public Optional<String> validate(CarSpec spec) {
        if (spec.getCarType() == CarType.TRUCK && spec.getEngine() == Engine.WIA) {
            return Optional.of("Truck에는 WIA엔진 사용 불가");
        }
        return Optional.empty();
    }
}
