package views;

import controllers.CityController;
import controllers.ShipmentController;
import controllers.TimeSlotController;
import models.City;
import models.Shipment;
import models.ShipmentDAO;
import models.TimeSlot;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.util.List;

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
    private JComboBox<TimeSlot> dropdownTimeSlot;
    private JCheckBox urgentDeliveryCheckBox;
    private JButton btnAddShipment;
    private JPanel customerBackPanel;
    private JComboBox dropdownDestination;
    private JTextField txtDeliveryDate;

    private ShipmentController shipmentController;
    private CityController cityController = new CityController();
    private TimeSlotController timeSlotController = new TimeSlotController();
    private ShipmentDAO shipmentDAO;
    private String username;

    //------------------- METHODS --------------------------------------------------------------------------------------

    private void clearForm() {
        txtReceiver.setText("");
        txtAreaDestination.setText("");
        txtPkgContents.setText("");
        dropdownTimeSlot.setSelectedIndex(0);
        urgentDeliveryCheckBox.setSelected(false);
    }

    private void loadShipments() {
        DefaultTableModel model = (DefaultTableModel) tableCustomerShipments.getModel();
        model.setDataVector(shipmentController.getCustomerShipments(username), new String[] {"Package ID", "Sent To", "Destination", "Destination Address", "Content", "Status", "Delivery Date", "Urgent", "Time Slot"});
    }

    private void populateDestinationDropdown() {
        dropdownDestination.removeAllItems();
        List<City> cities = cityController.getAllCities();
        for (City city : cities) {
            dropdownDestination.addItem(city.getCityName());
        }
    }

    private void populateTimeSlotDropdown() {
        dropdownTimeSlot.removeAllItems();
        List<TimeSlot> timeSlots = timeSlotController.getAllTimeSlots();
        if (timeSlots.isEmpty()) {
            dropdownTimeSlot.addItem(null);
        } else {
            for (TimeSlot timeSlot : timeSlots) {
                dropdownTimeSlot.addItem(timeSlot);
            }
            dropdownTimeSlot.setSelectedIndex(0);
        }

        dropdownTimeSlot.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("No time slot available");
                } else {
                    setText(value.toString());
                }
                return this;
            }
        });
    }

    private Integer getSelectedCityID() {
        String selectedCityName = (String) dropdownDestination.getSelectedItem();
        if (selectedCityName == null) {
            return null;
        }
        List<City> cities = cityController.getAllCities();
        for (City city : cities) {
            if (city.getCityName().equals(selectedCityName)) {
                return city.getCityID();
            }
        }
        return null;
    }

    private Integer getSelectedTimeSlotID() {
        TimeSlot selectedTimeSlot = (TimeSlot) dropdownTimeSlot.getSelectedItem();
        if (selectedTimeSlot == null) {
            return null;
        }
        Integer timeSlotID = selectedTimeSlot.getTimeSlotID();
        return timeSlotID;
    }

    //------------------ CUSTOMER VIEW ---------------------------------------------------------------------------------
    public CustomerView(String username) {
        setContentPane(customerBackPanel);
        setTitle("FastTrack Logistics - Customer Dashboard");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.username = username;
        this.shipmentController = new ShipmentController();
        this.shipmentDAO = new ShipmentDAO();

        //Populate dropdowns
        populateDestinationDropdown();
        populateTimeSlotDropdown();

        //Customer Shipment table model
        String[] customerShipmentColumns = {"Package ID", "Sent To", "Destination", "Destination Address", "Content", "Status", "Delivery Date", "Urgent", "Time Slot"};
        Object[][] customerShipmentData = {}; //to be dynamically generated

        DefaultTableModel modelShipments = new DefaultTableModel(customerShipmentData, customerShipmentColumns);
        tableCustomerShipments.setModel(modelShipments);

        loadShipments(); //Loading shipments into the table on page load

        //Button Actions
        btnAddShipment.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String receiverName = txtReceiver.getText();
                String destinationAddress = txtAreaDestination.getText();
                String contents = txtPkgContents.getText();
                String deliveryDateString = txtDeliveryDate.getText();
                boolean isUrgent = urgentDeliveryCheckBox.isSelected();

                //Validating input
                if (receiverName.isEmpty() || destinationAddress.isEmpty() || contents.isEmpty() || deliveryDateString.isEmpty() || dropdownDestination.getSelectedItem() == null || dropdownTimeSlot.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(customerBackPanel, "Please complete all fields", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                //Fix delivery date format
                Date deliveryDate;
                try {
                    deliveryDate = Date.valueOf(deliveryDateString); //Expects YYYY-MM-DD
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(customerBackPanel, "Date format is invalid. Use YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                //Getting TimeSlotID and CityID
                Integer timeslotID = getSelectedTimeSlotID();
                Integer cityID = getSelectedCityID();

                //System.out.println("Time Slot Id: " + timeslotID);
                Shipment shipment = new Shipment(receiverName, cityID, destinationAddress, contents, isUrgent, deliveryDate, timeslotID, "Pending");

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
                DefaultTableModel model = (DefaultTableModel) tableCustomerShipments.getModel();
                txtReceiver.setText(model.getValueAt(selectedRow, 1).toString());
                dropdownDestination.setSelectedItem(model.getValueAt(selectedRow, 2).toString());
                txtAreaDestination.setText(model.getValueAt(selectedRow, 3).toString());
                txtPkgContents.setText(model.getValueAt(selectedRow, 4).toString());
                txtDeliveryDate.setText(model.getValueAt(selectedRow, 6).toString());
                urgentDeliveryCheckBox.setSelected(model.getValueAt(selectedRow, 7) != null ? (Boolean) model.getValueAt(selectedRow, 7) : false);

                //Find timeslot object that matches the display string
                String timeSlotString = model.getValueAt(selectedRow, 8).toString();
                List<TimeSlot> timeSlots = timeSlotController.getAllTimeSlots();
                for (TimeSlot timeSlot : timeSlots) {
                    if (timeSlot.toString().equals(timeSlotString)) {
                        dropdownTimeSlot.setSelectedItem(timeSlot);
                        break;
                    }
                }
            } else {
                clearForm();
            }
        });

        btnUpdateShipment.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tableCustomerShipments.getSelectedRow();
                if (selectedRow >= 0) {
                    int shipmentID = (int) ((DefaultTableModel) tableCustomerShipments.getModel()).getValueAt(selectedRow, 0);
                    String receiverName = txtReceiver.getText();
                    String destinationAddress = txtAreaDestination.getText();
                    String contents = txtPkgContents.getText();
                    String deliveryDateString = txtDeliveryDate.getText();
                    boolean isUrgent = urgentDeliveryCheckBox.isSelected();

                    //Validation
                    if (receiverName.isEmpty() || destinationAddress.isEmpty() || contents.isEmpty() || deliveryDateString.isEmpty() || dropdownDestination.getSelectedItem() == null || dropdownTimeSlot.getSelectedItem() == null) {
                        JOptionPane.showMessageDialog(customerBackPanel, "Please complete all fields", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    //Fix Delivery Date Format
                    Date deliveryDate;
                    try {
                        deliveryDate = Date.valueOf(deliveryDateString); //YYYY-MM-DD
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(customerBackPanel, "Date format is invalid. Use YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    //Getting TimeSlotID and CityID
                    Integer timeSlotID = getSelectedTimeSlotID();
                    Integer cityID = getSelectedCityID();

                    //Before updating the shipment via the full constructor, we need to fetch the SenderID from the database
                    int senderID = shipmentController.getUserIDbyUsername(username);
                    if (senderID == -1) {
                        JOptionPane.showMessageDialog(customerBackPanel, "Sender not found in the database!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    Shipment shipment = new Shipment(shipmentID, senderID, receiverName, cityID, destinationAddress, contents, isUrgent, deliveryDate, timeSlotID, "Pending");

                    if (shipmentController.updateShipment(shipment)) {
                        JOptionPane.showMessageDialog(customerBackPanel, "Shipment updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        ((DefaultTableModel) tableCustomerShipments.getModel()).setDataVector(shipmentController.getCustomerShipments(username), customerShipmentColumns);
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
                    int shipmentID = (int) ((DefaultTableModel) tableCustomerShipments.getModel()).getValueAt(selectedRow, 0);
                    int confirm = JOptionPane.showConfirmDialog(customerBackPanel, "Are you sure you want to delete this shipment?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        if (shipmentController.deleteShipment(shipmentID)) {
                            JOptionPane.showMessageDialog(customerBackPanel, "Shipment deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            ((DefaultTableModel) tableCustomerShipments.getModel()).setDataVector(shipmentController.getCustomerShipments(username), customerShipmentColumns);
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

                {
                    int selectedRow = tableCustomerShipments.getSelectedRow();
                    if (selectedRow >= 0) {

                     int shipmentID = (int) ((DefaultTableModel) tableCustomerShipments.getModel()).getValueAt(selectedRow, 0);
                        String[] labels = {"Status: ", "Current Location: ", "Delivery Estimation: ", "Delay: "};
                        StringBuilder displayText = new StringBuilder();
                        for (int i = 0; i < labels.length; i++) {
                            displayText.append(labels[i]).append(ShipmentDAO.getShipmentTracking(shipmentID)[0][i]).append("\n");
                        }
                        JOptionPane.showMessageDialog(null,displayText.toString(), "Track Status", JOptionPane.INFORMATION_MESSAGE);
                    }
                    else {
                        JOptionPane.showMessageDialog(customerBackPanel, "Select a shipment to Track Status", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }
}
