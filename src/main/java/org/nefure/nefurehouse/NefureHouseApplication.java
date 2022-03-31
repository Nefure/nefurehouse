package org.nefure.nefurehouse;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author nefure
 */
@SpringBootApplication
@MapperScan(basePackages = "org.nefure.nefurehouse.mapper")
public class NefureHouseApplication {
    public static void main(String[] args) {
        SpringApplication.run(NefureHouseApplication.class,args);
    }
}
