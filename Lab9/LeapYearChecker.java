import java.util.Scanner;

public class LeapYearChecker {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Введите год: ");
            int year = Integer.parseInt(scanner.nextLine());

            if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
                System.out.println(year + " является високосным годом.");
            } else {
                System.out.println(year + " не является високосным годом.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Ошибка: введены некорректные данные. Введите целое число.");
        }
    }
}
