package com.example.lottery;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // Для запуска проверки тиражей по расписанию
public class LotteryApplication {

  public static void main(String[] args) {
//    try {
//      Server.createTcpServer("-tcpPort", "9092", "-tcpAllowOthers").start();
//    } catch (SQLException e) {
//      throw new RuntimeException(e);
//    }
    SpringApplication.run(LotteryApplication.class, args);
  }

  //	@Bean
  //	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
  //		return args -> {
  //			System.out.println("Start APP !!!");
  //		};
  //	}
}
