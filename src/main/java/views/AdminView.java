package views;

import controllers.*;
import models.City;
import models.Delivery;
import models.Shipment;
import models.TimeSlot;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class AdminView extends JFrame {
    private JPanel AdminBackPanel;
    private JTabbedPane tabbedPane1;
    private JTable tableShipments;
    private JPanel detailSection;
    private JPanel containerDetails;
    private JButton removeShipmentButton;
    private JButton updateShipmentButton;
    private JLabel lblDestination;
    private JLabel lblContent;
    private JLabel lblCustomer;
    //private JComboBox<String> lbldriverdetails;
    private JComboBox<String> dropdownDriver;
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
    private JTextArea txtAreaDeliveryAddress;
    private JComboBox dropdownDestination;
    private JComboBox dropdownTimeSlot;
    private JComboBox dropdownFilter;
    private JButton disapproveButton;
    private JComboBox dropdownStatus;
    private JComboBox dropdownLocation;
    private JSpinner spinnerDelay;
    private JButton setDeliveryEstimationButton;
    private JButton updateDeliveryButton;
    private JTextField txtReceiver;
    private JTextField txtShipmentID;
    private JButton addShipmentButton;
    private JCheckBox DriverAvailabilityAdminView;

    private DeliveryPersonnelController driverController;
    private TimeSlotController timeSlotController = new TimeSlotController();
    private DeliveryController deliveryController;
    private ShipmentController shipmentController = new ShipmentController();
    private RouteController routeController = new RouteController();
    private CityController cityController;





    //Shipment Table Methods
    private void loadShipments() {
        DefaultTableModel shipmentTableModel = (DefaultTableModel) tableShipments.getModel();
        shipmentTableModel.setRowCount(0);
        String filter = (String) dropdownFilter.getSelectedItem();
        Object[][] data;
        if ("All".equals(filter)) {
            data = shipmentController.getAllAdminShipments();
        } else {
            data = shipmentController.getAdminShipmentsByStatus(filter);
        }
        for (Object[] row : data) {
            shipmentTableModel.addRow(row);
        }
    }

    private void loadTimeSlots() {
        DefaultTableModel timeSlotModel = (DefaultTableModel) tableTimeSlots.getModel();
        timeSlotModel.setRowCount(0);
        List<TimeSlot> timeSlots = timeSlotController.getAllTimeSlots();
        for (TimeSlot ts : timeSlots) {
            timeSlotModel.addRow(new Object[] { ts.getTimeSlotID(), ts.getStartTime(), ts.getEndTime() });
        }
    }

    //Populating dropdowns
    //Populate TimeSlot Dropdown
    private void populateTimeSlotDropdown() {
        dropdownTimeSlot.removeAllItems();
        List<TimeSlot> timeSlots = timeSlotController.getAllTimeSlots();
        if (timeSlots.isEmpty()) {
            dropdownTimeSlot.addItem(null);
        } else {
            for (TimeSlot timeslot : timeSlots) {
                dropdownTimeSlot.addItem(timeslot);
            }
        }

        for (int i=0; i<dropdownTimeSlot.getItemCount(); i++) {
            TimeSlot timeSlot = (TimeSlot) dropdownTimeSlot.getItemAt(i);
            if (timeSlot != null && timeSlot.toString().equals(tableShipments.getModel().getValueAt(tableShipments.getSelectedRow(), 7).toString())) {
                dropdownTimeSlot.setSelectedIndex(i);
                break;
            }
        }

        dropdownTimeSlot.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("No TimeSlots available");
                } else {
                    setText(value.toString());
                }
                return this;
            }
        });
    }

    //Helper method to select timeSlot ID
    private Integer getSelectedTimeSlotID() {
        TimeSlot selectedTimeSlot = (TimeSlot) dropdownTimeSlot.getSelectedItem();
        if (selectedTimeSlot == null) {
            return null;
        }
        Integer timeSlotID = selectedTimeSlot.getTimeSlotID();
        return timeSlotID;
    }

    //Load all time slots on initial load
    private void initiateTimeSlotDropdown() {
        dropdownTimeSlot.removeAllItems();
        List<TimeSlot> timeSlots = timeSlotController.getAllTimeSlots();
        if (timeSlots.isEmpty()) {
            dropdownTimeSlot.addItem(null);
        } else {
            for (TimeSlot timeslot : timeSlots) {
                dropdownTimeSlot.addItem(timeslot);
            }
        }

        dropdownTimeSlot.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("No TimeSlots available");
                } else {
                    setText(value.toString());
                }
                return this;
            }
        });
    }

    //Populate Destination dropdown acc. to route
    private void populateDestinationDropdown (int RouteID) {
        dropdownDestination.removeAllItems();
        List<City> cities = routeController.getCitiesbyRoute(RouteID);
        if (cities.isEmpty()) {
            dropdownDestination.addItem(null);
        } else {
            for (City city : cities) {
                dropdownDestination.addItem(city);
            }
        }

        //Select the destination form the list
        for (int i=0; i<dropdownDestination.getItemCount(); i++) {
            City city = (City) dropdownDestination.getItemAt(i);
            if (city != null && city.getCityName().equalsIgnoreCase(tableShipments.getModel().getValueAt(tableShipments.getSelectedRow(), 3).toString())) {
                dropdownDestination.setSelectedIndex(i);
                break;
            }
        }

        dropdownDestination.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("No Desinations available");
                } else {
                    setText(value.toString());
                }
                return this;
            }
        });
    }

    private void initiateDestinationDropdown() {
        dropdownDestination.removeAllItems();
        List<City> cities = cityController.getAllCities();
        if (cities.isEmpty()) {
            dropdownDestination.addItem(null);
        } else {
            for (City city : cities) {
                dropdownDestination.addItem(city);
            }
        }

        dropdownDestination.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("No Desinations available");
                } else {
                    setText(value.toString());
                }
                return this;
            }
        });
    }

    //populating location dropdown
    private void populateLocationDropdown (int RouteID) {
        dropdownLocation.removeAllItems();

        if (tableShipments.getModel().getValueAt(tableShipments.getSelectedRow(), 12) == null) {
            dropdownLocation.addItem("Shipment Not Approved");
            dropdownLocation.setSelectedIndex(0);
            dropdownLocation.setRenderer(new DefaultListCellRenderer() {
                @Override
                public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    setText(value.toString());
                    return this;
                }
            });
            return;
        }

        List<City> cities = routeController.getCitiesbyRoute(RouteID);
        if (cities.isEmpty()) {
            dropdownLocation.addItem(null);
        } else {
            for (City city : cities) {
                dropdownLocation.addItem(city);
            }
        }

        //Select the destination form the list
        for (int i=0; i<dropdownLocation.getItemCount(); i++) {
            City city = (City) dropdownLocation.getItemAt(i);
            if (city != null && city.getCityName().equalsIgnoreCase(tableShipments.getModel().getValueAt(tableShipments.getSelectedRow(), 12).toString())) {
                dropdownLocation.setSelectedIndex(i);
                break;
            }
        }

        dropdownLocation.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof City) {
                    setText(value.toString());
                } else if (value instanceof String) {
                    setText((String) value);
                }
                return this;
            }
        });
    }

    private void clearShipmentDetails() {
        txtShipmentID.setText("");
        initiateDestinationDropdown();
        txtDeliveryDate.setText("");
        initiateTimeSlotDropdown();
        txtContent.setText("");
        txtCustomer.setText("");
        txtReceiver.setText("");
        txtReceiver.setText("");
        txtAreaDeliveryAddress.setText("");
        checkboxUrgent.setSelected(false);
        spinnerDelay.setValue(0);
    }

    //Driver Table Methods
    private void refreshDriverTable() {
        DefaultTableModel model = (DefaultTableModel) tableDrivers.getModel();
        model.setDataVector(driverController.getAllDrivers(), new String[] { "ID", "Name", "Schedule", "Route", "Average Rating" , "IsAvailable" });
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

        //---------------Initializing Controllers----------------//
        deliveryController = new DeliveryController();
        cityController = new CityController();
        //------------------------------------------------------//

        //Shipment table model
        String[] ShipmentColumns = {"ShipmentID", "SenderID", "Receiver Name", "Destination", "Destination Address", "Contents", "isUrgent", "Preferred Time Slot", "Delivery Date", "DriverID", "Driver Name", "Status", "Location", "Delay", "EstimatedDateTime", "ActualDeliveryTime"};
        Object[][] ShipmentData = {}; //to be dynamically generated

        DefaultTableModel modelShipments = new DefaultTableModel(ShipmentData, ShipmentColumns);
        tableShipments.setModel(modelShipments);

        //TimeSlot table model
        String[] TimeSlotColumns = {"TimeSlotID", "StartTime", "EndTime"};
        DefaultTableModel modelTimeSlots = new DefaultTableModel(TimeSlotColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }


        };
        tableTimeSlots.setModel(modelTimeSlots);

        //Driver table model
        String[] DriverColumns = {"ID", "Name", "Schedule", "Route", "Average Rating", "IsAvailable"};
        Object[][] DriverData = {}; //to be dynamically generated

        DefaultTableModel modelDrivers = new DefaultTableModel(DriverData, DriverColumns);
        tableDrivers.setModel(modelDrivers);

        //Delivery History table model
        String[] DeliveryHistoryColumns = {"Shipment ID", "Delivery Date", "Rating"};
        Object[][] DeliveryHistoryData = {}; //to be dynamically generated

        DefaultTableModel modelDeliveryHistory = new DefaultTableModel(DeliveryHistoryData, DeliveryHistoryColumns);
        tableDeliveryHistory.setModel(modelDeliveryHistory);

        //SHIPMENT SECTION----------------------------------------------------------------------------------------------

        //Loading initial data
        loadTimeSlots();
        loadShipments();
        initiateTimeSlotDropdown();
        initiateDestinationDropdown();

                                //--------- SHIPMENT TABLE BUTTON ACTIONS -----------
        //dropdown filter
        dropdownFilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadShipments();
            }
        });

        //Populating form when row is selected
        tableShipments.getSelectionModel().addListSelectionListener( event -> {
            int selectedRow = tableShipments.getSelectedRow();
            if (selectedRow >= 0) {
                DefaultTableModel model = (DefaultTableModel) tableShipments.getModel();
                txtShipmentID.setText(model.getValueAt(selectedRow, 0).toString());
                txtDeliveryDate.setText(model.getValueAt(selectedRow, 8).toString());
                txtContent.setText(model.getValueAt(selectedRow, 5).toString());
                txtCustomer.setText(model.getValueAt(selectedRow, 1).toString());
                txtReceiver.setText(model.getValueAt(selectedRow, 2).toString());
                txtAreaDeliveryAddress.setText(model.getValueAt(selectedRow, 4).toString());
                checkboxUrgent.setSelected(model.getValueAt(selectedRow, 6) != null ? (Boolean) model.getValueAt(selectedRow, 6) : false);
                spinnerDelay.setValue(model.getValueAt(selectedRow, 13));

                //Populating timeslot and destination dropdowns
                populateDestinationDropdown(routeController.getRouteIDFromCityName((String) model.getValueAt(selectedRow, 3)));
                populateTimeSlotDropdown();

                //Set the dropdown status to package status
                String status = model.getValueAt(selectedRow, 11).toString();
                dropdownStatus.setSelectedItem(status);

                //Set dropdown location
                populateLocationDropdown(routeController.getRouteIDFromCityName((String) model.getValueAt(selectedRow, 3)));
            }
        });

        approveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int shipmentID = Integer.parseInt(txtShipmentID.getText());
                int driverID = Integer.parseInt(dropdownDriver.getSelectedItem().toString());

                if (deliveryController.approveDelivery(shipmentID, driverID)) {
                    JOptionPane.showMessageDialog(AdminBackPanel, "Delivery Approved Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearShipmentDetails();
                    loadShipments();
                }
            }
        });

        disapproveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int shipmentID = Integer.parseInt(txtShipmentID.getText());

                if (deliveryController.disapproveDelivery(shipmentID)) {
                    JOptionPane.showMessageDialog(AdminBackPanel, "Delivery Disapproved!", "Disapproved", JOptionPane.INFORMATION_MESSAGE);
                    clearShipmentDetails();
                    loadShipments();
                }
            }
        });

        setDeliveryEstimationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tableShipments.getSelectedRow();

                if (selectedRow >= 0) {
                    DefaultTableModel model = (DefaultTableModel) tableShipments.getModel();

                    int shipmentID = Integer.parseInt(txtShipmentID.getText());
                    String deliveryDateString = model.getValueAt(selectedRow, 8).toString();
                    String timeSlotString = model.getValueAt(selectedRow, 7).toString();

                    String defaultInput = deliveryDateString + " " + timeSlotString;

                    String userInput = JOptionPane.showInputDialog(
                            AdminBackPanel,
                            "Enter estimated delivery date and time (YYYY-MM-DD HH:MM:SS):)",
                            "Set Estimated Delivery",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            null,
                            defaultInput
                    ).toString();

                    if (userInput != null && !userInput.trim().isEmpty()) {
                        try {
                            //Format Date
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            LocalDateTime estimation = LocalDateTime.parse(userInput.trim(), formatter);

                            if (deliveryController.setDeliveryEstimation(shipmentID, estimation)) {
                                JOptionPane.showMessageDialog(AdminBackPanel, "Delivery Estimation Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(AdminBackPanel, "Delivery Estimation Failed!", "Failure", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (DateTimeParseException ex) {
                            System.out.println("Invalid Date Time Format: " + ex.getMessage());
                            JOptionPane.showMessageDialog(AdminBackPanel, "Invalid Date Time Format. Please use YYYY-MM-DD HH:MM", "Date Time Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }

                    loadShipments();
                }
            }
        });

        updateDeliveryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int shipmentID = Integer.parseInt(txtShipmentID.getText());
                String status = dropdownStatus.getSelectedItem().toString();
                String location = dropdownLocation.getSelectedItem().toString();
                int delay = ((Number) spinnerDelay.getValue()).intValue();

                try {
                    if (deliveryController.updateDeliveryOperations(shipmentID, status, cityController.getCityIDByCityName(location), delay)) {
                        JOptionPane.showMessageDialog(AdminBackPanel, "Delivery Operations Updated Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(AdminBackPanel, "Delivery Operations Update Failed!\n" + ex.getMessage(), "Failure", JOptionPane.ERROR_MESSAGE);
                }
                loadShipments();
                clearShipmentDetails();
            }
        });


        addShipmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String destination = dropdownDestination.getSelectedItem().toString();
                int timeSlotID = getSelectedTimeSlotID();
                String content = txtContent.getText();
                int DriverID = Integer.parseInt(dropdownDriver.getSelectedItem().toString());
                String receiverName = txtReceiver.getText();
                String address = txtAreaDeliveryAddress.getText();
                boolean isUrgent = checkboxUrgent.isSelected();

                //Format date
                String deliveryDateString = txtDeliveryDate.getText();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date deliveryDate = null;

                try {
                    deliveryDate = sdf.parse(deliveryDateString);
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(AdminBackPanel, "Invalid Date Format. Use YYYY-MM-DD", "Date Format Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                //Validate input
                if (destination.trim().isEmpty() || timeSlotID <= 0 || receiverName.trim().isEmpty() || address.trim().isEmpty() || deliveryDateString.trim().isEmpty() || content.trim().isEmpty() || DriverID <= 0) {
                    JOptionPane.showMessageDialog(AdminBackPanel, "Please Complete All Required Fields", "Required", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                //getting City ID
                int cityID = cityController.getCityIDByCityName(destination);

                Shipment shipment = new Shipment(receiverName, cityID, address, content, isUrgent, deliveryDate, timeSlotID, "Approved");
                Delivery delivery = new Delivery(DriverID);
                int senderID = shipmentController.getUserIDbyUsername(username);

                if (deliveryController.addDelivery(senderID, shipment, delivery)) {
                    JOptionPane.showMessageDialog(AdminBackPanel, "Delivery Added Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadShipments();
                    clearShipmentDetails();
                } else {
                    JOptionPane.showMessageDialog(AdminBackPanel, "Failed to Add Delivery", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        updateShipmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tableShipments.getSelectedRow();

                if (selectedRow < 0) {
                    JOptionPane.showMessageDialog(AdminBackPanel, "Please Select a Shipment to Update", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int shipmentID = Integer.parseInt(txtShipmentID.getText());
                String destination = dropdownDestination.getSelectedItem().toString();
                int timeSlotID = getSelectedTimeSlotID();
                String content = txtContent.getText();
                int CustomerID = Integer.parseInt(txtCustomer.getText());
                int DriverID = Integer.parseInt(dropdownDriver.getSelectedItem().toString());
                String receiverName = txtReceiver.getText();
                String address = txtAreaDeliveryAddress.getText();
                boolean isUrgent = checkboxUrgent.isSelected();
                String status = dropdownStatus.getSelectedItem().toString();
                String location = dropdownLocation.getSelectedItem().toString();
                int delay = ((Number) spinnerDelay.getValue()).intValue();
                boolean isDelayed = delay > 0;

                //Format date for Shipment (Requires Date)
                String deliveryDateString = txtDeliveryDate.getText();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date deliveryDate = null; //This will be inserted into the Shipment table

                try {
                    deliveryDate = sdf.parse(deliveryDateString);
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(AdminBackPanel, "Invalid Date Format. Use YYYY-MM-DD", "Date Format Error", JOptionPane.ERROR_MESSAGE);
                    System.out.println("Parsing Delivery Date to be inserted into Shipment table failed.");
                    return;
                }

                //Format date for Delivery (Requires LocalDateTime)
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime deliveryDateTime = null; //This will be inserted into the Delivery Table

                try {
                    String deliveryDateTimeString = deliveryDateString + " " + tableShipments.getModel().getValueAt(selectedRow, 7).toString();
                    deliveryDateTime = LocalDateTime.parse(deliveryDateTimeString, dtf);
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(AdminBackPanel, "Invalid Date Format: Use YYYY-MM-DD", "Date Format Error", JOptionPane.ERROR_MESSAGE);
                    System.out.println("Parsing Delivery Date to be inserted into Delivery table failed.");
                    return;
                }

                //Validate input
                if (shipmentID <= 0 || destination.trim().isEmpty() || timeSlotID <= 0 || CustomerID <= 0 || receiverName.trim().isEmpty() || address.trim().isEmpty() || deliveryDateString.trim().isEmpty() || content.trim().isEmpty() || DriverID <= 0) {
                    JOptionPane.showMessageDialog(AdminBackPanel, "Please Complete All Required Fields", "Required", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                //getting City ID
                int cityID = cityController.getCityIDByCityName(destination);
                //getting current location
                int locationID = cityController.getCityIDByCityName(location);

                Shipment shipment = new Shipment(shipmentID, CustomerID, receiverName, cityID, address, content, isUrgent, deliveryDate, timeSlotID, status);
                Delivery delivery = new Delivery(shipmentID, DriverID, locationID, isDelayed, delay, deliveryDateTime);

                if (deliveryController.updateDelivery(shipment, delivery)) {
                    JOptionPane.showMessageDialog(AdminBackPanel, "Delivery Updated Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadShipments();
                    clearShipmentDetails();
                } else {
                    JOptionPane.showMessageDialog(AdminBackPanel, "Failed to Update Delivery", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        removeShipmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tableShipments.getSelectedRow();
                if (selectedRow < 0) {
                    JOptionPane.showMessageDialog(AdminBackPanel, "Please Select a Shipment to Update", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int shipmentID = Integer.parseInt(txtShipmentID.getText());

                //Validating
                if (shipmentID <= 0) {
                    JOptionPane.showMessageDialog(AdminBackPanel, "Invalid Shipment ID", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (deliveryController.deleteDelivery(shipmentID)) {
                    JOptionPane.showMessageDialog(AdminBackPanel, "Delivery Deleted Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadShipments();
                    clearShipmentDetails();
                } else {
                    JOptionPane.showMessageDialog(AdminBackPanel, "Failed to Delete Delivery", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });



                                //--------- TIMESLOT TABLE BUTTON ACTIONS -----------
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

        // Populate the driver dropdown with names
        populateDriverDropdown();

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
                boolean defaultIsAvailable = true;


                if (driverController.addDriver(username, password, email, driverName, schedule, routeID , defaultIsAvailable)) {
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
                    boolean defaultIsAvailable = true;


                    if (driverController.updateDrivers(driverID, username, password, email, driverName, schedule, routeID, defaultIsAvailable)) {
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
            public void actionPerformed(ActionEvent e) { clearDriverForm();
            }
        });


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

        DriverAvailabilityAdminView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tableDrivers.getSelectedRow();
                if (selectedRow >= 0) {
                    int driverID = (int) tableDrivers.getValueAt(selectedRow, 0);

                }

            }
        });
    }


    //Populate DriverDropDown
    public void  populateDriverDropdown() {

        dropdownDriver.removeAllItems();  //  Clear previous items

        //Fetch Driver Names
        List<String> driverNames = driverController.getAllDriverNames();

        //Addding driver names to the drop down
        for(String name: driverNames) {
            dropdownDriver.addItem(name); // add each driver name as an item
        }
    }
}
