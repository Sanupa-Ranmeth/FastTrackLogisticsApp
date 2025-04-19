package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

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

    public AdminView(String username) {
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
    }
}
