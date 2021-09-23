package server;
import java.io.*;
import java.net.Socket;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.lang.NumberFormatException;

public class ServerWorker extends Thread {

    private final Server server;
    private final Socket clientSocket;
    private String user = "guest";
    private String role = "guest";
    private DataInputStream inputFromClient;
    private DataOutputStream outputToClient;

    public ServerWorker(Server server, Socket clientSocket) throws IOException{
        this.server = server;
        this.clientSocket = clientSocket;
        inputFromClient = new DataInputStream(this.clientSocket.getInputStream());
        outputToClient = new DataOutputStream(this.clientSocket.getOutputStream());
    }

    @Override
    public void run() {
        try {
            handleClientSocket();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void handleClientSocket() throws IOException {
        outputToClient.writeUTF("******************************************************\n");
        outputToClient.writeUTF("           Welcome to StreamingWars system!           \n");
        outputToClient.writeUTF("******************************************************\n");
        outputToClient.writeUTF("Please login to the system\n");
        String line;
        final String DELIMITER = "[\n,]";
        while (true) {
            try {
                line = inputFromClient.readUTF();
                String[] tokens = line.split(DELIMITER);
                for (int i = 0; i < tokens.length; i++)
                    tokens[i] = tokens[i].trim();
                int cmd_valid = server.validate_cmd(tokens, role);
                if (cmd_valid != 0) {
                    if (cmd_valid == 1 || cmd_valid == 2)
                        outputToClient.writeUTF("Wrong command!");
                    else
                        outputToClient.writeUTF("Command not permitted!");
                }
                else if (tokens[0].equals("stop")) {
                    outputToClient.writeUTF("The connection is terminated!");
                    inputFromClient.close();
                    outputToClient.close();
                    clientSocket.close();
                    break;
                }
                else if (tokens[0].equals("help")) {
                    outputToClient.writeUTF("Your allowed commands are listed as below:");
                    String results = server.get_help(role);
                    outputToClient.writeUTF(results);
                }
                else if (tokens[0].equals("login")) {
                    handlelogin(tokens);
                }
                else if (tokens[0].equals("register")) {
                    handleregister(tokens);
                }
                else if (tokens[0].equals("logout")) {
                    user = "guest";
                    role = "guest";
                    outputToClient.writeUTF("Logout successfully!\n");
                }
                else if (tokens[0].equals("change_password")) {
                    int pw_msg = server.getUser().password_strength_check(tokens[2]);
                    if (pw_msg < 0) {
                        switch (pw_msg) {
                            case -1: outputToClient.writeUTF(("Password must contain at least 8 characters.\n")); break;
                            case -2: outputToClient.writeUTF(("Password must contain at least one digit.\n")); break;
                            case -3: outputToClient.writeUTF(("Password must contain at least one lowercase letter.\n")); break;
                            case -4: outputToClient.writeUTF(("Password must contain at least one uppercase letter.\n")); break;
                            case -5: outputToClient.writeUTF(("Password must contain at least one special character.\n")); break;
                            default: break;
                        }
                    }
                    else if (server.change_password(user, tokens[1], tokens[2]))
                        outputToClient.writeUTF("Password changed!\n");
                    else
                        outputToClient.writeUTF("Password change failed!\n");;
                }
                else if (tokens[0].equals("create_demo")) {
                    String demoShortName = tokens[1];
                    String demoLongName = tokens[2];
                    int demoAccounts = Integer.parseInt(tokens[3]);
                    if (server.create_demo(demoShortName, demoLongName, demoAccounts))
                        outputToClient.writeUTF("Demographic group named \"" + demoShortName + "\" with account numbers: " + tokens[3] + " has been created!\r\n");
                    else
                        outputToClient.writeUTF("Demographic group not created!\n");;
                }
                else if (tokens[0].equals("create_studio")) {
                    String studioShortName = tokens[1];
                    String studioLongName = tokens[2];
                    if (server.create_studio(studioShortName, studioLongName))
                        outputToClient.writeUTF("Studio named \"" + studioShortName + "\" has been created!\r\n");
                    else
                        outputToClient.writeUTF("Studio not created!\n");;
                }
                else if (tokens[0].equals("create_event")) {
                    String eventType = tokens[1];
                    String eventFullName= tokens[2];
                    int eventYear = Integer.parseInt(tokens[3]);
                    int eventDuration= Integer.parseInt(tokens[4]);
                    String eventStudioOwner = tokens[5];
                    int eventLicenseFee = Integer.parseInt(tokens[6]);
                    if (server.create_event(eventType, eventFullName, eventYear, eventDuration, eventStudioOwner, eventLicenseFee))
                        outputToClient.writeUTF("Event with the type as \"" + eventType + "\" has been created!\r\n");
                    else
                        outputToClient.writeUTF("Event not created!\n");;
                }
                else if (tokens[0].equals("create_stream")) {
                    String streamShortName= tokens[1];
                    String streamLongName = tokens[2];
                    int streamSubscription = Integer.parseInt(tokens[3]);
                    if (server.create_stream(streamShortName, streamLongName, streamSubscription))
                        outputToClient.writeUTF("Streaming service named \"" + streamShortName + "\" has been created\r\n");
                    else
                        outputToClient.writeUTF("Streaming service not created!\n");;
                }
                else if (tokens[0].equals("offer_movie") || tokens[0].equals("offer_ppv")) {
                    String offerType = tokens[0].substring(6);
                    String offerStream = tokens[1];
                    String offerEventName = tokens[2];
                    if (!role.equals("admin") && !user.equals(tokens[1])) {
                        outputToClient.writeUTF("Command not permitted!");
                        continue;
                    }
                    int offerEventYear = Integer.parseInt(tokens[3]);
                    int offerEventPrice = 0;
                    if (offerType.equals("ppv"))
                        offerEventPrice = Integer.parseInt(tokens[4]);
                    if (server.create_offer(offerType, offerStream, offerEventName, offerEventYear, offerEventPrice))
                        outputToClient.writeUTF("Prospective viewers are allowed to watch \"" + offerType + "\" with the name as \"" + offerEventName + "\"\r\n");
                    else
                        outputToClient.writeUTF("Offer not created!\n");
                }
                // update_demo
                else if (tokens[0].equals("update_demo")) {
                    String demoShortName = tokens[1];
                    String demoLongName = tokens[2];
                    if (!role.equals("admin")) {
                        outputToClient.writeUTF("Command not permitted!");
                        continue;
                    }
                    int demoAccounts = Integer.parseInt(tokens[3]);
                    int result = server.update_demo(demoShortName, demoLongName, demoAccounts);
                    if (result == 2)
                        outputToClient.writeUTF("Demographic group named \"" + demoShortName + "\" has been updated!\r\n");
                    else if (result == 1)
                        outputToClient.writeUTF("The number of accounts can not be changed!\r\n");
                    else
                        outputToClient.writeUTF("Command is invalid!\n");;
                }

                // update_event
                else if (tokens[0].equals("update_event")) {
                    String eventFullName= tokens[1];
                    int eventYear = Integer.parseInt(tokens[2]);
                    int eventDuration= Integer.parseInt(tokens[3]);
                    int eventLicenseFee = Integer.parseInt(tokens[4]);
                    String studioShortName = server.getEvent().get_studio(eventFullName,eventYear);
                    if (!role.equals("admin") && !user.equals(studioShortName)) {
                        outputToClient.writeUTF("Command not permitted!");
                        continue;
                    }
                    int result = server.update_event(eventFullName, eventYear, eventDuration, eventLicenseFee);
                    if (result == 2)
                        outputToClient.writeUTF("Event named \"" + eventFullName + "\" has been updated!\r\n");
                    else if (result == 1)
                        outputToClient.writeUTF("The license fee can not be changed!\r\n");
                    else
                        outputToClient.writeUTF("Command is invalid!\n");
                }

                // update_stream
                else if (tokens[0].equals("update_stream")) {
                    String streamShortName = tokens[1];
                    String streamLongName = tokens[2];
                    int streamSubscription = Integer.parseInt(tokens[3]);
                    if (!role.equals("admin") && !user.equals(streamShortName)) {
                        outputToClient.writeUTF("Command not permitted!");
                        continue;
                    }
                    int result = server.update_stream(streamShortName, streamLongName, streamSubscription);
                    if (result == 2)
                        outputToClient.writeUTF("Streaming service named \"" + streamShortName + "\" has been updated!\r\n");
                    else if (result == 1)
                        outputToClient.writeUTF("The subscription price can not be changed!\r\n");
                    else
                        outputToClient.writeUTF("Command is invalid!\n");;
                }

                // retract_movie
                else if (tokens[0].equals("retract_movie")) {
                    String streamShortName = tokens[1];
                    String eventFullName = tokens[2];
                    int eventYear = Integer.parseInt(tokens[3]);
                    if (!role.equals("admin") && !user.equals(streamShortName)) {
                        outputToClient.writeUTF("Command not permitted!");
                        continue;
                    }
                    int result = server.retract_movie(streamShortName, eventFullName, eventYear);
                    if (result == 2)
                        outputToClient.writeUTF("Movie \"" + eventFullName + "\" offered by streaming service \"" + streamShortName + "\" has been retracted!\r\n");
                    else if (result == 1)
                        outputToClient.writeUTF("The movie can not be retracted!\r\n");
                    else
                        outputToClient.writeUTF("Command is invalid!\n");
                }
                else if (tokens[0].equals("watch_event")) {
                    String watchDemoGroup = tokens[1];
                    int watchPercentage = Integer.parseInt(tokens[2]);
                    String watchStream = tokens[3];
                    String watchEventName = tokens[4];
                    int watchEventYear = Integer.parseInt(tokens[5]);
                    if (!role.equals("admin") && !user.equals(tokens[3])) {
                        outputToClient.writeUTF("Command not permitted!");
                        continue;
                    }
                    if (server.watch_event(watchDemoGroup, watchPercentage, watchStream, watchEventName, watchEventYear))
                        outputToClient.writeUTF("Group named \"" + watchDemoGroup + "\" with percentage of the accounts as \"" + tokens[2] + "\" percent have decided to access and watch the \"" + watchEventName + "\"\r\n");
                    else
                        outputToClient.writeUTF("Event not created!\n");;
                }
                else if (tokens[0].equals("archive_transaction")) {
                    int year = Integer.parseInt(tokens[1]);
                    int month = Integer.parseInt(tokens[2]);
                    if (server.archive_transaction(user,year,month))
                        outputToClient.writeUTF("Transactions archived!\r\n");
                    else
                        outputToClient.writeUTF("There is no transaction to be archived!\n");;
                }
                else if (tokens[0].equals("unarchive_transaction")) {
                    int year = Integer.parseInt(tokens[1]);
                    int month = Integer.parseInt(tokens[2]);
                    if (server.unarchive_transaction(user,year,month))
                        outputToClient.writeUTF("Transactions unarchived!\r\n");
                    else
                        outputToClient.writeUTF("There is no transaction to be unarchived!\n");;
                }
                else if (tokens[0].equals("next_month")) {
                    server.next_month();
                    outputToClient.writeUTF("The system time is change to next month");
                    outputToClient.writeUTF(server.display_time() + "\n");
                }
                else if (tokens[0].equals("display_demo")) {
                    if (!server.getDemo().isvalid(tokens[1]))
                        outputToClient.writeUTF("Demographic group not exists!\n");
                    else
                        outputToClient.writeUTF(server.display_demo(tokens[1]));
                }
                else if (tokens[0].equals("display_events")) {
                    outputToClient.writeUTF(server.display_events());
                }
                else if (tokens[0].equals("display_stream")) {
                    if (tokens.length == 1 && role.equals("stream"))
                        outputToClient.writeUTF(server.display_stream(user));
                    else if (tokens.length == 2 && (role.equals("admin") || user.equals(tokens[1])))
                        outputToClient.writeUTF(server.display_stream(tokens[1]));
                    else
                        outputToClient.writeUTF("Command not permitted!");
                }
                else if (tokens[0].equals("display_studio")) {
                    if (tokens.length == 1 && role.equals("studio"))
                        outputToClient.writeUTF(server.display_studio(user));
                    else if (tokens.length == 2 && (role.equals("admin") || user.equals(tokens[1])))
                        outputToClient.writeUTF(server.display_studio(tokens[1]));
                    else
                        outputToClient.writeUTF("Command not permitted!");
                }
                else if (tokens[0].equals("display_offers")) {
                    outputToClient.writeUTF(server.display_offers());
                }
                else if (tokens[0].equals("display_time")) {
                    outputToClient.writeUTF(server.display_time());
                }
                else if (tokens[0].equals("display_transactions")) {
                    outputToClient.writeUTF(server.display_transactions(user, role));
                }
                else if (tokens[0].equals("display_users")) {
                    outputToClient.writeUTF(server.display_users());
                }
                else {
                    outputToClient.writeUTF(("Command " + tokens[0] + " NOT acknowledged\n"));
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
                outputToClient.writeUTF(("Wrong command!\n"));
            }
            catch (NumberFormatException e) {
                e.printStackTrace();
                outputToClient.writeUTF(("Wrong command, integer needed!\n"));
            }
        }
        clientSocket.close();
    }

    public void handlelogin(String[] tokens) throws IOException, SQLException{
        String search = "SELECT * FROM user WHERE username = '" + tokens[1] + "';";
        ResultSet rs;
        Statement stmt = server.getConnection1().createStatement();
        rs = stmt.executeQuery(search);
        if (rs.next()) {
            String pw_hashed = rs.getString("password");
            if (BCrypt.checkpw(tokens[2], pw_hashed)) {
                user = tokens[1];
                role = server.get_role(tokens[1]);
                outputToClient.writeUTF("Login successfully!\nWelcome! User: " + user + " Role: " + role + " \n");
            } else {
                outputToClient.writeUTF("Login failed! Please check your username and password\n");
            }
        } else {
            outputToClient.writeUTF("Login failed! Please check your username and password\n");
        }
        stmt.close();
    }

    public void handleregister(String[] tokens) throws IOException, SQLException{
        if (server.getUser().isvalid(tokens[1]))
            outputToClient.writeUTF(("User already exists!\n"));
        else if (!tokens[3].equals("admin") && !tokens[3].equals("studio") && !tokens[3].equals("stream"))
            outputToClient.writeUTF(("Role not correct!\n"));
        else {
            int pw_msg = server.getUser().password_strength_check(tokens[2]);
            if (pw_msg < 0) {
                switch (pw_msg) {
                    case -1: outputToClient.writeUTF(("Password must contain at least 8 characters.\n")); break;
                    case -2: outputToClient.writeUTF(("Password must contain at least one digit.\n")); break;
                    case -3: outputToClient.writeUTF(("Password must contain at least one lowercase letter.\n")); break;
                    case -4: outputToClient.writeUTF(("Password must contain at least one uppercase letter.\n")); break;
                    case -5: outputToClient.writeUTF(("Password must contain at least one special character.\n")); break;
                    default: break;
                }
            }
            else {
                if (tokens.length == 4)
                    server.create_user(tokens[1], tokens[2], tokens[3], 0);
                else
                    server.create_user(tokens[1], tokens[2], tokens[3], Integer.parseInt(tokens[4]));
                outputToClient.writeUTF((tokens[1]+" ("+tokens[3]+") is successfully registered!\n"));
            }
        }
    }

}
