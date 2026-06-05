package PTPMUD.HoTroSinhVien.Controller.Admin;

import PTPMUD.HoTroSinhVien.DTO.Request.CreateDiemDTO;
import PTPMUD.HoTroSinhVien.DTO.Respone.DiemDTO;
import PTPMUD.HoTroSinhVien.DTO.ResponseObject;
import PTPMUD.HoTroSinhVien.Entity.DangKyLopHocPhan;
import PTPMUD.HoTroSinhVien.Entity.Diem;
import PTPMUD.HoTroSinhVien.Mapper.DiemMapper;
import PTPMUD.HoTroSinhVien.Repository.DangKyLopHocPhanRepository;
import PTPMUD.HoTroSinhVien.Repository.DiemRepository;
import PTPMUD.HoTroSinhVien.Service.DiemService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/admin/score")
public class AdminDiemController {

    DiemRepository diemRepository;
    DiemService diemService;
    DiemMapper diemMapper;
    DangKyLopHocPhanRepository dangKyLopHocPhanRepository;

    @GetMapping
    ResponseEntity<ResponseObject> getAllDiem() {
        List<DiemDTO> result = diemRepository.findAll()
                .stream()
                .map(diemMapper::entityToDto)
                .toList();

        return ResponseEntity.ok(
                new ResponseObject("ok", "Query all scores successfully", result)
        );
    }

    @GetMapping("/student/{idSv}/class/{idLhp}")
    ResponseEntity<ResponseObject> getDiemByIdSvAndIdLop(
            @PathVariable int idSv,
            @PathVariable int idLhp
    ) {
        DangKyLopHocPhan dangKy = dangKyLopHocPhanRepository
                .findBySinhVien_IdSvAndLopHocPhan_IdLopHP(idSv, idLhp);

        if (dangKy == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed", "Student has not registered this class", "")
            );
        }

        Diem diem = diemRepository.findByDangKyLopHocPhan(dangKy);

        if (diem == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed", "Can not found score", "")
            );
        }

        return ResponseEntity.ok(
                new ResponseObject("ok", "Query score successfully", diemMapper.entityToDto(diem))
        );
    }

    @GetMapping("/student/{idSv}")
    ResponseEntity<ResponseObject> getDiemByIdSv(@PathVariable int idSv) {
        List<DiemDTO> result = diemRepository.findByDangKyLopHocPhan_SinhVien_IdSv(idSv)
                .stream()
                .map(diemMapper::entityToDto)
                .toList();

        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed", "Can not found score of student with id : " + idSv, "")
            );
        }

        return ResponseEntity.ok(
                new ResponseObject("ok", "Query score successfully", result)
        );
    }

    @GetMapping("/student/{idSv}/summary")
    ResponseEntity<ResponseObject> getScoreSummary(@PathVariable int idSv) {
        return ResponseEntity.ok(
                new ResponseObject(
                        "ok",
                        "Query score summary successfully",
                        diemService.getSummaryByIdSv(idSv)
                )
        );
    }

    @PostMapping("/student/{idSv}/class/{idLhp}")
    ResponseEntity<ResponseObject> addDiem(
            @PathVariable int idSv,
            @PathVariable int idLhp,
            @RequestBody CreateDiemDTO request
    ) {
        DangKyLopHocPhan dangKy = dangKyLopHocPhanRepository
                .findBySinhVien_IdSvAndLopHocPhan_IdLopHP(idSv, idLhp);

        if (dangKy == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed", "Student has not registered this class", "")
            );
        }

        Diem existed = diemRepository.findByDangKyLopHocPhan(dangKy);
        if (existed != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ResponseObject("failed", "Score already exists", diemMapper.entityToDto(existed))
            );
        }

        Diem diem = diemService.dtoToEntity(idSv, idLhp, request);
        Diem savedDiem = diemRepository.save(diem);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseObject("ok", "Insert score successfully", diemMapper.entityToDto(savedDiem))
        );
    }

    @PutMapping("/student/{idSv}/class/{idLhp}")
    ResponseEntity<ResponseObject> updateDiem(
            @PathVariable int idSv,
            @PathVariable int idLhp,
            @RequestBody CreateDiemDTO request
    ) {
        DangKyLopHocPhan dangKy = dangKyLopHocPhanRepository
                .findBySinhVien_IdSvAndLopHocPhan_IdLopHP(idSv, idLhp);

        if (dangKy == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed", "Student has not registered this class", "")
            );
        }

        Diem diem = diemRepository.findByDangKyLopHocPhan(dangKy);
        if (diem == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed", "Can not found score", "")
            );
        }

        diemMapper.updateDiem(diem, request);
        Diem savedDiem = diemRepository.save(diem);

        return ResponseEntity.ok(
                new ResponseObject("ok", "Update score successfully", diemMapper.entityToDto(savedDiem))
        );
    }
}
