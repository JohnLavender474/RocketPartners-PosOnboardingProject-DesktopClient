package com.rocketpartners.onboarding.possystem.component;

import com.rocketpartners.onboarding.possystem.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class BackOfficeComponentTest {

    private ItemService itemService;
    private ItemBookLoaderComponent itemBookLoaderComponent;
    private PosComponent posComponent;
    private BackOfficeComponent backOfficeComponent;

    @BeforeEach
    void setUp() {
        itemService = Mockito.mock(ItemService.class);
        itemBookLoaderComponent = Mockito.mock(ItemBookLoaderComponent.class);
        backOfficeComponent = Mockito.spy(new BackOfficeComponent(itemBookLoaderComponent, itemService));
        posComponent = Mockito.mock(PosComponent.class);
        backOfficeComponent.addPosComponent(posComponent);
    }

    @Test
    void testBootUp() {
        backOfficeComponent.bootUp();
        verify(posComponent, times(1)).bootUp();
        verify(itemBookLoaderComponent, times(1)).loadItemBook(itemService);
    }

    @Test
    void testShutdown() {
        backOfficeComponent.shutdown();
        verify(posComponent, times(1)).shutdown();
    }

    @Test
    void testScheduledUpdate() {
        backOfficeComponent.update();
        backOfficeComponent.update();
        verify(posComponent, times(2)).update();
    }
}