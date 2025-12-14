import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class TextEditorWithAutoCorrect extends JFrame {
    private JTextArea textArea;
    private JFileChooser fileChooser;
    private File currentFile;
    
    // Карта для автозамены (неправильное → правильное)
    private Map<String, String> autoCorrectMap;
    
    // Флаги для управления потоками
    private AtomicBoolean autoCorrectRunning;
    private Thread autoCorrectThread;
    
    // Таймер для запуска проверки
    private Timer autoCorrectTimer;

    public TextEditorWithAutoCorrect() {
        // Настройка главного окна
        setTitle("Текстовый редактор с автозаменой");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Инициализация карты автозамены
        initializeAutoCorrectMap();
        
        // Инициализация флагов
        autoCorrectRunning = new AtomicBoolean(false);

        // Создание текстовой области с прокруткой
        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);
        
        // Панель статуса
        JLabel statusLabel = new JLabel("Готово");
        add(statusLabel, BorderLayout.SOUTH);

        // Инициализация JFileChooser
        fileChooser = new JFileChooser();

        // Создание меню
        createMenuBar();
        
        // Настройка автозамены
        setupAutoCorrect();

        // Отображение окна
        setVisible(true);
    }
    
    private void initializeAutoCorrectMap() {
        autoCorrectMap = new HashMap<>();
        // Примеры автозамены
        autoCorrectMap.put("програма", "программа");
        autoCorrectMap.put("програмирование", "программирование");
        autoCorrectMap.put("кофэ", "кофе");
        autoCorrectMap.put("здраствуйте", "здравствуйте");
        autoCorrectMap.put("привед", "привет");
        autoCorrectMap.put("огромное", "огромное");
        autoCorrectMap.put("пака", "пока");
        autoCorrectMap.put("спосибо", "спасибо");
        autoCorrectMap.put("извеняюсь", "извиняюсь");
        autoCorrectMap.put("симпотичный", "симпатичный");
    }
    
    private void setupAutoCorrect() {
        // Вариант 1: Запуск по нажатию пробела
        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    startAutoCorrectThread();
                }
            }
        });
        
        // Вариант 2: Запуск по таймеру (каждые 3 секунды)
        autoCorrectTimer = new Timer(3000, e -> {
            if (!textArea.getText().isEmpty()) {
                startAutoCorrectThread();
            }
        });
        autoCorrectTimer.start();
        
        // Добавим меню для управления автозаменой
        JMenu autoCorrectMenu = new JMenu("Автозамена");
        
        JMenuItem toggleTimerItem = new JMenuItem("Включить/выключить таймер");
        toggleTimerItem.addActionListener(e -> {
            if (autoCorrectTimer.isRunning()) {
                autoCorrectTimer.stop();
                JOptionPane.showMessageDialog(this, "Таймер автозамены выключен");
            } else {
                autoCorrectTimer.start();
                JOptionPane.showMessageDialog(this, "Таймер автозамены включен");
            }
        });
        
        JMenuItem manualCorrectItem = new JMenuItem("Выполнить автозамену");
        manualCorrectItem.addActionListener(e -> startAutoCorrectThread());
        
        JMenuItem editDictionaryItem = new JMenuItem("Редактировать словарь");
        editDictionaryItem.addActionListener(e -> editDictionary());
        
        autoCorrectMenu.add(toggleTimerItem);
        autoCorrectMenu.add(manualCorrectItem);
        autoCorrectMenu.addSeparator();
        autoCorrectMenu.add(editDictionaryItem);
        
        // Добавляем меню в строку меню
        JMenuBar menuBar = getJMenuBar();
        if (menuBar != null) {
            menuBar.add(autoCorrectMenu);
        }
    }
    
    private void startAutoCorrectThread() {
        // Если поток уже работает, не запускаем новый
        if (autoCorrectRunning.get()) {
            return;
        }
        
        autoCorrectRunning.set(true);
        
        autoCorrectThread = new Thread(() -> {
            try {
                // Даем пользователю немного времени закончить ввод
                Thread.sleep(100);
                
                // Получаем текст из текстовой области в потоке EDT
                String text = SwingUtilities.invokeAndWait(() -> textArea.getText());
                
                // Выполняем автозамену
                String correctedText = performAutoCorrect(text);
                
                // Если текст изменился, обновляем его в потоке EDT
                if (!text.equals(correctedText)) {
                    SwingUtilities.invokeLater(() -> {
                        textArea.setText(correctedText);
                        // Показываем сообщение о выполненной замене
                        JOptionPane.showMessageDialog(
                            TextEditorWithAutoCorrect.this,
                            "Автозамена выполнена!",
                            "Автозамена",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                autoCorrectRunning.set(false);
            }
        });
        
        autoCorrectThread.start();
    }
    
    private String performAutoCorrect(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        
        StringBuilder result = new StringBuilder(text);
        
        // Проходим по всем словам в карте автозамены
        for (Map.Entry<String, String> entry : autoCorrectMap.entrySet()) {
            String wrongWord = entry.getKey();
            String correctWord = entry.getValue();
            
            // Ищем и заменяем неправильные слова
            int index = result.indexOf(wrongWord);
            while (index != -1) {
                // Проверяем, что это отдельное слово (не часть другого слова)
                boolean isWordBoundary = true;
                
                // Проверяем символ перед словом
                if (index > 0) {
                    char before = result.charAt(index - 1);
                    if (Character.isLetterOrDigit(before)) {
                        isWordBoundary = false;
                    }
                }
                
                // Проверяем символ после слова
                int endIndex = index + wrongWord.length();
                if (endIndex < result.length()) {
                    char after = result.charAt(endIndex);
                    if (Character.isLetterOrDigit(after)) {
                        isWordBoundary = false;
                    }
                }
                
                if (isWordBoundary) {
                    // Заменяем слово
                    result.replace(index, index + wrongWord.length(), correctWord);
                    // Обновляем индекс для поиска следующего вхождения
                    index = result.indexOf(wrongWord, index + correctWord.length());
                } else {
                    // Пропускаем, если это не отдельное слово
                    index = result.indexOf(wrongWord, index + 1);
                }
            }
        }
        
        return result.toString();
    }
    
    private void editDictionary() {
        StringBuilder dictionaryText = new StringBuilder();
        for (Map.Entry<String, String> entry : autoCorrectMap.entrySet()) {
            dictionaryText.append(entry.getKey())
                         .append(" -> ")
                         .append(entry.getValue())
                         .append("\n");
        }
        
        JTextArea editArea = new JTextArea(dictionaryText.toString(), 10, 30);
        JScrollPane scrollPane = new JScrollPane(editArea);
        
        int result = JOptionPane.showConfirmDialog(
            this,
            scrollPane,
            "Редактирование словаря автозамены",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                autoCorrectMap.clear();
                String[] lines = editArea.getText().split("\n");
                for (String line : lines) {
                    String[] parts = line.split("->");
                    if (parts.length == 2) {
                        String wrong = parts[0].trim();
                        String correct = parts[1].trim();
                        if (!wrong.isEmpty() && !correct.isEmpty()) {
                            autoCorrectMap.put(wrong, correct);
                        }
                    }
                }
                JOptionPane.showMessageDialog(this, "Словарь обновлен!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Ошибка при обновлении словаря", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Меню "Файл"
        JMenu fileMenu = new JMenu("Файл");

        JMenuItem openItem = new JMenuItem("Открыть");
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        openItem.addActionListener(e -> openFile());

        JMenuItem saveItem = new JMenuItem("Сохранить");
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        saveItem.addActionListener(e -> saveFile());

        JMenuItem saveAsItem = new JMenuItem("Сохранить как");
        saveAsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, 
            InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        saveAsItem.addActionListener(e -> saveFileAs());

        JMenuItem exitItem = new JMenuItem("Выход");
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        exitItem.addActionListener(e -> {
            // Останавливаем таймер перед выходом
            if (autoCorrectTimer != null) {
                autoCorrectTimer.stop();
            }
            System.exit(0);
        });

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // Меню "Правка"
        JMenu editMenu = new JMenu("Правка");

        JMenuItem cutItem = new JMenuItem("Вырезать");
        cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
        cutItem.addActionListener(e -> textArea.cut());

        JMenuItem copyItem = new JMenuItem("Копировать");
        copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        copyItem.addActionListener(e -> textArea.copy());

        JMenuItem pasteItem = new JMenuItem("Вставить");
        pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
        pasteItem.addActionListener(e -> textArea.paste());

        JMenuItem selectAllItem = new JMenuItem("Выделить все");
        selectAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
        selectAllItem.addActionListener(e -> textArea.selectAll());

        editMenu.add(cutItem);
        editMenu.add(copyItem);
        editMenu.add(pasteItem);
        editMenu.addSeparator();
        editMenu.add(selectAllItem);

        // Меню "Справка"
        JMenu helpMenu = new JMenu("Справка");
        JMenuItem aboutItem = new JMenuItem("О программе");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

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
                setTitle("Текстовый редактор с автозаменой - " + selectedFile.getName());
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
            setTitle("Текстовый редактор с автозаменой - " + file.getName());
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
            "Текстовый редактор с автозаменой v1.0\n" +
            "Функции:\n" +
            "- Открытие и сохранение файлов\n" +
            "- Автозамена неправильных слов\n" +
            "- Замена работает в отдельном потоке\n" +
            "- Запуск по пробелу или по таймеру",
            "О программе",
            JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TextEditorWithAutoCorrect();
        });
    }
}
