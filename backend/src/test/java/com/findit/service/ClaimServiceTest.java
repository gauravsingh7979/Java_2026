package com.findit.service;

import com.findit.dto.claim.ClaimRequest;
import com.findit.dto.claim.ClaimResponse;
import com.findit.dto.claim.ClaimReviewRequest;
import com.findit.entity.Claim;
import com.findit.entity.FoundItem;
import com.findit.entity.Recovery;
import com.findit.entity.User;
import com.findit.enums.ClaimStatus;
import com.findit.enums.ItemCategory;
import com.findit.enums.ItemStatus;
import com.findit.enums.Role;
import com.findit.exception.InvalidClaimException;
import com.findit.mapper.ItemMapper;
import com.findit.repository.ClaimRepository;
import com.findit.repository.FoundItemRepository;
import com.findit.repository.UserRepository;
import com.findit.service.impl.ClaimServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClaimServiceTest {

    @Mock
    private ClaimRepository claimRepository;

    @Mock
    private FoundItemRepository foundItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RecoveryService recoveryService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AuditService auditService;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ClaimServiceImpl claimService;

    private User finder;
    private User claimant;
    private User admin;
    private FoundItem sampleFoundItem;

    @BeforeEach
    void setUp() {
        finder = User.builder().email("finder@example.com").firstName("Jane").lastName("Finder").role(Role.ROLE_USER).build();
        finder.setId(10L);

        claimant = User.builder().email("claimant@example.com").firstName("John").lastName("Claimant").role(Role.ROLE_USER).build();
        claimant.setId(20L);

        admin = User.builder().email("admin@example.com").firstName("Admin").lastName("Officer").role(Role.ROLE_ADMIN).build();
        admin.setId(1L);

        sampleFoundItem = FoundItem.builder()
                .trackingNumber("FND-2026-0001")
                .user(finder)
                .title("Black Wallet")
                .category(ItemCategory.WALLETS)
                .publicDescription("Black leather bifold")
                .privateDetails("Contains driver license for John")
                .dateFound(LocalDate.now())
                .locationFound("Library")
                .status(ItemStatus.ACTIVE)
                .build();
        sampleFoundItem.setId(100L);
    }

    @Test
    @DisplayName("User attempting to claim their own found item should throw InvalidClaimException")
    void testClaimOwnItemThrowsException() {
        when(userRepository.findById(10L)).thenReturn(Optional.of(finder));
        when(foundItemRepository.findById(100L)).thenReturn(Optional.of(sampleFoundItem));

        ClaimRequest request = ClaimRequest.builder()
                .foundItemId(100L)
                .ownershipExplanation("It is actually mine")
                .distinctiveCharacteristics("My name inside")
                .build();

        assertThatThrownBy(() -> claimService.submitClaim(request, 10L))
                .isInstanceOf(InvalidClaimException.class)
                .hasMessageContaining("You cannot submit an ownership claim for an item you reported found");

        verify(claimRepository, never()).save(any());
    }

    @Test
    @DisplayName("Admin claim approval should update status to APPROVED and initiate Recovery")
    void testAdminApproveClaimSuccess() {
        Claim claim = Claim.builder()
                .foundItem(sampleFoundItem)
                .claimant(claimant)
                .ownershipExplanation("Lost it yesterday")
                .distinctiveCharacteristics("John's license inside")
                .status(ClaimStatus.PENDING)
                .build();
        claim.setId(500L);

        Recovery recovery = Recovery.builder()
                .claim(claim)
                .recoveryCode("RCV-ABCD-1234")
                .claimant(claimant)
                .finder(finder)
                .build();
        recovery.setId(900L);

        when(claimRepository.findById(500L)).thenReturn(Optional.of(claim));
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(claimRepository.save(any(Claim.class))).thenAnswer(i -> i.getArgument(0));
        when(recoveryService.initiateRecovery(any(Claim.class))).thenReturn(recovery);

        ClaimReviewRequest reviewRequest = ClaimReviewRequest.builder()
                .decisionReason("Distinctive details match private item contents.")
                .build();

        ClaimResponse response = claimService.approveClaim(500L, reviewRequest, 1L);

        assertThat(claim.getStatus()).isEqualTo(ClaimStatus.APPROVED);
        assertThat(claim.getAdminDecisionReason()).isEqualTo("Distinctive details match private item contents.");
        assertThat(sampleFoundItem.getStatus()).isEqualTo(ItemStatus.CLAIMED);
        verify(recoveryService, times(1)).initiateRecovery(claim);
    }
}
