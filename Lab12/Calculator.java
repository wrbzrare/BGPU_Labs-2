import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Calculator extends JFrame {
    private JTextField display;
    private double firstNumber = 0;
    private String operator = "";
    private boolean startNewInput = true;
    
    public Calculator() {
        setTitle("Калькулятор");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // Создание дисплея
        display = new JTextField("0");
        display.setFont(new Font("Arial", Font.BOLD, 24));
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setEditable(false);
        display.setBackground(Color.WHITE);
        add(display, BorderLayout.NORTH);
        
        // Создание панели с кнопками
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 4, 5, 5));
        
        // Массив кнопок
        String[] buttons = {
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "0", ".", "=", "+",
            "C", "CE", "±", "⌫"
        };
        
        // Создание и добавление кнопок
        for (String text : buttons) {
            JButton button = createButton(text);
            buttonPanel.add(button);
        }
        
        add(buttonPanel, BorderLayout.CENTER);
        setVisible(true);
    }
    
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        
        // Установка цветов для разных типов кнопок
        if (text.matches("[0-9.]")) {
            button.setBackground(new Color(240, 240, 240));
        } else if (text.equals("=")) {
            button.setBackground(new Color(70, 130, 180));
            button.setForeground(Color.WHITE);
        } else if (text.matches("[+\\-*/]")) {
            button.setBackground(new Color(169, 169, 169));
        } else {
            button.setBackground(new Color(220, 220, 220));
        }
        
        button.addActionListener(new ButtonClickListener());
        
        // Добавление обработки нажатия клавиш
        button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
              .put(KeyStroke.getKeyStroke(text.charAt(0)), text);
        button.getActionMap().put(text, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button.doClick();
            }
        });
        
        return button;
    }
    
    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            
            if (command.matches("[0-9]")) {
                handleDigit(command);
            } else if (command.equals(".")) {
                handleDecimalPoint();
            } else if (command.matches("[+\\-*/]")) {
                handleOperator(command);
            } else if (command.equals("=")) {
                handleEquals();
            } else if (command.equals("C")) {
                handleClear();
            } else if (command.equals("CE")) {
                handleClearEntry();
            } else if (command.equals("±")) {
                handleSignChange();
            } else if (command.equals("⌫")) {
                handleBackspace();
            }
        }
    }
    
    private void handleDigit(String digit) {
        if (startNewInput) {
            display.setText(digit);
            startNewInput = false;
        } else {
            String currentText = display.getText();
            if (currentText.equals("0")) {
                display.setText(digit);
            } else {
                display.setText(currentText + digit);
            }
        }
    }
    
    private void handleDecimalPoint() {
        if (startNewInput) {
            display.setText("0.");
            startNewInput = false;
        } else if (!display.getText().contains(".")) {
            display.setText(display.getText() + ".");
        }
    }
    
    private void handleOperator(String op) {
        if (!operator.isEmpty() && !startNewInput) {
            handleEquals();
        }
        
        try {
            firstNumber = Double.parseDouble(display.getText());
            operator = op;
            startNewInput = true;
        } catch (NumberFormatException e) {
            display.setText("Ошибка");
            startNewInput = true;
        }
    }
    
    private void handleEquals() {
        if (operator.isEmpty()) return;
        
        try {
            double secondNumber = Double.parseDouble(display.getText());
            double result = 0;
            
            switch (operator) {
                case "+":
                    result = firstNumber + secondNumber;
                    break;
                case "-":
                    result = firstNumber - secondNumber;
                    break;
                case "*":
                    result = firstNumber * secondNumber;
                    break;
                case "/":
                    if (secondNumber == 0) {
                        display.setText("Деление на 0!");
                        startNewInput = true;
                        operator = "";
                        return;
                    }
                    result = firstNumber / secondNumber;
                    break;
            }
            
            // Форматирование результата
            if (result == (long) result) {
                display.setText(String.format("%d", (long) result));
            } else {
                display.setText(String.format("%.10f", result).replaceAll("0*$", "").replaceAll("\\.$", ""));
            }
            
            operator = "";
            startNewInput = true;
            
        } catch (NumberFormatException e) {
            display.setText("Ошибка");
            startNewInput = true;
            operator = "";
        }
    }
    
    private void handleClear() {
        display.setText("0");
        firstNumber = 0;
        operator = "";
        startNewInput = true;
    }
    
    private void handleClearEntry() {
        display.setText("0");
        startNewInput = true;
    }
    
    private void handleSignChange() {
        try {
            double value = Double.parseDouble(display.getText());
            value = -value;
            
            if (value == (long) value) {
                display.setText(String.format("%d", (long) value));
            } else {
                display.setText(String.valueOf(value));
            }
            
            startNewInput = true;
        } catch (NumberFormatException e) {
            display.setText("Ошибка");
            startNewInput = true;
        }
    }
    
    private void handleBackspace() {
        String currentText = display.getText();
        if (currentText.length() > 1) {
            display.setText(currentText.substring(0, currentText.length() - 1));
        } else {
            display.setText("0");
            startNewInput = true;
        }
    }
    
    public static void main(String[] args) {
        // Запуск в потоке обработки событий
        SwingUtilities.invokeLater(() -> {
            new Calculator();
        });
    }
}
