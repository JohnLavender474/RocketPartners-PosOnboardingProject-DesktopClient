package com.rocketpartners.onboarding.possystem.component;

import com.rocketpartners.onboarding.possystem.service.ItemService;
import lombok.NonNull;

/**
 * Interface for components that load the item book into the item repository.
 */
public interface ItemBookLoaderComponent {

    /**
     * Load the item book into the item service.
     *
     * @param itemService the item service to load the item book into
     */
    void loadItemBook(@NonNull ItemService itemService);
}
