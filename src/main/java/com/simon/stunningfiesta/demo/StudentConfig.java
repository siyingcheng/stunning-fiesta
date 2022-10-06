package com.simon.stunningfiesta.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Configuration
public class StudentConfig {

    @Bean
    CommandLineRunner commandLineRunner(StudentRepository studentRepository) {
        return args -> {
            Student simon = new Student(
                    "Simon",
                    "siyingcheng@126.com",
                    LocalDate.of(1988, Month.JANUARY, 16),
                    34
            );
            Student zuo = new Student(
                    "Zuo",
                    "zuo@126.com",
                    LocalDate.of(1988, Month.AUGUST, 25),
                    34
            );
            studentRepository.saveAll(List.of(simon, zuo));
        };
    }
}
