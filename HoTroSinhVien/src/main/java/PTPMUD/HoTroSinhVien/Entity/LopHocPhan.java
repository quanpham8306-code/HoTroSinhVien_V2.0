package PTPMUD.HoTroSinhVien.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "LopHocPhan")
public class LopHocPhan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idLopHP;

    @Column(unique = true)
    private String maLopHP;

    private String giangVien;

    private String phongHoc;

    private String thu;

    private LocalTime gioBatDau;

    private LocalTime gioKetThuc;

    private LocalDate ngayBatDau;

    private LocalDate ngayKetThuc;

    private int siSoToiDa;

    private String hocKy;

    private String namHoc;

    @ManyToOne
    @JoinColumn(name = "idMon")
    private MonHoc monHoc;



    public LopHocPhan(String giangVien, String phongHoc, String thu, LocalTime gioBatDau, LocalTime gioKetThuc, LocalDate ngayBatDau, LocalDate ngayKetThuc, int siSoToiDa, String hocKy, String namHoc, MonHoc monHoc) {
        this.giangVien = giangVien;
        this.phongHoc = phongHoc;
        this.thu = thu;
        this.gioBatDau = gioBatDau;
        this.gioKetThuc = gioKetThuc;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.siSoToiDa = siSoToiDa;
        this.hocKy = hocKy;
        this.namHoc = namHoc;
        this.monHoc = monHoc;
    }
}


