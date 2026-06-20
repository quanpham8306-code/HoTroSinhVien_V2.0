package PTPMUD.HoTroSinhVien.Controller.Admin;

import PTPMUD.HoTroSinhVien.DTO.Respone.ThoiKhoaBieuDTO;
import PTPMUD.HoTroSinhVien.DTO.ResponseObject;
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

    @GetMapping("/student/{idSv}")
    ResponseEntity<ResponseObject> getScheduleByIdSv(@PathVariable int idSv) {
        return ResponseEntity.ok(
                new ResponseObject(
                        "ok",
                        "Query schedule successfully",
                        thoiKhoaBieuService.getScheduleByIdSv(idSv)
                )
        );
    }

    @PostMapping("/student/{idSv}")
    ResponseEntity<ResponseObject> insertSchedule(
            @PathVariable int idSv,
            @RequestBody ThoiKhoaBieuDTO thoiKhoaBieuDTO
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseObject(
                        "ok",
                        "Insert schedule successfully",
                        thoiKhoaBieuService.createThoiKhoaBieu(idSv, thoiKhoaBieuDTO)
                )
        );
    }

    @PutMapping("/{idTkb}")
    ResponseEntity<ResponseObject> updateSchedule(
            @PathVariable int idTkb,
            @RequestBody ThoiKhoaBieuDTO thoiKhoaBieuDTO
    ) {
        return ResponseEntity.ok(
                new ResponseObject(
                        "ok",
                        "Update schedule successfully",
                        thoiKhoaBieuService.updateThoiKhoaBieu(idTkb, thoiKhoaBieuDTO)
                )
        );
    }

    @DeleteMapping("/{idTkb}")
    ResponseEntity<ResponseObject> deleteSchedule(@PathVariable int idTkb) {
        thoiKhoaBieuService.deleteThoiKhoaBieu(idTkb);

        return ResponseEntity.ok(
                new ResponseObject("ok", "Delete schedule successfully", "")
        );
    }



}
