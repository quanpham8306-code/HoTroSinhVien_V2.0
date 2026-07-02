package PTPMUD.HoTroSinhVien.Controller.Admin;

import PTPMUD.HoTroSinhVien.DTO.Respone.ThoiKhoaBieuDTO;
import PTPMUD.HoTroSinhVien.DTO.ResponseObject;
import PTPMUD.HoTroSinhVien.Repository.SinhVienRepository;
import PTPMUD.HoTroSinhVien.Repository.ThoiKhoaBieuRepository;
import PTPMUD.HoTroSinhVien.Service.ThoiKhoaBieuService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/admin/schedule")
public class AdminThoiKhoaBieuController {

    ThoiKhoaBieuService thoiKhoaBieuService;
    SinhVienRepository sinhVienRepository;

    @GetMapping
    ResponseEntity<ResponseObject> getAllSchedule() {
        return ResponseEntity.ok(
                new ResponseObject(
                        "ok",
                        "Query schedule successfully",
                        thoiKhoaBieuService.getAllSchedule()
                )
        );
    }

    @GetMapping("/student/{maSv}")
    ResponseEntity<ResponseObject> getScheduleByMaSV(@PathVariable String maSv) {
        return ResponseEntity.ok(
                new ResponseObject(
                        "ok",
                        "Query schedule successfully",
                        thoiKhoaBieuService.getScheduleByMaSv(maSv)
                )
        );
    }

    @PostMapping("/student/{maSv}")
    ResponseEntity<ResponseObject> insertSchedule(
            @PathVariable String maSv,
            @RequestBody ThoiKhoaBieuDTO thoiKhoaBieuDTO
    ) {
        int idSv = sinhVienRepository.findByMaSv(maSv).getIdSv();
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseObject(
                        "ok",
                        "Insert schedule successfully",
                        thoiKhoaBieuService.createThoiKhoaBieu(idSv, thoiKhoaBieuDTO)
                )
        );
    }

}
