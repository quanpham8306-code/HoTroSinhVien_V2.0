package PTPMUD.HoTroSinhVien.Service;

import PTPMUD.HoTroSinhVien.DTO.Request.CreateDiemDTO;
import PTPMUD.HoTroSinhVien.DTO.Respone.BangDiemDTO;
import PTPMUD.HoTroSinhVien.DTO.Respone.ScoreSummaryDTO;
import PTPMUD.HoTroSinhVien.Entity.DangKyLopHocPhan;
import PTPMUD.HoTroSinhVien.Entity.Diem;
import PTPMUD.HoTroSinhVien.Repository.DangKyLopHocPhanRepository;
import PTPMUD.HoTroSinhVien.Repository.DiemRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DiemService {

    DiemRepository diemRepository;
    DangKyLopHocPhanRepository dangKyLopHocPhanRepository;

    public Diem dtoToEntity(Integer idSv, Integer idLhp, CreateDiemDTO dto) {
        DangKyLopHocPhan dangKy = findDangKy(idSv, idLhp);

        Diem diem = new Diem();
        diem.setDangKyLopHocPhan(dangKy);
        updateDiemFromDto(diem, dto);
        return diem;
    }
    public List<BangDiemDTO> entityToBangDiemDTO(List<Diem> diems) {
        List<BangDiemDTO> dtos = new ArrayList<>();
        for (Diem diem : diems) {
            BangDiemDTO dto = new BangDiemDTO();
            dto.setMaMon(diem.getDangKyLopHocPhan().getLopHocPhan().getMonHoc().getMaMon());
            dto.setTenMon(diem.getDangKyLopHocPhan().getLopHocPhan().getMonHoc().getTenMonHoc());
            dto.setSoTin(diem.getDangKyLopHocPhan().getLopHocPhan().getMonHoc().getSoTinChi());
            dto.setDiemHocPhan(diem.getDiemHocPhan());
            dto.setDiemChu(diem.getDiemChu());
            dto.setTrangThai(diem.getTrangThai());
            dtos.add(dto);
        }
        return dtos;
    }

    @Transactional(readOnly = true)
    public ScoreSummaryDTO getSummaryByIdSv(int idSv) {
        List<Diem> danhSachDiem = diemRepository.findByDangKyLopHocPhan_SinhVien_IdSv(idSv);
        return buildSummary(danhSachDiem);
    }

    @Transactional(readOnly = true)
    public ScoreSummaryDTO getSummaryByMaSv(String maSv) {
        List<Diem> danhSachDiem = diemRepository.findByDangKyLopHocPhan_SinhVien_MaSv(maSv);
        return buildSummary(danhSachDiem);
    }

    @Transactional(readOnly = true)
    public ScoreSummaryDTO getSummaryByMaSvAndKy(String maSv,int ky) {
        List<Diem> danhSachDiem = diemRepository.findByDangKyLopHocPhan_SinhVien_MaSvAndDangKyLopHocPhan_LopHocPhan_HocKy(maSv,ky);
        return buildSummary(danhSachDiem);
    }

    public double tinhGPAById(int idSv) {
        return tinhGPA(diemRepository.findByDangKyLopHocPhan_SinhVien_IdSv(idSv));
    }

    public double tinhGPAByMaSv(String maSv) {
        return tinhGPA(diemRepository.findByDangKyLopHocPhan_SinhVien_MaSv(maSv));
    }

    public int tinhTinDaHoc(int idSv) {
        return tinhTongTin(diemRepository.findByDangKyLopHocPhan_SinhVien_IdSv(idSv));
    }

    public int tinhTinDaHocByMaSv(String maSv) {
        return tinhTongTin(diemRepository.findByDangKyLopHocPhan_SinhVien_MaSv(maSv));
    }

    public double GPATheoKy(int idSv, int ky) {
        return tinhGPA(diemRepository.findByDangKyLopHocPhan_SinhVien_IdSvAndDangKyLopHocPhan_LopHocPhan_HocKy(idSv, ky));
    }

    public double GPATheoKyByMaSv(String maSv, int ky) {
        return tinhGPA(diemRepository.findByDangKyLopHocPhan_SinhVien_MaSvAndDangKyLopHocPhan_LopHocPhan_HocKy(maSv, ky));
    }

    public int soMonDaHoc(int idSv) {
        return diemRepository.findByDangKyLopHocPhan_SinhVien_IdSv(idSv).size();
    }

    public int soMonDaHocByMaSv(String maSv) {
        return diemRepository.findByDangKyLopHocPhan_SinhVien_MaSv(maSv).size();
    }

    public String xepLoai(int idSv) {
        return xepLoaiTheoGPA(tinhGPAById(idSv));
    }

    public String xepLoaiByMaSv(String maSv) {
        return xepLoaiTheoGPA(tinhGPAByMaSv(maSv));
    }

    private ScoreSummaryDTO buildSummary(List<Diem> danhSachDiem) {
        double gpa = tinhGPA(danhSachDiem);
        return new ScoreSummaryDTO(
                gpa,
                tinhTongTin(danhSachDiem),
                danhSachDiem.size(),
                xepLoaiTheoGPA(gpa)
        );
    }
    private ScoreSummaryDTO buildSummaryByKy(List<Diem> danhSachDiem) {
        double gpa = tinhGPA(danhSachDiem);
        return new ScoreSummaryDTO(
                gpa,
                tinhTongTin(danhSachDiem),
                danhSachDiem.size(),
                xepLoaiTheoGPA(gpa)
        );
    }

    private double tinhGPA(List<Diem> danhSachDiem) {
        int tongTin = tinhTongTin(danhSachDiem);
        if (tongTin == 0) {
            return 0;
        }

        double tongDiem = danhSachDiem.stream()
                .mapToDouble(diem -> diem.getDiemHocPhan() * soTinChi(diem))
                .sum();

        return round(tongDiem / tongTin);
    }

    private int tinhTongTin(List<Diem> danhSachDiem) {
        return danhSachDiem.stream()
                .mapToInt(this::soTinChi)
                .sum();
    }

    private int soTinChi(Diem diem) {
        return diem.getDangKyLopHocPhan()
                .getLopHocPhan()
                .getMonHoc()
                .getSoTinChi();
    }

    private double round(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private String xepLoaiTheoGPA(double gpa) {
        return gpa >= 3.6 ? "Xuất sắc" :
                gpa >= 3.2 ? "Giỏi" :
                gpa >= 2.5 ? "Khá" :
                gpa >= 2.0 ? "Trung bình" :
                gpa >= 1.0 ? "Yếu" : "Kém";
    }

    private DangKyLopHocPhan findDangKy(Integer idSv, Integer idLhp) {
        if (idSv == null || idLhp == null) {
            throw new IllegalArgumentException("Thiếu id sinh viên hoặc id lớp học phần");
        }

        DangKyLopHocPhan dangKy = dangKyLopHocPhanRepository
                .findBySinhVien_IdSvAndLopHocPhan_IdLopHP(idSv, idLhp);

        if (dangKy == null) {
            throw new RuntimeException("Sinh viên chưa đăng ký lớp học phần này");
        }
        return dangKy;
    }

    private void updateDiemFromDto(Diem diem, CreateDiemDTO dto) {
        diem.setDiemQuaTrinh(dto.getDiemQuaTrinh());
        diem.setDiemCuoiKy(dto.getDiemCuoiKy());
        diem.setDiemHocPhan(dto.getDiemHocPhan());
        diem.setTrangThai(dto.getTrangThai());
    }
}
