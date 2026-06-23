package PTPMUD.HoTroSinhVien.Controller.Student;

import PTPMUD.HoTroSinhVien.DTO.Request.CheckLichAoRequest;
import PTPMUD.HoTroSinhVien.DTO.Respone.CheckLichAoResponse;
import PTPMUD.HoTroSinhVien.DTO.ResponseObject;
import PTPMUD.HoTroSinhVien.Entity.SinhVien;
import PTPMUD.HoTroSinhVien.Repository.SinhVienRepository;
import PTPMUD.HoTroSinhVien.Service.LichAoService;
import PTPMUD.HoTroSinhVien.Service.LopHocPhanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student/lich-ao")
@RequiredArgsConstructor
public class StudentLichAoController {

    private final LichAoService lichAoService;
    private final LopHocPhanService lopHocPhanService;
    private final SinhVienRepository sinhVienRepository;

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
    @GetMapping("/picked-class/{khoa}/{maMon}")
    public ResponseEntity<ResponseObject> pickedClass(
            @PathVariable String maMon,
            @PathVariable String khoa,
            Authentication authentication
    ){
        String maSv=authentication.getName();

        SinhVien sinhVien = sinhVienRepository.findByMaSv(maSv);
        if (sinhVien == null) {
            return ResponseEntity.badRequest().body(
                    new ResponseObject("failed", "Không tìm thấy sinh viên", null)
            );
        }
        return ResponseEntity.ok(
                new ResponseObject(
                        "ok",
                        "",
                        lopHocPhanService.pickedClass(maMon,khoa,sinhVienRepository.findByMaSv(maSv).getNganh())
                )
        );
    }
}