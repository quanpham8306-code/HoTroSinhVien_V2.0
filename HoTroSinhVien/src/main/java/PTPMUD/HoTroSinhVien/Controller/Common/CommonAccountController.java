package PTPMUD.HoTroSinhVien.Controller.Common;

import PTPMUD.HoTroSinhVien.DTO.Request.ChangePasswordRequest;
import PTPMUD.HoTroSinhVien.DTO.ResponseObject;
import PTPMUD.HoTroSinhVien.Service.TaiKhoanService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/common")
public class CommonAccountController {

    TaiKhoanService taiKhoanService;

    @PutMapping("/change-password")
    ResponseEntity<ResponseObject> changePassword(
            Authentication authentication,
            @RequestBody ChangePasswordRequest request
    ) {
        boolean changed = taiKhoanService.changePassword(authentication.getName(), request);

        if (!changed) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseObject("failed", "Wrong old password", "")
            );
        }

        return ResponseEntity.ok(
                new ResponseObject("ok", "Change password successfully", "")
        );
    }
}
