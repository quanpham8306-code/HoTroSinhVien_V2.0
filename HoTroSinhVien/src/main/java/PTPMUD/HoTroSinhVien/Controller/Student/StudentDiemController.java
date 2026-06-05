package PTPMUD.HoTroSinhVien.Controller.Student;

import PTPMUD.HoTroSinhVien.DTO.Respone.BangDiemDTO;
import PTPMUD.HoTroSinhVien.DTO.Respone.DiemDTO;
import PTPMUD.HoTroSinhVien.DTO.ResponseObject;
import PTPMUD.HoTroSinhVien.Mapper.DiemMapper;
import PTPMUD.HoTroSinhVien.Repository.DiemRepository;
import PTPMUD.HoTroSinhVien.Service.DiemService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/student/score")
public class StudentDiemController {

    DiemRepository diemRepository;
    DiemService diemService;
    DiemMapper diemMapper;

    @GetMapping("/me")
    ResponseEntity<ResponseObject> getMyScore(Authentication authentication) {
        String maSv = authentication.getName();

        List<DiemDTO> result = diemRepository.findByDangKyLopHocPhan_SinhVien_MaSv(maSv)
                .stream()
                .map(diemMapper::entityToDto)
                .toList();

        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed", "Can not found score of student", "")
            );
        }

        BangDiemDTO bangDiem = new BangDiemDTO(
                result,
                diemService.tinhGPAByMaSv(maSv)
        );

        return ResponseEntity.ok(
                new ResponseObject("ok", "Query score successfully", bangDiem)
        );
    }

    @GetMapping("/summary")
    ResponseEntity<ResponseObject> getMyScoreSummary(Authentication authentication) {
        String maSv = authentication.getName();

        return ResponseEntity.ok(
                new ResponseObject(
                        "ok",
                        "Query score summary successfully",
                        diemService.getSummaryByMaSv(maSv)
                )
        );
    }
}
