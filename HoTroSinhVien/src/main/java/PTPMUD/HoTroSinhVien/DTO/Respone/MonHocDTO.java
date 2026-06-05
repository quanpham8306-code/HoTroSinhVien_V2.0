package PTPMUD.HoTroSinhVien.DTO.Respone;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MonHocDTO {
    private String tenMonHoc;

    private Integer soTinChi;
}
