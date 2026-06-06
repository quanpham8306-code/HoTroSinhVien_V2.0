package PTPMUD.HoTroSinhVien.Service;

import PTPMUD.HoTroSinhVien.Entity.LopHocPhan;
import PTPMUD.HoTroSinhVien.Entity.MonHoc;
import PTPMUD.HoTroSinhVien.Mapper.MonHocMapper;
import PTPMUD.HoTroSinhVien.Repository.LopHocPhanRepository;
import PTPMUD.HoTroSinhVien.Repository.MonHocRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LopHocPhanService {
    LopHocPhanRepository lopHocPhanRepository;
    MonHocRepository monHocRepository;
    private final MonHocService monHocService;

    public LopHocPhan createLPH(LopHocPhan lopHocPhan, int idMonHoc){
        MonHoc monHoc = monHocRepository.findById(idMonHoc)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học"));

        int soLopHienCo = lopHocPhanRepository.countByMonHoc_IdMon(idMonHoc);

        String maLopHP = monHoc.getTenMonHoc() + "-N" + String.format("%02d", soLopHienCo + 1);

        lopHocPhan.setMaLopHP(maLopHP);
        lopHocPhan.setMonHoc(monHoc);

        return lopHocPhanRepository.save(lopHocPhan);
    }




}
