package views;

import controllers.NotificationController;
import models.Notification;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NotificationView {
    private JTable notifyTbl;
    private JButton btnmarkAsRead;
    private JButton btnrefresh;
    private JButton btndelete;
    private JLabel lblnotify;
    private NotificationController notificationController; // Declare as instance variable
    private DefaultTableModel tableModel; // Declare as instance variable

    public NotificationView(NotificationController notificationController) {
        this.notificationController = notificationController;
        initializeUI();
    }

    private void initializeUI() {
        JFrame frame = new JFrame("Customer Notification Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        String[] columnNames = {"Type", "Notification", "Generated On"};
        tableModel = new DefaultTableModel(columnNames, 0);
        notifyTbl = new JTable(tableModel); // Use notifyTbl instead of notificationTable
        JScrollPane scrollPane = new JScrollPane(notifyTbl);
        frame.add(scrollPane);

        frame.setVisible(true);
    }

    public void displayNotifications(int userID) {
        tableModel.setRowCount(0);

        List<Notification> notifications = notificationController.getUserNotifications(userID);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Notification notification : notifications) {
            String type;
            String message = notification.getMessage().toLowerCase();
            if (message.contains("approved")) {
                type = "Approval";
            } else if (message.contains("disapproved")) {
                type = "Disapproval";
            } else if (message.contains("estimated delivery")) {
                type = "Estimation";
            } else if (message.contains("en route")) {
                type = "Delivery Update";
            } else {
                type = "Other";
            }

            tableModel.addRow(new Object[]{
                    type,
                    notification.getMessage(),
                    notification.getTimeStamp().format(formatter)
            });
        }
    }
}