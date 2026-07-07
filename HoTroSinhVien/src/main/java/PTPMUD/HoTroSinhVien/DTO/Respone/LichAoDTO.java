package PTPMUD.HoTroSinhVien.DTO.Respone;

import PTPMUD.HoTroSinhVien.Entity.LopHocPhan;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LichAoDTO {

    private List<LopHocPhanDTO> lopHocPhanDTOList=new ArrayList<>();
}
