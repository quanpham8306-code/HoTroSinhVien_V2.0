package PTPMUD.HoTroSinhVien.DTO.Respone;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    private int thu;

    private LocalTime gioBatDau;

    private LocalTime gioKetThuc;


    private LocalDate ngayBatDau;

    private LocalDate ngayKetThuc;

    private Integer siSoToiDa;

    private int hocKy;

    private String namHoc;

    private String nganh;

    private String khoa;
}
