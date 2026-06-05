package PTPMUD.HoTroSinhVien.DTO.Respone;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DiemDTO {
    private String mon;
    private double diemQuaTrinh;
    private double diemCuoiKy;
    private double diemHocPhan;
    private String trangThai;
}
