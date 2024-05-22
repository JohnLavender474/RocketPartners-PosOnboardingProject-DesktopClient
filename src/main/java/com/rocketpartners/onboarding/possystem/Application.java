package com.rocketpartners.onboarding.possystem;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.rocketpartners.onboarding.possystem.component.BackOfficeComponent;
import com.rocketpartners.onboarding.possystem.component.PosComponent;
import com.rocketpartners.onboarding.possystem.display.CustomerViewController;
import com.rocketpartners.onboarding.possystem.model.PosSystem;
import com.rocketpartners.onboarding.possystem.repository.PosSystemRepository;
import com.rocketpartners.onboarding.possystem.repository.TransactionRepository;
import com.rocketpartners.onboarding.possystem.repository.inmemory.InMemoryPosSystemRepository;
import com.rocketpartners.onboarding.possystem.repository.inmemory.InMemoryTransactionRepository;
import com.rocketpartners.onboarding.possystem.service.PosSystemService;
import com.rocketpartners.onboarding.possystem.service.TransactionService;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.util.Arrays;

/**
 * The main entry point for the Point of Sale application. Starts up a new {@link BackOfficeComponent} with the
 * specified number of POS lanes.
 */
public class Application {

    public static boolean DEBUG;

    /**
     * Command line arguments for the Point of Sale application.
     */
    @Getter
    @Setter
    public static class Arguments {

        private static final String DEFAULT_APP_MODE = "dev";
        private static final String DEFAULT_STORE_NAME = "Rocket Partners Store";
        private static final int DEFAULT_NUMBER_LANES = 1;

        @Parameter(names = "-debug", description = "Enable debug mode")
        private boolean debug = DEBUG;

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
        System.out.println("[Application] Starting Point of Sale application with args: " + Arrays.toString(args));

        Arguments arguments = new Arguments();
        JCommander jCommander = JCommander.newBuilder()
                .addObject(arguments)
                .build();
        try {
            jCommander.parse(args);
        } catch (ParameterException e) {
            System.err.println("[Application] Error in main method while parsing parameters: " + e.getMessage());
            jCommander.usage();
            System.exit(1);
        }

        DEBUG = arguments.isDebug();

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
            if (Application.DEBUG) {
                System.out.println("[Application] Starting Point of Sale application in development mode");
            }

            BackOfficeComponent backOfficeComponent = new BackOfficeComponent();

            PosSystemRepository posSystemRepository = InMemoryPosSystemRepository.getInstance();
            PosSystemService posSystemService = new PosSystemService(posSystemRepository);

            TransactionRepository transactionRepository = InMemoryTransactionRepository.getInstance();
            TransactionService transactionService = new TransactionService(transactionRepository);

            // For each lane, there should be a separate pos component with its own pos system and customer view
            String storeName = arguments.getStoreName();
            int lanes = arguments.getLanes();
            for (int lane = 1; lane <= lanes; lane++) {
                if (Application.DEBUG) {
                    System.out.println("[Application] Creating POS system for store name: " + storeName + ", POS " +
                            "lane: " + lane);
                }

                PosComponent posComponent = new PosComponent(transactionService);
                backOfficeComponent.addPosComponent(posComponent);

                PosSystem posSystem = posSystemService.createAndPersist(storeName, lane);
                posComponent.setPosSystem(posSystem);

                CustomerViewController customerViewController = new CustomerViewController(posComponent, storeName, lane);
                posComponent.registerChildController(customerViewController);
            }

            backOfficeComponent.bootUp();

            // According to ChatGPT, the javax.swing.Timer ensures that the action performed in the ActionListener is
            // executed on the Event Dispatch Thread (EDT). Also, by default, the javax.swing.Timer ensures that the
            // next update is scheduled only after the previous one completes because it runs on the same thread.
            Timer timer = new Timer(1000, e -> backOfficeComponent.update());
            timer.setRepeats(true);
            timer.start();
        });
    }

    private static void startProdApplication(Arguments arguments) {
        if (Application.DEBUG) {
            System.out.println("Starting Point of Sale application in production mode");
        }
        throw new RuntimeException("Not implemented yet");
    }
}

