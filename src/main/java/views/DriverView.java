package views;

import javax.swing.*;
import controllers.DeliveryPersonnelController;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


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

    private DeliveryPersonnelController driverController = new DeliveryPersonnelController();


    public DriverView(String username) {
        setContentPane(driverBackPanel);
        setTitle("FastTrack Logistics - Driver Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        int userId = driverController.getUserIDbyUsername(username);
        System.out.println("User ID: " + userId);

        //View notification button functions
        viewNotificationsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new NotificationView(username).setVisible(true);
                dispose();
            }
        });

        // Set initial state from DB
        boolean isAvailable = driverController.getAvailability(userId);
        isAvailableButton.setText(isAvailable ? "Make Unavailable" : "Make Available");

        // Store availability state
        final boolean[] availabilityState = {isAvailable};

        isAvailableButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Toggle the state
                availabilityState[0] = !availabilityState[0];
                int availabilityValue = availabilityState[0] ? 1 : 0;

                // Update in DB
                driverController.updateAvailability(userId, availabilityValue);

                // Update button text
                isAvailableButton.setText(availabilityState[0] ? "Make UnAvailable " : "Make Available");

                // Notify user
                String message = availabilityState[0] ? "You are now marked as Available." : "You are now marked as UNAVAILABLE.";
                JOptionPane.showMessageDialog(null, message);
            }
        });
    }


}
