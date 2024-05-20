package com.rocketpartners.onboarding.possystem.repository;

import com.rocketpartners.onboarding.possystem.model.Customer;

import java.util.List;

/**
 * The {@code CustomerRepository} interface provides methods for performing CRUD operations
 * on {@link Customer} objects. It defines methods for saving, retrieving, and deleting customers,
 * as well as methods for checking the existence of a customer by their ID.
 */
public interface CustomerRepository {

    /**
     * Saves the given {@code Customer} to the repository.
     *
     * @param customer the customer to be saved
     */
    void saveCustomer(Customer customer);

    /**
     * Retrieves all {@code Customer} objects from the repository.
     *
     * @return a list of all customers in the repository
     */
    List<Customer> getAllCustomers();

    /**
     * Deletes the {@code Customer} with the specified ID from the repository.
     *
     * @param id the ID of the customer to be deleted
     */
    void deleteCustomerById(String id);

    /**
     * Retrieves the {@code Customer} with the specified ID from the repository.
     *
     * @param id the ID of the customer to be retrieved
     * @return the customer with the specified ID, or {@code null} if no such customer exists
     */
    Customer getCustomerById(String id);

    /**
     * Checks whether a {@code Customer} with the specified ID exists in the repository.
     *
     * @param id the ID of the customer to check for
     * @return {@code true} if a customer with the specified ID exists, {@code false} otherwise
     */
    boolean customerExists(String id);
}
