package client;
import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;
import java.awt.event.ActionEvent;

// the server and client GUI reference is https://blog.csdn.net/ahuanglu2116/article/details/101161605
// the idea is using a Client-server Communication system to simulate the Socket communication

class ClientGUI extends JFrame {

    private final JTextField textField_IP;
    private final JTextField textField_Port;
    private final JTextField txtCommand;
    private final JLabel label_user;

    private String server_ip, server_port;

    private DataOutputStream toServer;
    private DataInputStream fromServer;
    JTextArea txtMessage;
    Socket socket;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                ClientGUI frame = new ClientGUI();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Create the frame.
     */
    public ClientGUI() throws Exception {
        Properties props = new Properties();
        File configFile = new File("client.config");
        configFile.createNewFile();
        FileReader reader = new FileReader(configFile);
        props.load(reader);
        server_ip = props.getProperty("server_ip");
        if (server_ip == null)
            server_ip = "127.0.0.1";
        server_port = props.getProperty("server_port");
        if (server_port == null)
            server_port = "8000";
        reader.close();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(1000, 100, 900, 750);
        JPanel contentPane = new JPanel();
        contentPane.setToolTipText("Client");
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JPanel panel = new JPanel();
        panel.setBounds(12, 15, 745, 80);
        contentPane.add(panel);
        panel.setLayout(null);

        JLabel lblip = new JLabel("Server IP:");
        lblip.setBounds(12, 15, 65, 15);
        panel.add(lblip);

        textField_IP = new JTextField();
        textField_IP.setText(server_ip);
        textField_IP.setBounds(90, 15, 114, 19);
        panel.add(textField_IP);
        textField_IP.setColumns(10);

        JLabel label = new JLabel("Port:");
        label.setBounds(220, 15, 49, 15);
        panel.add(label);

        textField_Port = new JTextField();
        textField_Port.setText(server_port);
        textField_Port.setBounds(265, 15, 114, 19);
        panel.add(textField_Port);
        textField_Port.setColumns(10);

        label_user = new JLabel("User: guest");
        label_user.setBounds(12, 50, 200, 15);
        panel.add(label_user);

        JButton button = new JButton("Connect");
        button.setBounds(450, 15, 100, 20);
        panel.add(button);

        JButton button_1 = new JButton("Disconnect");
        button_1.setBounds(600, 15, 100, 20);
        panel.add(button_1);

        JPanel panel_1 = new JPanel();
        panel_1.setBounds(15, 100, 840, 540);
        JScrollPane scrollPane = new JScrollPane(panel_1);
        scrollPane.setBounds(15, 100, 840, 540);
        scrollPane.setBackground(Color.LIGHT_GRAY);
        scrollPane.getViewport().setViewPosition(new Point(0, scrollPane.getVerticalScrollBar().getMaximum()));
        contentPane.add(scrollPane);

        txtMessage = new JTextArea();
        DefaultCaret caret = (DefaultCaret)txtMessage.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        txtMessage.setBackground(Color.LIGHT_GRAY);
        txtMessage.setColumns(35);
        txtMessage.setRows(10);
        txtMessage.setTabSize(4);
        txtMessage.setEditable(false);
        //txtMessage.setText("message");
        scrollPane.setViewportView(txtMessage);
        txtCommand = new JTextField();
        txtCommand.addActionListener(new sendListener());
        txtCommand.setBounds(15, 645, 640, 25);
        contentPane.add(txtCommand);
        txtCommand.setColumns(10);

        JButton button_2 = new JButton("Enter");
        button_2.addActionListener(new sendListener());
        button_2.setBounds(670, 645, 80, 25);
        contentPane.add(button_2);

        JButton button_3 = new JButton("Help");
        button_3.addActionListener(new helpListener());
        button_3.setBounds(770, 645, 80, 25);
        contentPane.add(button_3);

        JButton button_4 = new JButton("Clear");
        button_4.addActionListener(new clearListener());
        button_4.setBounds(770, 60, 80, 25);
        contentPane.add(button_4);

        button.setEnabled(true);
        button_1.setEnabled(false);
        button_2.setEnabled(false);
        button_3.setEnabled(false);
        button_4.setEnabled(true);

        String finalServer_ip = server_ip;
        String finalServer_port = server_port;
        button.addActionListener(e -> {
            String ip = textField_IP.getText();
            int port = Integer.parseInt(textField_Port.getText());
            try {
                socket = new Socket(ip, port);
                Client_thread client_thread = new Client_thread();
                Thread thread = new Thread(client_thread);
                thread.start();
                button.setEnabled(false);
                button_1.setEnabled(true);
                button_2.setEnabled(true);
                button_3.setEnabled(true);
                props.setProperty("server_ip", finalServer_ip);
                props.setProperty("server_port", finalServer_port);
                FileWriter writer = new FileWriter(configFile);
                props.store(writer, "Properties");
                writer.close();
            } catch (IOException e1) {
                txtMessage.append(e1.toString() + '\n');
            }
        });

        button_1.addActionListener(e -> {
            try {
                toServer.writeUTF("stop\n");
                toServer.close();
                fromServer.close();
                socket.close();
                txtMessage.append("The client is disconnected" + "\n");
                button.setEnabled(true);
                button_1.setEnabled(false);
                button_2.setEnabled(false);
                button_3.setEnabled(false);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

    }

    private class sendListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
//                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                String time = df.format(Calendar.getInstance().getTime());
                toServer.writeUTF(txtCommand.getText().trim() + '\n');
                txtMessage.append(">" + txtCommand.getText().trim() + '\n');
                txtMessage.scrollRectToVisible(txtMessage.getVisibleRect());
                txtCommand.setText("");
            } catch (Exception e1) {
                System.err.println(e1);
            }
        }
    }

    private class helpListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                toServer.writeUTF("help\n");
                txtMessage.append(">help\n");
                txtCommand.setText("");

            } catch (IOException e1) {
                System.err.println(e1);
            }
        }
    }

    private class clearListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            txtMessage.setText("");
        }
    }

    public class Client_thread implements Runnable {
        public void run() {
            try {
                fromServer = new DataInputStream(socket.getInputStream());
                toServer = new DataOutputStream(socket.getOutputStream());
                while (true) {
                    String fromStr = fromServer.readUTF();
                    String[] lines = fromStr.split("\n");
                    if (lines[0].equals("Login successfully!"))
                        label_user.setText(lines[1]);
                    else if (lines[0].equals("Logout successfully!"))
                        label_user.setText("User: guest");
                    txtMessage.append(fromStr + "\n");
//                    txtMessage.append("The server responded:\n" + fromStr + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}