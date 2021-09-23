package server;
import java.sql.*;

public class User {

    private Server server;

    public User(Server server) {
        this.server = server;
    }

    public boolean isvalid(String userName) throws SQLException {
        String sql = "SELECT * FROM user WHERE username = '" + userName + "';";
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

    public boolean validate_user(String userName, String password) throws SQLException {
        String sql = "SELECT * FROM user WHERE username = '" + userName + "';";
        Statement stmt = server.getConnection1().createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next()) {
            String pw_hashed = rs.getString("password");
            stmt.close();
            return BCrypt.checkpw(password, pw_hashed);
        } else {
            stmt.close();
            return false;
        }
    }

    public boolean create_user(String userName, String password, String role, int expiration) throws SQLException {
        if (isvalid(userName))
            return false;
        int timestamp = server.get_time();
        String pw_hashed = hash_password(password);
        String sql = "INSERT INTO user (username,password,role,timestamp,expiration) "
                + " VALUES ('" + userName + "','" + pw_hashed + "','" + role + "'," + timestamp + "," + expiration + ");";
        Statement stmt = server.getConnection1().createStatement();
        stmt.executeUpdate(sql);
        stmt.close();
        return true;
    }

    public String hash_password(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    public int password_strength_check(String password) {
        if (password.length() < 8) {
            return -1;  // Password is too short
        }
        if (!password.matches("(?=.*[0-9]).*")) {
            return -2;  // Password does not contains number
        }
        if (!password.matches("(?=.*[a-z]).*")) {
            return -3;  // Password does not contains lower case letter
        }
        if (!password.matches("(?=.*[A-Z]).*")) {
            return -4;  // Password does not contains upper case letter
        }
        if (!password.matches("(?=.*[~!@#$%^&*()_-]).*")) {
            return -5;  // Password does not contains special character
        }
        return 1;
    }

    public boolean change_password(String userName, String old_password, String new_password) throws SQLException {
        if (!validate_user(userName, old_password))
            return false;
        String pw_hashed = hash_password(new_password);
        String sql = "UPDATE user"
                + " SET password = '" + pw_hashed + "'"
                + " WHERE username = '" + userName + "';";
        Statement stmt = server.getConnection1().createStatement();
        stmt.executeUpdate(sql);
        stmt.close();
        return true;
    }

    public String get_role(String userName) throws SQLException {
        String sql = "SELECT role FROM user WHERE username = '" + userName + "';";
        Statement stmt = server.getConnection1().createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next()) {
            String result = rs.getString("role");
            stmt.close();
            return result;
        } else {
            stmt.close();
            return "guest";
        }
    }

    public String display() throws SQLException {
        String results = "", sql;
        sql = "SELECT * FROM user;";
        Statement stmt;
        ResultSet rs;
        int timestamp, year, month;
        stmt = server.getConnection1().createStatement();
        rs = stmt.executeQuery(sql);
        results += "streamingwars database | user table:\n";
        while (rs.next()) {
            timestamp = rs.getInt("timestamp");
            month = (timestamp - 1) % 12 + 1;
            year = (timestamp - month) / 12;
            results += rs.getString("username") + "," + rs.getString("role") + "," + year + "-" + month + "," + rs.getString("expiration") + "\r\n";
        }
        rs.close();
        stmt.close();
        stmt = server.getConnection2().createStatement();
        rs = stmt.executeQuery(sql);
        results += "streamingwars_archive database | user table:\n";
        while (rs.next()) {
            timestamp = rs.getInt("timestamp");
            month = (timestamp - 1) % 12 + 1;
            year = (timestamp - month) / 12;
            results += rs.getString("username") + "," + rs.getString("role") + "," + year + "-" + month + "," + rs.getString("expiration") + "\r\n";
        }
        rs.close();
        stmt.close();
        return results;
    }

}