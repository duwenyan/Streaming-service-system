package server;
import java.sql.*;

public class Stream {

    private Server server;

    public Stream(Server server) {
        this.server = server;
    }

    public boolean isvalid(String streamShortName) throws SQLException {
        String sql = "SELECT * FROM stream WHERE streamShortName = '" + streamShortName + "';";
        ResultSet rs;
        Statement stmt = server.getConnection1().createStatement();
        rs = stmt.executeQuery(sql);
        if (rs.next()) {
            rs.close();
            stmt.close();
            return true;
        } else {
            rs.close();
            stmt.close();
            return false;
        }
    }

    public boolean create_stream(String streamShortName, String streamLongName, int streamSubscription) throws SQLException {
        if (isvalid(streamShortName))
            return false;
        String sql = "INSERT INTO stream (streamShortName,streamLongName,streamSubscription,streamCurrentRevenue,streamPreviousRevenue,streamTotalRevenue,streamLicensing) "
                + "VALUES ('" + streamShortName + "','" + streamLongName + "'," + streamSubscription + ",0,0,0,0);";
        Statement stmt = server.getConnection1().createStatement();
        stmt.executeUpdate(sql);
        stmt.close();
        return true;
    }

    //update stream
    public int update_stream(String streamShortName, String streamLongName, int streamSubscription) throws SQLException {
        if (!isvalid(streamShortName))
            return 0;
        int result = 2;
        String sql = "SELECT * FROM stream WHERE streamShortName = '" + streamShortName + "';";
        Statement stmt = server.getConnection1().createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next()) {
            int cr = rs.getInt("streamCurrentRevenue");
            int ss = rs.getInt("streamSubscription");
            if (cr != 0 && streamSubscription != ss) {
                streamSubscription = ss;
                result = 1;
            }
            sql = "UPDATE stream"
                    + " SET streamLongName = '" + streamLongName + "',"
                    + " streamSubscription = " + streamSubscription
                    + " WHERE streamShortName = '" + streamShortName + "';";
            stmt.executeUpdate(sql);
        }
        rs.close();
        stmt.close();
        return result;
    }


    public int get_streamSubscription(String streamShortName) throws SQLException {
        String sql = "SELECT streamSubscription FROM stream WHERE streamShortName = '" + streamShortName + "';";
        Statement stmt = server.getConnection1().createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next()) {
            int result = rs.getInt("streamSubscription");
            rs.close();
            stmt.close();
            return result;
        } else {
            rs.close();
            stmt.close();
            return 0;
        }
    }

    public void add_streamLicensing(String streamShortName, int payLicenseFee) throws SQLException {
        String sql = "UPDATE stream"
                + " SET streamLicensing = streamLicensing + " + payLicenseFee
                + " WHERE streamShortName = '" + streamShortName + "';";
        Statement stmt = server.getConnection1().createStatement();
        stmt.executeUpdate(sql);
        stmt.close();
    }

    public void add_streamCurrentRevenue(String streamShortName, int watchViewingCost) throws SQLException {
        String sql = "UPDATE stream"
                + " SET streamCurrentRevenue = streamCurrentRevenue + " + watchViewingCost
                + " WHERE streamShortName = '" + streamShortName + "';";
        Statement stmt = server.getConnection1().createStatement();
        stmt.executeUpdate(sql);
        stmt.close();
    }

    public void update_subscription_price(String streamShortName, int price) throws SQLException {
        String sql = "UPDATE stream"
                + " SET streamSubscription = " + price
                + " WHERE streamShortName = '" + streamShortName + "';";
        Statement stmt = server.getConnection1().createStatement();
        stmt.executeUpdate(sql);
        stmt.close();
    }

    public void next_month() throws SQLException {
        Statement stmt = server.getConnection1().createStatement();
        String sql;
        sql = "UPDATE stream SET streamTotalRevenue = streamTotalRevenue + streamCurrentRevenue;";
        stmt.executeUpdate(sql);
        sql = "UPDATE stream SET streamPreviousRevenue = streamCurrentRevenue;";
        stmt.executeUpdate(sql);
        sql = "UPDATE stream SET streamCurrentRevenue = 0;";
        stmt.executeUpdate(sql);
        stmt.close();
//        for (int i = 0; i < streamList.size(); i++) {
//            streamList.get(i).streamTotalRevenue += streamList.get(i).streamCurrentRevenue;
//            streamList.get(i).streamPreviousRevenue = streamList.get(i).streamCurrentRevenue;
//            streamList.get(i).streamCurrentRevenue = 0;
//        }
    }

    public String display(String streamShortName) throws SQLException {
        String sql = "SELECT * FROM stream WHERE streamShortName = '" + streamShortName + "';";
        Statement stmt = server.getConnection1().createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next()) {
            String results = "stream," + rs.getString("streamShortName") + "," + rs.getString("streamLongName") + "\r\n" +
                    "subscription," + rs.getString("streamSubscription") + "\r\n" +
                    "current_period," + rs.getString("streamCurrentRevenue") + "\r\n" +
                    "previous_period," + rs.getString("streamPreviousRevenue") + "\r\n" +
                    "total," + rs.getString("streamTotalRevenue") + "\r\n" +
                    "licensing," + rs.getString("streamLicensing") + "\r\n";
            rs.close();
            stmt.close();
            return results;
        } else {
            rs.close();
            stmt.close();
            return "";
        }
    }

}