package server;
import java.sql.*;

public class Event {

    private Server server;

    public Event(Server server) {
        this.server = server;
    }

    public boolean isvalid(String eventFullName, int eventYear) throws SQLException {
        String sql = "SELECT * FROM event WHERE eventFullName = '" + eventFullName + "' and eventYear = " + eventYear + ";";
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

    public String get_studio(String eventFullName, int eventYear) throws SQLException {
        String sql = "SELECT eventStudioOwner FROM event WHERE eventFullName = '" + eventFullName + "' and eventYear = " + eventYear + ";";
        ResultSet rs;
        Statement stmt = server.getConnection1().createStatement();
        rs = stmt.executeQuery(sql);
        String result = "";
        if (rs.next())
            result = rs.getString("eventStudioOwner");
        return result;
    }

    public boolean create_event(String eventType, String eventFullName, int eventYear, int eventDuration, String eventStudioOwner, int eventLicenseFee) throws SQLException {
        if (isvalid(eventFullName, eventYear))
            return false;
        String sql = "INSERT INTO event (eventType,eventFullName,eventYear,eventDuration,eventStudioOwner,eventLicenseFee,eventWatched) "
                + "VALUES ('" + eventType + "','" + eventFullName + "'," + eventYear + ",'" + eventDuration + "','" + eventStudioOwner + "'," + eventLicenseFee + ",0);";
        Statement stmt = server.getConnection1().createStatement();
        stmt.executeUpdate(sql);
        stmt.close();
        return true;
    }

    //update event
    public int update_event(String eventFullName, int eventYear, int eventDuration, int eventLicenseFee) throws SQLException {
        if (!isvalid(eventFullName, eventYear))
            return 0;
        int result = 2;
        String sql = "SELECT * FROM event WHERE eventFullName = '" + eventFullName + "' and eventYear = " + eventYear + ";";
        Statement stmt = server.getConnection1().createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next()) {
            int lf = rs.getInt("eventLicenseFee");
            int watched = rs.getInt("eventWatched");
            if (watched != 0 && eventLicenseFee != lf) {
                eventLicenseFee = lf;
                result = 1;
            }
            sql = "UPDATE event"
                    + " SET eventDuration = " + eventDuration + ","
                    + " eventLicenseFee = " + eventLicenseFee
                    + " WHERE eventFullName = '" + eventFullName + "' and eventYear = " + eventYear + ";";
            stmt.executeUpdate(sql);
        }
        rs.close();
        stmt.close();
        return result;
    }


    public String getEventOwner(String eventFullName, int eventYear) throws SQLException {
        String sql = "SELECT eventStudioOwner FROM event WHERE eventFullName = '" + eventFullName + "' and eventYear = " + eventYear + ";";
        Statement stmt = server.getConnection1().createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next()) {
            String result = rs.getString("eventStudioOwner");
            rs.close();
            stmt.close();
            return result;
        } else {
            stmt.close();
            rs.close();
            return "";
        }
    }

    public int getLicenseFee(String eventFullName, int eventYear) throws SQLException {
        String sql = "SELECT eventLicenseFee FROM event WHERE eventFullName = '" + eventFullName + "' and eventYear = " + eventYear + ";";
        Statement stmt = server.getConnection1().createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next()) {
            int result = rs.getInt("eventLicenseFee");
            rs.close();
            stmt.close();
            return result;
        } else {
            stmt.close();
            rs.close();
            return 0;
        }
    }

    public String display() throws SQLException {
        String sql = "SELECT * FROM event";
        Statement stmt = server.getConnection1().createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        String results = "";
        while (rs.next()) {
            results += rs.getString("eventType") + "," + rs.getString("eventFullName") + "," +
                    rs.getString("eventYear") + "," + rs.getString("eventDuration") + "," +
                    rs.getString("eventStudioOwner") + "," +  rs.getString("eventLicenseFee") + "\r\n";
        }
        rs.close();
        stmt.close();
        return results;
    }

}