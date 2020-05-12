package duplicatephotofinder.dao;

import duplicatephotofinder.db.Database;
import duplicatephotofinder.domain.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object class for interacting with
 * user data located in the database
 */
public class DatabaseUserDao implements UserDao {

    private final Database db;

    public DatabaseUserDao(Database db) {
        this.db = db;
    }

    /**
     * Create new User
     * @param user User type object
     * @return id of newly created user, or -1 if creation not successfull
     * @throws SQLException in case of sql error
     */
    @Override
    public int create(User user) throws SQLException {
        int id = -1;

        Connection conn = db.getConnection();

        PreparedStatement stmt = conn.prepareStatement("INSERT INTO User" +
                " (username, password) " +
                " VALUES (?, ?)");

        stmt.setString(1, user.getUsername());
        stmt.setString(2, user.getPassword());

        stmt.executeUpdate();

        ResultSet generatedKeys = stmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            id = generatedKeys.getInt(1);
        }

        generatedKeys.close();
        stmt.close();

        conn.close();

        return id;
    }

    /**
     * @param username client specified
     * @return User object created from database entry
     * @throws SQLException in case of sql error
     */
    @Override
    public User find(String username) throws SQLException {
        Connection conn = db.getConnection();

        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM User WHERE username = ?");
        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();

        if (!rs.next()) {
            return null;
        }

        User user = new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"));

        stmt.close();
        rs.close();
        conn.close();

        return user;
    }

}
