// Независимый класс
public class Engine {
    public void start() {
        System.out.println("Двигатель запущен");
    }
}

// Агрегация: машина использует двигатель, но не владеет им полностью
public class Car {
    private Engine engine;

    public Car(Engine engine) {
        this.engine = engine;
    }

    public void drive() {
        engine.start();
        System.out.println("Машина едет");
    }
}

// Демонстрация агрегации
public class Main {
    public static void main(String[] args) {
        Engine engine = new Engine(); // двигатель существует сам по себе
        Car car = new Car(engine);

        car.drive();
    }
}
