package com.rocketpartners.onboarding.possystem;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.rocketpartners.onboarding.possystem.component.BackOfficeComponent;
import com.rocketpartners.onboarding.possystem.component.PosComponent;
import com.rocketpartners.onboarding.possystem.display.controller.CustomerViewController;
import com.rocketpartners.onboarding.possystem.display.view.CustomerView;
import com.rocketpartners.onboarding.possystem.factory.TransactionFactory;
import com.rocketpartners.onboarding.possystem.model.PosSystem;
import com.rocketpartners.onboarding.possystem.repository.TransactionRepository;
import com.rocketpartners.onboarding.possystem.repository.inmemory.InMemoryPosSystemRepository;
import com.rocketpartners.onboarding.possystem.repository.inmemory.InMemoryTransactionRepository;
import lombok.Getter;

import javax.swing.*;

/**
 * The main entry point for the Point of Sale application. Starts up a new {@link BackOfficeComponent} with the
 * specified number of POS lanes.
 */
public class Application {

    /**
     * Command line arguments for the Point of Sale application.
     */
    @Getter
    public static class Arguments {

        private static final String DEFAULT_APP_MODE = "dev";
        private static final String DEFAULT_STORE_NAME = "Rocket Partners Store";
        private static final int DEFAULT_NUMBER_LANES = 1;

        @Parameter(names = "-mode", description = "The mode of the application")
        private String mode = DEFAULT_APP_MODE;

        @Parameter(names = "-storeName", description = "The name of the store")
        private String storeName = DEFAULT_STORE_NAME;

        @Parameter(names = "-lanes", description = "The number of POS lanes")
        private int lanes = DEFAULT_NUMBER_LANES;
    }

    /**
     * The main entry point for the Point of Sale application. Parses the command line arguments and starts the
     * application. If there are any issues with the command line arguments, the usage information is printed to the
     * standard error stream and the application exits with a status code of 1.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Arguments arguments = new Arguments();
        JCommander jCommander = JCommander.newBuilder()
                .addObject(arguments)
                .build();
        try {
            jCommander.parse(args);
        } catch (ParameterException e) {
            System.err.println(e.getMessage());
            jCommander.usage();
            System.exit(1);
        }

        String mode = arguments.getMode();
        if (mode.equals("dev")) {
            // Starts the application with data in memory, which means that all data is lost when the app is stopped.
            startDevApplication(arguments);
        } else if (mode.equals("prod")) {
            // Starts the application with a production database.
            startProdApplication(arguments);
        } else {
            throw new RuntimeException("Invalid mode: " + mode + ". Please use 'dev' or 'prod'");
        }
    }

    private static void startDevApplication(Arguments arguments) {
        SwingUtilities.invokeLater(() -> {
            BackOfficeComponent backOfficeComponent = new BackOfficeComponent();

            TransactionRepository transactionRepository = InMemoryTransactionRepository.getInstance();
            TransactionFactory transactionFactory = new TransactionFactory(transactionRepository);

            // For each lane, there should be a separate pos component with its own pos system and customer view
            int lanes = arguments.getLanes();
            for (int lane = 0; lane < lanes; lane++) {
                PosComponent posComponent = new PosComponent(transactionFactory);
                backOfficeComponent.addPosComponent(posComponent);

                PosSystem posSystem = new PosSystem();
                posComponent.setPosSystem(posSystem);
                posSystem.setStoreName(arguments.getStoreName());
                posSystem.setPosLane(lane);
                InMemoryPosSystemRepository.getInstance().savePosSystem(posSystem);

                CustomerView customerView = new CustomerView();
                CustomerViewController customerViewController = new CustomerViewController(customerView);
                posComponent.registerPosEventListener(customerViewController);
            }

            backOfficeComponent.bootUp();
        });
    }

    private static void startProdApplication(Arguments arguments) {
        throw new RuntimeException("Not implemented yet");
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

