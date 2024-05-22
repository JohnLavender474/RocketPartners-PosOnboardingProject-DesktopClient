package com.rocketpartners.onboarding.possystem.component;

import com.rocketpartners.onboarding.possystem.ApplicationProperties;
import com.rocketpartners.onboarding.possystem.model.Item;
import com.rocketpartners.onboarding.possystem.service.ItemService;
import com.rocketpartners.onboarding.possystem.utils.TsvFileReader;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Component that loads the item book from a TSV file into the item service and returns the instantiated persisted
 * items. This component is used for testing purposes only.
 */
public class LocalTestTsvItemBookLoaderComponent implements ItemBookLoaderComponent {

    /**
     * Get the application properties. Package-private for testing purposes.
     *
     * @return the application properties
     */
    ApplicationProperties getProps() {
        return new ApplicationProperties();
    }

    /**
     * Get a TSV file reader. Package-private for testing purposes. Package-private for testing purposes.
     *
     * @return the TSV file reader
     */
    TsvFileReader getTsvFileReader() {
        return new TsvFileReader();
    }

    @Override
    public List<Item> loadItemBook(@NonNull ItemService itemService) {
        ApplicationProperties props = getProps();
        String tsvFilePath = props.getProperty("test.item.book.tsv.file.path");
        List<String[]> tsvLines = getTsvFileReader().read(tsvFilePath);
        List<Item> items = new ArrayList<>();
        tsvLines.forEach(it -> {
            if (it.length != 3) {
                throw new RuntimeException("Invalid TSV file format. Expected 3 fields per line. Invalid line: " + Arrays.toString(it));
            }
            String itemUpc = it[0];
            String itemName = it[1];
            BigDecimal unitPrice = BigDecimal.valueOf(Double.parseDouble(it[2]));
            Item item = itemService.createAndPersist(itemUpc, itemName, unitPrice, null, null);
            items.add(item);
        });
        return items;
    }
}
