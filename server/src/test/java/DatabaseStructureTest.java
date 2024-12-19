import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import utils.DatabaseCheckDAO;

public class DatabaseStructureTest {
    private final DatabaseCheckDAO databaseCheckDAO = new DatabaseCheckDAO();

    @Test
    void testRolesTableExists() {
        Assertions.assertTrue(databaseCheckDAO.isTableExists("roles"), "Таблица roles должна существовать");
    }

    @Test
    void testPersonDataTableExists() {
        Assertions.assertTrue(databaseCheckDAO.isTableExists("person_data"), "Таблица person_data должна существовать");
    }

    @Test
    void testUsersTableExists() {
        Assertions.assertTrue(databaseCheckDAO.isTableExists("users"), "Таблица users должна существовать");
    }

    @Test
    void testServicesTableExists() {
        Assertions.assertTrue(databaseCheckDAO.isTableExists("services"), "Таблица services должна существовать");
    }

    @Test
    void testConsumablesTableExists() {
        Assertions.assertTrue(databaseCheckDAO.isTableExists("consumables"), "Таблица consumables должна существовать");
    }

    @Test
    void testServicesConsumablesTableExists() {
        Assertions.assertTrue(databaseCheckDAO.isTableExists("services_consumables"), "Таблица services_consumables должна существовать");
    }

    @Test
    void testMastersServicesTableExists() {
        Assertions.assertTrue(databaseCheckDAO.isTableExists("masters_services"), "Таблица masters_services должна существовать");
    }

    @Test
    void testMastersSchedulesTableExists() {
        Assertions.assertTrue(databaseCheckDAO.isTableExists("masters_schedules"), "Таблица masters_schedules должна существовать");
    }

    @Test
    void testOrdersTableExists() {
        Assertions.assertTrue(databaseCheckDAO.isTableExists("orders"), "Таблица orders должна существовать");
    }

    @Test
    void testSecretCodesTableExists() {
        Assertions.assertTrue(databaseCheckDAO.isTableExists("secret_codes"), "Таблица secret_codes должна существовать");
    }
}
