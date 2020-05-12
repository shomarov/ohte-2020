package duplicatephotofinder.dao;

import duplicatephotofinder.domain.User;

import java.sql.SQLException;

/**
 * Interface to be used for accessing and interacting with user data
 */
public interface UserDao {
    /**
     * @param user User type object
     * @return user id
     * @throws SQLException if there is an database error
     */
    int create(User user) throws SQLException;

    /**
     * @param username client specified
     * @return User found in database or null
     * @throws SQLException if there is an database error
     */
    User find(String username) throws SQLException;
}
