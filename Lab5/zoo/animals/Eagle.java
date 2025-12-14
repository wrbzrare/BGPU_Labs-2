package zoo.animals;

public class Eagle extends Animal implements Flyable {

    public Eagle(String name) {
        super(name);
    }

    @Override
    public void makeSound() {
        System.out.println(name + " кричит");
    }

    @Override
    public void fly() {
        System.out.println(name + " летит");
    }
}
