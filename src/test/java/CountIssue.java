import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
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
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> cc_query = cb.createQuery(Book.class);
        Root<Book> cc_query_root = cc_query.from(Book.class);

        cc_query.select(cc_query_root.get("title"))
                .groupBy(cc_query_root.get("title"))
                .having(cb.gt(cb.count(cc_query_root.get("authors")), 1));

        List<Book> resultList = entityManager.createQuery(cc_query).getResultList();
        System.out.println(resultList);
    }
}
