package zoo;

import zoo.animals.*;

public class ZooGame {
    public static void main(String[] args) {
        Animal lion = new Lion("Симба");
        Animal eagle = new Eagle("Орёл");

        lion.makeSound();
        lion.eat();

        eagle.makeSound();
        eagle.eat();

        Flyable flyable = (Flyable) eagle;
        flyable.fly();
    }
}
