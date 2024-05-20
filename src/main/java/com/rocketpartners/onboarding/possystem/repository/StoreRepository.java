package com.rocketpartners.onboarding.possystem.repository;

import com.rocketpartners.onboarding.possystem.model.Store;

public interface StoreRepository {

    void saveStore(Store store);

    Store getStoreById(String id);

    void deleteStore(Store store);

    void deleteStoreById(String id);

    boolean storeExists(String id);

    Store getStoreByName(String name);

    boolean storeExistsByName(String name);

    Store getStoreByAddress(String address);

    boolean storeExistsByAddress(String address);
}
