// Часть целого
class Heart {
    public void beat() {
        System.out.println("Сердце бьётся");
    }
}

// Композиция: человек полностью владеет сердцем
public class Human {
    private Heart heart;

    public Human() {
        this.heart = new Heart(); // создаётся внутри
    }

    public void live() {
        heart.beat();
        System.out.println("Человек живёт");
    }
}

// Демонстрация композиции
public class Main {
    public static void main(String[] args) {
        Human human = new Human();
        human.live();
    }
}
