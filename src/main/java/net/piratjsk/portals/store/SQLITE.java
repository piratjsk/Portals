package net.piratjsk.portals.store;

import net.piratjsk.portals.Portals;

import java.io.File;
import java.io.IOException;
import java.sql.*;

/*
 * @author _an0
 */
public class SQLITE {

    private Connection connection;
    private ResultSet key;

    private File sqlite = new File(Portals.getInstance().getDataFolder(), "portals.db");

    public SQLITE() {
        if (!Portals.getInstance().getDataFolder().exists()) {
            Portals.getInstance().getDataFolder().mkdir();
        }
        if (!sqlite.exists()) {
            try {
                sqlite.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        // tutaj mozesz dac kod, ktory generuje tabelki w bazie danych sqlite
    }

    private Connection getConnection() {
        try {
            if (connection == null) {
                connection = DriverManager.getConnection("jdbc:sqlite:" + sqlite.getAbsolutePath());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return connection;
    }

    public void executeUpdate(final String string) {
        Runnable update = new Runnable() {
            public void run() {
                try {
                    Connection conn = getConnection();
                    Statement statement = conn.createStatement();
                    statement.executeUpdate(string);
                    statement.close();
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        };
        update.run();
    }

    public ResultSet executeQuery(final String string) {
        key = null;
        Runnable query = new Runnable() {
            public void run() {
                try {
                    Connection conn = getConnection();
                    Statement statement =  conn.createStatement();
                    key = statement.executeQuery(string);
                    statement.close();
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        };
        query.run();
        return key;
    }

}
