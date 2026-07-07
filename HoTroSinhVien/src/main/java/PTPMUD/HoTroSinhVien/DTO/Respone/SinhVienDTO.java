package PTPMUD.HoTroSinhVien.DTO.Respone;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SinhVienDTO {

    private String maSv;
    private String hoTen;
    private LocalDate ngaySinh;
    private Boolean gioiTinh;
    private String email;
    private String soDienThoai;
    private String lop;
    private String cccd;
    private String diaChi;
    private String nganh;
    private String khoa;
    private int namNhapHoc;
}
