package views;

import controllers.ShipmentController;
import models.Shipment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class CustomerView extends JFrame {

    private JLabel lblWelcome;
    private JTable tableCustomerShipments;
    private JPanel containerButton;
    private JButton btnTrackStatus;
    private JButton btnDelete;
    private JButton btnUpdateShipment;
    private JTextField txtReceiver;
    private JTextArea txtAreaDestination;
    private JTextField txtPkgContents;
    private JComboBox dropdownTimeSlot;
    private JCheckBox urgentDeliveryCheckBox;
    private JButton btnAddShipment;
    private JPanel customerBackPanel;

    private ShipmentController shipmentController;
    private String username;

    private void clearForm() {
        txtReceiver.setText("");
        txtAreaDestination.setText("");
        txtPkgContents.setText("");
        dropdownTimeSlot.setSelectedIndex(0);
        urgentDeliveryCheckBox.setSelected(false);
    }

    public CustomerView(String username) {
        setContentPane(customerBackPanel);
        setTitle("FastTrack Logistics - Customer Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.username = username;
        this.shipmentController = new ShipmentController();

        //Customer Shipment table model
        String[] customerShipmentColumns = {"Package ID", "Sent To", "Destination", "Content", "Status", "Delivery Date", "Urgent", "Time Slot"};
        Object[][] customerShipmentData = {}; //to be dynamically generated

        DefaultTableModel modelShipments = new DefaultTableModel(customerShipmentData, customerShipmentColumns);
        tableCustomerShipments.setModel(modelShipments);

        //Button Actions
        btnAddShipment.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String receiverName = txtReceiver.getText();
                String destination = txtAreaDestination.getText();
                String contents = txtPkgContents.getText();
                String timeslot = dropdownTimeSlot.getSelectedItem().toString() != null ? dropdownTimeSlot.getSelectedItem().toString() : "";
                boolean isUrgent = urgentDeliveryCheckBox.isSelected();

                if (receiverName.isEmpty() || destination.isEmpty() || timeslot.isEmpty() || contents.isEmpty()) {
                    JOptionPane.showMessageDialog(customerBackPanel, "Please complete all fields", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Shipment shipment = new Shipment(receiverName, destination, contents, isUrgent, timeslot);

                if (shipmentController.addShipment(shipment, username)) {
                    JOptionPane.showMessageDialog(customerBackPanel, "Shipment added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    modelShipments.setDataVector(shipmentController.getCustomerShipments(username), customerShipmentColumns);
                }
                else {
                    JOptionPane.showMessageDialog(customerBackPanel, "Failed to add shipment!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        //Display Details in Shipment Details
        tableCustomerShipments.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = tableCustomerShipments.getSelectedRow();
            if (selectedRow >= 0) {
                txtReceiver.setText(modelShipments.getValueAt(selectedRow, 1).toString());
                txtAreaDestination.setText(modelShipments.getValueAt(selectedRow, 2).toString());
                txtPkgContents.setText(modelShipments.getValueAt(selectedRow, 3).toString());
                urgentDeliveryCheckBox.setSelected((Boolean)modelShipments.getValueAt(selectedRow, 6));
                dropdownTimeSlot.setSelectedItem(modelShipments.getValueAt(selectedRow, 7).toString());
            } else {
                clearForm();
            }
        });

        btnUpdateShipment.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tableCustomerShipments.getSelectedRow();
                if (selectedRow >= 0) {
                    int shipmentID = (int)modelShipments.getValueAt(selectedRow, 0);
                    String receiverName = txtReceiver.getText();
                    String destination = txtAreaDestination.getText();
                    String contents = txtPkgContents.getText();
                    String timeslot = dropdownTimeSlot.getSelectedItem() != null ? dropdownTimeSlot.getSelectedItem().toString() : "";
                    boolean isUrgent = urgentDeliveryCheckBox.isSelected();

                    Shipment shipment = new Shipment (shipmentID, 0, receiverName, destination, contents, isUrgent, timeslot);

                    if (shipmentController.updateShipment(shipment)) {
                        JOptionPane.showMessageDialog(customerBackPanel, "Shipment updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        modelShipments.setDataVector(shipmentController.getCustomerShipments(username), customerShipmentColumns);
                    } else {
                        JOptionPane.showMessageDialog(customerBackPanel, "Failed to update shipment!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(customerBackPanel, "Select a shipment to update.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tableCustomerShipments.getSelectedRow();
                if (selectedRow >= 0) {
                    int shipmentID = (int)modelShipments.getValueAt(selectedRow, 0);
                    int confirm = JOptionPane.showConfirmDialog(customerBackPanel, "Are you sure you want to delete this shipment?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        if (shipmentController.deleteShipment(shipmentID)) {
                            JOptionPane.showMessageDialog(customerBackPanel, "Shipment deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            modelShipments.setDataVector(shipmentController.getCustomerShipments(username), customerShipmentColumns);
                        } else {
                            JOptionPane.showMessageDialog(customerBackPanel, "Failed to delete shipment!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(customerBackPanel, "Select a shipment to delete", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnTrackStatus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //CODE GOES HERE
            }
        });
    }
}
