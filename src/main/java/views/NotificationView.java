package views;

import controllers.NotificationController;
import controllers.ShipmentController;
import models.Notification;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NotificationView extends JFrame {
    private JTable notifyTbl;
    private JButton btnmarkAsRead;
    private JButton btnrefresh;
    private JButton btndelete;
    private JLabel lblnotify;
    private JPanel notificationBackPanel;
    private JTextArea txtAreaMessage;
    private JButton btnBack;
    private final String userRole;

    DefaultTableModel notificationTableModel;
    private NotificationController notificationController; // Declare as instance variable
    private final ShipmentController shipmentController;

    public NotificationView(String username, String userRole) {
        setContentPane(notificationBackPanel);
        setTitle("Notifications");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.userRole = userRole;

        //Set table columns
        String[] notificationTableColumns = {"ID", "Type", "Content", "Generated"};
        Object[][] notificationTableData = {};
        notificationTableModel = new DefaultTableModel(notificationTableData, notificationTableColumns);
        notifyTbl.setModel(notificationTableModel);

        //Hide notificationID column
        notifyTbl.getColumnModel().getColumn(0).setMinWidth(0);
        notifyTbl.getColumnModel().getColumn(0).setMaxWidth(0);
        notifyTbl.getColumnModel().getColumn(0).setWidth(0);

        //Display notification content in the text area
        notifyTbl.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = notifyTbl.getSelectedRow();
                if (selectedRow != -1) {
                    String content = notificationTableModel.getValueAt(selectedRow, 2).toString();
                    txtAreaMessage.setText(content);
                }
            }
        });
        txtAreaMessage.setEditable(false);

        //Finding userID by Username
        shipmentController = new ShipmentController();
        int userID = shipmentController.getUserIDbyUsername(username);
        displayNotifications(userID); //display the notifications

        btnrefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayNotifications(userID);
            }
        });

        btndelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = notifyTbl.getSelectedRow();
                if (selectedRow != -1) {
                    try {
                        int notificationID = Integer.parseInt(notificationTableModel.getValueAt(selectedRow, 0).toString());
                        boolean success = notificationController.deleteNotification(notificationID);
                        if (success) {
                            JOptionPane.showMessageDialog(null, "Notification deleted successfully.");
                            displayNotifications(userID);
                        } else {
                            JOptionPane.showMessageDialog(null, "Failed to delete notification.");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid notification ID format.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a notification to delete.");
                }
            }
        });

        btnmarkAsRead.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = notifyTbl.getSelectedRow();
                if (selectedRow != -1) {
                    try {
                        int notificationID = Integer.parseInt(notificationTableModel.getValueAt(selectedRow, 0).toString());
                        boolean success = notificationController.markNotificationAsRead(notificationID);
                        if (success) {
                            JOptionPane.showMessageDialog(null, "Notification marked as read.");
                            displayNotifications(userID);
                        } else {
                            JOptionPane.showMessageDialog(null, "Failed to mark notification as read.");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid notification ID format.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a notification to mark as read.");
                }
            }
        });

        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ("driver".equalsIgnoreCase(userRole)) {
                    new DriverView(username).setVisible(true);
                    dispose();
                } else {
                    new CustomerView(username).setVisible(true);
                    dispose();
                }
            }
        });
    }

    public void displayNotifications(int userID) {
        notificationTableModel.setRowCount(0);

        notificationController = new NotificationController();
        List<Notification> notifications = notificationController.getUserNotifications(userID);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Notification notification : notifications) {
            notificationTableModel.addRow(new Object[]{
                    notification.getNotificationID(), //This column will be hidden
                    notification.getType(),
                    notification.getMessage(),
                    notification.getTimeStamp().format(formatter)
            });
        }
    }

}