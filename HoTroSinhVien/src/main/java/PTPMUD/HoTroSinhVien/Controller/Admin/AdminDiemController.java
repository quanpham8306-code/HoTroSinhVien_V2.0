package PTPMUD.HoTroSinhVien.Controller.Admin;

import PTPMUD.HoTroSinhVien.DTO.Request.CreateDiemDTO;
import PTPMUD.HoTroSinhVien.DTO.Respone.DiemAdminDTO;
import PTPMUD.HoTroSinhVien.DTO.Respone.DiemDTO;
import PTPMUD.HoTroSinhVien.DTO.ResponseObject;
import PTPMUD.HoTroSinhVien.Entity.DangKyLopHocPhan;
import PTPMUD.HoTroSinhVien.Entity.Diem;
import PTPMUD.HoTroSinhVien.Entity.MonHoc;
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
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOException;
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
        List<DiemAdminDTO> result = diemRepository.findAll()
                .stream()
                .map(diemMapper::entityDiemAdminDto)
                .toList();

        return ResponseEntity.ok(
                new ResponseObject("ok", "Query all scores successfully", result)
        );
    }

    @GetMapping("/{maSinhVien}/{maLop}")
    ResponseEntity<ResponseObject> getDiemByMaSvAndMaLop(
            @PathVariable String maSinhVien,
            @PathVariable String maLop
    ) {
        DangKyLopHocPhan dangKy = dangKyLopHocPhanRepository
                .findBySinhVien_MaSvAndLopHocPhan_MaLopHP(maSinhVien, maLop);

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
                new ResponseObject("ok", "Query score successfully", diemMapper.entityDiemDto(diem))
        );
    }

    @GetMapping("/student/{maSv}")
    ResponseEntity<ResponseObject> getDiemByIdSv(@PathVariable String maSv) {
        List<DiemAdminDTO> result = diemRepository.findByDangKyLopHocPhan_SinhVien_MaSv(maSv)
                .stream()
                .map(diemMapper::entityDiemAdminDto)
                .toList();

        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed", "Can not found score of student with ma sinh vien : " + maSv, "")
            );
        }

        return ResponseEntity.ok(
                new ResponseObject("ok", "Query score successfully", result)
        );
    }

    @GetMapping("/student/summary/{maSv}")
    ResponseEntity<ResponseObject> getScoreSummary(@PathVariable String maSv) {
        return ResponseEntity.ok(
                new ResponseObject(
                        "ok",
                        "Query score summary successfully",
                        diemService.getSummaryByMaSv(maSv)
                )
        );
    }

    @GetMapping ("/student/diem/{maSv}")
    ResponseEntity<ResponseObject> getDiem(@PathVariable String maSv) {
        return ResponseEntity.ok(
                new ResponseObject(
                        "ok",
                        "Query score summary successfully",
                        diemService. diemSVDTOS(maSv)
                )
        );
    }

    @PostMapping("/{maSv}/{maLopHP}")
    ResponseEntity<ResponseObject> addDiem(
            @PathVariable String maSv,
            @PathVariable String maLopHP,
            @RequestBody CreateDiemDTO request
    ) {
        DangKyLopHocPhan dangKy = dangKyLopHocPhanRepository
                .findBySinhVien_MaSvAndLopHocPhan_MaLopHP(maSv, maLopHP);

        if (dangKy == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed", "Student has not registered this class", "")
            );
        }

        Diem existed = diemRepository.findByDangKyLopHocPhan(dangKy);
        if (existed != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ResponseObject("failed", "Score already exists", diemMapper.entityDiemAdminDto(existed))
            );
        }

        Diem diem = diemService.dtoToEntity(maSv, maLopHP, request);
        Diem savedDiem = diemRepository.save(diem);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseObject("ok", "Insert score successfully", diemMapper.entityDiemAdminDto(savedDiem))
        );
    }

    @PutMapping("/{maSv}/{maLopHP}")
    ResponseEntity<ResponseObject> updateDiem(
            @PathVariable String maSv,
            @PathVariable String maLopHP,
            @RequestBody CreateDiemDTO request
    ) {
        DangKyLopHocPhan dangKy = dangKyLopHocPhanRepository
                .findBySinhVien_MaSvAndLopHocPhan_MaLopHP(maSv, maLopHP);

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
                new ResponseObject("ok", "Update score successfully", diemMapper.entityDiemAdminDto(savedDiem))
        );
    }

    @DeleteMapping("/{maSv}/{maLop}")
    ResponseEntity<ResponseObject> deleteMonHoc(
            @PathVariable String maSv,
            @PathVariable String maLop
    ) {
        List<Diem> diems = diemRepository.findByDangKyLopHocPhan_SinhVien_MaSvAndDangKyLopHocPhan_LopHocPhan_MaLopHP(maSv,maLop);
        if (diems != null) {
            diemRepository.deleteAll(diems);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok", "Delete Subject successfully", "")
            );
        }
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed", "Cannot found Subject student in class : " + maLop, "")
            );
    }

    @PostMapping("/importExcel")
    public ResponseEntity<ResponseObject> importExcelDiem(
            @RequestParam("danhSachDiem") MultipartFile file) throws Exception {

        List<String> errors = diemService.importExcelDiem(file);

        if (errors.isEmpty()) {
            return ResponseEntity.ok(
                    new ResponseObject(
                            "ok",
                            "Import Excel điểm thành công",
                            null
                    )
            );
        }

        return ResponseEntity.badRequest().body(
                new ResponseObject(
                        "failed",
                        "Import Excel thất bại",
                        errors
                )
        );
    }
}
