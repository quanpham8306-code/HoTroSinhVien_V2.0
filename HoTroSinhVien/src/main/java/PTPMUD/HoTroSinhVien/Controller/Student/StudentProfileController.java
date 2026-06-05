package PTPMUD.HoTroSinhVien.Controller.Student;

import PTPMUD.HoTroSinhVien.DTO.ResponseObject;
import PTPMUD.HoTroSinhVien.Entity.SinhVien;
import PTPMUD.HoTroSinhVien.Mapper.SinhVienMapper;
import PTPMUD.HoTroSinhVien.Repository.SinhVienRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping(path = "/api/student")
public class StudentProfileController {
    SinhVienRepository sinhVienRepository;
    SinhVienMapper sinhVienMapper;
    @GetMapping("/me")
    ResponseEntity<ResponseObject> getMyInf(Authentication authentication){
        SinhVien student = sinhVienRepository.findByMaSv(authentication.getName());
        return (student != null) ?
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("ok","Query student successfully.",sinhVienMapper.entityToDto(student))
                ):
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("false","Can not found student. ","")
                );
    }
}
