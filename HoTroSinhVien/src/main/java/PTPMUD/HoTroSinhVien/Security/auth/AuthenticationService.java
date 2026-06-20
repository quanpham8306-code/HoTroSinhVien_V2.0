package PTPMUD.HoTroSinhVien.Security.auth;

import PTPMUD.HoTroSinhVien.DTO.Request.LoginRequest;
import PTPMUD.HoTroSinhVien.DTO.Respone.LoginResponse;
import PTPMUD.HoTroSinhVien.Repository.TaiKhoanRepository;
import PTPMUD.HoTroSinhVien.Security.jwt.JwtService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    TaiKhoanRepository taiKhoanRepository;
    JwtService jwtService;
    PasswordEncoder passwordEncoder;
    public LoginResponse authenticate(LoginRequest loginRequest) {
        var taiKhoan = taiKhoanRepository.findByUsername(loginRequest.getUsername());
        if (taiKhoan.isEmpty()) {
            return null;
        }
        if (passwordEncoder.matches(
                loginRequest.getPassword(),
                taiKhoan.get().getPassword()))
        {
            System.out.println(taiKhoan.get().getUsername()+" "+taiKhoan.get().getRole());
            return new LoginResponse(
                    taiKhoan.get().getRole(),
                    jwtService.createToken(taiKhoan.get())
            );
        }
        return null;
    }
}
