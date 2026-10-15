package com.college.lostfound.service;

import com.college.lostfound.dto.ItemRequest;
import com.college.lostfound.dto.ItemResponse;
import com.college.lostfound.entity.*;
import com.college.lostfound.exception.UnauthorizedActionException;
import com.college.lostfound.repository.ClaimRepository;
import com.college.lostfound.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ClaimRepository claimRepository;

    @InjectMocks
    private ItemService itemService;

    private User student1;
    private User student2;
    private Item sampleItem;

    @BeforeEach
    void setUp() {
        student1 = User.builder()
                .id(1L)
                .fullName("Rahul Sharma")
                .email("rahul@college.edu.in")
                .role(Role.ROLE_STUDENT)
                .collegeIdNumber("21CS045")
                .department("Computer Science & Engineering")
                .enabled(true)
                .build();

        student2 = User.builder()
                .id(2L)
                .fullName("Pooja Nair")
                .email("pooja@college.edu.in")
                .role(Role.ROLE_STUDENT)
                .collegeIdNumber("21EC089")
                .department("Electronics & Communication")
                .enabled(true)
                .build();

        sampleItem = Item.builder()
                .id(101L)
                .title("College ID Card in Leather Holder")
                .type(ItemType.LOST)
                .category("ID Cards")
                .description("Lost near Central Library reception desk")
                .campusLocation("Central Library - Ground Floor")
                .incidentDate(LocalDate.now())
                .status(ItemStatus.OPEN)
                .reportedBy(student1)
                .build();
    }

    @Test
    @DisplayName("Should successfully create a new lost item report")
    void testCreateItem_Success() {
        ItemRequest request = ItemRequest.builder()
                .title("College ID Card in Leather Holder")
                .type(ItemType.LOST)
                .category("ID Cards")
                .description("Lost near Central Library reception desk")
                .campusLocation("Central Library - Ground Floor")
                .incidentDate(LocalDate.now())
                .build();

        when(itemRepository.save(any(Item.class))).thenReturn(sampleItem);

        ItemResponse response = itemService.createItem(request, student1);

        assertNotNull(response);
        assertEquals("College ID Card in Leather Holder", response.getTitle());
        assertEquals(ItemType.LOST, response.getType());
        assertTrue(response.isOwner());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    @DisplayName("Owner should be able to update their own item report")
    void testUpdateItem_AsOwner_Success() {
        ItemRequest updateReq = ItemRequest.builder()
                .title("College ID Card with Red Lanyard")
                .type(ItemType.LOST)
                .category("ID Cards")
                .description("Lost near Central Library counter")
                .campusLocation("Central Library - 1st Floor")
                .incidentDate(LocalDate.now())
                .build();

        when(itemRepository.findById(101L)).thenReturn(Optional.of(sampleItem));
        when(itemRepository.save(any(Item.class))).thenReturn(sampleItem);

        ItemResponse response = itemService.updateItem(101L, updateReq, student1);
        assertNotNull(response);
        verify(itemRepository, times(1)).save(sampleItem);
    }

    @Test
    @DisplayName("Non-owner user should NOT be able to update another user's item report")
    void testUpdateItem_UnauthorizedUser_ThrowsException() {
        ItemRequest updateReq = ItemRequest.builder()
                .title("Tampered Title")
                .type(ItemType.LOST)
                .category("ID Cards")
                .description("Tampered description")
                .campusLocation("Canteen")
                .incidentDate(LocalDate.now())
                .build();

        when(itemRepository.findById(101L)).thenReturn(Optional.of(sampleItem));

        assertThrows(UnauthorizedActionException.class, () -> {
            itemService.updateItem(101L, updateReq, student2);
        });

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    @DisplayName("Non-owner user should NOT be able to delete another user's item report")
    void testDeleteItem_UnauthorizedUser_ThrowsException() {
        when(itemRepository.findById(101L)).thenReturn(Optional.of(sampleItem));

        assertThrows(UnauthorizedActionException.class, () -> {
            itemService.deleteItem(101L, student2);
        });

        verify(itemRepository, never()).delete(any(Item.class));
    }
}
