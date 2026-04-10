import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

// Base class (Inheritance)
class Person implements Serializable {
    protected int id;
    protected String name;

    public Person(int id, String name) {
        this.id = id;
        this.name = name;
    }
}

// Derived class
class User extends Person {
    public User(int id, String name) {
        super(id, name);
    }
}

// Book class (Encapsulation)
class Book implements Serializable {
    private int bookId;
    private String title;
    private boolean isBorrowed;
    private LocalDate borrowDate;
    private User borrower;

    public Book(int bookId, String title) {
        this.bookId = bookId;
        this.title = title;
        this.isBorrowed = false;
    }

    public int getBookId() { return bookId; }
    public String getTitle() { return title; }
    public boolean isBorrowed() { return isBorrowed; }

    public void setBorrowed(boolean val) { isBorrowed = val; }

    public void setBorrowDate(LocalDate date) {
        borrowDate = date;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrower(User user) {
        borrower = user;
    }

    public User getBorrower() {
        return borrower;
    }
}

// Library class
class Library implements Serializable {
    private ArrayList<Book> books = new ArrayList<>();

    // Method Overloading (Polymorphism)
    public void addBook(int id, String title) {
        books.add(new Book(id, title));
    }

    public String borrowBook(int bookId, int userId, String name) {
        for (Book b : books) {
            if (b.getBookId() == bookId) {
                if (!b.isBorrowed()) {
                    User u = new User(userId, name);
                    b.setBorrowed(true);
                    b.setBorrowDate(LocalDate.now());
                    b.setBorrower(u);

                    return "Borrowed: " + b.getTitle() +
                           "\nUser: " + name +
                           "\nDate: " + b.getBorrowDate();
                } else {
                    return "Already Borrowed!";
                }
            }
        }
        return "Book Not Found!";
    }

    public String returnBook(int id) {
        for (Book b : books) {
            if (b.getBookId() == id) {
                if (b.isBorrowed()) {

                    LocalDate returnDate = LocalDate.now();
                    long days = ChronoUnit.DAYS.between(
                            b.getBorrowDate(), returnDate);

                    long lateDays = (days > 5) ? (days - 5) : 0;
                    int fine = (int) lateDays * 5;

                    User u = b.getBorrower();
                    b.setBorrowed(false);

                    return "Returned: " + b.getTitle() +
                           "\nUser: " + u.name +
                           "\nBorrow Date: " + b.getBorrowDate() +
                           "\nReturn Date: " + returnDate +
                           "\nLate Days: " + lateDays +
                           "\nFine: ₹" + fine;
                } else {
                    return "Book Not Borrowed!";
                }
            }
        }
        return "Book Not Found!";
    }

    public String viewIssuedBooks() {
        String result = "";
        for (Book b : books) {
            if (b.isBorrowed()) {
                result += "ID: " + b.getBookId() +
                          " | " + b.getTitle() +
                          " | User: " + b.getBorrower().name + "\n";
            }
        }
        return result.isEmpty() ? "No Issued Books" : result;
    }

    // File Handling (Output Stream)
    public void save() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream("data.dat"));
            oos.writeObject(books);
            oos.close();
        } catch (Exception e) {
            System.out.println("Save Error");
        }
    }

    // File Handling (Input Stream)
    public void load() {
        try {
            ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream("data.dat"));
            books = (ArrayList<Book>) ois.readObject();
            ois.close();
        } catch (Exception e) {
            System.out.println("No previous data");
        }
    }
}

// GUI Class
public class LibraryGUI extends JFrame {

    private Library lib = new Library();
    private JTextField bookId, title, userId, userName;
    private JTextArea output;

    public LibraryGUI() {

        lib.load(); // load file

        // LOGIN
        String user = JOptionPane.showInputDialog("Enter Username:");
        String pass = JOptionPane.showInputDialog("Enter Password:");

        if (!user.equals("admin") || !pass.equals("1234")) {
            JOptionPane.showMessageDialog(this, "Invalid Login");
            System.exit(0);
        }

        setTitle("Library System");
        setSize(500, 500);
        setLayout(new FlowLayout());

        bookId = new JTextField(10);
        title = new JTextField(10);
        userId = new JTextField(10);
        userName = new JTextField(10);

        add(new JLabel("Book ID")); add(bookId);
        add(new JLabel("Title")); add(title);
        add(new JLabel("User ID")); add(userId);
        add(new JLabel("User Name")); add(userName);

        JButton addBtn = new JButton("Add Book");
        JButton borrowBtn = new JButton("Borrow");
        JButton returnBtn = new JButton("Return");
        JButton viewBtn = new JButton("View Issued");
        JButton saveBtn = new JButton("Save");

        add(addBtn); add(borrowBtn);
        add(returnBtn); add(viewBtn);
        add(saveBtn);

        output = new JTextArea(15, 40);
        add(output);

        // Actions with Exception Handling
        addBtn.addActionListener(e -> {
            try {
                lib.addBook(
                    Integer.parseInt(bookId.getText()),
                    title.getText()
                );
                output.setText("Book Added!");
            } catch (Exception ex) {
                output.setText("Invalid Input!");
            }
        });

        borrowBtn.addActionListener(e -> {
            try {
                output.setText(
                    lib.borrowBook(
                        Integer.parseInt(bookId.getText()),
                        Integer.parseInt(userId.getText()),
                        userName.getText()
                    )
                );
            } catch (Exception ex) {
                output.setText("Error!");
            }
        });

        returnBtn.addActionListener(e -> {
            try {
                output.setText(
                    lib.returnBook(
                        Integer.parseInt(bookId.getText())
                    )
                );
            } catch (Exception ex) {
                output.setText("Error!");
            }
        });

        viewBtn.addActionListener(e -> {
            output.setText(lib.viewIssuedBooks());
        });

        saveBtn.addActionListener(e -> {
            lib.save();
            output.setText("Data Saved!");
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        new LibraryGUI();
    }
}