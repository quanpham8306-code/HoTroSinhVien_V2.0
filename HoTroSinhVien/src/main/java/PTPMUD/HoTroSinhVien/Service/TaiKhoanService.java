package PTPMUD.HoTroSinhVien.Service;

import PTPMUD.HoTroSinhVien.DTO.Request.ChangePasswordRequest;
import PTPMUD.HoTroSinhVien.Entity.TaiKhoan;
import PTPMUD.HoTroSinhVien.Repository.TaiKhoanRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TaiKhoanService {

    PasswordEncoder passwordEncoder;
    TaiKhoanRepository taiKhoanRepository;

    @Transactional
    public boolean changePassword(String username, ChangePasswordRequest request) {
        TaiKhoan taiKhoan = taiKhoanRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));

        if (!passwordEncoder.matches(request.getOldPassword(), taiKhoan.getPassword())) {
            return false;
        }

        taiKhoan.setPassword(passwordEncoder.encode(request.getNewPassword()));
        taiKhoanRepository.save(taiKhoan);
        return true;
    }
}
