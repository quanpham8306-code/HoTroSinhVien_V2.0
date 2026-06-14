package PTPMUD.HoTroSinhVien.Controller.Student;

import PTPMUD.HoTroSinhVien.DTO.ResponseObject;
import PTPMUD.HoTroSinhVien.Service.ThoiKhoaBieuService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/student/schedule")
public class StudentThoiKhoaBieuController {

    ThoiKhoaBieuService thoiKhoaBieuService;

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
    @GetMapping("/{ky}")
    ResponseEntity<ResponseObject> getScheduleByKy(
            Authentication authentication,
            @PathVariable int ky
    ) {
        String maSv = authentication.getName();

        return  ResponseEntity.ok(
                new ResponseObject(
                        "ok",
                        "Query schedule successfully",
                        thoiKhoaBieuService.getScheduleByKy(maSv, ky)
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

        @GetMapping("/export")
    ResponseEntity<InputStreamResource> exportMySchedule(Authentication authentication) throws Exception {
        ByteArrayInputStream excel = thoiKhoaBieuService.xuatExcelThoiKhoaBieu(authentication.getName());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=lichhocfull.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(excel));
    }
    @GetMapping("/excel/{maSv}")
    public ResponseEntity<InputStreamResource> exportExcel(
            @PathVariable String maSv) throws IOException {

        ByteArrayInputStream excelFile =
                thoiKhoaBieuService.xuatExcelThoiKhoaBieu(maSv);

        HttpHeaders headers = new HttpHeaders();
        headers.add(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=thoikhoabieu_" + maSv + ".xlsx"
        );

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(excelFile));
    }
}
