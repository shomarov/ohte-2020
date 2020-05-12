package duplicatephotofinder.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Database class for creating and initializing SQLite database
 */
public class Database {

    private final String url;

    /**
     * @param url of database file
     * @throws SQLException if error
     */
    public Database(String url) throws SQLException {
        this.url = url;
        init();
    }

    /**
     * Initializes database object
     * Uses helper method sqlCommands()
     *
     * @throws SQLException if error
     */
    private void init() throws SQLException {
        List<String> commands = sqlCommands();

        Connection conn = getConnection();
        Statement st = conn.createStatement();

        for (String command : commands) {
            st.executeUpdate(command);
        }
    }

    /**
     * Helper method for Connection initialization
     *
     * @return Connection object
     * @throws SQLException if error
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }

    /**
     * @return List<String> of commands used to initialize database
     */
    private List<String> sqlCommands() {
        ArrayList<String> commands = new ArrayList<>();

        commands.add("CREATE TABLE IF NOT EXISTS User (" +
                "id integer PRIMARY KEY, " +
                "username varchar(255) NOT NULL UNIQUE, " +
                "password varchar(255) NOT NULL);"
        );

        return commands;
    }
}
