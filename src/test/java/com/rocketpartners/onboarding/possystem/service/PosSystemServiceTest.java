package com.rocketpartners.onboarding.possystem.service;

import com.rocketpartners.onboarding.commons.model.PosSystem;
import com.rocketpartners.onboarding.possystem.repository.PosSystemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PosSystemServiceTest {

    private PosSystemRepository mockPosSystemRepository;
    private PosSystemService posSystemService;

    @BeforeEach
    void setUp() {
        mockPosSystemRepository = Mockito.mock(PosSystemRepository.class);
        posSystemService = new PosSystemService(mockPosSystemRepository);
    }

    @Test
    void testCreateAndPersist_PosSystemAlreadyExists() {
        String storeName = "TestStore";
        int posLane = 1;

        when(mockPosSystemRepository.posSystemExistsByStoreNameAndPosLane(storeName, posLane)).thenReturn(true);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            posSystemService.createAndPersist(storeName, posLane);
        });

        assertEquals("POS system already exists for store name and POS lane", thrown.getMessage());
    }

    @Test
    void testCreateAndPersist_PosSystemDoesNotExist() {
        String storeName = "TestStore";
        int posLane = 1;

        when(mockPosSystemRepository.posSystemExistsByStoreNameAndPosLane(storeName, posLane)).thenReturn(false);

        PosSystem createdPosSystem = posSystemService.createAndPersist(storeName, posLane);

        assertNotNull(createdPosSystem);
        assertEquals(storeName, createdPosSystem.getStoreName());
        assertEquals(posLane, createdPosSystem.getPosLane());

        verify(mockPosSystemRepository, times(1)).savePosSystem(createdPosSystem);
    }

    @Test
    void testSavePosSystem() {
        PosSystem posSystem = new PosSystem();
        posSystem.setStoreName("TestStore");
        posSystem.setPosLane(1);

        posSystemService.savePosSystem(posSystem);

        verify(mockPosSystemRepository, times(1)).savePosSystem(posSystem);
    }

    @Test
    void testGetPosSystemById() {
        String posSystemId = "testId";
        PosSystem expectedPosSystem = new PosSystem();
        expectedPosSystem.setId(posSystemId);

        when(mockPosSystemRepository.getPosSystemById(posSystemId)).thenReturn(expectedPosSystem);

        PosSystem actualPosSystem = posSystemService.getPosSystemById(posSystemId);

        assertEquals(expectedPosSystem, actualPosSystem);
    }

    @Test
    void testDeletePosSystemById() {
        String posSystemId = "testId";

        posSystemService.deletePosSystemById(posSystemId);

        verify(mockPosSystemRepository, times(1)).deletePosSystemById(posSystemId);
    }

    @Test
    void testPosSystemExists() {
        String posSystemId = "testId";

        when(mockPosSystemRepository.posSystemExists(posSystemId)).thenReturn(true);

        boolean exists = posSystemService.posSystemExists(posSystemId);

        assertTrue(exists);
    }

    @Test
    void testGetAllPosSystems() {
        List<PosSystem> expectedPosSystems = List.of(new PosSystem(), new PosSystem());

        when(mockPosSystemRepository.getAllPosSystems()).thenReturn(expectedPosSystems);

        List<PosSystem> actualPosSystems = posSystemService.getAllPosSystems();

        assertEquals(expectedPosSystems, actualPosSystems);
    }

    @Test
    void testGetPosSystemsByStoreName() {
        String storeName = "TestStore";
        List<PosSystem> expectedPosSystems = List.of(new PosSystem(), new PosSystem());

        when(mockPosSystemRepository.getPosSystemsByStoreName(storeName)).thenReturn(expectedPosSystems);

        List<PosSystem> actualPosSystems = posSystemService.getPosSystemsByStoreName(storeName);

        assertEquals(expectedPosSystems, actualPosSystems);
    }

    @Test
    void testGetPosSystemByStoreNameAndPosLane() {
        String storeName = "TestStore";
        int posLane = 1;
        PosSystem expectedPosSystem = new PosSystem();
        expectedPosSystem.setStoreName(storeName);
        expectedPosSystem.setPosLane(posLane);

        when(mockPosSystemRepository.getPosSystemByStoreNameAndPosLane(storeName, posLane)).thenReturn(expectedPosSystem);

        PosSystem actualPosSystem = posSystemService.getPosSystemByStoreNameAndPosLane(storeName, posLane);

        assertEquals(expectedPosSystem, actualPosSystem);
    }

    @Test
    void testPosSystemExistsByStoreNameAndPosLane() {
        String storeName = "TestStore";
        int posLane = 1;

        when(mockPosSystemRepository.posSystemExistsByStoreNameAndPosLane(storeName, posLane)).thenReturn(true);

        boolean exists = posSystemService.posSystemExistsByStoreNameAndPosLane(storeName, posLane);

        assertTrue(exists);
    }
}

