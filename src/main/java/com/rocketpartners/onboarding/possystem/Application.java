package com.rocketpartners.onboarding.possystem;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.formdev.flatlaf.FlatLightLaf;
import com.rocketpartners.onboarding.possystem.component.ItemBookLoaderComponent;
import com.rocketpartners.onboarding.possystem.component.LocalTestTsvItemBookLoaderComponent;
import com.rocketpartners.onboarding.possystem.component.PosComponent;
import com.rocketpartners.onboarding.possystem.display.*;
import com.rocketpartners.onboarding.possystem.model.PosSystem;
import com.rocketpartners.onboarding.possystem.repository.DiscountRepository;
import com.rocketpartners.onboarding.possystem.repository.ItemRepository;
import com.rocketpartners.onboarding.possystem.repository.PosSystemRepository;
import com.rocketpartners.onboarding.possystem.repository.TransactionRepository;
import com.rocketpartners.onboarding.possystem.repository.inmemory.InMemoryDiscountRepository;
import com.rocketpartners.onboarding.possystem.repository.inmemory.InMemoryItemRepository;
import com.rocketpartners.onboarding.possystem.repository.inmemory.InMemoryPosSystemRepository;
import com.rocketpartners.onboarding.possystem.repository.inmemory.InMemoryTransactionRepository;
import com.rocketpartners.onboarding.possystem.repository.mysql.*;
import com.rocketpartners.onboarding.possystem.service.*;
import lombok.*;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
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
        private static final String DEFAULT_DB_SOURCE = "inmemory";
        private static final String DEFAULT_MYSQL_DB_NAME = "pos_system";
        private static final String DEFAULT_MYSQL_URL = "jdbc:mysql://localhost:3306/pos_system";
        private static final String DEFAULT_MYSQL_USER = "myuser";
        private static final String DEFAULT_MYSQL_PASSWORD = "password";
        private static final String DEFAULT_STORE_NAME = "Rocket Partners Store";
        private static final int DEFAULT_LANE_NUMBER = 1;

        @Parameter(names = "-debug", description = "Enable debug mode. Values: true, false. Default: false.")
        private boolean debug = DEBUG;

        @Parameter(names = "-dbSource", description = "NOT IMPLEMENTED! The database source. Values: inmemory, mysql." +
                " Default: inmemory.")
        private String dbSource = DEFAULT_DB_SOURCE;

        @Parameter(names = "-mysqlDbName", description = "NOT IMPLEMENTED! The MySQL database name. Default: " +
                "pos_system.")
        private String mySqlDbName = DEFAULT_MYSQL_DB_NAME;

        @Parameter(names = "-mysqlUrl", description = "NOT IMPLEMENTED! The MySQL database URL. Default: " +
                "jdbc:mysql://localhost:3306/pos_system.")
        private String mySqlUrl = DEFAULT_MYSQL_URL;

        @Parameter(names = "-mysqlUser", description = "NOT IMPLEMENTED! The MySQL database user. Default: myuser.")
        private String mySqlUser = DEFAULT_MYSQL_USER;

        @Parameter(names = "-mysqlPassword", description = "NOT IMPLEMENTED! The MySQL database password. Default: " +
                "password.")
        private String mySqlPassword = DEFAULT_MYSQL_PASSWORD;

        @Parameter(names = "-appMode", description = "The mode of the application. Values: dev, prod. Default: dev.")
        private String appMode = DEFAULT_APP_MODE;

        @Parameter(names = "-storeName", description = "The name of the store. Default: Rocket Partners Store.")
        private String storeName = DEFAULT_STORE_NAME;

        @Parameter(names = "-laneNumber", description = "The POS lane number. Default: 1.")
        private int laneNumber = DEFAULT_LANE_NUMBER;
    }


    private record Services(@NonNull PosSystemService posSystemService, @NonNull ItemService itemService,
                            @NonNull DiscountService discountService, @NonNull TaxService taxService,
                            @NonNull TransactionService transactionService) {
    }

    private record Repositories(@NonNull PosSystemRepository posSystemRepository,
                                @NonNull ItemRepository itemRepository,
                                @NonNull DiscountRepository discountRepository,
                                @NonNull TransactionRepository transactionRepository) {
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
        SwingUtilities.invokeLater(() -> {
            FlatLightLaf.setup();

            if (Application.DEBUG) {
                System.out.println("[Application] Starting Point of Sale application in dev mode with args: " + arguments);
            }

            ItemBookLoaderComponent itemBookLoaderComponent = new LocalTestTsvItemBookLoaderComponent();
            Services services = createServices(arguments);

            String storeName = arguments.getStoreName();
            int laneNumber = arguments.getLaneNumber();

            PosComponent posComponent = new PosComponent(itemBookLoaderComponent, services.transactionService(),
                    services.itemService());
            PosSystem posSystem;
            if (services.posSystemService().posSystemExistsByStoreNameAndPosLane(storeName, laneNumber)) {
                posSystem = services.posSystemService().getPosSystemByStoreNameAndPosLane(storeName, laneNumber);
            } else {
                posSystem = services.posSystemService().createAndPersist(storeName, laneNumber);
            }
            posComponent.setPosSystem(posSystem);

            CustomerViewController customerViewController = new CustomerViewController(
                    posComponent, storeName, laneNumber);
            posComponent.registerChildController(customerViewController);

            ScannerViewController scannerViewController =
                    new ScannerViewController("Scanner View - Lane " + laneNumber, posComponent);
            scannerViewController.addScannerViewKeyboardFocusManager(KeyboardFocusManager.getCurrentKeyboardFocusManager());
            posComponent.registerChildController(scannerViewController);

            PayWithCardViewController payWithCardViewController =
                    new PayWithCardViewController("Pay with Card View - Lane " + laneNumber, posComponent);
            posComponent.registerChildController(payWithCardViewController);

            PayWithCashViewController payWithCashViewController =
                    new PayWithCashViewController("Pay with Cash View - Lane " + laneNumber, posComponent);
            posComponent.registerChildController(payWithCashViewController);

            ReceiptViewController receiptViewController =
                    new ReceiptViewController("Receipt View - Lane " + laneNumber);
            posComponent.registerChildController(receiptViewController);

            ErrorPopupViewController errorPopupViewController = new ErrorPopupViewController();
            posComponent.registerChildController(errorPopupViewController);

            DiscountsViewController discountsViewController =
                    new DiscountsViewController("Discounts View - Lane " + laneNumber, posComponent);
            posComponent.registerChildController(discountsViewController);

            PoleDisplayViewController poleDisplayViewController =
                    new PoleDisplayViewController("Pole Display View - Lane " + laneNumber);
            posComponent.registerChildController(poleDisplayViewController);

            posComponent.bootUp();

            // The javax.swing.Timer ensures that the action performed in the ActionListener is executed on the Event
            // Dispatch Thread (EDT). Also, by default, the Timer ensures that the next update is scheduled only after
            // the previous one completes because it runs on the same thread.
            Timer timer = new Timer(500, e -> posComponent.update());
            timer.setRepeats(true);
            timer.start();
        });
    }

    private static Services createServices(@NonNull Arguments arguments) {
        Repositories repositories = createRepositories(arguments);

        PosSystemService posSystemService = new PosSystemService(repositories.posSystemRepository());
        ItemService itemService = new ItemService(repositories.itemRepository());
        DiscountService discountService = new DiscountService(repositories.discountRepository());
        TaxService taxService = new TaxService();
        TransactionService transactionService = new TransactionService(
                repositories.transactionRepository(), discountService, itemService, taxService);

        return new Services(posSystemService, itemService, discountService, taxService, transactionService);
    }

    private static Repositories createRepositories(@NonNull Arguments arguments) {
        String dbSource = arguments.getDbSource();

        TransactionRepository transactionRepository;
        PosSystemRepository posSystemRepository;
        ItemRepository itemRepository;
        DiscountRepository discountRepository;

        if (dbSource.equals("inmemory")) {
            transactionRepository = new InMemoryTransactionRepository();
            posSystemRepository = new InMemoryPosSystemRepository();
            itemRepository = new InMemoryItemRepository();
            discountRepository = new InMemoryDiscountRepository();
        } else if (dbSource.equals("mysql")) {
            String url = arguments.getMySqlUrl();
            String username = arguments.getMySqlUser();
            String password = arguments.getMySqlPassword();
            String dbName = arguments.getMySqlDbName();
            try {
                MySQLDatabaseInitializer.getInstance().initializeDatabase(url, dbName, username, password);

                DatabaseConnectionManager connectionManager = new DatabaseConnectionManager(url, username, password);
                transactionRepository = new MySQLTransactionRepository(connectionManager);
                posSystemRepository = new MySQLPosSystemRepository(connectionManager);
                itemRepository = new MySQLItemRepository(connectionManager);
                discountRepository = new MySQLDiscountRepository(connectionManager);
            } catch (Exception e) {
                System.err.println("Failed to initialize MySQL assets: " + e.getMessage());
                throw new RuntimeException("Failed to initialize MySQL assets", e);
            }
        } else {
            throw new RuntimeException("Invalid database source: " + dbSource + ". Please use 'inmemory' or 'mysql'.");
        }

        return new Repositories(posSystemRepository, itemRepository, discountRepository, transactionRepository);
    }

    private static void startProdApplication(@NonNull Arguments arguments) {
        if (Application.DEBUG) {
            System.out.println("Starting Point of Sale application in production mode with args: " + arguments);
        }
        throw new RuntimeException("Prod application mode not implemented yet. Please run app with -debug flag.");
    }
}

