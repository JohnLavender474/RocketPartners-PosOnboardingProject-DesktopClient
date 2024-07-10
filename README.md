# POS Project: No, POS Does Not Stand for Piece of S***

As part of the onboarding process at Rocket Partners, I have been tasked with creating a mock POS (Point of Sale)
system in multiple phases.

In the first phase, I was to develop a fully working desktop app in Java Swing to simulate the components of a POS
system. In the second phase, I was to develop a "virtual journal" server application that would receive and print logs
from the client applications. In the third phase, I was to develop a "discount engine" which would be a Spring Boot
app that would calculate discounts for transactions based on a set of rules; this app would have a REST API and
would be deployed to AWS. 

This repository contains the code for the first phase of the project. The code for the second and third phases can be
found in separate repositories:
- [POS Virtual Journal](https://github.com/JohnLavender474/RocketPartners-POSOnboardingProject-VirtualJournal)
- [POS Discount Engine](https://github.com/JohnLavender474/RocketPartners-PosOnboardingProject-PosDiscountEngine)
- [POS Commons](https://github.com/JohnLavender474/RocketPartners-PosOnboardingProject-Commons)

---

## Steps to run
1. Before running this application, make sure you have the `POS Virtual Journal` and `POS Discount Engine` applications so that this application can communicate with them. Running them is optional, as this project has error handling in case they are not.
2. Clone this repository.
3. Open the project in your favorite IDE.
4. Run the `Application` class. This will start the POS system application. There are arguments that can optionally be set (please see the `Application` class).

This section explains each of the argument parameters for configuring the application.

    -debug

    Description: Enable or disable debug mode.

    Values: true, false

    Default: false

    
    ---


    -appMode
    
    Description: Set the mode of the application.
    
    Values: dev (development), prod (production)
    
    Default: dev


    ---
    
    
    -dbSource
    
    Description: (NOT IMPLEMENTED) Specify the database source.
    
    Values: inmemory, mysql
    
    Default: inmemory


    ---
    
    
    -mysqlDbName
    
    Description: (NOT IMPLEMENTED) The name of the MySQL database.
    
    Default: pos_system


    ---
    
    
    -mysqlUrl
    
    Description: (NOT IMPLEMENTED) The URL of the MySQL database.
    
    Default: jdbc:mysql://localhost:3306/pos_system


    ---
    
    
    -mysqlUser
    
    Description: (NOT IMPLEMENTED) The MySQL database user.
    
    Default: myuser


    ---
    
    
    -mysqlPassword
    
    Description: (NOT IMPLEMENTED) The MySQL database password.
    
    Default: password


    ---
    
    
    -storeName
    
    Description: The name of the store.
    
    Default: Rocket Partners Store


    ---
    
    
    -laneNumber
    
    Description: The POS lane number.
    
    Default: 1


    ---
    
    
    -discountEngineBaseUrl
    
    Description: The base URL of the discount engine.
    
    Default: http://localhost:8080


    ---
    
    
    -remoteJournalHost
    
    Description: The host of the remote journal.
    
    Default: localhost


    ---
    
    
    -remoteJournalPort
    
    Description: The port of the remote journal.
    
    Default: 12345


These parameters provide flexible configuration options for the application, allowing for customization based on different deployment and runtime requirements.

---

For this phase, I was to handle the transaction process, store a pricebook, generate a receipt, and more. As
for the design of the project, I opted for an event-driven architecture where the Swing components would interact with
parent components that would handle the business logic. This design choice was made to ensure that the Swing components
remained as lightweight as possible and that the business logic was separated from the view.

The "main driver" of the application is the `PosComponent` class. All other components subscribe to events that the
`PosComponent` class emits. The `PosComponent` class is responsible for handling the transaction process, storing the
pricebook, and generating the receipt.

The following class encompasses the events that the `PosComponent` class emits and receives:

```java
/**
 * Represents a point of sale event. An event has a type and a map of properties.
 */
@ToString
public class PosEvent {

    @Getter
    private final PosEventType type;
    private final Map<String, Object> props;

    /**
     * Constructor that accepts a type and creates an empty properties map.
     *
     * @param type The type of the event.
     */
    public PosEvent(@NonNull PosEventType type) {
        this(type, new HashMap<>());
    }

    /**
     * Constructor that accepts a type and a map of properties.
     *
     * @param type  The type of the event.
     * @param props The properties of the event.
     */
    public PosEvent(@NonNull PosEventType type, @NonNull Map<String, Object> props) {
        this.type = type;
        this.props = props;
    }

    /**
     * Returns true if the event contains a property with the specified key.
     *
     * @param key The key of the property.
     * @return True if the event contains the property, false otherwise.
     */
    public boolean containsProperty(@NonNull String key) {
        return props.containsKey(key);
    }

    /**
     * Add a property to the event.
     *
     * @param key The key of the property.
     * @return The event.
     */
    public Object getProperty(@NonNull String key) {
        return props.get(key);
    }

    /**
     * Get a property of the event and cast it to the specified class.
     *
     * @param key   The key of the property.
     * @param clazz The class to cast the property to.
     * @param <T>   The type of the property.
     * @return The property cast to the specified class.
     */
    public <T> T getProperty(@NonNull String key, @NonNull Class<T> clazz) {
        return clazz.cast(getProperty(key));
    }

    /**
     * Get a property of the event and cast it to the specified class. If the property does not exist, return the
     * default
     * value. If the property exists but cannot be cast to the specified class, an exception will be thrown.
     *
     * @param key          The key of the property.
     * @param defaultValue The default value to return if the property does not exist.
     * @return The property cast to the specified class or the default value if the property does not exist.
     */
    public Object getOrDefaultProperty(@NonNull String key, Object defaultValue) {
        return props.containsKey(key) ? getProperty(key) : defaultValue;
    }

    /**
     * Get a property of the event and cast it to the specified class. If the property does not exist, return the
     * default
     * value. If the property exists but cannot be cast to the specified class, an exception will be thrown.
     *
     * @param key          The key of the property.
     * @param clazz        The class to cast the property to.
     * @param defaultValue The default value to return if the property does not exist.
     * @param <T>          The type of the property.
     * @return The property cast to the specified class or the default value if the property does not exist.
     */
    public <T> T getOrDefaultProperty(@NonNull String key, @NonNull Class<T> clazz, T defaultValue) {
        return props.containsKey(key) ? getProperty(key, clazz) : defaultValue;
    }

    /**
     * Returns a copy of the properties map.
     *
     * @return A copy of the properties map.
     */
    public Map<String, Object> getCopyOfProps() {
        return new HashMap<>(props);
    }
}
```

The `PosEvent` class is a simple class that represents an event in the POS system. It has a type and a map of properties.
Then, any component that wants to listen to events can subscribe to the `PosComponent` class and receive events of
specific types by implementing the following interface:

```java
/**
 * Interface for classes that can listen for POS events.
 */
public interface IPosEventListener {

    /**
     * Get the event types that this listener is interested in. All events with an event type not contained in the
     * returned set will not be received.
     *
     * @return The event types that this listener is interested in.
     */
    @NonNull Set<PosEventType> getEventTypesToListenFor();

    /**
     * Called when a POS event is dispatched.
     *
     * @param event The event that was dispatched.
     */
    void onPosEvent(@NonNull PosEvent event);
}

```

The `PosComponent` class implements the following interface:

```java
/**
 * Interface for classes that can dispatch POS events. This interface is not intended for the POS event manager, but
 * rather for classes that can dispatch POS events up the chain.
 */
public interface IPosEventDispatcher {

    /**
     * Dispatch a POS event.
     *
     * @param event The POS event to dispatch.
     */
    void dispatchPosEvent(@NonNull PosEvent event);
}
```

With these three classes, I was able to create a simple event-driven architecture for the POS system. The `PosComponent`
and subcomponents are initialized in the main method:

```java
public static void main(String[] args) {
    PosComponent posComponent = new PosComponent(/* params here */);

    CustomerViewController customerViewController = new CustomerViewController(/* params here */);
    posComponent.registerChildController(customerViewController);
    
    // other controllers
}
```

What is a controller? A controller is basically the handler for a JFrame in the Swing app.

```java
/**
 * Interface for all controllers in the POS system. The IComponent interface is implemented to allow controllers to
 * follow the bootUp, update, and shutDown lifecycle of the parent component.
 */
public interface IController extends IPosEventDispatcher, IPosEventListener, IComponent {
}
```

It contains the logic for handling and dispatching events from and to the parent and JFrame classes. For example,
the `CustomerViewController`class looks like this:

```java
public class CustomerViewController implements IController {

    // other fields

    @NonNull
    private final IPosEventDispatcher parentPosEventDispatcher;
    @NonNull
    private final CustomerView customerView;

    /**
     * Constructor that accepts a parent POS event dispatcher, store name, and POS lane number.
     *
     * @param parentPosEventDispatcher The parent POS event dispatcher.
     * @param storeName                The store name.
     * @param posLane                  The POS lane number.
     */
    public CustomerViewController(@NonNull IPosEventDispatcher parentPosEventDispatcher,
                                  @NonNull String storeName, int posLane) {
        this.parentPosEventDispatcher = parentPosEventDispatcher;
        customerView = new CustomerView(this, storeName, posLane);
    }

    @Override
    public void dispatchPosEvent(@NonNull PosEvent event) {
        parentPosEventDispatcher.dispatchPosEvent(event);
    }

    @Override
    public void onPosEvent(@NonNull PosEvent posEvent) {
        switch (posEvent.getType()) {
            // handle events here
        }
    }
    
    // other methods
}
```

Inside the `CustomerView` class, the JFrame is created and the controller is passed in as a parameter. Button
listeners and other UI listeners in the view class can dispatch events to the parent, which in this case simply
propagates the event further up the chain to the `PosComponent` class.