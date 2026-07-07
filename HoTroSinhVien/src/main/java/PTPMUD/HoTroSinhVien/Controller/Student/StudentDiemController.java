package PTPMUD.HoTroSinhVien.Controller.Student;

import PTPMUD.HoTroSinhVien.DTO.Respone.BangDiemDTO;

import PTPMUD.HoTroSinhVien.DTO.Respone.DiemDTO;
import PTPMUD.HoTroSinhVien.DTO.Respone.ScoreSummaryDTO;
import PTPMUD.HoTroSinhVien.DTO.ResponseObject;
import PTPMUD.HoTroSinhVien.Entity.Diem;
import PTPMUD.HoTroSinhVien.Mapper.BangDiemMapper;
import PTPMUD.HoTroSinhVien.Mapper.DiemMapper;
import PTPMUD.HoTroSinhVien.Repository.DiemRepository;
import PTPMUD.HoTroSinhVien.Service.DangKyLopHocPhanService;
import PTPMUD.HoTroSinhVien.Service.DiemService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final DangKyLopHocPhanService dangKyLopHocPhanService;

    @GetMapping("/me")
    ResponseEntity<ResponseObject> getMyScore(Authentication authentication) {
        String maSv = authentication.getName();

        List<Diem> diems = diemRepository.findByDangKyLopHocPhan_SinhVien_MaSv(maSv);
        List<BangDiemDTO> result = diemService.entityToBangDiemDTO(diems);

        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed", "Can not found score of student", "")
            );
        }

        return ResponseEntity.ok(
                new ResponseObject("ok", "Query score successfully",result)
        );
    }
    @GetMapping("/{ky}/{namHoc}")
    ResponseEntity<ResponseObject> getDiemByIdSv(
            Authentication authentication,
            @PathVariable int ky,
            @PathVariable String namHoc

        )
    {
        String maSv = authentication.getName();

        List<Diem> diems = diemRepository.findByDangKyLopHocPhan_SinhVien_MaSvAndDangKyLopHocPhan_LopHocPhan_HocKyAndDangKyLopHocPhan_LopHocPhan_NamHoc(maSv,ky,namHoc);
        List<BangDiemDTO> bangDiemDTOS=diemMapper.entityDiemToBangDiemDTO(diems);

        if (bangDiemDTOS == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed", "Can not found score of student", "")
            );
        }

        return ResponseEntity.ok(
                new ResponseObject("ok", "Query score successfully",bangDiemDTOS)
        );
    }

    @GetMapping("/summary")
    ResponseEntity<ResponseObject> getMyScoreSummary(Authentication authentication) {
        String maSv = authentication.getName();
        ScoreSummaryDTO scoreSummaryDTO = diemService.getSummaryByMaSv(maSv);
        if(scoreSummaryDTO != null) {
            return ResponseEntity.ok(
                    new ResponseObject(
                            "ok",
                            "Query score summary successfully",
                            scoreSummaryDTO
                    )
            );
        }
        else
        {
        return ResponseEntity.ok(
                new ResponseObject(
                        "false",
                        "can not get score",
                        new ScoreSummaryDTO(0,0,0,"Chưa có.")
                )
        );
        }
    }
    @GetMapping("/summary/{ky}/{namHoc}")
    ResponseEntity<ResponseObject> getMyScoreSummary(
            Authentication authentication,
            @PathVariable int ky,
            @PathVariable String namHoc)
    {
        String maSv = authentication.getName();
        ScoreSummaryDTO scoreSummaryDTO = diemService.getSummaryByMaSvAndNamHocAndKy(maSv,ky,namHoc);
        if(scoreSummaryDTO !=null ){
            return ResponseEntity.ok(
                    new ResponseObject(
                            "ok",
                            "Query score summary successfully",
                            scoreSummaryDTO
                    )
            );
        }
        else
            return ResponseEntity.ok(
                    new ResponseObject(
                            "false",
                            "can not get score",
                            new ScoreSummaryDTO(0,0,0,"Chưa có.")
                    )
            );
    }
    @GetMapping ("/summary/listKyAndNam")
            ResponseEntity<?> getListKyAndNamHoc(Authentication authentication)
    {
        String maSv=authentication.getName();
        return ResponseEntity.status(HttpStatus.OK).body(
                dangKyLopHocPhanService.listHocKyNamHocDTOS(maSv)
        );
    }
}
