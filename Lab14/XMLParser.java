import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Stack;

public class XMLParser extends JFrame {
    private JTree tree;
    private DefaultTreeModel treeModel;

    public XMLParser() {
        setTitle("XML Parser with JTree");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Создаем корневой узел дерева
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("XML Files");
        treeModel = new DefaultTreeModel(root);
        tree = new JTree(treeModel);
        
        // Настраиваем интерфейс
        JScrollPane scrollPane = new JScrollPane(tree);
        add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        JButton openButton = new JButton("Open XML File");
        openButton.addActionListener(e -> openXMLFile());
        buttonPanel.add(openButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void openXMLFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("XML Files", "xml"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            parseXMLFile(file);
        }
    }

    private void parseXMLFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder xmlContent = new StringBuilder();
            String line;
            
            // Читаем весь файл
            while ((line = reader.readLine()) != null) {
                xmlContent.append(line.trim());
            }
            
            // Парсим XML и строим дерево
            DefaultMutableTreeNode fileRoot = parseXML(xmlContent.toString());
            fileRoot.setUserObject(file.getName());
            
            // Обновляем модель дерева
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
            root.removeAllChildren();
            root.add(fileRoot);
            treeModel.reload();
            
            // Разворачиваем все узлы
            expandAllNodes(tree, 0, tree.getRowCount());
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error parsing XML: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private DefaultMutableTreeNode parseXML(String xml) {
        DefaultMutableTreeNode root = null;
        Stack<DefaultMutableTreeNode> nodeStack = new Stack<>();
        int index = 0;
        int length = xml.length();
        
        while (index < length) {
            // Ищем открывающий тег
            if (xml.charAt(index) == '<') {
                int endTag = xml.indexOf('>', index);
                if (endTag == -1) break;
                
                String tagContent = xml.substring(index + 1, endTag);
                
                // Проверяем, является ли тег закрывающим
                if (tagContent.startsWith("/")) {
                    // Закрывающий тег
                    if (!nodeStack.isEmpty()) {
                        nodeStack.pop();
                    }
                    index = endTag + 1;
                } else {
                    // Открывающий тег
                    String tagName = tagContent;
                    
                    // Проверяем самозакрывающиеся теги
                    boolean isSelfClosing = tagName.endsWith("/");
                    if (isSelfClosing) {
                        tagName = tagName.substring(0, tagName.length() - 1);
                    }
                    
                    tagName = tagName.trim();
                    
                    // Создаем узел
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(tagName);
                    
                    if (nodeStack.isEmpty()) {
                        root = node;
                        nodeStack.push(node);
                    } else {
                        nodeStack.peek().add(node);
                        if (!isSelfClosing) {
                            nodeStack.push(node);
                        }
                    }
                    
                    index = endTag + 1;
                    
                    // Если не самозакрывающийся тег, ищем текст внутри
                    if (!isSelfClosing) {
                        // Ищем следующий тег
                        int nextTagStart = xml.indexOf('<', index);
                        if (nextTagStart != -1 && nextTagStart > index) {
                            String textContent = xml.substring(index, nextTagStart).trim();
                            if (!textContent.isEmpty()) {
                                // Добавляем текст как дочерний узел
                                node.add(new DefaultMutableTreeNode("Text: " + textContent));
                            }
                            index = nextTagStart;
                        }
                    }
                }
            } else {
                index++;
            }
        }
        
        return root != null ? root : new DefaultMutableTreeNode("Empty");
    }

    private void expandAllNodes(JTree tree, int startingIndex, int rowCount) {
        for (int i = startingIndex; i < rowCount; ++i) {
            tree.expandRow(i);
        }
        
        if (tree.getRowCount() != rowCount) {
            expandAllNodes(tree, rowCount, tree.getRowCount());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            XMLParser parser = new XMLParser();
            parser.setVisible(true);
        });
    }
}
