# jpa-criteria-api
The main goal of this project is to explore basic query features of 
`JPA 2.1` specification:  
* **JPQL**
* **JPA Criteria API** (typesafe with auto-generated
static metamodels)

As a `JPA` provider we choose `Hibernate` to test some of `HQL`.

## project structure
![](classes-diag.jpg)

## technologies used
* **Flyway** - constructs the database & provide datafeed in two migrations.
Ensures data coherence by checking if schema in database is up to date (
by validating additional database table concerning migrations: 
`flyway_schema_history`).  
_Reference_: https://flywaydb.org/documentation/
* **JPA 2.1**  
_Reference_: [Pro JPA 2 2nd Edition](https://www.amazon.com/Pro-JPA-Experts-Voice-Java/dp/1430249269)  
_Reference_: [JSR 338: JavaTM Persistence API, Version 2.1](http://download.oracle.com/otn-pub/jcp/persistence-2_1-fr-eval-spec/JavaPersistence.pdf)  
* **Hibernate** - provider of `JPA 2.1` specification. We don't use
`EclipseLink` despite the fact that it is `reference implementation` of
`JPA 2.1` because apart from `JPQL` we want to explore `HQL` as a more
powerful query language.  
_Reference_: [Java Persistence with Hibernate Second Edition](https://www.amazon.com/exec/obidos/ASIN/1617290459)  
_Reference_: [Hibernate Interview Questions and Answers](https://www.journaldev.com/3633/hibernate-interview-questions-and-answers)  
* **Jupiter & AssertJ** - to test result sets (`AssertJ` has very 
convinient methods to comparing lists regardless order).

## project description  
The main idea is to explore `JPA Criteria API` by writing a querys in 
`JPQL` or `HQL`, then to try re-write them in `JPA Criteria API` & check 
result-sets identity.  
Comparing equality of result sets with `JUnit` & `hamcrest`:
```
import static org.hamcrest.Matchers.containsInAnyOrder;
...
Assert.assertThat(entityManager.createQuery(cc_query)
                .getResultList(),
        containsInAnyOrder(jpql_query.getResultList().toArray()));
```
but we decide to use `Jupiter` & `AssertJ` (more expressive):
```
import static org.assertj.core.api.Assertions.assertThat;
...
assertThat(entityManager.createQuery(cc_query).getResultList())
        .containsExactlyInAnyOrderElementsOf(jpql_query.getResultList());
```

## project content
`src/main/java`: entities, utility classes & `META-INF` folder with 
`persistence.xml`  
`resources/db/migration`: `Flyway` migrations as `SQL` scripts  
`test/java`: showcase of `JPQL`, `HQL` & `Criteria API` with tests  
`target/generated-sources`: static metamodels of entities  

## tests
We have to classes with nearly the same content: `Tests` and 
`TestsWithFullTypeSafe`. The only difference is that in the first class 
we use strings to denote fields while in the letter we use static 
metamodels.  
Example:  
From `Tests`:  
```
orderBy(cb.asc(cc_query_root.get("title")));
```
From `TestsWithFullTypeSafe`:
```
orderBy(cb.asc(cc_query_root.get(Book_.title)));
```

All methods are quite simple & straightforward use of `Criteria API`.   
The most interesting are:  
* `getBookstoresWithMostExpensiveBook()` - we show how to use subqueries
* `getBookstoresThatHaveTitle()` - we show how to reference a `FROM` 
expression of the parent query in the `FROM` clause of a subquery
* `getBookstoresThatHaveAtLeastOneBookWrittenBy()` - we show how to 
reference a `JOIN` expression of the parent query in the `FROM` clause
of a subquery
* `countBooksByGenre()` - we show how to use `Tuple` with aliases
* `getBookstoresWithCountBooksAndPriceAverage()` - we show how to use 
`multiselect` with dedicated class

## static metamodels
They are auto-generated by `maven-compiler-plugin`.  
All you need to do is:  
```
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <source>1.8</source>
        <target>1.8</target>
        <compilerArguments>
            <processor>org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor</processor>
        </compilerArguments>
    </configuration>
</plugin>
```
```
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-jpamodelgen</artifactId>
    <version>5.2.17.Final</version>
    <scope>provided</scope>
</dependency>
```
Mark `target/generated-sources` as a `Generated Sources Root`.  
In `IntelliJ` just left-click on `target/generated-sources` -> 
`Mark Directory As` -> `Generated Sources Root`.