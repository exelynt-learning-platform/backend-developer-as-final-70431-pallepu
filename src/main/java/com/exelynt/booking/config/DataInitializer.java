package com.exelynt.booking.config;

import com.exelynt.booking.domain.*;
import com.exelynt.booking.repository.ReservationRepository;
import com.exelynt.booking.repository.ResourceRepository;
import com.exelynt.booking.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;
    private final ReservationRepository reservationRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, ResourceRepository resourceRepository, ReservationRepository reservationRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.resourceRepository = resourceRepository;
        this.reservationRepository = reservationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            log.info("Seeding initial database users and resources...");

            // 1. Seed Users
            User admin = userRepository.save(User.builder()
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .fullName("System Administrator")
                    .role(Role.ROLE_ADMIN)
                    .build());

            User user1 = userRepository.save(User.builder()
                    .email("user@example.com")
                    .password(passwordEncoder.encode("User@123"))
                    .fullName("John Doe")
                    .role(Role.ROLE_USER)
                    .build());

            User user2 = userRepository.save(User.builder()
                    .email("jane@example.com")
                    .password(passwordEncoder.encode("User@123"))
                    .fullName("Jane Smith")
                    .role(Role.ROLE_USER)
                    .build());

            log.info("Seeded users: admin@example.com, user@example.com, jane@example.com");

            // 2. Seed Resources
            Resource conferenceRoom = resourceRepository.save(Resource.builder()
                    .name("Executive Conference Room A")
                    .type(ResourceType.ROOM)
                    .description("Spacious meeting room equipped with video conferencing setup and whiteboard.")
                    .capacity(20)
                    .location("Building 1, Floor 3")
                    .pricePerHour(new BigDecimal("75.00"))
                    .build());

            Resource projector = resourceRepository.save(Resource.builder()
                    .name("4K Ultra High-Def Projector")
                    .type(ResourceType.EQUIPMENT)
                    .description("Portable 4K laser projector suitable for presentations.")
                    .capacity(1)
                    .location("IT Storage Room B")
                    .pricePerHour(new BigDecimal("25.00"))
                    .build());

            Resource electricCar = resourceRepository.save(Resource.builder()
                    .name("Tesla Model 3 Fleet #1")
                    .type(ResourceType.VEHICLE)
                    .description("Electric sedan available for business trips and client visits.")
                    .capacity(5)
                    .location("Parking Bay G12")
                    .pricePerHour(new BigDecimal("50.00"))
                    .build());

            Resource hotDesk = resourceRepository.save(Resource.builder()
                    .name("Quiet Pod Desk 04")
                    .type(ResourceType.DESK)
                    .description("Noise-cancelling single occupancy work pod.")
                    .capacity(1)
                    .location("Building 2, Floor 1")
                    .pricePerHour(new BigDecimal("15.00"))
                    .build());

            log.info("Seeded 4 resources.");

            // 3. Seed Reservations
            LocalDateTime now = LocalDateTime.now();

            reservationRepository.save(Reservation.builder()
                    .resource(conferenceRoom)
                    .user(user1)
                    .startTime(now.plusDays(1).withHour(10).withMinute(0))
                    .endTime(now.plusDays(1).withHour(12).withMinute(0))
                    .status(ReservationStatus.CONFIRMED)
                    .totalPrice(new BigDecimal("150.00"))
                    .build());

            reservationRepository.save(Reservation.builder()
                    .resource(projector)
                    .user(user1)
                    .startTime(now.plusDays(2).withHour(14).withMinute(0))
                    .endTime(now.plusDays(2).withHour(17).withMinute(0))
                    .status(ReservationStatus.PENDING)
                    .totalPrice(new BigDecimal("75.00"))
                    .build());

            reservationRepository.save(Reservation.builder()
                    .resource(electricCar)
                    .user(user2)
                    .startTime(now.plusDays(3).withHour(8).withMinute(0))
                    .endTime(now.plusDays(3).withHour(18).withMinute(0))
                    .status(ReservationStatus.CONFIRMED)
                    .totalPrice(new BigDecimal("500.00"))
                    .build());

            reservationRepository.save(Reservation.builder()
                    .resource(hotDesk)
                    .user(user1)
                    .startTime(now.minusDays(1).withHour(9).withMinute(0))
                    .endTime(now.minusDays(1).withHour(17).withMinute(0))
                    .status(ReservationStatus.CANCELLED)
                    .totalPrice(new BigDecimal("120.00"))
                    .build());

            log.info("Seeded 4 sample reservations.");
        }
    }
}
