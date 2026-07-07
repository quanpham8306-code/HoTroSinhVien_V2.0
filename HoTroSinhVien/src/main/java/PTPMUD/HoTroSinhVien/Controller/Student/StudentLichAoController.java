package PTPMUD.HoTroSinhVien.Controller.Student;

import PTPMUD.HoTroSinhVien.DTO.Request.CheckLichAoRequest;
import PTPMUD.HoTroSinhVien.DTO.Respone.CheckLichAoResponse;
import PTPMUD.HoTroSinhVien.DTO.Respone.LichAoDTO;
import PTPMUD.HoTroSinhVien.DTO.Respone.LopHocPhanDTO;
import PTPMUD.HoTroSinhVien.DTO.ResponseObject;
import PTPMUD.HoTroSinhVien.Entity.LichAo;
import PTPMUD.HoTroSinhVien.Entity.SinhVien;
import PTPMUD.HoTroSinhVien.Repository.DiemRepository;
import PTPMUD.HoTroSinhVien.Repository.LichAoRepository;
import PTPMUD.HoTroSinhVien.Repository.MonHocRepository;
import PTPMUD.HoTroSinhVien.Repository.SinhVienRepository;
import PTPMUD.HoTroSinhVien.Service.LichAoService;
import PTPMUD.HoTroSinhVien.Service.LopHocPhanService;
import PTPMUD.HoTroSinhVien.Service.MonHocService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.analysis.function.Sinh;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/student/lich-ao")
@RequiredArgsConstructor
public class StudentLichAoController {

    private final LichAoService lichAoService;
    private final LopHocPhanService lopHocPhanService;
    private final SinhVienRepository sinhVienRepository;
    private final MonHocRepository monHocRepository;
    private final MonHocService monHocService;
    private final LichAoRepository lichAoRepository;

//    @PostMapping("/check-them-lop")
//    public ResponseEntity<ResponseObject> checkThemLop(
//            @RequestBody CheckLichAoRequest request
//    ) {
//        CheckLichAoResponse result = lichAoService.checkThemLop(request);
//
//        if (result.isValid()) {
//            return ResponseEntity.ok(
//                    new ResponseObject("ok", result.getMessage(), result)
//            );
//        }
//
//        return ResponseEntity.badRequest().body(
//                new ResponseObject("failed", result.getMessage(), result)
//        );
//    }
     @PostMapping("/save")
public ResponseEntity<ResponseObject> saveLopAo(
        Authentication authentication,
        @RequestBody LichAoDTO lichAoDTO
) {
    String maSv = authentication.getName();

    if (lichAoService.check_lop_trung(lichAoDTO)) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ResponseObject("false", "Lịch đã bị trùng", null)
        );
    }

    LichAo lichAo = lichAoRepository.findBySinhVien_MaSv(maSv);

    String s = lichAoService.taoChuoiStringLopHP(lichAoDTO);
    SinhVien sinhVien = sinhVienRepository.findByMaSv(maSv);

    if (lichAo != null) {
        lichAo.setDsMaLopHP(s);
        lichAoRepository.save(lichAo);
    } else {
        lichAoRepository.save(new LichAo(s, sinhVien));
    }

    return ResponseEntity.status(HttpStatus.OK).body(
            new ResponseObject("ok", "Lưu thành công", null)
    );
}


    @GetMapping("me")
    public ResponseEntity<ResponseObject> getLichAo(Authentication authentication) {
        String maSv = authentication.getName();

        LichAo lichAo = lichAoRepository.findBySinhVien_MaSv(maSv);

        if (lichAo == null) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("false", "Không có lịch ảo", null)
            );
        }

        LichAoDTO lichAoDTO =
                lichAoService.convertStringToLichAoDTO(lichAo.getDsMaLopHP());

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("ok", "Hiển thị lịch ảo thành công", lichAoDTO)
        );
    }

    @GetMapping("/picked-class/{khoa}/{maMon}")
    public ResponseEntity<ResponseObject> pickedClass(
            @PathVariable String maMon,
            @PathVariable String khoa,
            Authentication authentication
    ){
        String maSv=authentication.getName();

        SinhVien sinhVien = sinhVienRepository.findByMaSv(maSv);
        if (sinhVien == null) {
            return ResponseEntity.badRequest().body(
                    new ResponseObject("failed", "Không tìm thấy sinh viên", null)
            );
        }
        return ResponseEntity.ok(
                new ResponseObject(
                        "ok",
                        "",
                        lopHocPhanService.getLopOfMon(maMon,khoa,sinhVienRepository.findByMaSv(maSv).getNganh())
                )
        );

    }
    @GetMapping("/getMon/{khoa}")
    public ResponseEntity<ResponseObject> getMonByKhoa(@PathVariable String khoa,
                                                       Authentication authentication)
    {
        String maSv=authentication.getName();
        SinhVien sinhVien=sinhVienRepository.findByMaSv(maSv);
        return ResponseEntity.status(HttpStatus.OK).body(new
                ResponseObject("ok","Tìm thành công",monHocService.getMonByKhoa(sinhVien.getNganh(),khoa)));
    }

    @GetMapping("exportExcel")
    public ResponseEntity<InputStreamResource> exportLichAoExcel(Authentication authentication) throws IOException {
        String maSv = authentication.getName();

        LichAo lichAo = lichAoRepository.findBySinhVien_MaSv(maSv);

        if (lichAo == null) {
            return ResponseEntity.notFound().build();
        }

        LichAoDTO lichAoDTO = lichAoService.convertStringToLichAoDTO(lichAo.getDsMaLopHP());


        ByteArrayInputStream in = lichAoService.exportExcel(lichAoDTO);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=lich_ao.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                ))
                .body(new InputStreamResource(in));
    }

    @PostMapping("importExcel")
    public ResponseEntity<ResponseObject> importLichAoExcel(
            Authentication authentication,
            @RequestParam("lichAo") MultipartFile file) throws IOException {
        String maSv = authentication.getName();
        LichAoDTO lichAoDTO = lichAoService.importExcelLichAo(file);
        if (lichAoService.check_lop_trung(lichAoDTO)) {
            return ResponseEntity.badRequest().body(
                    new ResponseObject("false", "Lịch đã bị trùng", null)
            );
        }

        String s = lichAoService.taoChuoiStringLopHP(lichAoDTO);
        SinhVien sinhVien = sinhVienRepository.findByMaSv(maSv);
        LichAo lichAo = lichAoRepository.findBySinhVien_MaSv(maSv);

        if (lichAo != null) {
            lichAo.setDsMaLopHP(s);
            lichAoRepository.save(lichAo);
        } else {
            lichAoRepository.save(new LichAo(s, sinhVien));
        }

        return ResponseEntity.ok(
                new ResponseObject("ok", "Import lịch ảo thành công", lichAoDTO)
        );
    }

    @DeleteMapping ("/delete")
    ResponseEntity<ResponseObject> deleteLichAo(Authentication authentication)
    {
        String maSv=authentication.getName();
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("ok", lichAoService.delete(maSv),"" )
        );
    }

}