package duplicatephotofinder.domain;

import duplicatephotofinder.dao.UserDao;

import java.sql.SQLException;

/**
 * This class contains methods for interactions with users account data
 */
public class UserService {

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Creates new user in database
     * @param username String
     * @param password String
     * @throws SQLException if error
     */
    public void createUser(String username, String password) throws SQLException {
        User user = new User(username, password);

        userDao.create(user);
    }

    /**
     * Uses userDao to find user in database and compares passwords
     * @param username String
     * @param password String
     * @return true if passwords match, otherwise false
     * @throws SQLException if error
     */
    public boolean comparePasswords(String username, String password) throws SQLException {
        User user = userDao.find(username);

        if (user == null) {
            return false;
        }

        return user.getPassword().equals(password);
    }

    /**
     * Method used for checking if user with specified username already exists in database
     * @param username String
     * @return true if user with specified username already exist in database
     * @throws SQLException if error
     */
    public boolean userExists(String username) throws SQLException {
        return userDao.find(username) != null;
    }

}
