package domain.rule;

import domain.CarSpec;
import domain.CarType;
import domain.Engine;

import java.util.Optional;

public class SuvToyotaRule implements CompatibilityRule {
    @Override
    public Optional<String> validate(CarSpec spec) {
        if (spec.getCarType() == CarType.SUV && spec.getEngine() == Engine.TOYOTA) {
            return Optional.of("SUV에는 TOYOTA엔진 사용 불가");
        }
        return Optional.empty();
    }
}
