package PTPMUD.HoTroSinhVien.Controller.Admin;

import PTPMUD.HoTroSinhVien.DTO.Request.CreateSinhVienDTO;
import PTPMUD.HoTroSinhVien.DTO.ResponseObject;
import PTPMUD.HoTroSinhVien.Entity.LopHocPhan;
import PTPMUD.HoTroSinhVien.Entity.SinhVien;
import PTPMUD.HoTroSinhVien.Mapper.LopHocPhanMapper;
import PTPMUD.HoTroSinhVien.Mapper.SinhVienMapper;
import PTPMUD.HoTroSinhVien.Repository.DangKyLopHocPhanRepository;
import PTPMUD.HoTroSinhVien.Repository.LopHocPhanRepository;
import PTPMUD.HoTroSinhVien.Repository.SinhVienRepository;
import PTPMUD.HoTroSinhVien.Service.DangKyLopHocPhanService;
import PTPMUD.HoTroSinhVien.Service.LopHocPhanService;
import PTPMUD.HoTroSinhVien.Service.SinhVienService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.math3.analysis.function.Sinh;
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
    private final DangKyLopHocPhanRepository dangKyLopHocPhanRepository;
    private final DangKyLopHocPhanService dangKyLopHocPhanService;

    @GetMapping()
    List<SinhVien> getAllStudent(){
        return SinhVienRepository.findAll();
    }

    @GetMapping("/{maSv}")
    ResponseEntity<ResponseObject> getStudent(@PathVariable String maSv){
        int id = SinhVienRepository.findByMaSv(maSv).getIdSv();
        Optional<SinhVien> student = SinhVienRepository.findById(id);
        return student.isPresent() ?
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("ok","Query student successfully",sinhVienMapper.entityToDto(student.get()))
                ):
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("false","Can not found student with maSv : " + maSv,"")
                );
    }

    @PostMapping()
    ResponseEntity<ResponseObject> insertStudent(@RequestBody CreateSinhVienDTO sinhVien){
        SinhVien newStudent = sinhVienMapper.dtoToEntity(sinhVien);
        return  ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseObject("ok","Insert student successfully", sinhVienMapper.entityToDto(SinhVienService.createSinhVien(newStudent)))
        );
    }

    @PutMapping("/{maSv}")
    ResponseEntity<ResponseObject> updateStudent(@PathVariable String maSv, @RequestBody CreateSinhVienDTO newStudent){
        int id = SinhVienRepository.findByMaSv(maSv).getIdSv();
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
                    new ResponseObject("failed","Can not found student with ma sv : " + maSv,"")
            );
    }

    @PostMapping ("/importExcel")
    ResponseEntity<?> importExcel(@RequestParam ("danhSachSinhVien") MultipartFile file){
        SinhVienService.importExcelSinhVien(file);
        return ResponseEntity.status(HttpStatus.OK).body("Import thành công");
    }


    @DeleteMapping("/delete/{maSv}")
    ResponseEntity<?> delete(@PathVariable String maSv)
    {
        SinhVienService.delete(maSv);
        return ResponseEntity.status(HttpStatus.OK).body("Delete thành công");
    }

}
