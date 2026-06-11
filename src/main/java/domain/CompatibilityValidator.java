package domain;

import domain.rule.*;

import java.util.List;
import java.util.Optional;

public class CompatibilityValidator {
    private final List<CompatibilityRule> rules;

    public CompatibilityValidator() {
        this.rules = List.of(
                new SedanContinentalRule(),
                new SuvToyotaRule(),
                new TruckWiaRule(),
                new TruckMandoRule(),
                new BoschBrakeSteeringRule()
        );
    }

    public boolean isValid(CarSpec spec) {
        return rules.stream().allMatch(rule -> rule.validate(spec).isEmpty());
    }

    public Optional<String> findViolation(CarSpec spec) {
        return rules.stream()
                .map(rule -> rule.validate(spec))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }
}
