import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LibraryManagementGUI extends JFrame {
    private List<Book> books;
    private JTable bookTable;
    private DefaultTableModel tableModel;

    public LibraryManagementGUI() {
        books = new ArrayList<>();
        setTitle("Enhanced Library Management System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Manage Books", createBookPanel());
        tabbedPane.add("Search & Issue", createIssuePanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createBookPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        tableModel = new DefaultTableModel(new Object[]{"ID", "Title", "Author", "Availability"}, 0);
        bookTable = new JTable(tableModel);
        panel.add(new JScrollPane(bookTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addBookBtn = new JButton("Add Book");
        JButton deleteBookBtn = new JButton("Delete Book");

        addBookBtn.addActionListener(e -> addBook());
        deleteBookBtn.addActionListener(e -> deleteBook());

        buttonPanel.add(addBookBtn);
        buttonPanel.add(deleteBookBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createIssuePanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        JTextField searchField = new JTextField();
        JTextArea resultArea = new JTextArea(10, 40);
        resultArea.setEditable(false);

        JButton searchBtn = new JButton("Search Book");
        JButton issueBtn = new JButton("Issue Book");
        JButton returnBtn = new JButton("Return Book");

        searchBtn.addActionListener(e -> searchBook(searchField.getText(), resultArea));
        issueBtn.addActionListener(e -> issueBook(resultArea));
        returnBtn.addActionListener(e -> returnBook(resultArea));

        panel.add(new JLabel("Enter Title or Author to Search:"));
        panel.add(searchField);
        panel.add(searchBtn);
        panel.add(new JScrollPane(resultArea));
        panel.add(issueBtn);
        panel.add(returnBtn);

        return panel;
    }

    private void addBook() {
        try {
            int id = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter Book ID:"));
            String title = JOptionPane.showInputDialog(this, "Enter Book Title:");
            String author = JOptionPane.showInputDialog(this, "Enter Book Author:");

            books.add(new Book(id, title, author));
            tableModel.addRow(new Object[]{id, title, author, "Available"});
            JOptionPane.showMessageDialog(this, "Book added successfully!");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter valid details.");
        }
    }

    private void deleteBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow != -1) {
            books.remove(selectedRow);
            tableModel.removeRow(selectedRow);
            JOptionPane.showMessageDialog(this, "Book deleted successfully!");
        } else {
            JOptionPane.showMessageDialog(this, "Please select a book to delete.");
        }
    }

    private void searchBook(String query, JTextArea resultArea) {
        resultArea.setText("");
        for (Book book : books) {
            if (book.getTitle().toLowerCase().contains(query.toLowerCase()) || book.getAuthor().toLowerCase().contains(query.toLowerCase())) {
                resultArea.append(book.getId() + ": " + book.getTitle() + " by " + book.getAuthor() + " (" + (book.isAvailable() ? "Available" : "Issued") + ")\n");
            }
        }
    }

    private void issueBook(JTextArea resultArea) {
        String idStr = JOptionPane.showInputDialog(this, "Enter Book ID to Issue:");
        try {
            int id = Integer.parseInt(idStr);
            for (Book book : books) {
                if (book.getId() == id && book.isAvailable()) {
                    book.setAvailable(false);
                    updateTable();
                    JOptionPane.showMessageDialog(this, "Book issued successfully!");
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "Book not found or already issued.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid ID format.");
        }
    }

    private void returnBook(JTextArea resultArea) {
        String idStr = JOptionPane.showInputDialog(this, "Enter Book ID to Return:");
        try {
            int id = Integer.parseInt(idStr);
            for (Book book : books) {
                if (book.getId() == id && !book.isAvailable()) {
                    book.setAvailable(true);
                    updateTable();
                    JOptionPane.showMessageDialog(this, "Book returned successfully!");
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "Invalid ID or book not issued.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid ID format.");
        }
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        for (Book book : books) {
            tableModel.addRow(new Object[]{book.getId(), book.getTitle(), book.getAuthor(), book.isAvailable() ? "Available" : "Issued"});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibraryManagementGUI().setVisible(true));
    }
}