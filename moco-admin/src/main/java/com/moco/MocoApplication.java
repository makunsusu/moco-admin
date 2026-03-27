package com.moco;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

/**
 * 启动程序
 * 
 * @author moco
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class MocoApplication
{
    public static void main(String[] args)
    {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(MocoApplication.class, args);
        System.out.println("\n" +
                "  __  __   ____    ____    ____  \n" +
                " |  \\/  | / __ \\  / ___|  / __ \\ \n" +
                " | |\\/| || |  | || |     | |  | |\n" +
                " | |  | || |__| || |___  | |__| |\n" +
                " |_|  |_| \\____/  \\____|  \\____/ \n" +
                "\n" +
                " moco-admin startup completed.\n");
    }
}
