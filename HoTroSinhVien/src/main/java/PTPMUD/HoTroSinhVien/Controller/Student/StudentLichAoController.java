package PTPMUD.HoTroSinhVien.Controller.Student;

import PTPMUD.HoTroSinhVien.DTO.Request.CheckLichAoRequest;
import PTPMUD.HoTroSinhVien.DTO.Respone.CheckLichAoResponse;
import PTPMUD.HoTroSinhVien.DTO.ResponseObject;
import PTPMUD.HoTroSinhVien.Service.LichAoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student/lich-ao")
@RequiredArgsConstructor
public class StudentLichAoController {

    private final LichAoService lichAoService;

    @PostMapping("/check-them-lop")
    public ResponseEntity<ResponseObject> checkThemLop(
            @RequestBody CheckLichAoRequest request
    ) {
        CheckLichAoResponse result = lichAoService.checkThemLop(request);

        if (result.isValid()) {
            return ResponseEntity.ok(
                    new ResponseObject("ok", result.getMessage(), result)
            );
        }

        return ResponseEntity.badRequest().body(
                new ResponseObject("failed", result.getMessage(), result)
        );
    }
}