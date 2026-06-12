package PTPMUD.HoTroSinhVien.Service;

import PTPMUD.HoTroSinhVien.DTO.Respone.LopHocPhanDTO;
import PTPMUD.HoTroSinhVien.DTO.Respone.ThoiKhoaBieuDTO;
import PTPMUD.HoTroSinhVien.Entity.DangKyLopHocPhan;
import PTPMUD.HoTroSinhVien.Entity.LopHocPhan;
import PTPMUD.HoTroSinhVien.Entity.SinhVien;
import PTPMUD.HoTroSinhVien.Entity.ThoiKhoaBieu;
import PTPMUD.HoTroSinhVien.Mapper.ThoiKhoaBieuMapper;
import PTPMUD.HoTroSinhVien.Repository.DangKyLopHocPhanRepository;
import PTPMUD.HoTroSinhVien.Repository.LopHocPhanRepository;
import PTPMUD.HoTroSinhVien.Repository.SinhVienRepository;
import PTPMUD.HoTroSinhVien.Repository.ThoiKhoaBieuRepository;
import PTPMUD.HoTroSinhVien.Util.LichHocValidator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ThoiKhoaBieuService {

    SinhVienRepository sinhVienRepository;
    DangKyLopHocPhanRepository dangKyRepository;
    ThoiKhoaBieuRepository thoiKhoaBieuRepository;
    LopHocPhanRepository lopHocPhanRepository;
    ThoiKhoaBieuMapper thoiKhoaBieuMapper;

    @Transactional(readOnly = true)
    public List<ThoiKhoaBieuDTO> getAllSchedule() {
        return thoiKhoaBieuRepository.findAll()
                .stream()
                .map(thoiKhoaBieuMapper::entityToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ThoiKhoaBieuDTO> getScheduleByIdSv(int idSv) {
        return thoiKhoaBieuRepository.findBySinhVien_IdSv(idSv)
                .stream()
                .map(thoiKhoaBieuMapper::entityToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ThoiKhoaBieuDTO> getScheduleByMaSv(String maSv) {
        return thoiKhoaBieuRepository.findBySinhVien_MaSv(maSv)
                .stream()
                .map(thoiKhoaBieuMapper::entityToDto)
                .toList();
    }

    @Transactional
    public ThoiKhoaBieu taoThoiKhoaBieuChinhThuc(int idSv) {
        SinhVien sinhVien = findSinhVien(idSv);
        List<DangKyLopHocPhan> danhSachDangKy = dangKyRepository.findBySinhVien_IdSv(idSv);

        if (danhSachDangKy.isEmpty()) {
            throw new IllegalArgumentException("Sinh viên chưa đăng ký lớp học phần nào");
        }

        ThoiKhoaBieu thoiKhoaBieu = new ThoiKhoaBieu();
        thoiKhoaBieu.setSinhVien(sinhVien);
        thoiKhoaBieu.setLoaiLich(ThoiKhoaBieu.CHINH_THUC);

        danhSachDangKy.stream()
                .map(DangKyLopHocPhan::getLopHocPhan)
                .forEach(lop -> addLopHocPhan(thoiKhoaBieu, lop));

        validateKhongTrungLich(thoiKhoaBieu);
        return thoiKhoaBieuRepository.save(thoiKhoaBieu);
    }

    @Transactional
    public ThoiKhoaBieuDTO createThoiKhoaBieu(int idSv, ThoiKhoaBieuDTO dto) {
        ThoiKhoaBieu thoiKhoaBieu = convertToEntity(idSv, dto);
        validateKhongTrungLich(thoiKhoaBieu);
        return thoiKhoaBieuMapper.entityToDto(thoiKhoaBieuRepository.save(thoiKhoaBieu));
    }

    @Transactional
    public ThoiKhoaBieuDTO updateThoiKhoaBieu(int idTkb, ThoiKhoaBieuDTO dto) {
        ThoiKhoaBieu oldSchedule = findThoiKhoaBieu(idTkb);
        ThoiKhoaBieu newSchedule = convertToEntity(oldSchedule.getSinhVien().getIdSv(), dto);

        validateKhongTrungLich(newSchedule);

        oldSchedule.setLoaiLich(newSchedule.getLoaiLich());

        thoiKhoaBieuMapper.updateThoiKhoaBieu(oldSchedule,new ThoiKhoaBieu());

        return thoiKhoaBieuMapper.entityToDto(thoiKhoaBieuRepository.save(oldSchedule));
    }

    @Transactional
    public void deleteThoiKhoaBieu(int idTkb) {
        if (!thoiKhoaBieuRepository.existsById(idTkb)) {
            throw new RuntimeException("Không tìm thấy thời khóa biểu với id: " + idTkb);
        }
        thoiKhoaBieuRepository.deleteById(idTkb);
    }

    public ThoiKhoaBieu convertToEntity(int idSv, ThoiKhoaBieuDTO dto) {
        validateRequest(dto);

        ThoiKhoaBieu thoiKhoaBieu = new ThoiKhoaBieu();
        thoiKhoaBieu.setSinhVien(findSinhVien(idSv));
        thoiKhoaBieu.setLoaiLich(resolveLoaiLich(dto.getLoaiLich()));

        dto.getLopHocPhanDTOList().stream()
                .map(this::findLopHocPhan)
                .forEach(lop -> addLopHocPhan(thoiKhoaBieu, lop));

        return thoiKhoaBieu;
    }

    public boolean isTrungLich(ThoiKhoaBieu thoiKhoaBieu) {
        return findLopTrungLich(thoiKhoaBieu) != null;
    }

    @Transactional(readOnly = true)
    public ByteArrayInputStream xuatExcelThoiKhoaBieu(String maSv) throws IOException {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Lịch học");
            createHeader(sheet);
            fillData(sheet, maSv);
            autoSizeColumns(sheet, 10);
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    private void validateRequest(ThoiKhoaBieuDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Thiếu dữ liệu thời khóa biểu");
        }
        if (dto.getLopHocPhanDTOList() == null || dto.getLopHocPhanDTOList().isEmpty()) {
            throw new IllegalArgumentException("Thời khóa biểu phải có ít nhất một lớp học phần");
        }
    }

    private SinhVien findSinhVien(int idSv) {
        return sinhVienRepository.findById(idSv)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên với id: " + idSv));
    }

    private ThoiKhoaBieu findThoiKhoaBieu(int idTkb) {
        return thoiKhoaBieuRepository.findById(idTkb)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thời khóa biểu với id: " + idTkb));
    }

    private LopHocPhan findLopHocPhan(LopHocPhanDTO dto) {
        if (dto == null || dto.getMaLopHP() == null || dto.getMaLopHP().isBlank()) {
            throw new IllegalArgumentException("Lớp học phần trong DTO thiếu mã lớp");
        }

        LopHocPhan lopHocPhan = lopHocPhanRepository.findByMaLopHP(dto.getMaLopHP());
        if (lopHocPhan == null) {
            throw new RuntimeException("Không tìm thấy lớp học phần có mã: " + dto.getMaLopHP());
        }
        return lopHocPhan;
    }

    private String resolveLoaiLich(String loaiLich) {
        return loaiLich == null || loaiLich.isBlank() ? ThoiKhoaBieu.LICH_AO : loaiLich;
    }

    private void addLopHocPhan(ThoiKhoaBieu thoiKhoaBieu, LopHocPhan lopHocPhan) {
        thoiKhoaBieu.getLopHocPhan().add(lopHocPhan);
    }

    private void validateKhongTrungLich(ThoiKhoaBieu thoiKhoaBieu) {
        LopHocPhan lopTrung = findLopTrungLich(thoiKhoaBieu);
        if (lopTrung != null) {
            throw new IllegalArgumentException("Các lớp học phần trong thời khóa biểu bị trùng lịch");
        }
    }

    private LopHocPhan findLopTrungLich(ThoiKhoaBieu thoiKhoaBieu) {

        List<LopHocPhan> lopHocPhans=thoiKhoaBieu.getLopHocPhan();
        for (int i = 0; i < lopHocPhans.size(); i++) {
            LopHocPhan lopTrung = LichHocValidator.timLopBiTrung(lopHocPhans.subList(i + 1, lopHocPhans.size()), lopHocPhans.get(i));
            if (lopTrung != null) {
                return lopTrung;
            }
        }
        return null;
    }

    private void createHeader(Sheet sheet) {
        Row header = sheet.createRow(0);
        String[] titles = {
                "Loại lịch", "Mã lớp", "Môn học", "Ngày bắt đầu", "Ngày kết thúc",
                "Thứ", "Giờ bắt đầu", "Giờ kết thúc", "Phòng học", "Giảng viên"
        };

        for (int i = 0; i < titles.length; i++) {
            header.createCell(i).setCellValue(titles[i]);
        }
    }

    private void fillData(Sheet sheet,String maSv) {
        int rowIndex = 1;

        for (ThoiKhoaBieu thoiKhoaBieu : thoiKhoaBieuRepository.findBySinhVien_MaSv(maSv))
            for(LopHocPhan lopHocPhan: thoiKhoaBieu.getLopHocPhan())
            rowIndex = writeRow(sheet, rowIndex, thoiKhoaBieu, lopHocPhan);

    }

    private int writeRow(Sheet sheet, int rowIndex, ThoiKhoaBieu thoiKhoaBieu, LopHocPhan lop) {
        Row row = sheet.createRow(rowIndex++);
        row.createCell(0).setCellValue(empty(thoiKhoaBieu.getLoaiLich()));
        row.createCell(1).setCellValue(empty(lop.getMaLopHP()));
        row.createCell(2).setCellValue(lop.getMonHoc() == null ? "" : empty(lop.getMonHoc().getTenMonHoc()));
        row.createCell(3).setCellValue(lop.getNgayBatDau() == null ? "" : lop.getNgayBatDau().toString());
        row.createCell(4).setCellValue(lop.getNgayKetThuc() == null ? "" : lop.getNgayKetThuc().toString());
        row.createCell(5).setCellValue((lop.getThu()));
        row.createCell(6).setCellValue(lop.getGioBatDau() == null ? "" : lop.getGioBatDau().toString());
        row.createCell(7).setCellValue(lop.getGioKetThuc() == null ? "" : lop.getGioKetThuc().toString());
        row.createCell(8).setCellValue(empty(lop.getPhongHoc()));
        row.createCell(9).setCellValue(empty(lop.getGiangVien()));
        return rowIndex;
    }

    private void autoSizeColumns(Sheet sheet, int totalColumns) {
        for (int i = 0; i < totalColumns; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private String empty(String value) {
        return value == null ? "" : value;
    }
}
