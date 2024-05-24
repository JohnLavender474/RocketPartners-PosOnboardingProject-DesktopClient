package com.rocketpartners.onboarding.possystem;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.formdev.flatlaf.FlatLightLaf;
import com.rocketpartners.onboarding.possystem.component.ItemBookLoaderComponent;
import com.rocketpartners.onboarding.possystem.component.LocalTestTsvItemBookLoaderComponent;
import com.rocketpartners.onboarding.possystem.component.PosComponent;
import com.rocketpartners.onboarding.possystem.display.CustomerViewController;
import com.rocketpartners.onboarding.possystem.display.ScannerViewController;
import com.rocketpartners.onboarding.possystem.model.PosSystem;
import com.rocketpartners.onboarding.possystem.repository.ItemRepository;
import com.rocketpartners.onboarding.possystem.repository.PosSystemRepository;
import com.rocketpartners.onboarding.possystem.repository.TransactionRepository;
import com.rocketpartners.onboarding.possystem.repository.inmemory.InMemoryItemRepository;
import com.rocketpartners.onboarding.possystem.repository.inmemory.InMemoryPosSystemRepository;
import com.rocketpartners.onboarding.possystem.repository.inmemory.InMemoryTransactionRepository;
import com.rocketpartners.onboarding.possystem.service.ItemService;
import com.rocketpartners.onboarding.possystem.service.PosSystemService;
import com.rocketpartners.onboarding.possystem.service.TransactionService;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * The main entry point for the Point of Sale application. Starts up a new {@link PosComponent} with the
 * specified pos lane number.
 */
public class Application {

    public static boolean DEBUG;

    /**
     * Command line arguments for the Point of Sale application.
     */
    @Getter
    @Setter
    @ToString
    public static class Arguments {

        private static final String DEFAULT_APP_MODE = "dev";
        private static final String DEFAULT_STORE_NAME = "Rocket Partners Store";
        private static final int DEFAULT_LANE_NUMBER = 1;

        @Parameter(names = "-debug", description = "Enable debug mode")
        private boolean debug = DEBUG;

        @Parameter(names = "-appMode", description = "The mode of the application")
        private String appMode = DEFAULT_APP_MODE;

        @Parameter(names = "-storeName", description = "The name of the store")
        private String storeName = DEFAULT_STORE_NAME;

        @Parameter(names = "-laneNumber", description = "The POS lane number")
        private int laneNumber = DEFAULT_LANE_NUMBER;
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

        String mode = arguments.getAppMode();
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

    private static void startDevApplication(@NonNull Arguments arguments) {
        // TODO: currently, the application will stop if all Swing frames are closed. To fix this, we can look at
        //  the possibility of using a daemon thread to keep the application running and allow the user to open
        //  frames using the command line even after all frames are closed.

        SwingUtilities.invokeLater(() -> {
            FlatLightLaf.setup();

            if (Application.DEBUG) {
                System.out.println("[Application] Starting Point of Sale application in dev mode with args: " + arguments);
            }

            PosSystemRepository posSystemRepository = new InMemoryPosSystemRepository();
            PosSystemService posSystemService = new PosSystemService(posSystemRepository);
            TransactionRepository transactionRepository = new InMemoryTransactionRepository();
            TransactionService transactionService = new TransactionService(transactionRepository);
            ItemRepository itemRepository = new InMemoryItemRepository();
            ItemService itemService = new ItemService(itemRepository);

            ItemBookLoaderComponent itemBookLoaderComponent = new LocalTestTsvItemBookLoaderComponent();
            String storeName = arguments.getStoreName();
            int laneNumber = arguments.getLaneNumber();

            PosComponent posComponent = new PosComponent(itemBookLoaderComponent, transactionService, itemService);
            PosSystem posSystem = posSystemService.createAndPersist(storeName, laneNumber);
            posComponent.setPosSystem(posSystem);

            CustomerViewController customerViewController = new CustomerViewController(posComponent, storeName, laneNumber);
            posComponent.registerChildController(customerViewController);

            ScannerViewController scannerViewController =
                    new ScannerViewController("Scanner View - Lane " + laneNumber, posComponent);
            scannerViewController.addScannerViewKeyboardFocusManager(KeyboardFocusManager.getCurrentKeyboardFocusManager());
            posComponent.registerChildController(scannerViewController);

            posComponent.bootUp();

            // According to ChatGPT, the javax.swing.Timer ensures that the action performed in the ActionListener is
            // executed on the Event Dispatch Thread (EDT). Also, by default, the javax.swing.Timer ensures that the
            // next update is scheduled only after the previous one completes because it runs on the same thread.
            Timer timer = new Timer(500, e -> posComponent.update());
            timer.setRepeats(true);
            timer.start();
        });
    }

    private static void startProdApplication(@NonNull Arguments arguments) {
        if (Application.DEBUG) {
            System.out.println("Starting Point of Sale application in production mode with args: " + arguments);
        }
        throw new RuntimeException("Prod application mode not implemented yet. Please run app with -debug flag.");
    }
}

