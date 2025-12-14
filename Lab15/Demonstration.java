
import javax.swing.*;
import java.awt.*;

public class LayoutManagerDemo {
    public static void main(String[] args) {
        // Создаем главное окно
        JFrame frame = new JFrame("Демо менеджеров компоновки");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        
        // Устанавливаем BorderLayout для главного окна
        frame.setLayout(new BorderLayout());
        
        // Создаем панель с FlowLayout
        JPanel flowPanel = new JPanel();
        flowPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        flowPanel.setBackground(new Color(230, 240, 255));
        
        // Добавляем компоненты на FlowLayout панель
        flowPanel.add(new JButton("Кнопка 1"));
        flowPanel.add(new JButton("Кнопка 2"));
        flowPanel.add(new JButton("Кнопка 3"));
        flowPanel.add(new JButton("Кнопка 4"));
        flowPanel.add(new JButton("Кнопка 5"));
        
        // Добавляем метку с описанием в центр панели
        JLabel label = new JLabel("Это панель с FlowLayout", SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(200, 30));
        flowPanel.add(label);
        
        // Добавляем компоненты в разные области BorderLayout
        frame.add(new JButton("Север (North)"), BorderLayout.NORTH);
        frame.add(new JButton("Юг (South)"), BorderLayout.SOUTH);
        frame.add(new JButton("Запад (West)"), BorderLayout.WEST);
        frame.add(new JButton("Восток (East)"), BorderLayout.EAST);
        
        // Помещаем нашу FlowLayout панель в центр BorderLayout
        frame.add(flowPanel, BorderLayout.CENTER);
        
        // Делаем окно видимым
        frame.setVisible(true);
    }
}
