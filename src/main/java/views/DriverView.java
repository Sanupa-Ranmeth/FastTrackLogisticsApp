package views;

import controllers.DeliveryController;
import controllers.DeliveryPersonnelController;
import controllers.RouteController;
import controllers.CityController;
import models.City;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class DriverView extends JFrame {
    private JPanel driverBackPanel;
    private JLabel lblWelcome;
    private JTable tableDriverAssignedShipments;
    private JPanel containerButton;
    private JButton updateLocationButton;
    private JButton delayButton;
    private JButton updateStatusButton;
    private JButton updateEstimationButton;
    private JButton viewHistoryButton;

    // Controllers
    private DeliveryPersonnelController driverController;
    private DeliveryController deliveryController;
    private RouteController routeController;
    private CityController cityController;

    // Table models
    private DefaultTableModel assignedShipmentsModel;

    // Current driver information
    private String username;
    private int driverID;

    // Table column names
    private final String[] assignedShipmentColumns = {
            "Shipment ID", "Sender", "Receiver", "Destination", "Address",
            "Content", "Status", "Urgent", "Estimation", "Current Location"
    };

    public DriverView(String username) {
        // Initialize panel if not created by GUI designer
        if (driverBackPanel == null) {
            initComponents();
        }

        setContentPane(driverBackPanel);
        setTitle("FastTrack Logistics - Driver Dashboard");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialize controllers
        this.username = username;
        this.driverController = new DeliveryPersonnelController();
        this.deliveryController = new DeliveryController();
        this.routeController = new RouteController();
        this.cityController = new CityController();

        // Get driver ID from username
        this.driverID = driverController.getDriverIDFromUsername(username);

        // Set welcome message
        lblWelcome.setText("Welcome, " + driverController.getDriverNameByID(driverID));

        // Initialize table model
        assignedShipmentsModel = new DefaultTableModel(null, assignedShipmentColumns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableDriverAssignedShipments.setModel(assignedShipmentsModel);

        // Load assigned shipments
        loadAssignedShipments();

        // Setup UI interaction
        setupEventHandlers();
    }

    private void initComponents() {
        driverBackPanel = new JPanel(new BorderLayout(10, 10));
        driverBackPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Welcome label at top
        lblWelcome = new JLabel("Welcome");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 18));
        driverBackPanel.add(lblWelcome, BorderLayout.NORTH);

        // Table in center
        tableDriverAssignedShipments = new JTable();
        JScrollPane scrollPane = new JScrollPane(tableDriverAssignedShipments);
        driverBackPanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons at bottom
        containerButton = new JPanel(new GridLayout(1, 6, 5, 0));

        updateLocationButton = new JButton("Update Location");
        delayButton = new JButton("Mark Delayed");
        updateStatusButton = new JButton("Update Status");
        updateEstimationButton = new JButton("Update Estimation");
        viewHistoryButton = new JButton("View History");

        containerButton.add(updateLocationButton);
        containerButton.add(delayButton);
        containerButton.add(updateStatusButton);
        containerButton.add(updateEstimationButton);
        containerButton.add(viewHistoryButton);

        driverBackPanel.add(containerButton, BorderLayout.SOUTH);
    }

    private void loadAssignedShipments() {
        // Clear the table
        assignedShipmentsModel.setRowCount(0);

        // Get assigned shipments for this driver
        Object[][] shipments = deliveryController.getAssignedShipments(driverID);

        // Add to table model
        if (shipments != null) {
            for (Object[] shipment : shipments) {
                assignedShipmentsModel.addRow(shipment);
            }
        }
    }

    private void setupEventHandlers() {
        // Update Location Button
        updateLocationButton.addActionListener(e -> updateShipmentLocation());

        // Mark as Delayed Button
        delayButton.addActionListener(e -> markShipmentAsDelayed());

        // Update Status Button
        updateStatusButton.addActionListener(e -> updateShipmentStatus());

        // Update Estimation Button
        updateEstimationButton.addActionListener(e -> updateDeliveryEstimation());

        // View History Button
        viewHistoryButton.addActionListener(e -> viewDeliveryHistory());
    }

    private void updateShipmentLocation() {
        int selectedRow = tableDriverAssignedShipments.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(driverBackPanel,
                    "Please select a shipment to update location",
                    "Selection Required", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int shipmentID = (int) tableDriverAssignedShipments.getValueAt(selectedRow, 0);

        // Get all cities for the route assigned to this driver
        int routeID = driverController.getDriverRouteID(driverID);
        List<City> cities = routeController.getCitiesbyRoute(routeID);

        if (cities.isEmpty()) {
            JOptionPane.showMessageDialog(driverBackPanel,
                    "No cities found for your route",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create a dropdown for selecting the current location
        JComboBox<City> locationDropdown = new JComboBox<>();
        for (City city : cities) {
            locationDropdown.addItem(city);
        }

        // Create a custom panel for input
        JPanel panel = new JPanel();
        panel.add(new JLabel("Current Location:"));
        panel.add(locationDropdown);

        int result = JOptionPane.showConfirmDialog(driverBackPanel, panel,
                "Update Location", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            City selectedCity = (City) locationDropdown.getSelectedItem();
            int locationID = selectedCity.getCityID();

            // Update the location
            if (deliveryController.updateDeliveryOperations(shipmentID, "In Transit", locationID, 0)) {
                JOptionPane.showMessageDialog(driverBackPanel,
                        "Location updated successfully",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAssignedShipments();
            } else {
                JOptionPane.showMessageDialog(driverBackPanel,
                        "Failed to update location",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void markShipmentAsDelayed() {
        int selectedRow = tableDriverAssignedShipments.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(driverBackPanel,
                    "Please select a shipment to mark as delayed",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int shipmentID = (int) tableDriverAssignedShipments.getValueAt(selectedRow, 0);

        String delayStr = JOptionPane.showInputDialog(driverBackPanel,
                "Enter delay in minutes:",
                "Mark Shipment as Delayed", JOptionPane.QUESTION_MESSAGE);

        if (delayStr == null || delayStr.trim().isEmpty()) {
            return; // User canceled or entered empty string
        }

        try {
            int delay = Integer.parseInt(delayStr);
            if (delay <= 0) {
                JOptionPane.showMessageDialog(driverBackPanel,
                        "Delay must be a positive number",
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Use a shorter status value that fits in the database column
            if (deliveryController.updateDeliveryOperations(shipmentID, "Delay", null, delay)) {
                JOptionPane.showMessageDialog(driverBackPanel,
                        "Shipment marked as delayed successfully",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAssignedShipments();
            } else {
                JOptionPane.showMessageDialog(driverBackPanel,
                        "Failed to mark shipment as delayed",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(driverBackPanel,
                    "Please enter a valid number for delay",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateShipmentStatus() {
        int selectedRow = tableDriverAssignedShipments.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(driverBackPanel,
                    "Please select a shipment to update status",
                    "Selection Required", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int shipmentID = (int) tableDriverAssignedShipments.getValueAt(selectedRow, 0);

        // Use status values that match your database column size
        String[] statusOptions = {"In Transit", "Delivered", "Failed Delivery"};

        String selectedStatus = (String) JOptionPane.showInputDialog(driverBackPanel,
                "Select new status:",
                "Update Status",
                JOptionPane.QUESTION_MESSAGE,
                null,
                statusOptions,
                statusOptions[0]);

        if (selectedStatus != null) {
            // For delivered status, set the actual delivery time
            LocalDateTime actualDeliveryTime = null;
            if ("Delivered".equals(selectedStatus)) {
                actualDeliveryTime = LocalDateTime.now();
            }

            // Update the status
            if (deliveryController.updateDeliveryStatus(shipmentID, selectedStatus, actualDeliveryTime)) {
                JOptionPane.showMessageDialog(driverBackPanel,
                        "Status updated successfully",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAssignedShipments();
            } else {
                JOptionPane.showMessageDialog(driverBackPanel,
                        "Failed to update status",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateDeliveryEstimation() {
        int selectedRow = tableDriverAssignedShipments.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(driverBackPanel,
                    "Please select a shipment to update estimation",
                    "Selection Required", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int shipmentID = (int) tableDriverAssignedShipments.getValueAt(selectedRow, 0);

        // Get the current estimated time
        String currentEstimation = tableDriverAssignedShipments.getValueAt(selectedRow, 8) != null ?
                tableDriverAssignedShipments.getValueAt(selectedRow, 8).toString() :
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String estimationInput = JOptionPane.showInputDialog(driverBackPanel,
                "Enter estimated delivery date and time (YYYY-MM-DD HH:MM:SS):",
                currentEstimation);

        if (estimationInput != null && !estimationInput.trim().isEmpty()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime estimation = LocalDateTime.parse(estimationInput.trim(), formatter);

                if (deliveryController.setDeliveryEstimation(shipmentID, estimation)) {
                    JOptionPane.showMessageDialog(driverBackPanel,
                            "Delivery estimation updated successfully",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadAssignedShipments();
                } else {
                    JOptionPane.showMessageDialog(driverBackPanel,
                            "Failed to update delivery estimation",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(driverBackPanel,
                        "Invalid date time format. Please use YYYY-MM-DD HH:MM:SS",
                        "Format Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewDeliveryHistory() {
        // Create a new table model for history
        DefaultTableModel historyModel = new DefaultTableModel(
                new String[]{"Shipment ID", "Delivery Date", "Status", "Recipient", "Rating"}, 0);

        // Get delivery history for this driver
        Object[][] historyData = deliveryController.getDriverDeliveryHistory(driverID);

        if (historyData != null && historyData.length > 0) {
            for (Object[] row : historyData) {
                historyModel.addRow(row);
            }

            // Create a new table
            JTable historyTable = new JTable(historyModel);
            historyTable.setEnabled(false);

            // Show in a scroll pane
            JScrollPane scrollPane = new JScrollPane(historyTable);
            scrollPane.setPreferredSize(new java.awt.Dimension(600, 300));

            JOptionPane.showMessageDialog(driverBackPanel, scrollPane,
                    "Delivery History", JOptionPane.PLAIN_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(driverBackPanel,
                    "No delivery history found",
                    "Empty History", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}