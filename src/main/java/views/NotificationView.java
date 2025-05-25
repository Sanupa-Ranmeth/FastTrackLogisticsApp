package views;

import controllers.NotificationController;
import controllers.ShipmentController;
import models.Notification;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NotificationView extends JFrame {
    private JTable notifyTbl;
    private JButton btnmarkAsRead;
    private JButton btnrefresh;
    private JButton btndelete;
    private JLabel lblnotify;
    private JPanel notificationBackPanel;

    DefaultTableModel notificationTableModel;
    private NotificationController notificationController; // Declare as instance variable
    private ShipmentController shipmentController;

    public NotificationView(String username) {
        setContentPane(notificationBackPanel);
        setTitle("Notifications");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Set table columns
        String[] notificationTableColumns = {"Type", "Content", "Generated"};
        Object[][] notificationTableData = {};
        notificationTableModel = new DefaultTableModel(notificationTableData, notificationTableColumns);
        notifyTbl.setModel(notificationTableModel);

        //Finding userID by Username
        shipmentController = new ShipmentController();
        int userID = shipmentController.getUserIDbyUsername(username);
        displayNotifications(userID); //display the notifications
    }

    public void displayNotifications(int userID) {
        notificationTableModel.setRowCount(0);

        notificationController = new NotificationController();
        List<Notification> notifications = notificationController.getUserNotifications(userID);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Notification notification : notifications) {
            notificationTableModel.addRow(new Object[]{
                    notification.getType(),
                    notification.getMessage(),
                    notification.getTimeStamp().format(formatter)
            });
        }
    }
}