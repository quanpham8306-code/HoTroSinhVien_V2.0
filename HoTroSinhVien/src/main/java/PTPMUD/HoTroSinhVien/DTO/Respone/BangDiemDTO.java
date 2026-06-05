package PTPMUD.HoTroSinhVien.DTO.Respone;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BangDiemDTO {
    private String maMon;
    private String tenMon;
    private int soTin;
    private double diemHocPhan;
    private String diemChu;
    private String trangThai;
}