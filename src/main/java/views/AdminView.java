package views;

import controllers.DeliveryPersonnelController;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
    private JLabel lblDestination;
    private JLabel lblContent;
    private JLabel lblCustomer;
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

    private DeliveryPersonnelController driverController;

    //Driver Table Functions
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

    public AdminView(String username) {
        setContentPane(AdminBackPanel);
        setTitle("FastTrack Logistics - Admin Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Shipment table model
        String[] ShipmentColumns = {"ID", "Customer", "Destination", "Content", "Driver", "Status"};
        Object[][] ShipmentData = {}; //to be dynamically generated

        DefaultTableModel modelShipments = new DefaultTableModel(ShipmentData, ShipmentColumns);
        tableShipments.setModel(modelShipments);


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

        //DRIVER SECTION -----------------------------------------------------------------------------------------------
        driverController = new DeliveryPersonnelController();

        txtDriverID.setEditable(false);
        txtRating.setEditable(false); //These fields will not be editable

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
