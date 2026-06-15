package PTPMUD.HoTroSinhVien.Service;

import PTPMUD.HoTroSinhVien.DTO.Respone.BuoiHoc;
import PTPMUD.HoTroSinhVien.DTO.Respone.LopHocPhanDTO;
import PTPMUD.HoTroSinhVien.Entity.DangKyLopHocPhan;
import PTPMUD.HoTroSinhVien.Entity.LopHocPhan;
import PTPMUD.HoTroSinhVien.Entity.SinhVien;
import PTPMUD.HoTroSinhVien.Mapper.LopHocPhanMapper;
import PTPMUD.HoTroSinhVien.Repository.DangKyLopHocPhanRepository;
import PTPMUD.HoTroSinhVien.Repository.LopHocPhanRepository;
import PTPMUD.HoTroSinhVien.Repository.SinhVienRepository;
import PTPMUD.HoTroSinhVien.Util.LichHocValidator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DangKyLopHocPhanService {

    SinhVienRepository sinhVienRepository;
    LopHocPhanRepository lopHocPhanRepository;
    DangKyLopHocPhanRepository dangKyRepository;
    private final LopHocPhanMapper lopHocPhanMapper;

    @Transactional
    public DangKyLopHocPhan dangKyLopHocPhan(int idSv, int idLopHP) {
        SinhVien sinhVien = findSinhVien(idSv);
        LopHocPhan lopMoi = findLopHocPhan(idLopHP);

        validateDangKy(idSv, lopMoi);

        return dangKyRepository.save(new DangKyLopHocPhan(sinhVien, lopMoi));
    }

    private void validateDangKy(int idSv, LopHocPhan lopMoi) {
        if (dangKyRepository.existsBySinhVien_IdSvAndLopHocPhan_IdLopHP(idSv, lopMoi.getIdLopHP())) {
            throw new IllegalArgumentException("Sinh viên đã đăng ký lớp này");
        }

        if (dangKyRepository.countByLopHocPhan_IdLopHP(lopMoi.getIdLopHP()) >= lopMoi.getSiSoToiDa()) {
            throw new IllegalArgumentException("Lớp học phần đã đủ sĩ số");
        }

        List<LopHocPhan> dsLopDaDangKy = dangKyRepository.findBySinhVien_IdSv(idSv)
                .stream()
                .map(DangKyLopHocPhan::getLopHocPhan)
                .toList();

        validateKhongTrungMon(dsLopDaDangKy, lopMoi);
        validateKhongTrungLich(dsLopDaDangKy, lopMoi);
    }

    private void validateKhongTrungMon(List<LopHocPhan> dsLopDaDangKy, LopHocPhan lopMoi) {
        dsLopDaDangKy.stream()
                .filter(lop -> lop.getMonHoc().getIdMon() == lopMoi.getMonHoc().getIdMon())
                .findFirst()
                .ifPresent(lop -> {
                    throw new IllegalArgumentException("Sinh viên đã đăng ký môn này ở lớp " + lop.getMaLopHP());
                });
    }

    private void validateKhongTrungLich(List<LopHocPhan> dsLopDaDangKy, LopHocPhan lopMoi) {
        LopHocPhan lopTrung = LichHocValidator.timLopBiTrung(dsLopDaDangKy, lopMoi);
        if (lopTrung != null) {
            throw new IllegalArgumentException("Trùng lịch với lớp: " + lopTrung.getMaLopHP());
        }
    }

    private SinhVien findSinhVien(int idSv) {
        return sinhVienRepository.findById(idSv)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên với id: " + idSv));
    }

    private LopHocPhan findLopHocPhan(int idLopHP) {
        return lopHocPhanRepository.findById(idLopHP)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học phần với id: " + idLopHP));
    }

    public List<BuoiHoc> baBuoiGanNhat(String maSv){
        List<BuoiHoc> buoiHocList=new ArrayList<>();
        LocalDate ngay=LocalDate.now();
        LocalDate day=ngay;
        while(buoiHocList.size()<3 && day.isBefore(LocalDate.now().plusYears(1)))
        {
            int thu=day.getDayOfWeek().getValue();
            List<DangKyLopHocPhan> dangKyLopHocPhanList=dangKyRepository.findBySinhVien_MaSvAndLopHocPhan_Thu(maSv,thu);
            dangKyLopHocPhanList.sort(Comparator.comparing(DangKyLopHocPhan -> DangKyLopHocPhan.getLopHocPhan().getGioBatDau()));
            for(DangKyLopHocPhan dangKyLopHocPhan:dangKyLopHocPhanList)
                if(!day.isBefore( dangKyLopHocPhan.getLopHocPhan().getNgayBatDau()) && !day.isAfter( dangKyLopHocPhan.getLopHocPhan().getNgayKetThuc()) && buoiHocList.size()<3)
                {
                    LopHocPhanDTO lopHocPhanDTO=lopHocPhanMapper.entityToDto(dangKyLopHocPhan.getLopHocPhan());
                    BuoiHoc buoiHoc=new BuoiHoc(day,lopHocPhanDTO);
                    buoiHocList.add(buoiHoc);
                }
            day=day.plusDays(1);
        }
    return buoiHocList;
    }
}
