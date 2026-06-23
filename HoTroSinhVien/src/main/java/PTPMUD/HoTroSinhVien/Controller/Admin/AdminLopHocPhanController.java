package PTPMUD.HoTroSinhVien.Controller.Admin;

import PTPMUD.HoTroSinhVien.DTO.Respone.LopHocPhanDTO;
import PTPMUD.HoTroSinhVien.DTO.ResponseObject;
import PTPMUD.HoTroSinhVien.Entity.LopHocPhan;
import PTPMUD.HoTroSinhVien.Mapper.LopHocPhanMapper;
import PTPMUD.HoTroSinhVien.Repository.LopHocPhanRepository;
import PTPMUD.HoTroSinhVien.Service.LopHocPhanService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/admin/class")
public class AdminLopHocPhanController {

    LopHocPhanRepository lopHocPhanRepository;
    LopHocPhanService lopHocPhanService;
    LopHocPhanMapper lopHocPhanMapper;

    @GetMapping
    ResponseEntity<ResponseObject> getAllLopHP() {
        List<LopHocPhanDTO> result = lopHocPhanRepository.findAll()
                .stream()
                .map(lopHocPhanMapper::entityToDto)
                .toList();

        return ResponseEntity.ok(
                new ResponseObject("ok", "Query classes successfully", result)
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<ResponseObject> getLopHP(@PathVariable int id) {
        return lopHocPhanRepository.findById(id)
                .map(lopHP -> ResponseEntity.ok(
                        new ResponseObject("ok", "Query class successfully", lopHocPhanMapper.entityToDto(lopHP))
                ))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("failed", "Can not found class with id : " + id, "")
                ));
    }

    @PostMapping
    ResponseEntity<ResponseObject> insertLPH(@RequestBody LopHocPhanDTO request) {
        LopHocPhan newLPH = lopHocPhanMapper.dtoToEntity(request);
        LopHocPhan savedLPH = lopHocPhanRepository.save(newLPH);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseObject("ok", "Insert class successfully", lopHocPhanMapper.entityToDto(savedLPH))
        );
    }

    @PutMapping("/{id}")
    ResponseEntity<ResponseObject> updateLopHocPhan(
            @PathVariable int id,
            @RequestBody LopHocPhanDTO request
    ) {
        return lopHocPhanRepository.findById(id)
                .map(oldLPH -> {
                    LopHocPhan newLPH = lopHocPhanMapper.dtoToEntity(request);
                    lopHocPhanMapper.updateLPH(oldLPH, newLPH);
                    LopHocPhan savedLPH = lopHocPhanRepository.save(oldLPH);

                    return ResponseEntity.ok(
                            new ResponseObject("ok", "Update class successfully", lopHocPhanMapper.entityToDto(savedLPH))
                    );
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("failed", "Can not found class with id : " + id, "")
                ));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ResponseObject> deleteLopHP(@PathVariable int id) {
        if (!lopHocPhanRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed", "Can not found class with id : " + id, "")
            );
        }

        lopHocPhanRepository.deleteById(id);
        return ResponseEntity.ok(
                new ResponseObject("ok", "Delete class successfully", "")
        );
    }

    @PostMapping("/importExcel")
    ResponseEntity<?> importExcel(@RequestParam("lichDangKy") MultipartFile file) {

        List<String> errors = lopHocPhanService.importExcel(file);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }
        return ResponseEntity.ok("Import thành công");
    }


}
