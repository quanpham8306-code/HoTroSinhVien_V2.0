package PTPMUD.HoTroSinhVien.DTO.Respone;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThoiKhoaBieuDTO {

    private int ky;

    private String namHoc;

    @Builder.Default
    private List<LopHocPhanDTO> lopHocPhanDTOList = new ArrayList<>();
}
