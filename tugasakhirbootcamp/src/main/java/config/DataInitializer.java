package config;


import entity.Role;
import entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import repository.RoleRepository;
import repository.UserRepository;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create roles if they don't exist
        if (roleRepository.findByName("ADMIN").isEmpty()) {
            roleRepository.save(Role.builder().name("ADMIN").description("Administrator role").build());
        }
        if (roleRepository.findByName("LIBRARIAN").isEmpty()) {
            roleRepository.save(Role.builder().name("LIBRARIAN").description("Librarian role").build());
        }
        if (roleRepository.findByName("MEMBER").isEmpty()) {
            roleRepository.save(Role.builder().name("MEMBER").description("Member role").build());
        }

        // Create a default ADMIN user if not exists
        if (userRepository.findByUsername("admin").isEmpty()) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setEmail("admin@example.com");
            adminUser.setPassword(passwordEncoder.encode("adminpassword")); // Ganti dengan password yang kuat
            adminUser.setEnabled(true);

            Set<Role> adminRoles = new HashSet<>();
            roleRepository.findByName("ADMIN").ifPresent(adminRoles::add);
            adminUser.setRoles(adminRoles);
            userRepository.save(adminUser);
        }

        // Create a default LIBRARIAN user if not exists
        if (userRepository.findByUsername("librarian").isEmpty()) {
            User librarianUser = new User();
            librarianUser.setUsername("librarian");
            librarianUser.setEmail("librarian@example.com");
            librarianUser.setPassword(passwordEncoder.encode("librarianpassword")); // Ganti dengan password yang kuat
            librarianUser.setEnabled(true);

            Set<Role> librarianRoles = new HashSet<>();
            roleRepository.findByName("LIBRARIAN").ifPresent(librarianRoles::add);
            librarianUser.setRoles(librarianRoles);
            userRepository.save(librarianUser);
        }

        // Create a default MEMBER user if not exists
        if (userRepository.findByUsername("member").isEmpty()) {
            User memberUser = new User();
            memberUser.setUsername("member");
            memberUser.setEmail("member@example.com");
            memberUser.setPassword(passwordEncoder.encode("memberpassword")); // Ganti dengan password yang kuat
            memberUser.setEnabled(true);

            Set<Role> memberRoles = new HashSet<>();
            roleRepository.findByName("MEMBER").ifPresent(memberRoles::add);
            memberUser.setRoles(memberRoles);
            userRepository.save(memberUser);
        }
    }
}
