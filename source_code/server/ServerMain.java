package server;
import java.io.*;
import java.sql.*;
import java.util.Properties;

public class ServerMain {
    public static void main(String[] args) throws Exception {
        File configFile = new File("server.config");
        FileReader reader = new FileReader(configFile);
        Properties props = new Properties();
        props.load(reader);
        int port = Integer.parseInt(props.getProperty("port"));
        String encryptionkey = props.getProperty("encryptionkey");

        File file = new File("database");
        file.mkdirs();
        Class.forName("org.sqlite.JDBC");
        Connection c1 = DriverManager.getConnection("jdbc:sqlite:database/streamingwars.db");
        Connection c2 = DriverManager.getConnection("jdbc:sqlite:database/streamingwars_archieve.db");
        System.out.println("Database connection successfully!");
        Server server = new Server(port, c1, c2, encryptionkey);
        server.start();
        System.out.println("Server started at port: " + port);
    }
}