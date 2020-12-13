package com.slamine.course;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.slamine.core.model") //Enable Entity scan for specific package otherwise, the class will not be found because it is on external module
@EnableJpaRepositories("com.slamine.core.repository")//Enable JPA Repo scan on specific package, this tell spring to scan this package instead of scanning on entire project
public class CourseApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourseApplication.class, args);
    }

}
