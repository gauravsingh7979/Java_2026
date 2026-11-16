package com.findit.config;

import com.findit.entity.FoundItem;
import com.findit.entity.LostItem;
import com.findit.entity.User;
import com.findit.enums.ContactPreference;
import com.findit.enums.ItemCategory;
import com.findit.enums.ItemStatus;
import com.findit.enums.Role;
import com.findit.repository.FoundItemRepository;
import com.findit.repository.LostItemRepository;
import com.findit.repository.UserRepository;
import com.findit.service.MatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Profile("!test")
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSeeder.class);

    private final UserRepository userRepository;
    private final LostItemRepository lostItemRepository;
    private final FoundItemRepository foundItemRepository;
    private final MatchService matchService;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(UserRepository userRepository,
                          LostItemRepository lostItemRepository,
                          FoundItemRepository foundItemRepository,
                          MatchService matchService,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.lostItemRepository = lostItemRepository;
        this.foundItemRepository = foundItemRepository;
        this.matchService = matchService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already seeded. Skipping initial seeding.");
            return;
        }

        log.info("Seeding initial FindIt platform accounts and sample items...");

        User superAdmin = userRepository.save(User.builder()
                .email("superadmin@findit.org")
                .password(passwordEncoder.encode("SuperAdmin@123"))
                .firstName("Chief")
                .lastName("Administrator")
                .phoneNumber("+1-555-0100")
                .role(Role.ROLE_SUPER_ADMIN)
                .active(true)
                .verified(true)
                .build());

        User admin = userRepository.save(User.builder()
                .email("admin@findit.org")
                .password(passwordEncoder.encode("Admin@123"))
                .firstName("Security")
                .lastName("Officer")
                .phoneNumber("+1-555-0101")
                .role(Role.ROLE_ADMIN)
                .active(true)
                .verified(true)
                .build());

        User john = userRepository.save(User.builder()
                .email("john.doe@university.edu")
                .password(passwordEncoder.encode("User@1234"))
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("+1-555-0102")
                .role(Role.ROLE_USER)
                .active(true)
                .verified(true)
                .build());

        User sarah = userRepository.save(User.builder()
                .email("sarah.smith@university.edu")
                .password(passwordEncoder.encode("User@1234"))
                .firstName("Sarah")
                .lastName("Smith")
                .phoneNumber("+1-555-0103")
                .role(Role.ROLE_USER)
                .active(true)
                .verified(true)
                .build());

        LostItem lostWallet = lostItemRepository.save(LostItem.builder()
                .trackingNumber("LOST-2026-WL001")
                .user(john)
                .title("Black Leather Bifold Wallet")
                .category(ItemCategory.WALLETS)
                .description("Lost my black leather Tommy Hilfiger bifold wallet. Contains student card, metro pass, and some cash.")
                .brand("Tommy Hilfiger")
                .primaryColor("Black")
                .dateLost(LocalDate.now().minusDays(2))
                .locationLost("University Main Library 2nd Floor")
                .distinctiveCharacteristics("Embossed TH logo on bottom right corner, small scratch on inside fold.")
                .contactPreference(ContactPreference.IN_APP)
                .status(ItemStatus.ACTIVE)
                .build());

        LostItem lostAirPods = lostItemRepository.save(LostItem.builder()
                .trackingNumber("LOST-2026-AP002")
                .user(sarah)
                .title("Apple AirPods Pro 2nd Gen with Matte Black Case")
                .category(ItemCategory.ELECTRONICS)
                .description("Lost Apple AirPods Pro with a silicon matte black case with an engraved initial 'S'.")
                .brand("Apple")
                .primaryColor("White")
                .dateLost(LocalDate.now().minusDays(1))
                .locationLost("Student Center Cafeteria Table 14")
                .distinctiveCharacteristics("Left earbud has a small sticker dot, engraved letter S on case.")
                .contactPreference(ContactPreference.EMAIL)
                .status(ItemStatus.ACTIVE)
                .build());

        FoundItem foundWallet = foundItemRepository.save(FoundItem.builder()
                .trackingNumber("FND-2026-WL001")
                .user(sarah)
                .title("Black Leather Bifold Wallet")
                .category(ItemCategory.WALLETS)
                .publicDescription("Found a black leather wallet near the study desks in the library. Bifold style with brand logo.")
                .privateDetails("Contains Student ID for John Doe, student ID #25BCS80034, 2 twenty-dollar bills, and a blue metro pass.")
                .brand("Tommy Hilfiger")
                .primaryColor("Black")
                .dateFound(LocalDate.now().minusDays(1))
                .locationFound("University Main Library 2nd Floor study cubicles")
                .storageLocation("Campus Security Office - Safe Locker #4")
                .status(ItemStatus.ACTIVE)
                .build());

        FoundItem foundKeys = foundItemRepository.save(FoundItem.builder()
                .trackingNumber("FND-2026-KY002")
                .user(john)
                .title("Set of 3 Keys on Blue Carabiner")
                .category(ItemCategory.KEYS)
                .publicDescription("Found a bundle of three keys with a metallic blue carabiner clip outside the gym.")
                .privateDetails("One key has a red rubber sleeve marked 'Room 302', small bronze bike lock key.")
                .brand("Yale")
                .primaryColor("Blue")
                .dateFound(LocalDate.now())
                .locationFound("Campus Recreation Center Entrance")
                .storageLocation("Front Desk Lost & Found Box")
                .status(ItemStatus.ACTIVE)
                .build());

        log.info("Sample items seeded. Triggering smart matching engine...");
        matchService.runMatchingForLostItem(lostWallet);
        matchService.runMatchingForLostItem(lostAirPods);
        matchService.runMatchingForFoundItem(foundWallet);
        matchService.runMatchingForFoundItem(foundKeys);

        log.info("FindIt platform successfully initialized with test accounts and matching data!");
    }
}
