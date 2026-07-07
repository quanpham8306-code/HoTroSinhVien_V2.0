package PTPMUD.HoTroSinhVien.Controller.Student;

import PTPMUD.HoTroSinhVien.DTO.ResponseObject;
import PTPMUD.HoTroSinhVien.Repository.SinhVienRepository;
import PTPMUD.HoTroSinhVien.Service.DangKyLopHocPhanService;
import PTPMUD.HoTroSinhVien.Service.ThoiKhoaBieuService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/student/schedule")
public class StudentThoiKhoaBieuController {

    ThoiKhoaBieuService thoiKhoaBieuService;
    private final DangKyLopHocPhanService dangKyLopHocPhanService;

    @GetMapping("/me")
    ResponseEntity<ResponseObject> getMySchedule(Authentication authentication) {
        return ResponseEntity.ok(
                new ResponseObject(
                        "ok",
                        "Query schedule successfully",
                        thoiKhoaBieuService.getScheduleByMaSv(authentication.getName())
                )
        );
    }
    @GetMapping("/{ky}/{namHoc}")
    ResponseEntity<ResponseObject> getScheduleByKyAndNamHoc(
            Authentication authentication,
            @PathVariable int ky,
            @PathVariable String namHoc
    ) {
        String maSv = authentication.getName();

        return  ResponseEntity.ok(
                new ResponseObject(
                        "ok",
                        "Query schedule successfully",
                        thoiKhoaBieuService.getScheduleByKyAndNamHoc(maSv,ky,namHoc)
                )
        );
    }
    @GetMapping("/date/{date}")
    ResponseEntity<ResponseObject> getScheduleByDate(
            Authentication authentication,
            @PathVariable LocalDate date)
    {
        String maSv = authentication.getName();

        return  ResponseEntity.ok(
                new ResponseObject(
                        "ok",
                        "Query schedule successfully",
                        thoiKhoaBieuService.getScheduleByDate(maSv, date)
                )
        );
    }



    @GetMapping("/baBuoiGanNhat")
    public ResponseEntity<ResponseObject> baBuoiGanNhat(Authentication authentication) throws Exception{
        String maSv=authentication.getName();
        return ResponseEntity.ok(
                new ResponseObject(
                        "ok",
                        "",
                        dangKyLopHocPhanService.baBuoiGanNhat(maSv)
                )
        );
    }


}
