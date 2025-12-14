import java.io.*;
import java.util.*;
import java.util.zip.*;

public class FileCompression {
    
    // Метод для создания сжатой версии с удалением дубликатов
    public static void compressFile(String inputFile, String compressedFile) throws IOException {
        Map<String, Integer> lineMap = new HashMap<>();
        List<String> lines = new ArrayList<>();
        List<Integer> lineIndices = new ArrayList<>();
        
        // Чтение и обработка файла
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            int index = 0;
            
            while ((line = reader.readLine()) != null) {
                if (!lineMap.containsKey(line)) {
                    lineMap.put(line, index++);
                    lines.add(line);
                }
                lineIndices.add(lineMap.get(line));
            }
        }
        
        // Запись сжатой версии
        try (DataOutputStream dos = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(compressedFile)))) {
            
            // Записываем количество уникальных строк
            dos.writeInt(lines.size());
            
            // Записываем уникальные строки
            for (String uniqueLine : lines) {
                dos.writeUTF(uniqueLine);
            }
            
            // Записываем последовательность индексов
            dos.writeInt(lineIndices.size());
            for (Integer idx : lineIndices) {
                dos.writeInt(idx);
            }
            
            // Записываем статистику
            dos.writeInt(lineIndices.size() - lines.size()); // количество удаленных дубликатов
        }
        
        System.out.println("Файл сжат. Удалено " + (lineIndices.size() - lines.size()) + " дубликатов.");
        System.out.println("Исходный размер: " + lineIndices.size() + " строк");
        System.out.println("Сжатый размер: " + lines.size() + " уникальных строк");
    }
    
    // Метод для восстановления исходного файла из сжатой версии
    public static void decompressFile(String compressedFile, String outputFile) throws IOException {
        List<String> lines = new ArrayList<>();
        List<Integer> lineIndices = new ArrayList<>();
        
        try (DataInputStream dis = new DataInputStream(
                new BufferedInputStream(new FileInputStream(compressedFile)))) {
            
            // Читаем уникальные строки
            int uniqueCount = dis.readInt();
            for (int i = 0; i < uniqueCount; i++) {
                lines.add(dis.readUTF());
            }
            
            // Читаем последовательность индексов
            int totalCount = dis.readInt();
            for (int i = 0; i < totalCount; i++) {
                lineIndices.add(dis.readInt());
            }
            
            // Читаем статистику (опционально)
            int removedDuplicates = dis.readInt();
            System.out.println("Восстановление: было удалено " + removedDuplicates + " дубликатов");
        }
        
        // Восстанавливаем исходный файл
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            for (Integer idx : lineIndices) {
                writer.println(lines.get(idx));
            }
        }
        
        System.out.println("Файл восстановлен. Восстановлено строк: " + lineIndices.size());
    }
    
    // Альтернативный вариант с использованием GZIP
    public static void compressWithGZIP(String inputFile, String compressedFile) throws IOException {
        StringBuilder content = new StringBuilder();
        Map<String, Integer> uniqueLines = new LinkedHashMap<>();
        int duplicateCount = 0;
        
        // Чтение и обработка файла
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            int lineNumber = 1;
            
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
                
                if (uniqueLines.containsKey(line)) {
                    duplicateCount++;
                    System.out.println("Дубликат в строке " + lineNumber + 
                                     " (первый раз в строке " + uniqueLines.get(line) + "): " + line);
                } else {
                    uniqueLines.put(line, lineNumber);
                }
                lineNumber++;
            }
        }
        
        // Сжатие с использованием GZIP
        try (GZIPOutputStream gzos = new GZIPOutputStream(
                new FileOutputStream(compressedFile));
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(gzos, "UTF-8"))) {
            
            // Записываем информацию о дубликатах
            writer.println("=== СЖАТАЯ ВЕРСИЯ ФАЙЛА ===");
            writer.println("Удалено дублирующихся строк: " + duplicateCount);
            writer.println("Осталось уникальных строк: " + uniqueLines.size());
            writer.println("=== НАЧАЛО ДАННЫХ ===");
            
            // Записываем уникальные строки
            for (String uniqueLine : uniqueLines.keySet()) {
                writer.println(uniqueLine);
            }
        }
        
        System.out.println("GZIP сжатие завершено. Файл: " + compressedFile);
    }
    
    // Восстановление из GZIP
    public static void decompressGZIP(String compressedFile, String outputFile) throws IOException {
        try (GZIPInputStream gzis = new GZIPInputStream(
                new FileInputStream(compressedFile));
             BufferedReader reader = new BufferedReader(new InputStreamReader(gzis, "UTF-8"));
             PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            
            String line;
            boolean dataSection = false;
            int linesWritten = 0;
            
            while ((line = reader.readLine()) != null) {
                if (line.equals("=== НАЧАЛО ДАННЫХ ===")) {
                    dataSection = true;
                    continue;
                }
                
                if (dataSection) {
                    writer.println(line);
                    linesWritten++;
                } else {
                    System.out.println("Метаданные: " + line);
                }
            }
            
            System.out.println("Восстановлено строк: " + linesWritten);
        }
    }
    
    public static void main(String[] args) {
        try {
            String inputFile = "input.txt";
            String compressedFile = "compressed.dat";
            String restoredFile = "restored.txt";
            String gzipFile = "compressed.gz";
            
            System.out.println("=== Вариант 1: Сжатие с индексами ===");
            compressFile(inputFile, compressedFile);
            decompressFile(compressedFile, restoredFile);
            
            System.out.println("\n=== Вариант 2: Сжатие GZIP ===");
            compressWithGZIP(inputFile, gzipFile);
            decompressGZIP(gzipFile, "restored_gzip.txt");
            
            // Проверка целостности
            if (compareFiles(inputFile, restoredFile)) {
                System.out.println("Проверка: файлы идентичны!");
            } else {
                System.out.println("Проверка: файлы РАЗЛИЧАЮТСЯ!");
            }
            
        } catch (IOException e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Вспомогательный метод для сравнения файлов
    private static boolean compareFiles(String file1, String file2) throws IOException {
        List<String> lines1 = readAllLines(file1);
        List<String> lines2 = readAllLines(file2);
        
        if (lines1.size() != lines2.size()) {
            System.out.println("Размеры файлов различаются: " + 
                             lines1.size() + " vs " + lines2.size());
            return false;
        }
        
        for (int i = 0; i < lines1.size(); i++) {
            if (!lines1.get(i).equals(lines2.get(i))) {
                System.out.println("Различие в строке " + (i + 1) + ":");
                System.out.println("  Файл1: " + lines1.get(i));
                System.out.println("  Файл2: " + lines2.get(i));
                return false;
            }
        }
        
        return true;
    }
    
    private static List<String> readAllLines(String filename) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }
}
