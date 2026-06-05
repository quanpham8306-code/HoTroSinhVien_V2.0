package PTPMUD.HoTroSinhVien.Security.config;

import PTPMUD.HoTroSinhVien.Entity.TaiKhoan;
import PTPMUD.HoTroSinhVien.Repository.TaiKhoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements  CommandLineRunner {
    private final TaiKhoanRepository taiKhoanRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if (taiKhoanRepository.findByUsername("admin").isEmpty()) {

            TaiKhoan admin = new TaiKhoan();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("1"));
            admin.setRole("ADMIN");

            taiKhoanRepository.save(admin);

            System.out.println("Admin account created!");
        }
    }
}
