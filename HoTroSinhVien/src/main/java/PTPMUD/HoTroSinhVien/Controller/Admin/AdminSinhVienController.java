package PTPMUD.HoTroSinhVien.Controller.Admin;

import PTPMUD.HoTroSinhVien.DTO.Request.CreateSinhVienDTO;
import PTPMUD.HoTroSinhVien.DTO.ResponseObject;
import PTPMUD.HoTroSinhVien.Entity.SinhVien;
import PTPMUD.HoTroSinhVien.Mapper.SinhVienMapper;
import PTPMUD.HoTroSinhVien.Repository.SinhVienRepository;
import PTPMUD.HoTroSinhVien.Service.SinhVienService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping(path = "/api/admin/student")
public class AdminSinhVienController {
    SinhVienRepository SinhVienRepository;
    SinhVienService SinhVienService;
    SinhVienMapper sinhVienMapper;

    @GetMapping()
    List<SinhVien> getAllStudent(){
        return SinhVienRepository.findAll();
    }

    @GetMapping("/{id}")
    ResponseEntity<ResponseObject> getStudent(@PathVariable int id){
        Optional<SinhVien> student = SinhVienRepository.findById(id);
        return student.isPresent() ?
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("ok","Query student successfully",sinhVienMapper.entityToDto(student.get()))
                ):
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("false","Can not found student with id : "+id,"")
                );
    }

    @PostMapping()
    ResponseEntity<ResponseObject> insertStudent(@RequestBody CreateSinhVienDTO sinhVien){
        SinhVien newStudent = sinhVienMapper.dtoToEntity(sinhVien);
        return  ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseObject("ok","Insert student successfully", sinhVienMapper.entityToDto(SinhVienService.createSinhVien(newStudent)))
        );
    }

    @PutMapping("/{id}")
    ResponseEntity<ResponseObject> updateStudent(@PathVariable int id, @RequestBody CreateSinhVienDTO newStudent){
        Optional<SinhVien> student = SinhVienRepository.findById(id);
        if(student.isPresent()){
            SinhVien oldStudent =  student.get();
            sinhVienMapper.updateSinhVien(oldStudent,newStudent);
            SinhVienRepository.save(oldStudent);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok","Update student successfully", sinhVienMapper.entityToDto(oldStudent))
            );
        }
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed","Can not found student with id : " + id,"")
            );
    }

    @PostMapping ("/importExcel")
    ResponseEntity<?> importExcel(@RequestParam ("danhSachSinhVien") MultipartFile file){
        SinhVienService.importExcelSinhVien(file);
        return ResponseEntity.status(HttpStatus.OK).body("Import thành công");
    }
}
