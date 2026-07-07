package PTPMUD.HoTroSinhVien.DTO.Request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateSinhVienDTO {
    private String hoTen;
    private LocalDate ngaySinh;
    private Boolean gioiTinh;
    private String email;
    private String soDienThoai;
    private String lop;
    private String cccd;
    private String diaChi;
    private String nganh;
    private int namNhapHoc;
}
