package com.officesupply.config;

import com.officesupply.entity.InventoryItem;
import com.officesupply.entity.User;
import com.officesupply.repository.InventoryItemRepository;
import com.officesupply.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedUsers();
        seedInventory();
    }

    private void seedUsers() {
        if (!userRepository.existsByUsername("admin")) {
            userRepository.save(User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .role("ADMIN")
                    .build());
            log.info("Seeded admin user");
        }
        if (!userRepository.existsByUsername("employee1")) {
            userRepository.save(User.builder()
                    .username("employee1")
                    .password(passwordEncoder.encode("emp123"))
                    .role("EMPLOYEE")
                    .build());
            log.info("Seeded employee1 user");
        }
    }

    private void seedInventory() {
        if (inventoryItemRepository.count() == 0) {
            inventoryItemRepository.saveAll(List.of(
                InventoryItem.builder().name("Ballpoint Pens").quantity(150).description("Blue ballpoint pens, box of 50").build(),
                InventoryItem.builder().name("A4 Paper Reams").quantity(80).description("500 sheets per ream, 80gsm").build(),
                InventoryItem.builder().name("Notebooks").quantity(60).description("A5 lined notebooks, 200 pages").build(),
                InventoryItem.builder().name("Staplers").quantity(25).description("Standard desktop staplers").build(),
                InventoryItem.builder().name("Permanent Markers").quantity(40).description("Black permanent markers, pack of 10").build(),
                InventoryItem.builder().name("Sticky Notes").quantity(100).description("3x3 inch, assorted colors").build(),
                InventoryItem.builder().name("Scissors").quantity(15).description("Standard office scissors").build()
            ));
            log.info("Seeded 7 inventory items");
        }
    }
}
