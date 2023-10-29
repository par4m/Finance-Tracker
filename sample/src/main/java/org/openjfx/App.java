package org.openjfx;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class App extends Application {
    private TableView<Expense> expenseTable;
    private TableView<Expense> historyTable;
    private ObservableList<Expense> expenses;
    private DecimalFormat currencyFormat = new DecimalFormat("#,##0.00");
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Initialize data
        expenses = FXCollections.observableArrayList();
        loadExpensesFromStorage();

        // Create tabs
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab expensesTab = createExpensesTab();
        Tab historyTab = createHistoryTab();

        tabPane.getTabs().addAll(expensesTab, historyTab);

        // Create the main scene
        BorderPane root = new BorderPane(tabPane);
        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Expense Tracker");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadExpensesFromStorage() {
        ArrayList<Expense> loadedExpenses = ExpenseStorage.loadExpenses();
        if (loadedExpenses != null) {
            expenses.addAll(loadedExpenses);
        }
    }

    private Tab createExpensesTab() {
        Tab tab = new Tab("Expenses");
        tab.setClosable(false);

        // Create UI components for Expenses tab
        VBox expensesVBox = new VBox();
        expensesVBox.setSpacing(10);

        Label titleLabel = new Label("Add an Expense");
        titleLabel.getStyleClass().add("section-title");

        TextField nameField = new TextField();
        nameField.setPromptText("Expense Name");

        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Select Date");

        ChoiceBox<String> categoryChoiceBox = new ChoiceBox<>();
        categoryChoiceBox.getItems().addAll("Personal", "Entertainment", "Travel", "Necessary");
        categoryChoiceBox.setValue("Personal");

        TextField amountField = new TextField();
        amountField.setPromptText("Amount");

        Button addButton = new Button("Add Expense");
        addButton.setOnAction(e -> addExpense(nameField, datePicker, categoryChoiceBox, amountField));

        // Create the Expenses table
        expenseTable = new TableView<>();
        expenseTable.setPlaceholder(new Label("No Expenses"));

        TableColumn<Expense, String> nameColumn = new TableColumn<>("Expense Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Expense, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Expense, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Expense, Double> amountColumn = new TableColumn<>("Amount");
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

        expenseTable.getColumns().addAll(nameColumn, dateColumn, categoryColumn, amountColumn);
        expenseTable.setItems(expenses);

        expensesVBox.getChildren().addAll(titleLabel, nameField, datePicker, categoryChoiceBox, amountField, addButton, expenseTable);

        tab.setContent(expensesVBox);
        return tab;
    }

    private Tab createHistoryTab() {
        Tab tab = new Tab("History");
        tab.setClosable(false);

        // Create the Expenses table for the History tab
        historyTable = new TableView<>();
        historyTable.setPlaceholder(new Label("No Expenses"));

        TableColumn<Expense, String> nameColumn = new TableColumn<>("Expense Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Expense, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Expense, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Expense, Double> amountColumn = new TableColumn<>("Amount");
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

        historyTable.getColumns().addAll(nameColumn, dateColumn, categoryColumn, amountColumn);
        updateHistoryTable();

        tab.setContent(historyTable);
        return tab;
    }

    private void updateHistoryTable() {
        // Filter and display expenses for the current month
        Calendar currentMonth = Calendar.getInstance();
        currentMonth.set(Calendar.DAY_OF_MONTH, 1); // Start of the current month

        ObservableList<Expense> currentMonthExpenses = FXCollections.observableArrayList();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        for (Expense expense : expenses) {
            try {
                Date expenseDate = dateFormat.parse(expense.getDate());
                Calendar expenseCalendar = Calendar.getInstance();
                expenseCalendar.setTime(expenseDate);

                if (expenseCalendar.get(Calendar.YEAR) == currentMonth.get(Calendar.YEAR) &&
                        expenseCalendar.get(Calendar.MONTH) == currentMonth.get(Calendar.MONTH)) {
                    currentMonthExpenses.add(expense);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        historyTable.setItems(currentMonthExpenses);
    }

    private void addExpense(TextField nameField, DatePicker datePicker, ChoiceBox<String> categoryChoiceBox, TextField amountField) {
        String name = nameField.getText();
        String date = datePicker.getValue().toString();
        String category = categoryChoiceBox.getValue();
        double amount = 0.0;

        try {
            amount = Double.parseDouble(amountField.getText());
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid amount.");
            return;
        }

        Expense expense = new Expense(name, date, category, amount);
        expenses.add(expense);

        // Clear input fields
        nameField.clear();
        datePicker.setValue(null);
        amountField.clear();

        // Show a confirmation dialog
        showAlert("Expense Added", "Expense has been added successfully.");

        // Update the history table
        updateHistoryTable();

        // Save expenses to storage
        ExpenseStorage.saveExpenses(new ArrayList<>(expenses));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class Expense {
        private final String name;
        private final String date;
        private final String category;
        private final double amount;

        public Expense(String name, String date, String category, double amount) {
            this.name = name;
            this.date = date;
            this.category = category;
            this.amount = amount;
        }

        public String getName() {
            return name;
        }

        public String getDate() {
            return date;
        }

        public String getCategory() {
            return category;
        }

        public double getAmount() {
            return amount;
        }
    }

    public static class ExpenseStorage {
        private static final String FILENAME = "expenses.txt";

        public static void saveExpenses(ArrayList<Expense> expenses) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(FILENAME))) {
                for (Expense e : expenses) {
                    writer.println(e.getDate() + "," + e.getName() + "," + e.getCategory() + "," + e.getAmount());
                }
            } catch (IOException e) {
                System.out.println("Error saving expenses: " + e.getMessage());
            }
        }

        public static ArrayList<Expense> loadExpenses() {
            ArrayList<Expense> expenses = new ArrayList<>();

            try (BufferedReader reader = new BufferedReader(new FileReader(FILENAME))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 4) {
                        Expense expense = new Expense(parts[1], parts[0], parts[2], Double.parseDouble(parts[3]));
                        expenses.add(expense);
                    }
                }
            } catch (IOException | NumberFormatException e) {
                System.out.println("Error loading expenses: " + e.getMessage());
            }
            return expenses;
        }
    }
}