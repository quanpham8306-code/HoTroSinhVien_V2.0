package PTPMUD.HoTroSinhVien.Controller.Admin;

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
    List<MonHoc> getAllMonHoc(){
        return MonHocRepository.findAll();
    }

    @GetMapping("/{id}")
    ResponseEntity<ResponseObject> getMonHoc(@PathVariable int id){
        Optional<MonHoc> monHoc = MonHocRepository.findById(id);
        return monHoc.isPresent() ?
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("ok","Query Subject successfully",monHocMapper.entityToDto(monHoc.get()))
                ):
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("false","Can not found Subject with id : "+id,"")
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

    @PutMapping("/{id}")
    ResponseEntity<ResponseObject> updateMonhoc(
            @PathVariable int id,
            @RequestBody MonHocDTO monHocDTO
    ) {
        Optional<MonHoc> monHoc = MonHocRepository.findById(id);

        if (monHoc.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject(
                            "failed",
                            "Can not find subject with id : " + id,
                            ""
                    )
            );
        }

        MonHoc oldMonHoc = monHoc.get();
        monHocMapper.updateMonHoc(oldMonHoc, monHocDTO);
        MonHoc savedMonHoc = MonHocRepository.save(oldMonHoc);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(
                        "ok",
                        "Update Subject successfully",
                        monHocMapper.entityToDto(savedMonHoc)
                )
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ResponseObject> deleteMonHoc(@PathVariable int id) {
        boolean exists = MonHocRepository.existsById(id);
        if (exists) {
            MonHocRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok", "Delete Subject successfully", "")
            );
        }
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed", "Cannot found Subject student with id : " + id, "")
            );
    }
}
