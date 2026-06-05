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
    private String loaiLich; // CHINH_THUC, LICH_AO

    @Builder.Default
    private List<LopHocPhanDTO> lopHocPhanDTOList = new ArrayList<>();
}
