package com.rocketpartners.onboarding.possystem.component;

import com.rocketpartners.onboarding.possystem.model.Item;
import com.rocketpartners.onboarding.possystem.service.ItemService;
import lombok.NonNull;

import java.util.List;

/**
 * Interface for components that load the item book into the item repository.
 */
public interface ItemBookLoaderComponent {

    /**
     * Load the item book into the item service and return the instantiated persisted items.
     *
     * @param itemService the item service to load the item book into
     */
    List<Item> loadItemBook(@NonNull ItemService itemService);
}
