package views;

import controllers.DeliveryPersonnelController;
import controllers.TimeSlotController;
import models.TimeSlot;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Time;
import java.util.List;

public class AdminView extends JFrame {
    private JPanel AdminBackPanel;
    private JTabbedPane tabbedPane1;
    private JTable tableShipments;
    private JPanel detailSection;
    private JPanel containerDetails;
    private JPanel containerButtons;
    private JButton addShipmentButton;
    private JButton removeShipmentButton;
    private JButton updateShipmentButton;
    private JComboBox comboBox1;
    private JButton approveButton;
    private JTable tableDrivers;
    private JTable tableDeliveryHistory;
    private JButton addDriverButton;
    private JButton removeDriverButton;
    private JButton updateDriverButton;
    private JComboBox dropdownYear;
    private JComboBox dropdownMonth;
    private JButton generateReportButton;
    private JPanel reportPanel;
    private JTextField txtDriverID;
    private JTextField txtDriverName;
    private JTextField txtSchedule;
    private JTextField txtRouteID;
    private JButton clearFormButton;
    private JTextField txtRating;
    private JTable tableTimeSlots;
    private JButton addSlotButton;
    private JButton removeSlotButton;
    private JPanel containerTimeSlotDetails;
    private JTextField txtStartTime;
    private JTextField txtEndTime;
    private JTextField txtContent;
    private JTextField txtCustomer;
    private JTextField txtDeliveryDate;
    private JCheckBox checkboxUrgent;
    private JTextArea textArea1;
    private JComboBox dropdownDestination;
    private JComboBox dropdownTimeSlot;

    private DeliveryPersonnelController driverController;
    private TimeSlotController timeSlotController = new TimeSlotController();

    //Shipment Table Methods
    private void loadTimeSlots() {
        DefaultTableModel timeSlotModel = (DefaultTableModel) tableTimeSlots.getModel();
        timeSlotModel.setRowCount(0);
        List<TimeSlot> timeSlots = timeSlotController.getAllTimeSlots();
        for (TimeSlot ts : timeSlots) {
            timeSlotModel.addRow(new Object[] { ts.getTimeSlotID(), ts.getStartTime(), ts.getEndTime() });
        }
    }

    //Driver Table Methods
    private void refreshDriverTable() {
        DefaultTableModel model = (DefaultTableModel) tableDrivers.getModel();
        model.setDataVector(driverController.getAllDrivers(), new String[] { "ID", "Name", "Schedule", "Route", "Average Rating" });
    }

    private void clearDriverForm() {
        txtDriverID.setText("");
        txtDriverName.setText("");
        txtSchedule.setText("");
        txtRouteID.setText("");
    }

    //------------------ADMIN VIEW--------------------------------------------------------------------------------------------------------------------

    public AdminView(String username) {
        setContentPane(AdminBackPanel);
        setTitle("FastTrack Logistics - Admin Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Initializing table data
        loadTimeSlots();

        //Shipment table model
        String[] ShipmentColumns = {"ID", "Customer", "Destination", "Content", "Driver", "Status"};
        Object[][] ShipmentData = {}; //to be dynamically generated

        DefaultTableModel modelShipments = new DefaultTableModel(ShipmentData, ShipmentColumns);
        tableShipments.setModel(modelShipments);

        //TimeSlot table model
        String[] TimeSlotColumns = {"TimeSlotID", "StartTime", "EndTime"};
        DefaultTableModel modelTimeSlots = new DefaultTableModel(TimeSlotColumns, 0);
        tableTimeSlots.setModel(modelTimeSlots);

        //Driver table model
        String[] DriverColumns = {"ID", "Name", "Schedule", "Route", "Average Rating"};
        Object[][] DriverData = {}; //to be dynamically generated

        DefaultTableModel modelDrivers = new DefaultTableModel(DriverData, DriverColumns);
        tableDrivers.setModel(modelDrivers);

        //Delivery History table model
        String[] DeliveryHistoryColumns = {"Shipment ID", "Delivery Date", "Rating"};
        Object[][] DeliveryHistoryData = {}; //to be dynamically generated

        DefaultTableModel modelDeliveryHistory = new DefaultTableModel(DeliveryHistoryData, DeliveryHistoryColumns);
        tableDeliveryHistory.setModel(modelDeliveryHistory);

        //SHIPMENT SECTION----------------------------------------------------------------------------------------------

        addSlotButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Time startTime = Time.valueOf(txtStartTime.getText() + ":00");
                    Time endTime = Time.valueOf(txtEndTime.getText() + ":00");

                    if (timeSlotController.addTimeSlot(startTime, endTime)) {
                        JOptionPane.showMessageDialog(AdminBackPanel, "Time slot added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadTimeSlots();
                        txtStartTime.setText("");
                        txtEndTime.setText("");
                    } else {
                        JOptionPane.showMessageDialog(AdminBackPanel, "Time slot could not be added!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(AdminBackPanel, "Invalid time format. Use HH:MM", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        removeSlotButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tableTimeSlots.getSelectedRow();
                if (selectedRow >= 0) {
                    int timeSlotID = (int) tableTimeSlots.getValueAt(selectedRow, 0);
                    if (timeSlotController.removeTimeSlot(timeSlotID)) {
                        JOptionPane.showMessageDialog(AdminBackPanel, "Time slot removed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadTimeSlots();
                    } else {
                        JOptionPane.showMessageDialog(AdminBackPanel, "Failed to remove time slot!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(AdminBackPanel, "Select a time slot first!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        //DRIVER SECTION -----------------------------------------------------------------------------------------------
        driverController = new DeliveryPersonnelController();

        txtDriverID.setEditable(false);
        txtRating.setEditable(false); //These fields will not be editable

        refreshDriverTable();

        //Button actions
        addDriverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = JOptionPane.showInputDialog("Enter Username");
                String password = JOptionPane.showInputDialog("Enter Password");
                String email = JOptionPane.showInputDialog("Enter Email");

                String driverName = txtDriverName.getText();
                String schedule = txtSchedule.getText();
                int routeID = Integer.parseInt(txtRouteID.getText());

                if (driverController.addDriver(username, password, email, driverName, schedule, routeID)) {
                    JOptionPane.showMessageDialog(AdminBackPanel, "Driver added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshDriverTable();
                    clearDriverForm();
                }
            }
        });

        removeDriverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tableDrivers.getSelectedRow();
                if (selectedRow >= 0) {
                    int driverID = (int) tableDrivers.getValueAt(selectedRow, 0);
                    int confirm = JOptionPane.showConfirmDialog(AdminBackPanel, "Are you sure you want to delete this driver?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        if (driverController.deleteDriver(driverID)) {
                            JOptionPane.showMessageDialog(AdminBackPanel, "Driver deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            refreshDriverTable();
                            clearDriverForm();
                        } else {
                            JOptionPane.showMessageDialog(AdminBackPanel, "Failed to delete driver!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(AdminBackPanel, "Select a driver to delete", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        updateDriverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tableDrivers.getSelectedRow();
                if (selectedRow >= 0) {
                    int driverID = (int) tableDrivers.getValueAt(selectedRow, 0);
                    String username = JOptionPane.showInputDialog("Enter Username");
                    String password = JOptionPane.showInputDialog("Enter Password");
                    String email = JOptionPane.showInputDialog("Enter Email");
                    String driverName = txtDriverName.getText();
                    String schedule = txtSchedule.getText();
                    int routeID = Integer.parseInt(txtRouteID.getText());

                    if (driverController.updateDrivers(driverID, username, password, email, driverName, schedule, routeID)) {
                        JOptionPane.showMessageDialog(AdminBackPanel, "Driver updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        refreshDriverTable();
                        clearDriverForm();
                    } else {
                        JOptionPane.showMessageDialog(AdminBackPanel, "Failed to update driver!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(AdminBackPanel, "Select a driver to update", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        clearFormButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearDriverForm();
            }
        });

        //Populate form
        tableDrivers.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int selectedRow = tableDrivers.getSelectedRow();
                if (selectedRow >= 0) {
                    txtDriverID.setText(tableDrivers.getValueAt(selectedRow, 0).toString());
                    txtDriverName.setText(tableDrivers.getValueAt(selectedRow, 1).toString());
                    txtSchedule.setText(tableDrivers.getValueAt(selectedRow, 2).toString());
                    txtRouteID.setText(tableDrivers.getValueAt(selectedRow, 3).toString());
                } else {
                    clearDriverForm();
                }
            }
        });
    }
}
