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

    @GetMapping("/{maLop}")
    ResponseEntity<ResponseObject> getLopHP(@PathVariable String maLop) {
        LopHocPhan lopHocPhan = lopHocPhanRepository.findByMaLopHP(maLop);
        return lopHocPhan != null ?
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("ok","Query class successfully",lopHocPhanMapper.entityToDto(lopHocPhan))
                ):
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("false","Can not found class with ma mon : "+ maLop,"")
                );
    }

    @PostMapping
    ResponseEntity<ResponseObject> insertLPH(@RequestBody LopHocPhanDTO request) {
        LopHocPhan newLPH = lopHocPhanMapper.dtoToEntity(request);
        if(lopHocPhanRepository.findByMaLopHP(request.getMaLopHP()) == null) {
            lopHocPhanRepository.save(newLPH);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ResponseObject("ok", "Insert class successfully", lopHocPhanMapper.entityToDto(newLPH))
            );
        }
        else
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ResponseObject(
                            "false",
                            "class has exist",
                            ""
                    )
            );
    }

    @PutMapping("/{maLopHP}")
    ResponseEntity<ResponseObject> updateLopHocPhan(
            @PathVariable String maLopHP,
            @RequestBody LopHocPhanDTO request
    ) {
        LopHocPhan lopHocPhan = lopHocPhanRepository.findByMaLopHP(maLopHP);
        if(lopHocPhan != null) {
            LopHocPhan newLPH = lopHocPhanMapper.dtoToEntity(request);
            lopHocPhanMapper.updateLPH(lopHocPhan, newLPH);
            LopHocPhan savedLPH = lopHocPhanRepository.save(lopHocPhan);

            return ResponseEntity.ok(
                    new ResponseObject("ok", "Update class successfully", lopHocPhanMapper.entityToDto(savedLPH))
            );
        }
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed", "Can not found class with ma lop : " +  maLopHP, "")
            );
    }

    @DeleteMapping("/{maLop}")
    ResponseEntity<ResponseObject> deleteLopHP(@PathVariable String maLopHP) {
        int id = lopHocPhanRepository.findByMaLopHP(maLopHP).getIdLopHP();
        if (!lopHocPhanRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed", "Can not found class with ma lop : " + maLopHP, "")
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
