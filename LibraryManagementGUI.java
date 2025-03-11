import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.*;

public class LibraryManagementGUI extends JFrame {
    private List<Book> books;
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private Map<Integer, LocalDate> issuedBooks;
    private Map<String, String> users;
    private String currentUserRole;

    public LibraryManagementGUI() {
        books = new ArrayList<>();
        issuedBooks = new HashMap<>();
        users = new HashMap<>();

        users.put("admin", "admin123");
        users.put("user", "user123");

        login();
        setTitle("Enhanced Library Management System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Manage Books", createBookPanel());
        tabbedPane.add("Search & Issue", createIssuePanel());
        tabbedPane.add("Membership Management", createMembershipPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void login() {
        while (true) {
            JTextField usernameField = new JTextField();
            JPasswordField passwordField = new JPasswordField();

            Object[] message = { "Username:", usernameField, "Password:", passwordField };
            int option = JOptionPane.showConfirmDialog(this, message, "Login", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                if (users.containsKey(username) && users.get(username).equals(password)) {
                    currentUserRole = username.equals("admin") ? "admin" : "user";
                    JOptionPane.showMessageDialog(this, "Login successful! Welcome " + currentUserRole + ".");
                    break;
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials. Try again.");
                }
            } else {
                System.exit(0);
            }
        }
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

        if (currentUserRole.equals("admin")) {
            buttonPanel.add(addBookBtn);
            buttonPanel.add(deleteBookBtn);
        }
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createIssuePanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2));
        JLabel bookIdLabel = new JLabel("Book ID:");
        JTextField bookIdField = new JTextField();
        JLabel issueDateLabel = new JLabel("Issue Date:");
        LocalDate today = LocalDate.now();
        JTextField issueDateField = new JTextField(today.toString());
        issueDateField.setEditable(false);
        JButton issueButton = new JButton("Issue Book");

        issueButton.addActionListener(e -> {
            try {
                int bookId = Integer.parseInt(bookIdField.getText());
                Optional<Book> bookOpt = books.stream().filter(b -> b.getId() == bookId && b.isAvailable()).findFirst();
                if (bookOpt.isPresent()) {
                    issuedBooks.put(bookId, today);
                    bookOpt.get().setAvailable(false);
                    updateTable();
                    JOptionPane.showMessageDialog(this, "Book issued successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Book not available or invalid ID.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Book ID.");
            }
        });

        panel.add(bookIdLabel);
        panel.add(bookIdField);
        panel.add(issueDateLabel);
        panel.add(issueDateField);
        panel.add(new JLabel());
        panel.add(issueButton);

        return panel;
    }

    private JPanel createMembershipPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1));
        JLabel membershipLabel = new JLabel("Select Membership Duration:");
        String[] options = {"6 months", "1 year", "2 years"};
        JComboBox<String> membershipOptions = new JComboBox<>(options);
        JButton confirmButton = new JButton("Confirm Membership");

        confirmButton.addActionListener(e -> {
            String selectedOption = (String) membershipOptions.getSelectedItem();
            JOptionPane.showMessageDialog(this, "Membership confirmed for: " + selectedOption);
        });

        panel.add(membershipLabel);
        panel.add(membershipOptions);
        panel.add(confirmButton);
        return panel;
    }

    private void addBook() {
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();

        Object[] message = {
                "Book Title:", titleField,
                "Author:", authorField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add Book", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String title = titleField.getText();
            String author = authorField.getText();
            if (!title.isEmpty() && !author.isEmpty()) {
                Book book = new Book(books.size() + 1, title, author, true);
                books.add(book);
                updateTable();
                JOptionPane.showMessageDialog(this, "Book added successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            }
        }
    }

    private void deleteBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow != -1) {
            int bookId = (int) tableModel.getValueAt(selectedRow, 0);
            books.removeIf(book -> book.getId() == bookId);
            updateTable();
            JOptionPane.showMessageDialog(this, "Book deleted successfully!");
        } else {
            JOptionPane.showMessageDialog(this, "Please select a book to delete.");
        }
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        for (Book book : books) {
            tableModel.addRow(new Object[]{
                    book.getId(), book.getTitle(), book.getAuthor(), book.isAvailable() ? "Available" : "Issued"
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibraryManagementGUI().setVisible(true));
    }
}
