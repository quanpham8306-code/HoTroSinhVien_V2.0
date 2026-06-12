package PTPMUD.HoTroSinhVien.DTO.Respone;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpcomingScheduleDTO {
    private String tenMonHoc;
    private String maLop;
    private String phongHoc;

    private int thu;
    private LocalDate ngayHocGanNhat;

    private LocalTime gioBatDau;
    private LocalTime gioKetThuc;
}
