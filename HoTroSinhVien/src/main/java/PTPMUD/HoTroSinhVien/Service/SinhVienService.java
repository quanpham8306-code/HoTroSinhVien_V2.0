package PTPMUD.HoTroSinhVien.Service;

import PTPMUD.HoTroSinhVien.Entity.SinhVien;
import PTPMUD.HoTroSinhVien.Entity.TaiKhoan;
import PTPMUD.HoTroSinhVien.Repository.SinhVienRepository;
import PTPMUD.HoTroSinhVien.Repository.TaiKhoanRepository;
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

    public SinhVien createSinhVien(SinhVien sinhVien) {

        SinhVien savedSinhVien = sinhVienRepository.save(sinhVien);

        String maSv = String.format("SV%03d", savedSinhVien.getIdSv());
        savedSinhVien.setMaSv(maSv);

        savedSinhVien = sinhVienRepository.save(savedSinhVien);

        TaiKhoan taiKhoan = new TaiKhoan();

        taiKhoan.setUsername(maSv);
        taiKhoan.setPassword(passwordEncoder.encode("1"));
        taiKhoan.setRole("STUDENT");
        taiKhoan.setSinhVien(savedSinhVien);

        taiKhoanRepository.save(taiKhoan);

        return savedSinhVien;
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
}

