package com.rocketpartners.onboarding.possystem;

import com.rocketpartners.onboarding.possystem.component.BackOfficeComponent;
import com.rocketpartners.onboarding.possystem.component.PosComponent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.swing.*;

/**
 * The main entry point for the Point of Sale application.
 */
@EnableScheduling
@SpringBootApplication
public class PosApplication {

    /**
     * The main entry point for the Point of Sale application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(PosApplication.class, args);
        /*
        ConfigurableApplicationContext context = createApplicationContext(args);
        startApplication(context);
         */

        // TODO:
        /*
        try {
            Server server = new Server();
            server.start(5000);
        }
        catch (Exception e) {
            System.out.println(e);
        }
         */
    }

    private static ConfigurableApplicationContext createApplicationContext(String... args) {
        return new SpringApplicationBuilder(PosApplication.class)
                .headless(false)
                .run(args);
    }

    private static void startApplication(ConfigurableApplicationContext context) {
        SwingUtilities.invokeLater(() -> {
            BackOfficeComponent backOfficeComponent = context.getBean(BackOfficeComponent.class);
            // in a production version of this app, multiple PosComponent instances would be instantiated, and therefore
            // using Spring bean injection would not be appropriate
            PosComponent posComponent = context.getBean(PosComponent.class);
            backOfficeComponent.addPosComponent(posComponent);
        });
    }
}

/*

@SpringBootApplication
public class PoSApplication extends JFrame {

    public static void main(String... args) {
        ConfigurableApplicationContext context = createApplicationContext(args);
        displayLoginFrame(context);
        try {
            Server server = new Server();
            server.start(5000);
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    private static ConfigurableApplicationContext createApplicationContext(String... args) {
        return new SpringApplicationBuilder(PoSApplication.class)
                .headless(false)
                .run(args);
    }

    private static void displayLoginFrame(ConfigurableApplicationContext context) {
        SwingUtilities.invokeLater(() -> {
            LoginViewController loginViewController = context.getBean(LoginViewController.class);
            loginViewController.prepareAndOpenFrame();
        });
    }
}

 */

