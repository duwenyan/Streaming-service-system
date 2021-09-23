package server;

import java.sql.*;


public class Demo {

    private Server server;

    public Demo(Server server) {
        this.server = server;
    }

    public boolean isvalid(String demoShortName) throws SQLException {
        String sql = "SELECT * FROM demo WHERE demoShortName = '" + demoShortName + "';";
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

    public boolean create_demo(String demoShortName, String demoLongName, int demoAccounts) throws SQLException {
        if (isvalid(demoShortName))
            return false;
        String sql = "INSERT INTO demo (demoShortName,demoLongName,demoAccounts,demoCurrentSpending,demoPreviousSpending,demoTotalSpending,demoWatchingHistory) "
                + "VALUES ('" + demoShortName + "','" + demoLongName + "','" + demoAccounts + "',0,0,0,0);";
        Statement stmt = server.getConnection1().createStatement();
        stmt.executeUpdate(sql);
        stmt.close();
        return true;
    }

    // update_demo
    public int update_demo(String demoShortName, String demoLongName, int demoAccounts) throws SQLException {
        if (!isvalid(demoShortName))
            return 0;
        int result = 2;
        String sql = "SELECT * FROM demo WHERE demoShortName = '" + demoShortName + "';";
        Statement stmt = server.getConnection1().createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next()) {
            int cs = rs.getInt("demoCurrentSpending");
            int da = rs.getInt("demoAccounts");
            if (cs != 0 && demoAccounts != da) {
                demoAccounts = da;
                result = 1;
            }
            sql = "UPDATE demo"
                    + " SET demoLongName = '" + demoLongName + "',"
                    + " demoAccounts = " + demoAccounts
                    + " WHERE demoShortName = '" + demoShortName + "';";
            stmt.executeUpdate(sql);
        }
        rs.close();
        stmt.close();
        return result;
    }

    public int get_demoAccounts(String demoShortName) throws SQLException {
        String sql = "SELECT demoAccounts FROM demo WHERE demoShortName = '" + demoShortName + "';";
        Statement stmt = server.getConnection1().createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next()) {
            int result = rs.getInt("demoAccounts");
            rs.close();
            stmt.close();
            return result;
        } else {
            rs.close();
            stmt.close();
            return 0;
        }
    }

    public void add_demoCurrentSpending(String demoShortName, int watchViewingCost) throws SQLException {
        String sql = "UPDATE demo"
                + " SET demoCurrentSpending = demoCurrentSpending + " + watchViewingCost
                + " WHERE demoShortName = '" + demoShortName + "';";
        Statement stmt = server.getConnection1().createStatement();
        stmt.executeUpdate(sql);
        stmt.close();
    }

    public void next_month() throws SQLException {
        Statement stmt = server.getConnection1().createStatement();
        String sql;
        sql = "UPDATE demo SET demoTotalSpending = demoTotalSpending + demoCurrentSpending;";
        stmt.executeUpdate(sql);
        sql = "UPDATE demo SET demoPreviousSpending = demoCurrentSpending;";
        stmt.executeUpdate(sql);
        sql = "UPDATE demo SET demoCurrentSpending = 0;";
        stmt.executeUpdate(sql);
        stmt.close();
//        for (int i = 0; i < demoList.size(); i++) {
//            demoList.get(i).demoTotalSpending += demoList.get(i).demoCurrentSpending;
//            demoList.get(i).demoPreviousSpending = demoList.get(i).demoCurrentSpending;
//            demoList.get(i).demoCurrentSpending = 0;
//        }
    }

    public String display(String demoShortName) throws SQLException {
        String sql = "SELECT * FROM demo WHERE demoShortName = '" + demoShortName + "';";
        Statement stmt = server.getConnection1().createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next()) {
            String results = "demo," + rs.getString("demoShortName") + "," + rs.getString("demoLongName") + "," + rs.getString("demoAccounts") + "\r\n" +
                    "current_period," + rs.getString("demoCurrentSpending") + "\r\n" +
                    "previous_period," + rs.getString("demoPreviousSpending") + "\r\n" +
                    "total," + rs.getString("demoTotalSpending") + "\r\n";
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
