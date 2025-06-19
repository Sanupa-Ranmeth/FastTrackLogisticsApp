package views;

import controllers.DeliveryController;
import controllers.DeliveryPersonnelController;
import utilities.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Vector;

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
    private JButton isAvailableButton;
    private JButton viewNotificationsButton;

    private final DeliveryPersonnelController driverController = new DeliveryPersonnelController();
    private final DeliveryController deliveryController = new DeliveryController();
    private final int userId;
    private DefaultTableModel tableModel;
    private final String[] columnNames = {"ID", "Sender", "Receiver", "Destination", "Address", "Contents", "Status", "Urgent", "Est. Delivery", "Current Location"};

    public DriverView(String username) {
        setContentPane(driverBackPanel);
        setTitle("FastTrack Logistics - Driver Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        userId = driverController.getUserIDbyUsername(username);
        System.out.println("User ID: " + userId);

        lblWelcome.setText("Welcome, " + username + "!");

        // Initialize table model and set up table
        setupTable();

        // Load shipments data
        loadAssignedShipments();

        // View notification button
        viewNotificationsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new NotificationView(username, "driver").setVisible(true);
                dispose();
            }
        });

        // Set initial availability state from DB
        boolean isAvailable = DeliveryPersonnelController.getAvailability(userId);
        isAvailableButton.setText(isAvailable ? "Make Unavailable" : "Make Available");

        // Store availability state
        final boolean[] availabilityState = {isAvailable};

        // Availability toggle button
        isAvailableButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Toggle the state
                availabilityState[0] = !availabilityState[0];
                int availabilityValue = availabilityState[0] ? 1 : 0;

                // Update in DB
                DeliveryPersonnelController.updateAvailability(userId, availabilityValue);

                // Update button text
                isAvailableButton.setText(availabilityState[0] ? "Make Unavailable" : "Make Available");

                // Notify user
                String message = availabilityState[0] ? "You are now marked as Available." : "You are now marked as UNAVAILABLE.";
                JOptionPane.showMessageDialog(null, message);
            }
        });

        // Update Location button
        updateLocationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tableDriverAssignedShipments.getSelectedRow() == -1) {
                    JOptionPane.showMessageDialog(null, "Please select a shipment to update location");
                    return;
                }

                int shipmentId = (int) tableDriverAssignedShipments.getValueAt(tableDriverAssignedShipments.getSelectedRow(), 0);

                // Get available cities from database
                Vector<String> cities = getCityList();
                if (cities.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Failed to load city data");
                    return;
                }

                String selectedCity = (String) JOptionPane.showInputDialog(
                        null,
                        "Select current location:",
                        "Update Location",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        cities.toArray(),
                        cities.get(0));

                if (selectedCity != null) {
                    int locationId = getCityId(selectedCity);
                    if (locationId > 0) {
                        String currentStatus = (String) tableDriverAssignedShipments.getValueAt(
                                tableDriverAssignedShipments.getSelectedRow(), 6);

                        if (deliveryController.updateDeliveryOperations(shipmentId, currentStatus, locationId, 0)) {
                            JOptionPane.showMessageDialog(null, "Location updated successfully");
                            loadAssignedShipments(); // Refresh the table
                        } else {
                            JOptionPane.showMessageDialog(null, "Failed to update location");
                        }
                    }
                }
            }
        });

        // Update Status button
        updateStatusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tableDriverAssignedShipments.getSelectedRow() == -1) {
                    JOptionPane.showMessageDialog(null, "Please select a shipment to update status");
                    return;
                }

                int shipmentId = (int) tableDriverAssignedShipments.getValueAt(tableDriverAssignedShipments.getSelectedRow(), 0);
                String[] statuses = {"In Transit", "Delivered", "Failed Delivery"};

                String selectedStatus = (String) JOptionPane.showInputDialog(
                        null,
                        "Select new status:",
                        "Update Status",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        statuses,
                        "In Transit");

                if (selectedStatus != null) {
                    LocalDateTime actualDeliveryTime = null;

                    if ("Delivered".equals(selectedStatus) || "Failed Delivery".equals(selectedStatus)) {
                        actualDeliveryTime = LocalDateTime.now();
                    }

                    if (deliveryController.updateDeliveryStatus(shipmentId, selectedStatus, actualDeliveryTime)) {
                        JOptionPane.showMessageDialog(null, "Status updated successfully");
                        loadAssignedShipments(); // Refresh the table
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to update status");
                    }
                }
            }
        });

        // Delay button
        delayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tableDriverAssignedShipments.getSelectedRow() == -1) {
                    JOptionPane.showMessageDialog(null, "Please select a shipment to report delay");
                    return;
                }

                int shipmentId = (int) tableDriverAssignedShipments.getValueAt(tableDriverAssignedShipments.getSelectedRow(), 0);
                String delayMinutesStr = JOptionPane.showInputDialog("Enter delay in minutes:");

                if (delayMinutesStr != null && !delayMinutesStr.isEmpty()) {
                    try {
                        int delayMinutes = Integer.parseInt(delayMinutesStr);
                        if (delayMinutes <= 0) {
                            JOptionPane.showMessageDialog(null, "Please enter a positive number for delay");
                            return;
                        }

                        String currentStatus = (String) tableDriverAssignedShipments.getValueAt(
                                tableDriverAssignedShipments.getSelectedRow(), 6);

                        Integer currentLocation = null;
                        String locationStr = (String) tableDriverAssignedShipments.getValueAt(
                                tableDriverAssignedShipments.getSelectedRow(), 9);
                        if (locationStr != null && !locationStr.isEmpty()) {
                            currentLocation = getCityId(locationStr);
                        }

                        if (deliveryController.updateDeliveryOperations(shipmentId, "Delay", currentLocation, delayMinutes)) {
                            JOptionPane.showMessageDialog(null, "Delay reported successfully");
                            loadAssignedShipments(); // Refresh the table
                        } else {
                            JOptionPane.showMessageDialog(null, "Failed to report delay");
                        }

                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Please enter a valid number for delay");
                    }
                }
            }
        });

        // Update Estimation button
        updateEstimationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tableDriverAssignedShipments.getSelectedRow() == -1) {
                    JOptionPane.showMessageDialog(null, "Please select a shipment to update estimation");
                    return;
                }

                int shipmentId = (int) tableDriverAssignedShipments.getValueAt(tableDriverAssignedShipments.getSelectedRow(), 0);
                String estimatedTimeStr = JOptionPane.showInputDialog(
                        "Enter new estimated delivery time (yyyy-MM-dd HH:mm):",
                        LocalDateTime.now().plusHours(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

                if (estimatedTimeStr != null && !estimatedTimeStr.isEmpty()) {
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                        LocalDateTime estimatedTime = LocalDateTime.parse(estimatedTimeStr, formatter);

                        if (deliveryController.setDeliveryEstimation(shipmentId, estimatedTime)) {
                            JOptionPane.showMessageDialog(null, "Estimation updated successfully");
                            loadAssignedShipments(); // Refresh the table
                        } else {
                            JOptionPane.showMessageDialog(null, "Failed to update estimation");
                        }

                    } catch (DateTimeParseException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid date format. Please use yyyy-MM-dd HH:mm");
                    }
                }
            }
        });

        // View History button
        viewHistoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create a new dialog to show history
                JDialog historyDialog = new JDialog(DriverView.this, "Delivery History", true);
                historyDialog.setSize(600, 400);
                historyDialog.setLocationRelativeTo(DriverView.this);

                // Get history data
                Object[][] historyData = deliveryController.getDriverDeliveryHistory(userId);
                String[] historyColumns = {"Shipment ID", "Delivery Time", "Status", "Receiver", "Rating"};

                // Create table
                JTable historyTable = new JTable(historyData, historyColumns);
                JScrollPane scrollPane = new JScrollPane(historyTable);
                historyDialog.add(scrollPane);

                historyDialog.setVisible(true);
            }
        });
    }

    private void setupTable() {
        // Create table model
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        // Set model to table
        tableDriverAssignedShipments.setModel(tableModel);
        tableDriverAssignedShipments.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void loadAssignedShipments() {
        // Clear existing data
        tableModel.setRowCount(0);

        // Get assigned shipments
        Object[][] shipments = deliveryController.getAssignedShipments(userId);

        // Add rows to table model
        for (Object[] shipment : shipments) {
            tableModel.addRow(shipment);
        }
    }

    private Vector<String> getCityList() {
        Vector<String> cities = new Vector<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT CityName FROM City ORDER BY CityName");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                cities.add(rs.getString("CityName"));
            }

        } catch (Exception e) {
            System.out.println("Error loading city data: " + e.getMessage());
        }

        return cities;
    }

    private int getCityId(String cityName) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT CityID FROM City WHERE CityName = ?")) {

            stmt.setString(1, cityName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("CityID");
            }

        } catch (Exception e) {
            System.out.println("Error getting city ID: " + e.getMessage());
        }

        return -1;
    }
}