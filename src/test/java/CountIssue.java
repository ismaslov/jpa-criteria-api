import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CountIssue {

    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("NewPersistenceUnit");
    private static final EntityManager entityManager = emf.createEntityManager();

    @BeforeAll
    static void prepareForTests() {
        prepareDatabaseForTests();
    }

    @AfterAll
    static void closeEntityManager() {
        entityManager.close();
    }

    private static void prepareDatabaseForTests() {
        Flyway flyway = new Flyway();
        flyway.setDataSource("jdbc:h2:file:./database", null, null);
        flyway.migrate();
    }

    @Test
    void getBooksThatHaveMoreThanOneAuthor() {
        String query = "SELECT b.title FROM Book b GROUP BY b.title HAVING count(b.authors) > 1";
        entityManager.createQuery(query, String.class).getResultList();
    }
}
