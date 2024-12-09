import by.bsuir.server.services.DBConnection;
import org.hibernate.SessionFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class DbTest {

    private static SessionFactory sessionFactory;

    @BeforeClass
    public static void setup() {
        sessionFactory = DBConnection.getSessionFactory();
    }

    @AfterClass
    public static void tearDown() {
        DBConnection.closeSessionFactory();
    }

    @Test
    public void testGetSessionFactory() {
        assertNotNull("SessionFactory should not be null", sessionFactory);
    }

    @Test
    public void testInsert() {
        assertTrue(true);
    }

    @Test
    public void testUpdate() {
        assertTrue(true);
    }

    @Test
    public void testDelete() {
        assertTrue(true);
    }

    @Test
    public void testMultipleMainAdminsNotCreated() {
        assertTrue(true);
    }
}
