package duplicatephotofinder.dao;

import duplicatephotofinder.db.Database;
import duplicatephotofinder.domain.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

public class DatabaseUserDaoTest {

    Database db;
    DatabaseUserDao userDao;

    @Before
    public void setup() throws SQLException {
        db = new Database("jdbc:sqlite:test.db");

        userDao = new DatabaseUserDao(db);
    }

    @After
    public void cleanup() throws SQLException {
        db.getConnection().close();

        File dbFile = new File("test.db");
        dbFile.delete();
    }

    @Test
    public void savesNewUserToDatabaseAndReturnsId() throws SQLException {
        User user = new User("root", "secret");
        int id = userDao.create(user);

        assertEquals(1, id);
    }

    @Test
    public void findsUserByUsername() throws SQLException {
        User user = new User("user", "password");
        userDao.create(user);

        User savedUser = userDao.find("user");

        assertEquals(user.getUsername(), savedUser.getUsername());
    }

}
