import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.table.DefaultTableModel;

public class StylishShoppingCart {

    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    private HashMap<String, String> users = new HashMap<>();
    private ArrayList<Product> products = new ArrayList<>();
    private ArrayList<Product> cart = new ArrayList<>();
    private ArrayList<Order> orders = new ArrayList<>();
    
    private JTable cartTable;
    private DefaultTableModel cartModel;
    private JLabel totalLabel;
    private static String currentUser = ""; // Global user variable
    private static boolean isAdmin = false; // Admin flag to differentiate admin actions

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StylishShoppingCart().startApp());
    }

    public void startApp() {
        frame = new JFrame("🛒 Stylish Shopping Cart");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);

        // Load products BEFORE panels are created
        loadDummyProducts();

        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);

        mainPanel.add(loginPanel(), "Login");
        mainPanel.add(registerPanel(), "Register");
        mainPanel.add(shopPanel(), "Shop");
        mainPanel.add(adminPanel(), "Admin");

        frame.add(mainPanel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        cardLayout.show(mainPanel, "Login");
    }

    private JPanel loginPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 100, 40, 100));
        panel.setBackground(new Color(255, 255, 255));

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();

        userField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        passField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JButton loginBtn = new JButton("Login");
        JButton toRegister = new JButton("Register");

        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        toRegister.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginBtn.setBackground(new Color(100, 149, 237));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setFont(new Font("Arial", Font.BOLD, 14));

        toRegister.setBackground(new Color(46, 204, 113));
        toRegister.setForeground(Color.WHITE);
        toRegister.setFocusPainted(false);
        toRegister.setFont(new Font("Arial", Font.PLAIN, 14));

        loginBtn.addActionListener(e -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());
            if (users.containsKey(user) && users.get(user).equals(pass)) {
                currentUser = user; // Set current user
                isAdmin = currentUser.equals("admin"); // Check if the logged in user is the admin
                JOptionPane.showMessageDialog(frame, "Login Successful!");
                if (isAdmin) {
                    cardLayout.show(mainPanel, "Admin");
                } else {
                    cardLayout.show(mainPanel, "Shop");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid credentials.");
            }
        });

        toRegister.addActionListener(e -> cardLayout.show(mainPanel, "Register"));

        panel.add(userLabel);
        panel.add(userField);
        panel.add(passLabel);
        panel.add(passField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(loginBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(toRegister);

        return panel;
    }

    private JPanel registerPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 100, 40, 100));
        panel.setBackground(new Color(255, 255, 255));

        JLabel userLabel = new JLabel("Create Username:");
        JTextField userField = new JTextField();
        JLabel passLabel = new JLabel("Create Password:");
        JPasswordField passField = new JPasswordField();

        userField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        passField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JButton registerBtn = new JButton("Register");
        JButton toLogin = new JButton("Back to Login");

        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        toLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

        registerBtn.setBackground(new Color(46, 204, 113));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFocusPainted(false);
        registerBtn.setFont(new Font("Arial", Font.BOLD, 14));

        toLogin.setBackground(new Color(100, 149, 237));
        toLogin.setForeground(Color.WHITE);
        toLogin.setFocusPainted(false);
        toLogin.setFont(new Font("Arial", Font.PLAIN, 14));

        registerBtn.addActionListener(e -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());
            if (!user.isEmpty() && !pass.isEmpty()) {
                users.put(user, pass);
                JOptionPane.showMessageDialog(frame, "Registration Successful!");
                cardLayout.show(mainPanel, "Login");
            } else {
                JOptionPane.showMessageDialog(frame, "Please fill all fields.");
            }
        });

        toLogin.addActionListener(e -> cardLayout.show(mainPanel, "Login"));

        panel.add(userLabel);
        panel.add(userField);
        panel.add(passLabel);
        panel.add(passField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(registerBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(toLogin);

        return panel;
    }

    private JPanel shopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 255, 255));

        DefaultListModel<Product> productModel = new DefaultListModel<>();
        for (Product p : products) {
            productModel.addElement(p);
        }

        JList<Product> productList = new JList<>(productModel);
        productList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productList.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JScrollPane productScroll = new JScrollPane(productList);
        productScroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        cartModel = new DefaultTableModel(new String[]{"Name", "Price"}, 0);
        cartTable = new JTable(cartModel);
        cartTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane cartScroll = new JScrollPane(cartTable);
        cartScroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        JButton addBtn = new JButton("Add to Cart");
        JButton checkoutBtn = new JButton("Checkout");
        totalLabel = new JLabel("Total: $0.00");

        addBtn.setBackground(new Color(52, 152, 219));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.setFont(new Font("Arial", Font.BOLD, 14));

        checkoutBtn.setBackground(new Color(231, 76, 60));
        checkoutBtn.setForeground(Color.WHITE);
        checkoutBtn.setFocusPainted(false);
        checkoutBtn.setFont(new Font("Arial", Font.BOLD, 14));

        addBtn.addActionListener(e -> {
            Product selected = productList.getSelectedValue();
            if (selected != null) {
                cart.add(selected);
                cartModel.addRow(new Object[]{selected.name, selected.price});
                updateTotal();
            }
        });

        checkoutBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(frame, "Enter Your Name for Billing:");
            if (name != null && !name.isEmpty()) {
                double total = getTotal();
                saveOrderToFile(name, total);
                JOptionPane.showMessageDialog(frame, "Thank you, " + name + "! Your order total is $" + total);
                cart.clear();
                cartModel.setRowCount(0);
                updateTotal();
            }
        });

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        buttonPanel.add(addBtn);
        buttonPanel.add(checkoutBtn);
        buttonPanel.add(totalLabel);

        panel.add(productScroll, BorderLayout.WEST);
        panel.add(cartScroll, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel adminPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 100, 40, 100));
        panel.setBackground(new Color(255, 255, 255));

        JLabel titleLabel = new JLabel("Admin Panel");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JButton addProductBtn = new JButton("Add Product");
        JButton removeProductBtn = new JButton("Remove Product");
        JButton updateProductBtn = new JButton("Update Product");

        addProductBtn.setFont(new Font("Arial", Font.BOLD, 14));
        removeProductBtn.setFont(new Font("Arial", Font.BOLD, 14));
        updateProductBtn.setFont(new Font("Arial", Font.BOLD, 14));

        addProductBtn.addActionListener(e -> addProduct());
        removeProductBtn.addActionListener(e -> removeProduct());
        updateProductBtn.addActionListener(e -> updateProduct());

        panel.add(titleLabel);
        panel.add(addProductBtn);
        panel.add(removeProductBtn);
        panel.add(updateProductBtn);

        return panel;
    }

    private void addProduct() {
        String name = JOptionPane.showInputDialog(frame, "Enter Product Name:");
        String priceStr = JOptionPane.showInputDialog(frame, "Enter Product Price:");
        double price = Double.parseDouble(priceStr);

        Product newProduct = new Product(products.size() + 1, name, price);
        products.add(newProduct);

        JOptionPane.showMessageDialog(frame, "Product added successfully!");
    }

    private void removeProduct() {
        String productName = JOptionPane.showInputDialog(frame, "Enter Product Name to Remove:");
        products.removeIf(product -> product.name.equals(productName));

        JOptionPane.showMessageDialog(frame, "Product removed successfully!");
    }

    private void updateProduct() {
        String productName = JOptionPane.showInputDialog(frame, "Enter Product Name to Update:");
        for (Product p : products) {
            if (p.name.equals(productName)) {
                String newName = JOptionPane.showInputDialog(frame, "Enter New Product Name:");
                String newPriceStr = JOptionPane.showInputDialog(frame, "Enter New Product Price:");
                double newPrice = Double.parseDouble(newPriceStr);
                p.name = newName;
                p.price = newPrice;

                JOptionPane.showMessageDialog(frame, "Product updated successfully!");
                return;
            }
        }
        JOptionPane.showMessageDialog(frame, "Product not found.");
    }

    private void loadDummyProducts() {
        products.add(new Product(1, "Laptop", 800));
        products.add(new Product(2, "Smartphone", 500));
        products.add(new Product(3, "Headphones", 50));
        products.add(new Product(4, "Watch", 120));
        products.add(new Product(5, "Backpack", 40));
    }

    private void updateTotal() {
        totalLabel.setText("Total: $" + getTotal());
    }

    private double getTotal() {
        return cart.stream().mapToDouble(p -> p.price).sum();
    }

    private void saveOrderToFile(String name, double total) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("orders.txt", true))) {
            writer.println("Customer: " + name);
            for (Product p : cart) {
                writer.println(" - " + p.name + ": $" + p.price);
            }
            writer.println("Total: $" + total);
            writer.println("------");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class Product {
        int id;
        String name;
        double price;

        Product(int id, String name, double price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }

        public String toString() {
            return name + " ($" + price + ")";
        }
    }

    static class Order {
        String customerName;
        ArrayList<Product> products;
        double total;

        Order(String customerName, ArrayList<Product> products, double total) {
            this.customerName = customerName;
            this.products = products;
            this.total = total;
        }
    }
}
