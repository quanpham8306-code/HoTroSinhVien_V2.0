package PTPMUD.HoTroSinhVien.DTO.Request;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Data
@Setter
@Getter
@NoArgsConstructor
public class CheckLichAoRequest {
    private List<String> selectedMaLopHPs;
    private String newMaLop;
}