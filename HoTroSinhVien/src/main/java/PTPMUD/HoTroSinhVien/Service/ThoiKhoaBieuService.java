package PTPMUD.HoTroSinhVien.Service;

import PTPMUD.HoTroSinhVien.DTO.Respone.LopHocPhanDTO;
import PTPMUD.HoTroSinhVien.DTO.Respone.ThoiKhoaBieuDTO;
import PTPMUD.HoTroSinhVien.Entity.*;
import PTPMUD.HoTroSinhVien.Mapper.LopHocPhanMapper;
import PTPMUD.HoTroSinhVien.Mapper.ThoiKhoaBieuMapper;
import PTPMUD.HoTroSinhVien.Repository.*;
import PTPMUD.HoTroSinhVien.Util.LichHocValidator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
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
    LopHocPhanMapper lopHocPhanMapper;
    ChiTietThoiKhoaBieuRepository chiTietThoiKhoaBieuRepository;

    @Transactional(readOnly = true)
    public List<ThoiKhoaBieuDTO> getAllSchedule() {
        return thoiKhoaBieuRepository.findAll()
                .stream()
                .map(this::entityToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ThoiKhoaBieuDTO> getScheduleByIdSv(int idSv) {
        return thoiKhoaBieuRepository.findBySinhVien_IdSv(idSv)
                .stream()
                .map(this::entityToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ThoiKhoaBieuDTO> getScheduleByMaSv(String maSv) {
        return thoiKhoaBieuRepository.findBySinhVien_MaSv(maSv)
                .stream()
                .map(this::entityToDTO)
                .toList();
    }

    private ThoiKhoaBieuDTO entityToDTO(ThoiKhoaBieu thoiKhoaBieu) {
        List<LopHocPhan> lopHocPhanList =
                chiTietThoiKhoaBieuRepository.findByThoiKhoaBieu(thoiKhoaBieu)
                        .stream()
                        .map(ChiTietThoiKhoaBieu::getLopHocPhan)
                        .toList();

        return ThoiKhoaBieuDTO.builder()
                .loaiLich(thoiKhoaBieu.getLoaiLich())
                .lopHocPhanDTOList(lopHocPhanMapper.entityToDTO(lopHocPhanList))
                .build();
    }

    @Transactional
    public ThoiKhoaBieu taoThoiKhoaBieuChinhThuc(int idSv) {
        SinhVien sinhVien = findSinhVien(idSv);
        List<DangKyLopHocPhan> danhSachDangKy = dangKyRepository.findBySinhVien_IdSv(idSv);

        if (danhSachDangKy.isEmpty()) {
            throw new IllegalArgumentException("Sinh viên chưa đăng ký lớp học phần nào");
        }

        List<LopHocPhan> lopHocPhanList = danhSachDangKy.stream()
                .map(DangKyLopHocPhan::getLopHocPhan)
                .toList();

        validateKhongTrungLich(lopHocPhanList);

        ThoiKhoaBieu thoiKhoaBieu = new ThoiKhoaBieu();
        thoiKhoaBieu.setSinhVien(sinhVien);
        thoiKhoaBieu.setLoaiLich(ThoiKhoaBieu.CHINH_THUC);
        thoiKhoaBieu = thoiKhoaBieuRepository.save(thoiKhoaBieu);

        for(LopHocPhan lopHocPhan:lopHocPhanList)
            chiTietThoiKhoaBieuRepository.save(new ChiTietThoiKhoaBieu(thoiKhoaBieu,lopHocPhan));
        return thoiKhoaBieuRepository.save(thoiKhoaBieu);
    }

    @Transactional
    public ThoiKhoaBieuDTO createThoiKhoaBieu(int idSv, ThoiKhoaBieuDTO thoiKhoaBieuDTO) {
        SinhVien sinhVien=findSinhVien(idSv);
        List<LopHocPhan> lopHocPhanList=convertToEntity(thoiKhoaBieuDTO);
        validateKhongTrungLich(lopHocPhanList);

        ThoiKhoaBieu thoiKhoaBieu=new ThoiKhoaBieu();
        thoiKhoaBieu.setSinhVien(sinhVien);
        thoiKhoaBieu.setLoaiLich(resolveLoaiLich(thoiKhoaBieuDTO.getLoaiLich()));
        thoiKhoaBieu=thoiKhoaBieuRepository.save(thoiKhoaBieu);

        for(LopHocPhan lopHocPhan:lopHocPhanList)
            chiTietThoiKhoaBieuRepository.save(new ChiTietThoiKhoaBieu(thoiKhoaBieu,lopHocPhan));
        return thoiKhoaBieuMapper.entityToDto(thoiKhoaBieu);
    }

    private List<LopHocPhan> convertToEntity(ThoiKhoaBieuDTO thoiKhoaBieuDTO) {
        validateRequest(thoiKhoaBieuDTO);

        return  thoiKhoaBieuDTO.getLopHocPhanDTOList()
                .stream()
                .map(this::findLopHocPhan)
                .toList();
    }

    @Transactional
    public ThoiKhoaBieuDTO updateThoiKhoaBieu(int idTkb, ThoiKhoaBieuDTO dto) {
        ThoiKhoaBieu oldSchedule = findThoiKhoaBieu(idTkb);

        List<LopHocPhan> lopHocPhanList = dto.getLopHocPhanDTOList()
                .stream()
                .map(this::findLopHocPhan)
                .toList();

        validateKhongTrungLich(lopHocPhanList);

        oldSchedule.setLoaiLich(resolveLoaiLich(dto.getLoaiLich()));

        thoiKhoaBieuRepository.save(oldSchedule);

        chiTietThoiKhoaBieuRepository.deleteByThoiKhoaBieu(oldSchedule);

        for (LopHocPhan lopHocPhan : lopHocPhanList) {
            ChiTietThoiKhoaBieu chiTiet =
                    new ChiTietThoiKhoaBieu(oldSchedule, lopHocPhan);

            chiTietThoiKhoaBieuRepository.save(chiTiet);
        }

        return thoiKhoaBieuMapper.entityToDto(oldSchedule);
    }



    private void validateKhongTrungLich(List<LopHocPhan> lopHocPhanList) {
        LopHocPhan lopTrung = findLopTrungLich(lopHocPhanList);
        if (lopTrung != null) {
            throw new IllegalArgumentException("Các lớp học phần trong thời khóa biểu bị trùng lịch");
        }
    }

    public ThoiKhoaBieuDTO getScheduleByKy(String maSv, int hocKy)
    {
        ThoiKhoaBieuDTO dto = new ThoiKhoaBieuDTO();
        List<DangKyLopHocPhan> dangKyLopHocPhanList = dangKyRepository.findBySinhVien_MaSvAndLopHocPhan_HocKy(maSv,hocKy);
        List<LopHocPhan> lopHocPhanList = dangKyLopHocPhanList.stream()
                .map(DangKyLopHocPhan::getLopHocPhan)
                .toList();
        dto.setLopHocPhanDTOList(lopHocPhanMapper.entityToDTO(lopHocPhanList));
        return dto;
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

    private LopHocPhan findLopTrungLich(List<LopHocPhan> lopHocPhanList) {

        for (int i = 0; i < lopHocPhanList.size(); i++) {
            LopHocPhan lopTrung = LichHocValidator.timLopBiTrung(
                    lopHocPhanList.subList(i + 1, lopHocPhanList.size()),
                    lopHocPhanList.get(i));
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

        for (ThoiKhoaBieu thoiKhoaBieu : thoiKhoaBieuRepository.findBySinhVien_MaSv(maSv)) {
            if (thoiKhoaBieu.getLoaiLich().equalsIgnoreCase("LICH_AO")) {

                List<ChiTietThoiKhoaBieu> chiTietList =
                        chiTietThoiKhoaBieuRepository.findByThoiKhoaBieu(thoiKhoaBieu);

                for (ChiTietThoiKhoaBieu chiTiet : chiTietList) {
                    rowIndex = writeRow(
                            sheet,
                            rowIndex,
                            thoiKhoaBieu,
                            chiTiet.getLopHocPhan()
                    );
                }
            }
        }

    }

    private int writeRow(Sheet sheet, int rowIndex, ThoiKhoaBieu thoiKhoaBieu, LopHocPhan lop) {
        Row row = sheet.createRow(rowIndex++);
        row.createCell(0).setCellValue(empty(thoiKhoaBieu.getLoaiLich()));
        row.createCell(1).setCellValue(empty(lop.getMaLopHP()));
        row.createCell(2).setCellValue(lop.getNganh() == null ? "" : lop.getNganh());
        row.createCell(3).setCellValue(lop.getKhoa() == null ? "" : lop.getKhoa());
        row.createCell(4).setCellValue(lop.getSiSoToiDa());
        row.createCell(5).setCellValue(lop.getMonHoc() == null ? "" : empty(lop.getMonHoc().getTenMonHoc()));
        row.createCell(6).setCellValue(lop.getNgayBatDau() == null ? "" : lop.getNgayBatDau().toString());
        row.createCell(7).setCellValue(lop.getNgayKetThuc() == null ? "" : lop.getNgayKetThuc().toString());
        row.createCell(8).setCellValue((lop.getThu()));
        row.createCell(9).setCellValue(lop.getGioBatDau() == null ? "" : lop.getGioBatDau().toString());
        row.createCell(10).setCellValue(lop.getGioKetThuc() == null ? "" : lop.getGioKetThuc().toString());
        row.createCell(11).setCellValue(lop.getNamHoc() == null ? "" : lop.getNamHoc());
        row.createCell(12).setCellValue(lop.getHocKy());
        row.createCell(13).setCellValue(empty(lop.getPhongHoc()));
        row.createCell(14).setCellValue(empty(lop.getGiangVien()));
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
    public ThoiKhoaBieuDTO getScheduleByDate(String maSv, LocalDate date) {
        LocalDate weekEnd = date.plusDays(6);

        List<DangKyLopHocPhan> dangKyList =
                dangKyRepository.findBySinhVien_MaSv(maSv);

        List<LopHocPhan> lopHocPhanList = dangKyList.stream()
                .map(DangKyLopHocPhan::getLopHocPhan)
                .filter(lhp -> isInWeek(lhp, date, weekEnd))
                .toList();

        ThoiKhoaBieuDTO dto = new ThoiKhoaBieuDTO();
        dto.setLopHocPhanDTOList(lopHocPhanMapper.entityToDTO(lopHocPhanList));

        return dto;
    }
    private boolean isInWeek(LopHocPhan lhp, LocalDate weekStart, LocalDate weekEnd) {
        if (lhp.getNgayBatDau() == null || lhp.getNgayKetThuc() == null) {
            return false;
        }

        return !lhp.getNgayBatDau().isAfter(weekEnd)
                && !lhp.getNgayKetThuc().isBefore(weekStart);
    }

    @Transactional
    public void nhapExcelThoiKhoaBieu(MultipartFile file, String maSv) throws Exception {

        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        DataFormatter formatter = new DataFormatter();

        SinhVien sinhVien = sinhVienRepository.findByMaSv(maSv);
        if (sinhVien == null) {
            throw new RuntimeException("Không tìm thấy sinh viên");
        }

        ThoiKhoaBieu thoiKhoaBieu = new ThoiKhoaBieu("CHINH_THUC");
        thoiKhoaBieu.setSinhVien(sinhVien);
        thoiKhoaBieu = thoiKhoaBieuRepository.save(thoiKhoaBieu);

        List<LopHocPhan> lopHocPhanList = new ArrayList<>();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            String maLopHP = formatter.formatCellValue(row.getCell(1));
            if (maLopHP == null || maLopHP.isBlank()) continue;

            LopHocPhan lopHocPhan = lopHocPhanRepository.findByMaLopHP(maLopHP);

            if (lopHocPhan == null) {
                throw new RuntimeException("Không tìm thấy lớp học phần: " + maLopHP);
            }

            lopHocPhanList.add(lopHocPhan);
        }

        validateKhongTrungLich(lopHocPhanList);

        for (LopHocPhan lopHocPhan : lopHocPhanList) {
            chiTietThoiKhoaBieuRepository.save(
                    new ChiTietThoiKhoaBieu(thoiKhoaBieu, lopHocPhan)
            );

            if (!dangKyRepository.existsBySinhVien_IdSvAndLopHocPhan_IdLopHP(
                    sinhVien.getIdSv(),
                    lopHocPhan.getIdLopHP()
            )) {
                DangKyLopHocPhan dangKy =
                        new DangKyLopHocPhan(sinhVien, lopHocPhan);

                dangKyRepository.save(dangKy);
            }
        }
    }

}
