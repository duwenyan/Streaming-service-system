package server;
import java.sql.*;

public class Offer {

    private Server server;

    public Offer(Server server) {
        this.server = server;
    }

    public boolean isvalid(String offerStream, String offerEventName, int offerEventYear) throws SQLException {
        String sql = "SELECT * FROM offer WHERE offerStream = '" + offerStream + "' and offerEventName = '" + offerEventName + "' and offerEventYear = " + offerEventYear + ";";
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

    public boolean create_offer(String offerType, String offerStream, String offerEventName, int offerEventYear, int offerEventPrice) throws SQLException {
        if (isvalid(offerStream, offerEventName, offerEventYear))
            return false;
        String sql = "INSERT INTO offer (offerStream,offerType,offerEventName,offerEventYear,offerEventPrice,offerEventWatched) "
                + "VALUES ('" + offerStream + "','" + offerType + "','" + offerEventName + "'," + offerEventYear + "," + offerEventPrice + ",0);";
        Statement stmt = server.getConnection1().createStatement();
        stmt.executeUpdate(sql);
        stmt.close();
        return true;
    }

    public int retract_movie(String offerStream, String offerEventName, int offerEventYear) throws SQLException {
        if (!isvalid(offerStream, offerEventName, offerEventYear))
            return 0;
        int result = 2;
        String sql = "SELECT * FROM offer"
                        + " WHERE offerStream = '" + offerStream + "' and offerEventName = '" + offerEventName + "' and offerEventYear = " + offerEventYear + ";";
        Statement stmt = server.getConnection1().createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next()) {
            int watched = rs.getInt("offerEventWatched");
            if (watched != 0)
                result = 1;
            else {
                sql = "DELETE FROM offer"
                        + " WHERE offerStream = '" + offerStream + "' and offerEventName = '" + offerEventName + "' and offerEventYear = " + offerEventYear + ";";
                stmt.executeUpdate(sql);
            }
        }
        rs.close();
        stmt.close();
        return result;
    }

    public String get_offerType(String offerStream, String offerEventName, int offerEventYear) throws SQLException {
        String sql = "SELECT offerType FROM offer WHERE offerStream = '" + offerStream + "' and offerEventName = '" + offerEventName + "' and offerEventYear = " + offerEventYear + ";";
        Statement stmt = server.getConnection1().createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next()) {
            String result = rs.getString("offerType");
            rs.close();
            stmt.close();
            return result;
        } else {
            rs.close();
            stmt.close();
            return "";
        }
    }

    public int get_offerEventPrice(String offerStream, String offerEventName, int offerEventYear) throws SQLException {
        String sql = "SELECT offerEventPrice FROM offer WHERE offerStream = '" + offerStream + "' and offerEventName = '" + offerEventName + "' and offerEventYear = " + offerEventYear + ";";
        Statement stmt = server.getConnection1().createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next()) {
            int result = rs.getInt("offerEventPrice");
            rs.close();
            stmt.close();
            return result;
        } else {
            rs.close();
            stmt.close();
            return 0;
        }
    }

    public String display() throws SQLException {
        String sql = "SELECT * FROM offer";
        Statement stmt = server.getConnection1().createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        String results = "";
        while (rs.next()) {
            results += rs.getString("offerStream") + "," + rs.getString("offerType") + "," +
                    rs.getString("offerEventName") + "," + rs.getInt("offerEventYear");
            if (rs.getString("offerType").equals("ppv"))
                results += "," + rs.getString("offerEventPrice") + "\r\n";
            else
                results += "\r\n";
        }
        rs.close();
        stmt.close();
        return results;
    }

}