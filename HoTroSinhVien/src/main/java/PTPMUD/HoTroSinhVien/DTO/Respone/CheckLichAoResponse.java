package PTPMUD.HoTroSinhVien.DTO.Respone;

import PTPMUD.HoTroSinhVien.Entity.LopHocPhan;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CheckLichAoResponse {
    private boolean valid;
    private String message;
    private LopHocPhanDTO lopHocPhanDTO;
}