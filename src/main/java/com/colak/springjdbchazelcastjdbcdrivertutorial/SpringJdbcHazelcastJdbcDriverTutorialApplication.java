package com.colak.springjdbchazelcastjdbcdrivertutorial;

import com.colak.springjdbchazelcastjdbcdrivertutorial.model.Person;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.sql.SqlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Map;

import static java.lang.String.format;

@SpringBootApplication
@Slf4j
public class SpringJdbcHazelcastJdbcDriverTutorialApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringJdbcHazelcastJdbcDriverTutorialApplication.class, args);
    }

    @Bean
    @Order(1)
    public CommandLineRunner dataInsert(HazelcastInstance hazelcastInstance) {
        return args -> {
            IMap<Integer, Person> people = hazelcastInstance.getMap("person");
            people.put(1, new Person("John", 23));
            people.put(2, new Person("Mary", 35));
            people.put(3, new Person("Amber", 15));
            people.put(4, new Person("Matthew", 68));
            people.put(5, new Person("Carol", 45));
            people.put(6, new Person("Carolyn", 45));

            SqlService sqlService = hazelcastInstance.getSql();
            String sql = """
                    CREATE OR REPLACE MAPPING person (
                      __key INT, name VARCHAR, age INT
                    ) TYPE IMap OPTIONS
                    (
                      'keyFormat' = 'int',
                      'valueFormat' = 'java',
                      'valueJavaClass' = 'com.colak.springjdbchazelcastjdbcdrivertutorial.model.Person'
                    )
                    """;

            long result = sqlService.executeUpdate(sql);
            log.info("Mapping Result : {}", result);
        };
    }

    @Bean
    @Order(2)
    public CommandLineRunner queryData(JdbcTemplate jdbcTemplate) {
        return args -> {
            List<Map<String, Object>> maps = jdbcTemplate.queryForList("SELECT * FROM person");
            log.info("Person map : {} ", maps);

            var rowMapper = BeanPropertyRowMapper.newInstance(Person.class);
            List<Person> allPeople = jdbcTemplate.query("SELECT * FROM person", rowMapper);
            log.info("Select name and age : {}", allPeople);

            Integer age = jdbcTemplate.queryForObject("SELECT age FROM person WHERE name = 'Matthew'", Integer.class);
            log.info("Select Age By Name: {}", age);

            List<Person> person = jdbcTemplate.query("SELECT name, age FROM person WHERE name = ?", rowMapper, "Carolyn");
            log.info("Select Name By Name : {}", person);
        };
    }
}
