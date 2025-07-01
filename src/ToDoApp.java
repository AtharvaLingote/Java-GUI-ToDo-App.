import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class ToDoApp {

    private static final String TASK_FILE = "data/tasks.txt";

    public static void main(String[] args) {
        JFrame frame = new JFrame("📝 To-Do List App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 500);
        frame.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("📋 Your Tasks", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        frame.add(titleLabel, BorderLayout.NORTH);

        JPanel taskPanel = new JPanel();
        taskPanel.setLayout(new BoxLayout(taskPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(taskPanel);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        JTextField taskField = new JTextField();
        JButton addButton = new JButton("Add Task");
        inputPanel.add(taskField, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);
        frame.add(inputPanel, BorderLayout.SOUTH);

        java.util.List<String> taskList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(TASK_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                taskList.add(line);
            }
        } catch (IOException e) {
            System.out.println("No existing tasks found.");
        }

        final Runnable[] renderTasks = new Runnable[1];
        renderTasks[0] = () -> {
            taskPanel.removeAll();
            for (String task : taskList) {
                JPanel taskBox = new JPanel(new BorderLayout());
                JLabel label = new JLabel("• " + task);
                label.setFont(new Font("SansSerif", Font.PLAIN, 16));
                JButton deleteButton = new JButton("❌");

                deleteButton.addActionListener(e -> {
                    taskList.remove(task);
                    saveTasksToFile(taskList);
                    renderTasks[0].run();
                });

                taskBox.add(label, BorderLayout.CENTER);
                taskBox.add(deleteButton, BorderLayout.EAST);
                taskPanel.add(taskBox);
            }
            taskPanel.revalidate();
            taskPanel.repaint();
        };

        addButton.addActionListener(e -> {
            String taskText = taskField.getText().trim();
            if (!taskText.isEmpty()) {
                taskList.add(taskText);
                saveTasksToFile(taskList);
                renderTasks[0].run();
                taskField.setText("");
            }
        });

        renderTasks[0].run();
        frame.setVisible(true);
    }

    private static void saveTasksToFile(java.util.List<String> tasks) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TASK_FILE))) {
            for (String task : tasks) {
                writer.write(task);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println(" Error saving tasks: " + e.getMessage());
        }
    }
}