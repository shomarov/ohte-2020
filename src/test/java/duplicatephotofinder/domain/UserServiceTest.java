package duplicatephotofinder.domain;

import duplicatephotofinder.dao.DatabaseUserDao;
import duplicatephotofinder.db.Database;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.sql.SQLException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UserServiceTest {

    Database db;
    DatabaseUserDao userDao;
    UserService userService;

    @Before
    public void setup() throws SQLException {
        db = new Database("jdbc:sqlite:test.db");
        userDao = new DatabaseUserDao(db);
        userService = new UserService(userDao);

        userService.createUser("user", "password");
    }

    @After
    public void cleanup() throws SQLException {
        db.getConnection().close();

        File dbFile = new File("test.db");
        dbFile.delete();
    }

    @Test
    public void userCreatedSuccessfully() throws SQLException {
        assertTrue(userService.userExists("user"));
    }

    @Test
    public void userExistsReturnsFalseIfNoUserFoundWithSpecifiedUsername() throws SQLException {
        assertFalse(userService.userExists("nonexisting"));
    }

    @Test
    public void comparePasswordsReturnTrueIfMatch() throws SQLException {
        assertTrue(userService.comparePasswords("user", "password"));
    }

    @Test
    public void comparePasswordsReturnsFalseIfWrong() throws SQLException {
        assertFalse(userService.comparePasswords("user", "wrong"));
    }

    @Test
    public void comparingPasswordsReturnsFalseIfUserDoesNotExist() throws SQLException {
        assertFalse(userService.comparePasswords("nonexisting", "wrong"));
    }

}
