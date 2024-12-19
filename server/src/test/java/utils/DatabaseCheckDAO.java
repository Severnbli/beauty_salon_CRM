package utils;

import by.bsuir.server.services.DBConnection;
import org.hibernate.Session;

public class DatabaseCheckDAO {
    public boolean isTableExists(String tableName) {
        try (Session session = DBConnection.getSessionFactory().openSession()) {
            String sql = "SELECT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = :tableName)";
            return session.createNativeQuery(sql)
                          .setParameter("tableName", tableName)
                          .getSingleResult()
                          .equals(true);
        }
    }
}