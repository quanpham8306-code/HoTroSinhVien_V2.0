package PTPMUD.HoTroSinhVien.Service;

import PTPMUD.HoTroSinhVien.DTO.Respone.BuoiHoc;
import PTPMUD.HoTroSinhVien.DTO.Respone.HocKyNamHocDTO;
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

    public void nhapExcelListSinhVienVaoLopHP(MultipartFile file,String maLopHP)
    {
        LopHocPhan lopHocPhan=lopHocPhanRepository.findByMaLopHP(maLopHP);
        if(lopHocPhan==null){
            throw new RuntimeException("Lớp học phần không tồn tại");
        }
        try {
            Workbook workbook= WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            for(int i=1;i<=sheet.getLastRowNum();i++)
            {
                Row row=sheet.getRow(i);
                if(row==null) continue;
                else {
                    String maSv=formatter.formatCellValue(row.getCell(1)).trim();
                    SinhVien sinhVien=sinhVienRepository.findByMaSv(maSv);
                    if (sinhVien == null) {
                        System.out.println("Dòng " + (i + 1) + ": mã sinh viên không tồn tại");
                        continue;
                    }
                    if(!dangKyRepository.existsBySinhVien_IdSvAndLopHocPhan_IdLopHP(sinhVien.getIdSv(), lopHocPhan.getIdLopHP()))
                    {
                        DangKyLopHocPhan  dangKyLopHocPhan=new DangKyLopHocPhan(sinhVien,lopHocPhan);
                        dangKyRepository.save(dangKyLopHocPhan);
                    }
                    else
                        System.out.println("Dòng " + (i + 1) + ": sinh viên đã có trong lớp");
                }
            }
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException("Không thể đọc file excel",e);
        }
    }

    public void themSinhVienVaoLopHP(String maSv,String maLopHp)
    { SinhVien sinhVien=sinhVienRepository.findByMaSv(maSv);
        if(!sinhVienRepository.existsByMaSv(maSv));

        if (sinhVien == null) {
            throw new RuntimeException("Mã sinh viên không tồn tại");
        }
        LopHocPhan lopHocPhan=lopHocPhanRepository.findByMaLopHP(maLopHp);
        {
            System.out.println("Mã sinh viên không tồn tại");
        }
        if (lopHocPhan == null) {
            throw new RuntimeException("Mã lớp học phần không tồn tại");
        }
        if(dangKyRepository.existsBySinhVien_IdSvAndLopHocPhan_IdLopHP(sinhVien.getIdSv(), lopHocPhan.getIdLopHP()))
            throw new RuntimeException("Sinh viên đã có trong lớp");
        DangKyLopHocPhan dangKyLopHocPhan = new DangKyLopHocPhan(sinhVien, lopHocPhan);
        dangKyRepository.save(dangKyLopHocPhan);

        }

}

