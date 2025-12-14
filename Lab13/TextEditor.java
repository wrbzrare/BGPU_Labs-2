import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class TextEditor extends JFrame {
    private JTextArea textArea;
    private JFileChooser fileChooser;
    private File currentFile;

    public TextEditor() {
        // Настройка главного окна
        setTitle("Текстовый редактор");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Создание текстовой области с прокруткой
        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        // Инициализация JFileChooser
        fileChooser = new JFileChooser();

        // Создание меню
        createMenuBar();

        // Отображение окна
        setVisible(true);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Меню "Файл"
        JMenu fileMenu = new JMenu("Файл");

        // Пункт "Открыть"
        JMenuItem openItem = new JMenuItem("Открыть");
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        openItem.addActionListener(e -> openFile());

        // Пункт "Сохранить"
        JMenuItem saveItem = new JMenuItem("Сохранить");
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        saveItem.addActionListener(e -> saveFile());

        // Пункт "Сохранить как"
        JMenuItem saveAsItem = new JMenuItem("Сохранить как");
        saveAsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, 
            InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        saveAsItem.addActionListener(e -> saveFileAs());

        // Пункт "Выход"
        JMenuItem exitItem = new JMenuItem("Выход");
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        exitItem.addActionListener(e -> System.exit(0));

        // Добавление пунктов в меню
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // Меню "Правка"
        JMenu editMenu = new JMenu("Правка");

        // Пункт "Вырезать"
        JMenuItem cutItem = new JMenuItem("Вырезать");
        cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
        cutItem.addActionListener(e -> textArea.cut());

        // Пункт "Копировать"
        JMenuItem copyItem = new JMenuItem("Копировать");
        copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        copyItem.addActionListener(e -> textArea.copy());

        // Пункт "Вставить"
        JMenuItem pasteItem = new JMenuItem("Вставить");
        pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
        pasteItem.addActionListener(e -> textArea.paste());

        // Пункт "Удалить"
        JMenuItem deleteItem = new JMenuItem("Удалить");
        deleteItem.addActionListener(e -> textArea.replaceSelection(""));

        // Пункт "Выделить все"
        JMenuItem selectAllItem = new JMenuItem("Выделить все");
        selectAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
        selectAllItem.addActionListener(e -> textArea.selectAll());

        // Добавление пунктов в меню "Правка"
        editMenu.add(cutItem);
        editMenu.add(copyItem);
        editMenu.add(pasteItem);
        editMenu.add(deleteItem);
        editMenu.addSeparator();
        editMenu.add(selectAllItem);

        // Меню "Справка"
        JMenu helpMenu = new JMenu("Справка");
        JMenuItem aboutItem = new JMenuItem("О программе");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        // Добавление меню в строку меню
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void openFile() {
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                textArea.setText(content.toString());
                currentFile = selectedFile;
                setTitle("Текстовый редактор - " + selectedFile.getName());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                    "Ошибка при открытии файла: " + e.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveFile() {
        if (currentFile != null) {
            saveToFile(currentFile);
        } else {
            saveFileAs();
        }
    }

    private void saveFileAs() {
        int returnValue = fileChooser.showSaveDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            saveToFile(selectedFile);
        }
    }

    private void saveToFile(File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(textArea.getText());
            currentFile = file;
            setTitle("Текстовый редактор - " + file.getName());
            JOptionPane.showMessageDialog(this,
                "Файл успешно сохранен!",
                "Сохранение",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Ошибка при сохранении файла: " + e.getMessage(),
                "Ошибка",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
            "Текстовый редактор",
            "О программе",
            JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        // Запуск приложения в потоке обработки событий EDT
        SwingUtilities.invokeLater(() -> {
            new TextEditor();
        });
    }
}
