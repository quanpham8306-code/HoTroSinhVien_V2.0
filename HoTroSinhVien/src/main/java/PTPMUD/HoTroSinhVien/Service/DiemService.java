package PTPMUD.HoTroSinhVien.Service;

import PTPMUD.HoTroSinhVien.DTO.Request.CreateDiemDTO;
import PTPMUD.HoTroSinhVien.DTO.Respone.*;
import PTPMUD.HoTroSinhVien.Entity.DangKyLopHocPhan;
import PTPMUD.HoTroSinhVien.Entity.Diem;
import PTPMUD.HoTroSinhVien.Repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private final SinhVienRepository sinhVienRepository;
    LopHocPhanRepository lopHocPhanRepository;
    MonHocRepository monHocRepository;


    public Diem dtoToEntity(String maSv, String maLop, CreateDiemDTO dto) {
        DangKyLopHocPhan dangKy = dangKyLopHocPhanRepository.findBySinhVien_MaSvAndLopHocPhan_MaLopHP(maSv, maLop);
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

    public List<DiemAdminDTO> diemSVDTOS(String maSv)
    {
        if(sinhVienRepository.findByMaSv(maSv)==null)
            throw  new RuntimeException("Không tồn tại sinh viên này");
        List<Diem> diems=diemRepository.findByDangKyLopHocPhan_SinhVien_MaSv(maSv);
        List<DiemAdminDTO> diemAdminDTOS =new ArrayList<>();
        for(Diem diem:diems){
            DiemAdminDTO diemAdminDTO =new DiemAdminDTO();
            diemAdminDTO.setMaSv(maSv);
            diemAdminDTO.setMaLopHP(diem.getDangKyLopHocPhan().getLopHocPhan().getMonHoc().getMaMon());
            diemAdminDTO.setMon(diem.getDangKyLopHocPhan().getLopHocPhan().getMonHoc().getTenMonHoc());
            diemAdminDTO.setDiemHocPhan(diem.getDiemHocPhan());
            diemAdminDTO.setDiemQuaTrinh(diem.getDiemQuaTrinh());
            diemAdminDTO.setDiemCuoiKy(diem.getDiemCuoiKy());
            diemAdminDTO.setTrangThai(diem.getTrangThai());
            diemAdminDTOS.add(diemAdminDTO);
        }
        return diemAdminDTOS;
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
    public ScoreSummaryDTO getSummaryByMaSvAndNamHocAndKy(String maSv,int ky,String namHoc) {
        List<Diem> danhSachDiem = diemRepository.findByDangKyLopHocPhan_SinhVien_MaSvAndDangKyLopHocPhan_LopHocPhan_HocKyAndDangKyLopHocPhan_LopHocPhan_NamHoc(
                maSv,ky,namHoc
        );
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
    private double tinhGPA(List<Diem> danhSachDiem) {
        int tongTin = tinhTongTin(danhSachDiem);
        if (tongTin == 0) {
            return 0;
        }

        double tongDiem = danhSachDiem.stream()
                .mapToDouble(diem -> diem.getDiemHocPhan() * soTinChi(diem))
                .sum();

        double gpa = ((tongDiem / tongTin)/10)*4;

        return gpa;
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

    public List<String> importExcelDiem (MultipartFile file) throws IOException {
        List<String> error = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);

                if (row == null) {
                    continue;
                }
                try {
                    String maSv =getStringCell(row,0,formatter);
                    if (maSv.isBlank()) {
                        throw new RuntimeException("Họ tên đang để trống");
                    }
                    if(sinhVienRepository.findByMaSv(maSv)==null)
                        throw new RuntimeException("Không có sinh viên này với mã:"+maSv);
                    String maLopHp=getStringCell(row,1,formatter);
                    if(maLopHp.isBlank())
                        throw new RuntimeException("Lớp học phần đang để trống");
                    if(lopHocPhanRepository.findByMaLopHP(maLopHp)==null)
                        throw new RuntimeException("Lớp học phần không tồn tại");
                    String tenMon=getStringCell(row,2,formatter);
                    if(monHocRepository.findBytenMonHoc(tenMon)==null)
                        throw new RuntimeException("Môn học không tồn tai");
                    if(!lopHocPhanRepository.existsByMonHoc_TenMonHoc(tenMon))
                        throw new RuntimeException("Môn học này không có lớp học phần");
                    if(!dangKyLopHocPhanRepository.existsBySinhVien_MaSvAndLopHocPhan_MaLopHP(maSv,maLopHp))
                        throw new RuntimeException("Sinh viên này chưa đăng ký lớp học phần này");
                    int diemQuaTrinh=getIntCell(row,3,formatter);
                    if(diemQuaTrinh>10 || diemQuaTrinh <0)
                        throw new RuntimeException("Điểm quá trình chỉ được từ 0 đến 10");
                    int diemCuoiKy=getIntCell(row,4,formatter);
                    if(diemCuoiKy>10 || diemCuoiKy <0)
                        throw new RuntimeException("Điểm cuối kỳ chỉ được từ 0 đến 10");
                    if(diemRepository.findByDangKyLopHocPhan(dangKyLopHocPhanRepository.findBySinhVien_MaSvAndLopHocPhan_MaLopHP(maSv,maLopHp))!=null)
                        throw new RuntimeException("Sinh viên đã có điểm lớp học phần này");
                    DangKyLopHocPhan dangKyLopHocPhan=dangKyLopHocPhanRepository.findBySinhVien_MaSvAndLopHocPhan_MaLopHP(maSv,maLopHp);
                    Diem diem=new Diem(dangKyLopHocPhan,diemQuaTrinh,diemCuoiKy);
                    diemRepository.save(diem);
                } catch (RuntimeException e) {
                  error.add("Dòng "+(i+1)+" lỗi: "+e.getMessage());
                }
            }
        }

        return  error;
    }

    private String getStringCell(
            Row row,
            int index,
            DataFormatter formatter) {

        Cell cell = row.getCell(index);

        if (cell == null) {
            return "";
        }

        return formatter
                .formatCellValue(cell)
                .trim();
    }
    private int getIntCell(Row row, int cellIndex, DataFormatter formatter) {
        Cell cell = row.getCell(cellIndex);

        if (cell == null || cell.getCellType() == CellType.BLANK) {
            throw new RuntimeException("Điểm đang để trống ở cột " + (cellIndex + 1));
        }

        try {
            String value = formatter.formatCellValue(cell).trim();

            if (value.isBlank()) {
                throw new RuntimeException("Điểm đang để trống ở cột " + (cellIndex + 1));
            }

            if (value.endsWith(".0")) {
                value = value.substring(0, value.length() - 2);
            }

            return Integer.parseInt(value);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Dữ liệu số không hợp lệ ở cột " + (cellIndex + 1)
            );
        }
    }
}
