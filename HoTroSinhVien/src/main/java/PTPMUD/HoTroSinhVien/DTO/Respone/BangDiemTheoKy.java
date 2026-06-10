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
public class BangDiemTheoKy {
    private int hocKy;
    private List<BangDiemDTO> bangDiemDTOS;
}
