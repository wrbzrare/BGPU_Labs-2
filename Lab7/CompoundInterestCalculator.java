import java.util.Scanner;

public class CompoundInterestCalculator {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Выберите режим:");
        System.out.println("1 - Вычислить итоговую сумму");
        System.out.println("2 - Вычислить процент");
        int mode = scanner.nextInt();

        if (mode == 1) {
            System.out.print("Введите начальное число: ");
            double principal = scanner.nextDouble();

            System.out.print("Введите процент: ");
            double percent = scanner.nextDouble();

            System.out.print("Введите количество периодов: ");
            int periods = scanner.nextInt();

            double rate = percent / 100;
            double result = principal * Math.pow(1 + rate, periods);

            System.out.println("Итоговая сумма: " + result);

        } else if (mode == 2) {
            System.out.print("Введите начальное число: ");
            double start = scanner.nextDouble();

            System.out.print("Введите конечное число: ");
            double end = scanner.nextDouble();

            System.out.print("Введите количество периодов: ");
            int periods = scanner.nextInt();

            double rate = Math.pow(end / start, 1.0 / periods) - 1;
            double percent = rate * 100;

            System.out.println("Необходимый процент: " + percent);

        } else {
            System.out.println("Неверный режим");
        }
    }
}
