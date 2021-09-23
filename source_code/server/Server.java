package server;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.sql.*;

public class Server extends Thread{

    private final int serverPort;
    private Demo demo;
    private Studio studio;
    private Event event;
    private Stream stream;
    private Offer offer;
    private User user;
    private Function function;
    private int monthTimeStamp;
    private int yearTimeStamp;
    private Connection c1, c2;
    private SimpleCrypto simpleCrypto;

    public Server(int serverPort, Connection c1, Connection c2, String encryptionkey) throws SQLException{
        this.serverPort = serverPort;
        this.c1 = c1;
        this.c2 = c2;
        monthTimeStamp = 10;
        yearTimeStamp = 2020;
        init_database();
        demo = new Demo(this);
        studio = new Studio(this);
        event = new Event(this);
        stream = new Stream(this);
        offer = new Offer(this);
        user = new User(this);
        function = new Function();
        simpleCrypto = new SimpleCrypto(encryptionkey);
    }

    public int get_time() {
        return yearTimeStamp * 12 + monthTimeStamp;
    }

    public void init_database() throws SQLException{
        String sql;
        Statement stmt;
        ResultSet rs;
        stmt = c1.createStatement();
        sql = "CREATE TABLE IF NOT EXISTS system"
                + "(year INT NOT NULL,"
                + "month INT NOT NULL);";
        stmt.executeUpdate(sql);
        sql = "SELECT * FROM system";
        stmt = c1.createStatement();
        rs = stmt.executeQuery(sql);
        if (rs.next()) {
            yearTimeStamp = rs.getInt("year");
            monthTimeStamp = rs.getInt("month");
        } else {
            sql = "INSERT INTO system (year, month)"
                    + "VALUES (2020,10);";
            stmt.executeUpdate(sql);
            yearTimeStamp = 2020;
            monthTimeStamp = 10;
        }
        rs.close();
        stmt.close();

        int timestamp = yearTimeStamp * 12 + monthTimeStamp;
        sql = "CREATE TABLE IF NOT EXISTS user"
                + "(username TEXT NOT NULL,"
                + "password TEXT NOT NULL,"
                + "role TEXT NOT NULL,"
                + "timestamp INT NOT NULL,"
                + "expiration INT NOT NULL);";
        stmt.executeUpdate(sql);
        sql = "SELECT * FROM user";
        rs = stmt.executeQuery(sql);
        if (!rs.next()) {
            String pw_hashed = BCrypt.hashpw("admin", BCrypt.gensalt(12));
            sql = "INSERT INTO user (username,password,role,timestamp,expiration) "
                    + "VALUES ('admin','" + pw_hashed + "','admin'," + timestamp + ",0);";
            stmt.executeUpdate(sql);
        }

        sql = "CREATE TABLE IF NOT EXISTS demo"
                + "(demoShortName TEXT NOT NULL,"
                + "demoLongName TEXT NOT NULL,"
                + "demoAccounts INT NOT NULL,"
                + "demoCurrentSpending INT NOT NULL,"
                + "demoPreviousSpending INT NOT NULL,"
                + "demoTotalSpending INT NOT NULL,"
                + "demoWatchingHistory TEXT NOT NULL);";
        stmt.executeUpdate(sql);

        sql = "CREATE TABLE IF NOT EXISTS studio"
                + "(studioShortName TEXT NOT NULL,"
                + "studioLongName TEXT NOT NULL,"
                + "studioCurrentRevenue INT NOT NULL,"
                + "studioPreviousRevenue INT NOT NULL,"
                + "studioTotalRevenue INT NOT NULL);";
        stmt.executeUpdate(sql);

        sql = "CREATE TABLE IF NOT EXISTS stream"
                + "(streamShortName TEXT NOT NULL,"
                + "streamLongName TEXT NOT NULL,"
                + "streamSubscription INT NOT NULL,"
                + "streamCurrentRevenue INT NOT NULL,"
                + "streamPreviousRevenue INT NOT NULL,"
                + "streamTotalRevenue INT NOT NULL,"
                + "streamLicensing INT NOT NULL);";
        stmt.executeUpdate(sql);

        sql = "CREATE TABLE IF NOT EXISTS event"
                + "(eventType TEXT NOT NULL,"
                + "eventFullName TEXT NOT NULL,"
                + "eventYear INT NOT NULL,"
                + "eventDuration INT NOT NULL,"
                + "eventStudioOwner TEXT NOT NULL,"
                + "eventLicenseFee INT NOT NULL,"
                + "eventWatched INT NOT NULL);";
        stmt.executeUpdate(sql);

        sql = "CREATE TABLE IF NOT EXISTS offer"
                + "(offerStream TEXT NOT NULL,"
                + "offerType TEXT NOT NULL,"
                + "offerEventName TEXT NOT NULL,"
                + "offerEventYear INT NOT NULL,"
                + "offerEventPrice INT NOT NULL,"
                + "offerEventWatched INT NOT NULL);";
        stmt.executeUpdate(sql);

        sql = "CREATE TABLE IF NOT EXISTS watchGroupStreams"
                + "(watchDemoGroup TEXT NOT NULL,"
                + "watchStream TEXT NOT NULL,"
                + "watchViewerCount INT NOT NULL);";
        stmt.executeUpdate(sql);

        sql = "CREATE TABLE IF NOT EXISTS transactions"
                + "(userName TEXT NOT NULL,"
                + "description TEXT NOT NULL,"
                + "timestamp INT NOT NULL);";
        stmt.executeUpdate(sql);

        stmt.close();

        // archive database
        stmt = c2.createStatement();
        sql = "CREATE TABLE IF NOT EXISTS user"
                + "(username TEXT NOT NULL,"
                + "password TEXT NOT NULL,"
                + "role TEXT NOT NULL,"
                + "timestamp INT NOT NULL,"
                + "expiration INT NOT NULL);";
        stmt.executeUpdate(sql);
        sql = "CREATE TABLE IF NOT EXISTS transactions"
                + "(userName TEXT NOT NULL,"
                + "description TEXT NOT NULL,"
                + "timestamp INT NOT NULL);";
        stmt.executeUpdate(sql);
        stmt.close();
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);
            while(true) {
                Socket clientSocket = serverSocket.accept();
                ServerWorker worker = new ServerWorker(this, clientSocket);
                worker.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Demo getDemo() {
        return demo;
    }

    public Studio getStudio() {
        return studio;
    }

    public Event getEvent() {
        return event;
    }

    public Stream getStream() {
        return stream;
    }

    public Offer getOffer() {
        return offer;
    }

    public User getUser() {
        return user;
    }

    public Function getFunction() {
        return function;
    }

    public Connection getConnection1() {
        return c1;
    }

    public Connection getConnection2() {
        return c2;
    }

    public String get_help(String role) {
        return function.get_help(role);
    }

    public int validate_cmd(String[] tokens, String role) {
        return function.isvalid(tokens, role);
    }

    public boolean create_demo(String demoShortName, String demoLongName, int demoAccounts) throws SQLException {
        return demo.create_demo(demoShortName, demoLongName, demoAccounts);
    }

    public boolean create_studio(String studioShortName, String studioLongName) throws SQLException {
        return studio.create_studio(studioShortName, studioLongName);
    }

    public boolean create_event(String eventType, String eventFullName, int eventYear, int eventDuration, String eventStudioOwner, int eventLicenseFee) throws SQLException {
        return event.create_event(eventType, eventFullName, eventYear, eventDuration, eventStudioOwner, eventLicenseFee);
    }

    public boolean create_stream(String streamShortName, String streamLongName, int streamSubscription) throws SQLException {
        return stream.create_stream(streamShortName, streamLongName, streamSubscription);
    }

    public boolean create_offer(String offerType, String offerStream, String offerEventName, int offerEventYear, int offerEventPrice) throws SQLException {
        // The streaming service must license the event from the studio
        if (!event.isvalid(offerEventName, offerEventYear) || !stream.isvalid(offerStream))
            return false;
        String payStudio = event.getEventOwner(offerEventName, offerEventYear);
        int payLicenseFee = event.getLicenseFee(offerEventName, offerEventYear);
        if (!offer.create_offer(offerType, offerStream, offerEventName, offerEventYear, offerEventPrice))
            return false;
        stream.add_streamLicensing(offerStream, payLicenseFee);
        studio.add_CurrentRevenue(payStudio, payLicenseFee);
        String description = "Streaming service (" + offerStream + ") paid " + payLicenseFee + " license fee to Studio (" + payStudio + ") for Event (" + offerEventName + "," + offerEventYear + ")";
        String description_en = simpleCrypto.encrypt(description);
        int timestamp = yearTimeStamp * 12 + monthTimeStamp;
        String sql;
        Statement stmt;
        sql = "INSERT INTO transactions (userName,description,timestamp) "
                + " VALUES ('" + payStudio + "','" + description_en + "'," +  timestamp + ");";
        stmt = c1.createStatement();
        stmt.executeUpdate(sql);
        sql = "INSERT INTO transactions (userName,description,timestamp) "
                + " VALUES ('" + offerStream + "','" + description_en + "'," +  timestamp + ");";
        stmt = c1.createStatement();
        stmt.executeUpdate(sql);
        stmt.close();

        return true;
    }

    public boolean create_user(String userName, String password, String role, int expiration) throws SQLException {
        return user.create_user(userName, password, role, expiration);
    }

    public boolean change_password(String userName, String old_password, String new_password) throws SQLException {
        return user.change_password(userName, old_password, new_password);
    }

    public boolean validate_user(String userName, String password) throws SQLException {
        return user.validate_user(userName, password);
    }

    public String get_role(String userName) throws SQLException {
        return user.get_role(userName);
    }

    // update_demo
    public int update_demo(String demoShortName, String demoLongName, int demoAccounts) throws SQLException {
        return demo.update_demo(demoShortName, demoLongName, demoAccounts);
    }

    //update_event
    public int update_event(String eventFullName, int eventYear, int eventDuration, int eventLicenseFee) throws SQLException {
        return event.update_event(eventFullName, eventYear, eventDuration, eventLicenseFee);
    }


    // update_stream
    public int update_stream(String streamShortName, String streamLongName, int streamSubscription) throws SQLException {
        return stream.update_stream(streamShortName, streamLongName, streamSubscription);
    }

    // retract_movie
    public int retract_movie(String streamShortName, String eventFullName, int eventYear) throws SQLException {
        return offer.retract_movie(streamShortName, eventFullName, eventYear);
    }

    public boolean watch_event(String watchDemoGroup, int watchPercentage, String watchStream, String watchEventName, int watchEventYear) throws SQLException {
        // Identify the demo group, the streaming service, the event selected
        if (!demo.isvalid(watchDemoGroup) || !stream.isvalid(watchStream) || !event.isvalid(watchEventName, watchEventYear))
            return false;
        int demoAccounts = demo.get_demoAccounts(watchDemoGroup);
        int watchViewerCount = demoAccounts * watchPercentage / 100;
        int watchSubscriptionFee = stream.get_streamSubscription(watchStream);
        String watchType = offer.get_offerType(watchStream, watchEventName, watchEventYear);
        int watchPayPerViewPrice = offer.get_offerEventPrice(watchStream, watchEventName, watchEventYear);
        int watchViewingCost = 0;
        String sql;
        Statement stmt = c1.createStatement();
        if (watchType.equals("movie")) {
            // For movies: identify the increased percentage of new customers and subscription fee
            sql = "SELECT watchViewerCount FROM watchGroupStreams WHERE watchDemoGroup = '" + watchDemoGroup + "' and watchStream = '" + watchStream + "';";
            ResultSet rs;
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                int watchViewerCount_prev = rs.getInt("watchViewerCount");
                if (watchViewerCount > watchViewerCount_prev) {
                    watchViewingCost = (watchViewerCount - watchViewerCount_prev) * watchSubscriptionFee;
                    sql = "UPDATE watchGroupStreams"
                            + " SET watchViewerCount = " + watchViewerCount_prev
                            + " WHERE watchDemoGroup = '" + watchDemoGroup + "' and watchStream = '" + watchStream + "';";
                    stmt.executeUpdate(sql);
                }
            }
            else {
                sql = "INSERT INTO watchGroupStreams (watchDemoGroup,watchStream,watchViewerCount) "
                        + " VALUES ('" + watchDemoGroup + "','" + watchStream + "'," + watchViewerCount + ");";
                watchViewingCost = watchViewerCount * watchSubscriptionFee;
                stmt.executeUpdate(sql);
            }
        }
        else if (watchType.equals("ppv")) {
            // For Pay-Per-Views: identify the demo viewers affected and event price
            watchViewingCost = watchViewerCount * watchPayPerViewPrice;
        }
        // Adjust funds for watching events
        demo.add_demoCurrentSpending(watchDemoGroup, watchViewingCost);
        stream.add_streamCurrentRevenue(watchStream, watchViewingCost);
        String description = "Demographic group (" + watchDemoGroup + ") add spending " + watchViewingCost + " to Streaming service (" + watchStream + ") for Event (" + watchEventName + "," + watchEventYear + "," + watchType + ")";
        String description_en = simpleCrypto.encrypt(description);
        int timestamp = yearTimeStamp * 12 + monthTimeStamp;
        sql = "INSERT INTO transactions (userName,description,timestamp) "
                + " VALUES ('" + watchStream + "','" + description_en + "'," +  timestamp + ");";
        stmt.executeUpdate(sql);
        // Update watched status for event
        sql = "UPDATE event"
                + " SET eventWatched = 1"
                + " WHERE eventFullName = '" + watchEventName + "' and eventYear = " + watchEventYear + ";";
        stmt.executeUpdate(sql);
        // Update watched status for offer
        sql = "UPDATE offer"
                + " SET offerEventWatched = 1"
                + " WHERE offerStream = '" + watchStream + "' and offerEventName = '" + watchEventName + "' and offerEventYear = " + watchEventYear + ";";
        stmt.executeUpdate(sql);
        stmt.close();
        return true;
    }

    public boolean archive_transaction(String user, int year, int month) throws SQLException {
        int timestamp = year*12 + month;
        Statement stmt = c1.createStatement();
        Statement stmt2 = c2.createStatement();
        String sql;
        sql = "SELECT * FROM transactions WHERE userName = '" + user + "' AND timestamp = " + timestamp + ";";
        ResultSet rs = stmt.executeQuery(sql);
        boolean result = false;
        while (rs.next()) {
            sql = "INSERT INTO transactions (userName,description,timestamp) "
                    + " VALUES ('" + rs.getString("userName") + "','" + rs.getString("description") + "'," +  rs.getInt("timestamp") + ");";
            stmt2.executeUpdate(sql);
            result = true;
        }
        sql = "DELETE from transactions WHERE userName = '" + user + "' AND timestamp = " + timestamp + ";";
        stmt.executeUpdate(sql);
        return result;
    }

    public boolean unarchive_transaction(String user, int year, int month) throws SQLException {
        int timestamp = year*12 + month;
        Statement stmt = c1.createStatement();
        Statement stmt2 = c2.createStatement();
        String sql;
        sql = "SELECT * FROM transactions WHERE userName = '" + user + "' AND timestamp = " + timestamp + ";";
        ResultSet rs = stmt2.executeQuery(sql);
        boolean result = false;
        while (rs.next()) {
            sql = "INSERT INTO transactions (userName,description,timestamp) "
                    + " VALUES ('" + rs.getString("userName") + "','" + rs.getString("description") + "'," +  rs.getInt("timestamp") + ");";
            stmt.executeUpdate(sql);
            result = true;
        }
        sql = "DELETE from transactions WHERE userName = '" + user + "' AND timestamp = " + timestamp + ";";
        stmt2.executeUpdate(sql);
        return result;
    }

    public void next_month() throws SQLException{
        // Update the current timestamp
        if (monthTimeStamp == 12) {
            yearTimeStamp++;
        }
        monthTimeStamp = (monthTimeStamp % 12) + 1;

        int timestamp = get_time();

        // Update current, previous and total dollar amounts
        demo.next_month();
        stream.next_month();
        studio.next_month();

        Statement stmt = c1.createStatement();
        Statement stmt2 = c2.createStatement();
        String sql;

        // Remove all movie and Pay-Per-View offerings
        sql = "DELETE FROM offer;";
        stmt.executeUpdate(sql);

        // Reset the subscription counts for the month
        sql = "DELETE FROM watchGroupStreams;";
        stmt.executeUpdate(sql);

        sql = "UPDATE system"
                + " SET year = " + yearTimeStamp + ","
                + " month = " + monthTimeStamp + ";";
        stmt.executeUpdate(sql);

        // archive database
        sql = "SELECT * FROM user WHERE expiration <> 0 AND (timestamp + expiration) < " + timestamp + ";";
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            sql= "INSERT INTO user (username,password,role,timestamp,expiration)"
                + " VALUES ('" + rs.getString("username") + "','" + rs.getString("password") + "','" + rs.getString("role") + "'," + rs.getInt("timestamp") + "," + rs.getInt("expiration") + ");";
            stmt2.executeUpdate(sql);
        }
        sql = "DELETE from user WHERE expiration <> 0 AND (timestamp + expiration) < " + timestamp + ";";
        stmt.executeUpdate(sql);

        // Reset the watched status of events
        sql = "UPDATE event SET eventWatched = 0;";
        stmt.executeUpdate(sql);

        rs.close();
        stmt.close();
        stmt2.close();
    }

    public String display_demo(String demoShortName) throws SQLException {
        return demo.display(demoShortName);
    }

    public String display_events() throws SQLException {
        return event.display();
    }

    public String display_stream(String streamShortName) throws SQLException {
        return stream.display(streamShortName);
    }

    public String display_studio(String studioShortName) throws SQLException {
        return studio.display(studioShortName);
    }

    public String display_offers() throws SQLException {
        return offer.display();
    }

    public String display_time() {
        return "time," + monthTimeStamp + "," + yearTimeStamp;
    }

    public String display_transactions(String username, String role) throws SQLException {
        String results = "";
        String sql, description_en, description;
        int timestamp, year, month;
        if (role.equals("studio") || role.equals("stream"))
            sql = "SELECT * FROM transactions WHERE userName = '" + username + "';";
        else
            return results;
        Statement stmt = c1.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            timestamp = rs.getInt("timestamp");
            month = (timestamp - 1) % 12 + 1;
            year = (timestamp - month) / 12;
            description_en = rs.getString("description");
            description = simpleCrypto.decrypt(description_en);
            results += year + "-" + month + ": " + description + "\n";
        }
        rs.close();
        stmt.close();
        return results;
    }

    public String display_users() throws SQLException {
        return user.display();
    }
}
