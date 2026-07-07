package PTPMUD.HoTroSinhVien.Service;

import PTPMUD.HoTroSinhVien.Entity.DangKyLopHocPhan;
import PTPMUD.HoTroSinhVien.Entity.SinhVien;
import PTPMUD.HoTroSinhVien.Entity.TaiKhoan;
import PTPMUD.HoTroSinhVien.Entity.LichAo;
import PTPMUD.HoTroSinhVien.Repository.DangKyLopHocPhanRepository;
import PTPMUD.HoTroSinhVien.Repository.LichAoRepository;
import PTPMUD.HoTroSinhVien.Repository.SinhVienRepository;
import PTPMUD.HoTroSinhVien.Repository.TaiKhoanRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SinhVienService {
    PasswordEncoder passwordEncoder;
    SinhVienRepository sinhVienRepository;
    TaiKhoanRepository taiKhoanRepository;
    private final DangKyLopHocPhanService dangKyLopHocPhanService;
    private final DangKyLopHocPhanRepository dangKyLopHocPhanRepository;
    LichAoRepository  lichAoRepository;

    @Transactional
    public SinhVien createSinhVien(SinhVien sinhVien) {

        String nganh = sinhVien.getNganh().trim();
        sinhVien.setNganh(nganh);

        int namNhapHoc = sinhVien.getNamNhapHoc();

        if (namNhapHoc < 2000 || namNhapHoc > 2100) {
            throw new RuntimeException("Năm nhập học không hợp lệ");
        }

        String maNganh = getMaNganh(nganh);
        String namShort = String.valueOf(namNhapHoc).substring(2);

        String prefix = maNganh + namShort;

        int nextStt = getNextSttByPrefix(prefix);
        String maSv = prefix + String.format("%03d", nextStt);

        while (
                sinhVienRepository.existsByMaSv(maSv)
                        || taiKhoanRepository.existsByUsername(maSv)
        ) {
            nextStt++;
            maSv = prefix + String.format("%03d", nextStt);
        }

        sinhVien.setSttTrongNganh(nextStt);
        sinhVien.setMaSv(maSv);

        TaiKhoan taiKhoan = new TaiKhoan(
                sinhVien.getMaSv(),
                passwordEncoder.encode("1"),
                "STUDENT"
        );

        sinhVien.setTaiKhoan(taiKhoan);
        taiKhoanRepository.save(taiKhoan);

        return sinhVienRepository.save(sinhVien);
    }

    private int getNextSttByPrefix(String prefix) {
        return sinhVienRepository
                .findTopByMaSvStartingWithOrderByMaSvDesc(prefix)
                .map(sv -> {
                    String maSv = sv.getMaSv();

                    if (maSv == null || maSv.length() <= prefix.length()) {
                        return 1;
                    }

                    String sttText = maSv.substring(prefix.length());

                    try {
                        return Integer.parseInt(sttText) + 1;
                    } catch (NumberFormatException e) {
                        return 1;
                    }
                })
                .orElse(1);
    }


    private String getMaNganh(String nganh) {
        return switch (nganh.trim().toLowerCase()) {
            case "công nghệ thông tin" -> "CNTT";
            case "an toàn thông tin" -> "ATTT";
            case "điện tử viễn thông" -> "DTVT";
            default -> getFirstLetters(nganh);
        };
    }

    private String getFirstLetters(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }

        String normalized = removeVietnameseAccent(text);
        StringBuilder result = new StringBuilder();

        String[] words = normalized.trim().split("\\s+");

        for (String word : words) {
            result.append(Character.toUpperCase(word.charAt(0)));
        }

        return result.toString();
    }

    private String removeVietnameseAccent(String text) {
        String normalized = java.text.Normalizer.normalize(
                text,
                java.text.Normalizer.Form.NFD
        );

        return normalized
                .replaceAll("\\p{M}", "")
                .replace("Đ", "D")
                .replace("đ", "d");
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
            return 0;
        }

        try {
            String value = formatter.formatCellValue(cell).trim();

            if (value.isBlank()) {
                return 0;
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

    private LocalDate getDateCell(Row row, int index) {

        Cell cell = row.getCell(index);

        if (cell == null) {
            return null;
        }

        try {

            if (!DateUtil.isCellDateFormatted(cell)) {
                return null;
            }

            return cell
                    .getLocalDateTimeCellValue()
                    .toLocalDate();

        } catch (Exception e) {

            return null;
        }
    }



    public List<String> importExcelSinhVien(MultipartFile file) {

        List<String> errors = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {

            DataFormatter formatter = new DataFormatter();
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);

                if (row == null) {
                    continue;
                }

                try {

                    SinhVien sinhVien = new SinhVien();
                    String hoTen = getStringCell(row, 0, formatter);
                    if (hoTen.isBlank()) {
                        throw new RuntimeException("Họ tên không được để trống");
                    }
                    sinhVien.setHoTen(hoTen);


                    LocalDate ngaySinh = getDateCell(row, 1);
                    sinhVien.setNgaySinh(ngaySinh);

                    String gt = getStringCell(row, 2, formatter);
                    if (gt.equalsIgnoreCase("Nam")) {
                        sinhVien.setGioiTinh(true);
                    } else if (gt.equalsIgnoreCase("Nữ")
                            || gt.equalsIgnoreCase("Nu")) {
                        sinhVien.setGioiTinh(false);
                    } else {
                        throw new RuntimeException(
                                "Giới tính chỉ được nhập Nam hoặc Nữ");
                    }


                    String email = getStringCell(row, 3, formatter);
                    if (!email.matches(
                            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                        email="";
                        errors.add("Dòng " + (i + 1) + ":Email chưa đúng định dạng");
                    }
                    sinhVien.setEmail(email);


                    String lop = getStringCell(row, 4, formatter);
                    sinhVien.setLop(lop);

                    String sdt = getStringCell(row, 5, formatter);
                    if (!sdt.matches("\\d{10,11}")) {
                        sdt="";
                        errors.add("Dòng " + (i + 1) + ":Số điện thoại chưa đúng định dạng");
                    }
                    sinhVien.setSoDienThoai(sdt);


                    String cccd = getStringCell(row, 6, formatter);
                    if (!cccd.matches("\\d{12}")) {
                        cccd="";
                        errors.add("Dòng " + (i + 1) + ":Số căn cước phải đủ 12 số");
                    }
                    sinhVien.setCccd(cccd);


                    sinhVien.setDiaChi(
                            getStringCell(row, 7, formatter)
                    );
                    String nganh = getStringCell(row, 8, formatter);
                    if (nganh.isBlank()) {
                        throw new RuntimeException("Ngành không được để trống");
                    }
                    sinhVien.setNganh(nganh);

                    int namNhapHoc = getIntCell(row, 9, formatter);
                    if (namNhapHoc < 2000 || namNhapHoc > 2100) {
                        throw new RuntimeException("Năm nhập học không hợp lệ");
                    }
                    sinhVien.setNamNhapHoc(namNhapHoc);


                    if ((!cccd.isBlank()) && sinhVienRepository.existsSinhVienByCccd(cccd)  ) {
                        throw new RuntimeException(
                                "CCCD đã tồn tại");
                    }

                    if ( !email.isBlank() &&sinhVienRepository.existsSinhVienByEmail(email) ) {
                        throw new RuntimeException(
                                "Email đã tồn tại");
                    }


                    createSinhVien(sinhVien);

                } catch (Exception e) {

                    errors.add("Dòng " + (i + 1) + " lỗi : " + e.getMessage());
                }
            }

        } catch (Exception e) {

            throw new RuntimeException("Không thể đọc file Excel", e);
        }

        return errors;
    }
    @Transactional
    public void delete(String maSv) {

        SinhVien sinhVien = sinhVienRepository.findByMaSv(maSv);

        if (sinhVien == null) {
            throw new RuntimeException("Sinh viên không tồn tại");
        }

        List<DangKyLopHocPhan> dangKyList =
                dangKyLopHocPhanRepository.findBySinhVien_MaSv(maSv);

        dangKyLopHocPhanRepository.deleteAll(dangKyList);

        LichAo lichAo=lichAoRepository.findBySinhVien_MaSv(maSv);
        lichAoRepository.delete(lichAo);

        taiKhoanRepository.findByUsername(maSv)
                .ifPresent(taiKhoanRepository::delete);

        sinhVienRepository.delete(sinhVien);
    }
}

