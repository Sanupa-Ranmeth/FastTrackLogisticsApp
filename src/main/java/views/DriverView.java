package views;

import javax.swing.*;

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

    public DriverView(String username) {
        setContentPane(driverBackPanel);
        setTitle("FastTrack Logistics - Driver Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
