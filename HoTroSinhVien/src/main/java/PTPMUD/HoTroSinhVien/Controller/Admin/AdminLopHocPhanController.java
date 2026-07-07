package PTPMUD.HoTroSinhVien.Controller.Admin;

import PTPMUD.HoTroSinhVien.DTO.Request.DangKyLopRequest;
import PTPMUD.HoTroSinhVien.DTO.Respone.LopHocPhanDTO;
import PTPMUD.HoTroSinhVien.DTO.ResponseObject;
import PTPMUD.HoTroSinhVien.Entity.*;
import PTPMUD.HoTroSinhVien.Mapper.LopHocPhanMapper;
import PTPMUD.HoTroSinhVien.Repository.DangKyLopHocPhanRepository;
import PTPMUD.HoTroSinhVien.Repository.DiemRepository;
import PTPMUD.HoTroSinhVien.Repository.LopHocPhanRepository;
import PTPMUD.HoTroSinhVien.Repository.MonHocRepository;
import PTPMUD.HoTroSinhVien.Service.DangKyLopHocPhanService;
import PTPMUD.HoTroSinhVien.Service.LopHocPhanService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/admin/class")
public class AdminLopHocPhanController {

    LopHocPhanRepository lopHocPhanRepository;
    LopHocPhanService lopHocPhanService;
    LopHocPhanMapper lopHocPhanMapper;
    DangKyLopHocPhanService dangKyLopHocPhanService;
    DangKyLopHocPhanRepository dangKyLopHocPhanRepository;
    MonHocRepository monHocRepository;
    DiemRepository diemRepository;

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

    @GetMapping("/{maLopHP}")
    ResponseEntity<ResponseObject> getLopHP(@PathVariable String maLopHP) {
        LopHocPhan lopHocPhan = lopHocPhanRepository.findByMaLopHP(maLopHP);
        return lopHocPhan != null ?
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("ok","Query class successfully",lopHocPhanMapper.entityToDto(lopHocPhan))
                ):
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("false","Can not found class with ma mon : "+ maLopHP,"")
                );
    }

    @GetMapping("/registered_class/{maSv}")
    public ResponseEntity<ResponseObject> getClassOfStudent(@PathVariable String maSv) {

        List<DangKyLopHocPhan> dangKyLopHocPhanList =
                dangKyLopHocPhanRepository.findBySinhVien_MaSv(maSv.trim());

        List<LopHocPhanDTO> result = dangKyLopHocPhanList.stream()
                .map(dk -> lopHocPhanMapper.entityToDto(dk.getLopHocPhan()))
                .toList();

        return ResponseEntity.ok(
                new ResponseObject("ok", "Hiển thị thành công", result)
        );
    }

    @PostMapping
    ResponseEntity<ResponseObject> insertLPH(@RequestBody LopHocPhanDTO request) {
        LopHocPhan newLPH = lopHocPhanMapper.dtoToEntity(request);
        MonHoc monHoc=monHocRepository.findBytenMonHoc(request.getTenMonHoc());
        if(lopHocPhanRepository.findByMaLopHP(request.getMaLopHP()) == null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ResponseObject("ok", lopHocPhanService.taoLHP(newLPH,request.getTenMonHoc()), "")
            );
        }
        else
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ResponseObject(
                            "false",
                            "Lớp học đã tồn tại",
                            "Lớp học đã tồn tại"
                    )
            );
    }

    @PutMapping("/{maLopHP}")
    ResponseEntity<ResponseObject> updateLopHocPhan(
            @PathVariable String maLopHP,
            @RequestBody LopHocPhanDTO request
    ) {
        LopHocPhan lopHocPhan = lopHocPhanRepository.findByMaLopHP(maLopHP);

        if (lopHocPhan == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed", "Can not found class with ma lop : " + maLopHP, "")
            );
        }

        lopHocPhanMapper.updateLPH(lopHocPhan,request);

        LopHocPhan savedLPH = lopHocPhanRepository.save(lopHocPhan);

        return ResponseEntity.ok(
                new ResponseObject("ok", "Update class successfully", lopHocPhanMapper.entityToDto(savedLPH))
        );
    }


    @PostMapping("/importExcel")
    ResponseEntity<?> importExcel(@RequestParam("danhSachLop") MultipartFile file) {

        List<String> errors = lopHocPhanService.importExcel(file);

        return ResponseEntity.ok().body(errors);
    }

    @PostMapping ("/add_student")
    ResponseEntity<?> themSinhVienVaoLop(@RequestBody  DangKyLopRequest dangKyLopRequest)
    {
        System.out.println(dangKyLopRequest);
        try {
            dangKyLopHocPhanService.themSinhVienVaoLopHP(dangKyLopRequest.getMaSv(), dangKyLopRequest.getMaLopHp());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok","Thêm sinh viên vào lớp thành công","null"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    new ResponseObject("false",e.getMessage(),"null")
            );
        }
    }

    @PostMapping("/importExcelSinhVienVaoLopHP/{maLopHP}")
    public ResponseEntity<ResponseObject> importExcelSinhVienVaoLopHP(
            @RequestParam("danhSachSinhVienVaoLop") MultipartFile file,
            @PathVariable String maLopHP) {

        try {
            List<String> errors =
                    dangKyLopHocPhanService.nhapExcelListSinhVienVaoLopHP(file, maLopHP);

            return ResponseEntity.ok(
                    new ResponseObject(
                            "ok",
                            "Import excel danh sách sinh viên thành công",
                            errors
                    )
            );

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    new ResponseObject(
                            "false",
                            e.getMessage(),
                            null
                    )
            );
        }
    }

    @DeleteMapping ("/delete/{maLopHP}")
    ResponseEntity<?> delete ( @PathVariable String maLopHP){
         try{
             lopHocPhanService.delete(maLopHP);
             return ResponseEntity.status(HttpStatus.OK).body(
                     new ResponseObject("ok","Xóa lớp học phần thành công","null")
             );
         }catch(RuntimeException e){
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                     new ResponseObject("false",e.getMessage(),"null")
             );
         }
    }

    @DeleteMapping ("/delete/{maSv}/{maLopHP}")
    ResponseEntity<?> deleteLopOfSinhVien(@PathVariable String maSv,@PathVariable String maLopHP) {
        try {
            dangKyLopHocPhanService.xoaLopHPOfSinhVien(maSv,maLopHP);
           return ResponseEntity.status(HttpStatus.OK).body(
                   new ResponseObject("ok","Xóa thành công","null")
           );
        } catch (RuntimeException e) {
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                  new ResponseObject("false",e.getMessage(),"null")
          );
        }

    }

    @GetMapping ("/student/{maLopHP}")
    ResponseEntity<ResponseObject> getStudentOfClass(@PathVariable String maLopHP)
    {
        LopHocPhan lopHocPhan=lopHocPhanRepository.findByMaLopHP(maLopHP);
        if(lopHocPhan==null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseObject("false","Không tồn tại lớp học phần này","")
            );
        else
        {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok","Hiện thị danh sách thành công",dangKyLopHocPhanService.xemSinhVienOfLop(maLopHP))
            );
        }
    }
}
