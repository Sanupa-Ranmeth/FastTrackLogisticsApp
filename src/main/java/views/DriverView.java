package views;

import javax.swing.*;
import controllers.DeliveryPersonnelController;
import models.DeliveryPersonnelDAO;

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
    private JCheckBox isAvailableCheckBox;

    private DeliveryPersonnelController driverController = new DeliveryPersonnelController();


    public DriverView(String username) {
        setContentPane(driverBackPanel);
        setTitle("FastTrack Logistics - Driver Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Set initial state from DB
        boolean isAvailable = driverController.getAvailability(username);
        isAvailableCheckBox.setSelected(isAvailable);

        isAvailableCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    boolean selected = isAvailableCheckBox.isSelected(); //true= ON | false = OFF
                    int availabilityValue = selected?1:0;

                    driverController.updateAvailability(username, availabilityValue);

                    //Notify the USER
                    String message = selected? "You are now marked as Available." : "You are now marked as UNAVAILABLE.";
                    JOptionPane.showMessageDialog(null, message);



            }
        });
    }

}
