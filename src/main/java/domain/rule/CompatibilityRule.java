package domain.rule;

import domain.CarSpec;

import java.util.Optional;

public interface CompatibilityRule {
    Optional<String> validate(CarSpec spec);
}
