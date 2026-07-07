package PTPMUD.HoTroSinhVien.Service;

import PTPMUD.HoTroSinhVien.DTO.Respone.BuoiHoc;
import PTPMUD.HoTroSinhVien.DTO.Respone.HocKyNamHocDTO;
import PTPMUD.HoTroSinhVien.DTO.Respone.LopHocPhanDTO;
import PTPMUD.HoTroSinhVien.DTO.Respone.SinhVienDTO;
import PTPMUD.HoTroSinhVien.Entity.DangKyLopHocPhan;
import PTPMUD.HoTroSinhVien.Entity.Diem;
import PTPMUD.HoTroSinhVien.Entity.LopHocPhan;
import PTPMUD.HoTroSinhVien.Entity.SinhVien;
import PTPMUD.HoTroSinhVien.Mapper.LopHocPhanMapper;
import PTPMUD.HoTroSinhVien.Mapper.SinhVienMapper;
import PTPMUD.HoTroSinhVien.Repository.DangKyLopHocPhanRepository;
import PTPMUD.HoTroSinhVien.Repository.DiemRepository;
import PTPMUD.HoTroSinhVien.Repository.LopHocPhanRepository;
import PTPMUD.HoTroSinhVien.Repository.SinhVienRepository;
import PTPMUD.HoTroSinhVien.Util.LichHocValidator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DangKyLopHocPhanService {

    SinhVienRepository sinhVienRepository;
    LopHocPhanRepository lopHocPhanRepository;
    DangKyLopHocPhanRepository dangKyRepository;
    LopHocPhanMapper lopHocPhanMapper;
    DangKyLopHocPhanRepository dangKyLopHocPhanRepository;
    SinhVienMapper sinhVienMapper;
    DiemRepository diemRepository;

    @Transactional
    public DangKyLopHocPhan dangKyLopHocPhan(String maSv , String  maLopHP) {
        SinhVien sinhVien = findSinhVien(maSv);
        LopHocPhan lopMoi = findLopHocPhan(maLopHP);

        validateDangKy(maSv, lopMoi);

        return dangKyRepository.save(new DangKyLopHocPhan(sinhVien, lopMoi));
    }

    private void validateDangKy(String maSv, LopHocPhan lopMoi) {
        if (dangKyRepository.existsBySinhVien_MaSvAndLopHocPhan_MaLopHP(maSv, lopMoi.getMaLopHP())) {
            throw new IllegalArgumentException("Sinh viên đã đăng ký lớp này");
        }

        if (dangKyRepository.countByLopHocPhan_IdLopHP(lopMoi.getIdLopHP()) >= lopMoi.getSiSoToiDa()) {
            throw new IllegalArgumentException("Lớp học phần đã đủ sĩ số");
        }

        List<LopHocPhan> dsLopDaDangKy = dangKyRepository.findBySinhVien_MaSv(maSv)
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

    private SinhVien findSinhVien(String maSv) {
        SinhVien sinhVien = sinhVienRepository.findByMaSv(maSv);

        if (sinhVien == null) {
            throw new RuntimeException("Không tìm thấy sinh viên với mã: " + maSv);
        }

        return sinhVien;
    }

    private LopHocPhan findLopHocPhan(String maLopHP) {
        LopHocPhan lopHocPhan = lopHocPhanRepository.findByMaLopHP(maLopHP);

        if (lopHocPhan == null) {
            throw new RuntimeException("Không tìm thấy lớp học phần với mã: " + maLopHP);
        }

        return lopHocPhan;
    }

    private boolean check_cung_nganh(String maSv,String maLopHP){

        SinhVien sinhVien=sinhVienRepository.findByMaSv(maSv);
        LopHocPhan lopHocPhan=lopHocPhanRepository.findByMaLopHP(maLopHP);
        if(sinhVien.getNganh().equalsIgnoreCase(lopHocPhan.getNganh()))
            return true;
        else
            return false;
    }

    public List<BuoiHoc> baBuoiGanNhat(String maSv) {
        List<BuoiHoc> buoiHocList = new ArrayList<>();
        LocalDate ngay = LocalDate.now();
        LocalDate day = ngay;
        while (buoiHocList.size() < 3 && day.isBefore(LocalDate.now().plusYears(1))) {
            int thu = day.getDayOfWeek().getValue();
            List<DangKyLopHocPhan> dangKyLopHocPhanList = dangKyRepository.findBySinhVien_MaSvAndLopHocPhan_Thu(maSv, thu);
            dangKyLopHocPhanList.sort(Comparator.comparing(DangKyLopHocPhan -> DangKyLopHocPhan.getLopHocPhan().getGioBatDau()));
            for (DangKyLopHocPhan dangKyLopHocPhan : dangKyLopHocPhanList)
                if (!day.isBefore(dangKyLopHocPhan.getLopHocPhan().getNgayBatDau()) && !day.isAfter(dangKyLopHocPhan.getLopHocPhan().getNgayKetThuc()) && buoiHocList.size() < 3) {
                    LopHocPhanDTO lopHocPhanDTO = lopHocPhanMapper.entityToDto(dangKyLopHocPhan.getLopHocPhan());
                    BuoiHoc buoiHoc = new BuoiHoc(day, lopHocPhanDTO);
                    buoiHocList.add(buoiHoc);
                }
            day = day.plusDays(1);
        }
        return buoiHocList;
    }

    public List<HocKyNamHocDTO> listHocKyNamHocDTOS(String maSv)
    {
        List<HocKyNamHocDTO> lichHocKyNamHocDTOS=new ArrayList<>();
        List<DangKyLopHocPhan> dangKyLopHocPhanList=dangKyRepository.findBySinhVien_MaSv(maSv);
        for(DangKyLopHocPhan dangKyLopHocPhan:dangKyLopHocPhanList)
        {
            HocKyNamHocDTO hocKyNamHocDTO=new HocKyNamHocDTO(
                    dangKyLopHocPhan.getLopHocPhan().getHocKy(),
                    dangKyLopHocPhan.getLopHocPhan().getNamHoc()
                    );
            if(!lichHocKyNamHocDTOS.contains(hocKyNamHocDTO))
            {
                lichHocKyNamHocDTOS.add(hocKyNamHocDTO);
            }
        }

        return lichHocKyNamHocDTOS;
    }

    @Transactional
    public List<String> nhapExcelListSinhVienVaoLopHP(MultipartFile file, String maLopHP) {
        LopHocPhan lopHocPhan = lopHocPhanRepository.findByMaLopHP(maLopHP);
        List<String> errors = new ArrayList<>();
        if (lopHocPhan == null) {
            throw new RuntimeException("Lớp học phần không tồn tại");
        }
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                String maSv = formatter.formatCellValue(row.getCell(0)).trim();
                if (maSv.isBlank()) {
                    errors.add("Dòng " + (i + 1) + ": mã sinh viên trống");
                    continue;
                }
                SinhVien sinhVien = sinhVienRepository.findByMaSv(maSv);
                if (sinhVien == null) {
                    errors.add("Dòng " + (i + 1) + ": mã sinh viên không tồn tại - " + maSv);
                    continue;
                }

                boolean existed = dangKyRepository
                        .existsBySinhVien_IdSvAndLopHocPhan_IdLopHP(
                                sinhVien.getIdSv(),
                                lopHocPhan.getIdLopHP()
                        );
                if (existed) {
                    errors.add("Dòng " + (i + 1) + ": sinh viên đã có trong lớp - " + maSv);
                    continue;
                }
                if(check_cung_nganh(maSv,maLopHP)==false){
                    errors.add("Dòng " + (i + 1) + ": sinh viên khác ngành" );
                    continue;}
                int khoaSV = Integer.parseInt(sinhVien.getKhoa().substring(1));
                int khoaLHP = Integer.parseInt(lopHocPhan.getKhoa().substring(1));

                if (khoaSV < khoaLHP) {
                    errors.add("Dòng " + (i + 1) + ": sinh viên khóa dưới không được đăng ký");
                }
                List<LopHocPhan> lopHocPhans = dangKyRepository.findBySinhVien_MaSv(maSv)
                        .stream()
                        .map(DangKyLopHocPhan::getLopHocPhan)
                        .toList();
                validateKhongTrungMon(lopHocPhans,lopHocPhan);
                validateKhongTrungLich(lopHocPhans,lopHocPhan);
                dangKyRepository.save(new DangKyLopHocPhan(sinhVien, lopHocPhan));
            }
        } catch (IOException e) {
            throw new RuntimeException("Không thể đọc file excel", e);
        }

        return errors;
    }

    public void themSinhVienVaoLopHP(String maSv,String maLopHp)
    {
        SinhVien sinhVien=sinhVienRepository.findByMaSv(maSv);
        if (sinhVien == null) {
            throw new RuntimeException("Mã sinh viên tồn tại");
        }
        LopHocPhan lopHocPhan=lopHocPhanRepository.findByMaLopHP(maLopHp);
        if (lopHocPhan == null) {
            throw new RuntimeException("Mã lớp học phần không tồn tại");
        }

        if(check_cung_nganh(maSv,maLopHp)==false)
            throw  new RuntimeException("Sinh viên không thể vào lớp học phần do khác ngành học");

        validateDangKy(sinhVien.getMaSv(),lopHocPhan);

        if(dangKyRepository.existsBySinhVien_IdSvAndLopHocPhan_IdLopHP(sinhVien.getIdSv(), lopHocPhan.getIdLopHP()))
            throw new RuntimeException("Sinh viên đã có trong lớp");

        List<LopHocPhan> lopHocPhans = dangKyRepository.findBySinhVien_MaSv(maSv)
                .stream()
                .map(DangKyLopHocPhan::getLopHocPhan)
                .toList();
        validateKhongTrungMon(lopHocPhans,lopHocPhan);
        validateKhongTrungLich(lopHocPhans,lopHocPhan);

        DangKyLopHocPhan dangKyLopHocPhan = new DangKyLopHocPhan(sinhVien, lopHocPhan);
        dangKyRepository.save(dangKyLopHocPhan);
        }

        public void xoaLopHPOfSinhVien(String maSv,String maLopHP)
        {
            SinhVien sinhVien= sinhVienRepository.findByMaSv(maSv);
            findSinhVien(maSv);
            LopHocPhan lopHocPhan=lopHocPhanRepository.findByMaLopHP(maLopHP);

            if(!dangKyRepository.existsBySinhVien_MaSvAndLopHocPhan_MaLopHP(maSv,maLopHP))
                throw  new RuntimeException("Sinh viên có mã sinh viên "+maSv+ " không có trong lớp học phần "+maLopHP);
            DangKyLopHocPhan dangKyLopHocPhan=dangKyLopHocPhanRepository.findBySinhVien_MaSvAndLopHocPhan_MaLopHP(maSv,maLopHP);
            Diem diem=diemRepository.findByDangKyLopHocPhan(dangKyLopHocPhan);
            if(diem!=null)
                diemRepository.delete(diem);
            dangKyRepository.delete(dangKyRepository.findBySinhVien_MaSvAndLopHocPhan_MaLopHP(maSv,maLopHP));
        }

    public List<SinhVienDTO> xemSinhVienOfLop(String maSv)
    {
        List<SinhVienDTO> sinhVienDTOS=new ArrayList<>();
        List<DangKyLopHocPhan> dangKyLopHocPhanList=dangKyLopHocPhanRepository.findByLopHocPhan_MaLopHP(maSv);
        for(DangKyLopHocPhan dangKyLopHocPhan: dangKyLopHocPhanList)
        {
            SinhVien sinhVien=dangKyLopHocPhan.getSinhVien();
            SinhVienDTO sinhVienDTO=sinhVienMapper.entityToDto(sinhVien);
            sinhVienDTOS.add(sinhVienDTO);
        }
        return sinhVienDTOS;
    }

}

