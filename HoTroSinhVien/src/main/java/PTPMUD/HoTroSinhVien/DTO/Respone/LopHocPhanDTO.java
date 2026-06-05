package PTPMUD.HoTroSinhVien.DTO.Respone;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LopHocPhanDTO {
    private String maLopHP;

    private String tenMonHoc;

    private String giangVien;

    private String phongHoc;

    private String thu;

    private LocalTime gioBatDau;

    private LocalTime gioKetThuc;

    private LocalDate ngayBatDau;

    private LocalDate ngayKetThuc;

    private Integer siSoToiDa;

    private String hocKy;

    private String namHoc;

}
