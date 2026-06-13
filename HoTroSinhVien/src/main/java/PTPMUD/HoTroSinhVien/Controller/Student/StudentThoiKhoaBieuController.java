package PTPMUD.HoTroSinhVien.Controller.Student;

import PTPMUD.HoTroSinhVien.DTO.ResponseObject;
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

    @GetMapping("/export")
    ResponseEntity<InputStreamResource> exportMySchedule(Authentication authentication) throws Exception {
        ByteArrayInputStream excel = thoiKhoaBieuService.xuatExcelThoiKhoaBieu(authentication.getName());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=lichhocfull.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(excel));
    }

    @PostMapping("/import")
    ResponseEntity<?> importMySchedulen(
            Authentication authentication,
            @RequestParam ("My schedule") MultipartFile file){

        thoiKhoaBieuService.importExcel(file,authentication.getName());
        return ResponseEntity.status(HttpStatus.OK).body("Import thành công");
    }

    }



