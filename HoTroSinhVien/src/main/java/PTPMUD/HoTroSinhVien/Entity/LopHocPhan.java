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
    @Column(nullable = false,columnDefinition = "NVARCHAR(100)")
    private String giangVien;

    private String phongHoc;

    private int thu;

    private LocalTime gioBatDau;

    private LocalTime gioKetThuc;

    private LocalDate ngayBatDau;

    private LocalDate ngayKetThuc;

    private int siSoToiDa;

    private int hocKy;
    @Column(nullable = true)
    private String khoa;
    private String namHoc;
    @Column(nullable = false,columnDefinition = "NVARCHAR(100)")
    private String nganh;
    @ManyToOne
    @JoinColumn(name = "idMon")
    private MonHoc monHoc;



    public LopHocPhan(String giangVien, String phongHoc, int thu, LocalTime gioBatDau, LocalTime gioKetThuc, LocalDate ngayBatDau, LocalDate ngayKetThuc, int siSoToiDa, int hocKy, String khoa, MonHoc monHoc,String nganh,String namHoc) {
        this.giangVien = giangVien;
        this.phongHoc = phongHoc;
        this.thu = thu;
        this.gioBatDau = gioBatDau;
        this.gioKetThuc = gioKetThuc;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.siSoToiDa = siSoToiDa;
        this.hocKy = hocKy;
        this.khoa = khoa;
        this.monHoc = monHoc;
        this.nganh=nganh;
        this.namHoc=namHoc;
    }
}


