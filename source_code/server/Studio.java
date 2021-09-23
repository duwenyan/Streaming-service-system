package server;
import java.sql.*;

public class Studio {

    private Server server;

    public Studio(Server server) {
        this.server = server;
    }

    public boolean isvalid(String studioShortName) throws SQLException {
        String sql = "SELECT * FROM studio WHERE studioShortName = '" + studioShortName + "';";
        ResultSet rs;
        Statement stmt = server.getConnection1().createStatement();
        rs = stmt.executeQuery(sql);
        if (rs.next()) {
            stmt.close();
            return true;
        } else {
            stmt.close();
            return false;
        }
    }

    public boolean create_studio(String studioShortName, String studioLongName) throws SQLException {
        if (isvalid(studioShortName))
            return false;
        String sql = "INSERT INTO studio (studioShortName,studioLongName,studioCurrentRevenue,studioPreviousRevenue,studioTotalRevenue)"
                + " VALUES ('" + studioShortName + "','" + studioLongName + "',0,0,0);";
        Statement stmt = server.getConnection1().createStatement();
        stmt.executeUpdate(sql);
        stmt.close();
        return true;
    }

    public void add_CurrentRevenue(String studioShortName, int payLicenseFee) throws SQLException {
        String sql = "UPDATE studio"
                + " SET studioCurrentRevenue = studioCurrentRevenue + " + payLicenseFee
                + " WHERE studioShortName = '" + studioShortName + "';";
        Statement stmt = server.getConnection1().createStatement();
        stmt.executeUpdate(sql);
        stmt.close();
    }

    public void next_month() throws SQLException {
        Statement stmt = server.getConnection1().createStatement();
        String sql;
        sql = "UPDATE studio SET studioTotalRevenue = studioTotalRevenue + studioCurrentRevenue;";
        stmt.executeUpdate(sql);
        sql = "UPDATE studio SET studioPreviousRevenue = studioCurrentRevenue;";
        stmt.executeUpdate(sql);
        sql = "UPDATE studio SET studioCurrentRevenue = 0;";
        stmt.executeUpdate(sql);
        stmt.close();
//        for (int i = 0; i < studioList.size(); i++) {
//            studioList.get(i).studioTotalRevenue += studioList.get(i).studioCurrentRevenue;
//            studioList.get(i).studioPreviousRevenue = studioList.get(i).studioCurrentRevenue;
//            studioList.get(i).studioCurrentRevenue = 0;
//        }
    }

    public String display(String studioShortName) throws SQLException {
        String sql = "SELECT * FROM studio WHERE studioShortName = '" + studioShortName + "';";
        Statement stmt = server.getConnection1().createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next()) {
            String results = "studio," + rs.getString("studioShortName") + "," + rs.getString("studioLongName") + "\r\n" +
                         "current_period," + rs.getString("studioCurrentRevenue") + "\r\n" +
                         "previous_period," + rs.getString("studioPreviousRevenue") + "\r\n" +
                         "total," + rs.getString("studioTotalRevenue") + "\r\n";
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