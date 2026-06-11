package domain;

public class CarSpec {
    private CarType carType;
    private Engine engine;
    private BrakeSystem brakeSystem;
    private SteeringSystem steeringSystem;

    public CarType getCarType() { return carType; }
    public void setCarType(CarType carType) { this.carType = carType; }

    public Engine getEngine() { return engine; }
    public void setEngine(Engine engine) { this.engine = engine; }

    public BrakeSystem getBrakeSystem() { return brakeSystem; }
    public void setBrakeSystem(BrakeSystem brakeSystem) { this.brakeSystem = brakeSystem; }

    public SteeringSystem getSteeringSystem() { return steeringSystem; }
    public void setSteeringSystem(SteeringSystem steeringSystem) { this.steeringSystem = steeringSystem; }
}
