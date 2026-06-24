package PTPMUD.HoTroSinhVien.Controller.Admin;

import PTPMUD.HoTroSinhVien.DTO.Respone.DiemDTO;
import PTPMUD.HoTroSinhVien.DTO.Respone.MonHocDTO;
import PTPMUD.HoTroSinhVien.DTO.ResponseObject;
import PTPMUD.HoTroSinhVien.Entity.MonHoc;
import PTPMUD.HoTroSinhVien.Mapper.MonHocMapper;
import PTPMUD.HoTroSinhVien.Repository.MonHocRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping(path = "/api/admin/subject")
public class AdminMonHocController {
    MonHocRepository MonHocRepository;
    MonHocMapper monHocMapper;

    @GetMapping()
    List<MonHocDTO> getAllMonHoc(){
        List<MonHocDTO> result = MonHocRepository.findAll()
                .stream()
                .map(monHocMapper::entityToDto)
                .toList();
        return result;
    }

    @GetMapping("/{maMonHoc}")
    ResponseEntity<ResponseObject> getMonHoc(@PathVariable String maMonHoc){
        MonHoc monHoc = MonHocRepository.findByMaMon(maMonHoc);
        return monHoc != null ?
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("ok","Query Subject successfully",monHocMapper.entityToDto(monHoc))
                ):
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("false","Can not found Subject with ma mon : "+maMonHoc,"")
                );
    }

    @PostMapping()
    ResponseEntity<ResponseObject> insertMonhoc(@RequestBody MonHocDTO monHocDTO){
        MonHoc newMonHoc = monHocMapper.dtoToEntity(monHocDTO);
        MonHoc foundMonHoc = MonHocRepository.findBytenMonHoc(newMonHoc.getTenMonHoc().trim());
        if(foundMonHoc!=null){
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new ResponseObject("false","Subject with name "+newMonHoc.getTenMonHoc()+" already exists","")
            );
        }
        else
            return  ResponseEntity.status(HttpStatus.CREATED).body(
                    new ResponseObject("ok","Insert student successfully",monHocMapper.entityToDto(MonHocRepository.save(newMonHoc)))
            );
    }

    @PutMapping("/{maMonHoc}")
    ResponseEntity<ResponseObject> updateMonhoc(
            @PathVariable String maMonHoc,
            @RequestBody MonHocDTO monHocDTO
    ) {
        MonHoc monHoc = MonHocRepository.findByMaMon(maMonHoc);

        if (monHoc == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject(
                            "failed",
                            "Can not find subject with ma mon : " + maMonHoc,
                            ""
                    )
            );
        }

        monHocMapper.updateMonHoc(monHoc, monHocDTO);
        MonHoc savedMonHoc = MonHocRepository.save(monHoc);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(
                        "ok",
                        "Update Subject successfully",
                        monHocMapper.entityToDto(savedMonHoc)
                )
        );
    }

    @DeleteMapping("/{maMonHoc}")
    ResponseEntity<ResponseObject> deleteMonHoc(@PathVariable String maMonHoc) {
        MonHoc monHoc = MonHocRepository.findByMaMon(maMonHoc);
        if (monHoc != null) {
            MonHocRepository.deleteById(monHoc.getIdMon());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok", "Delete Subject successfully", "")
            );
        }
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed", "Cannot found Subject student with ma mon : " + maMonHoc, "")
            );
    }
}
