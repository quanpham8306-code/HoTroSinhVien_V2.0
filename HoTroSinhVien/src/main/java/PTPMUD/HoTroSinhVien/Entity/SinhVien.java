package PTPMUD.HoTroSinhVien.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "SinhVien")
public class SinhVien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idSv;

    @Column(unique = true)
    private String maSv;

    @Column(nullable = false,columnDefinition = "NVARCHAR(100)")
    private String hoTen;

    private LocalDate ngaySinh;

    private Boolean gioiTinh;
    @Email
    private String email;

    private String soDienThoai;

    private String lop;

    private String cccd;
    @Column(columnDefinition = "NVARCHAR(100)")
    private String diaChi;
    @Column(nullable = false,columnDefinition = "NVARCHAR(100)")
    private String nganh;
    @Column(nullable = false)
    private int namNhapHoc;

    @OneToOne(mappedBy = "sinhVien")
    private TaiKhoan taiKhoan;

    private int sttTrongNganh;

    public SinhVien(String hoTen, LocalDate ngaySinh, Boolean gioiTinh, String email, String soDienThoai, String lop, String cccd, String diaChi, String nganh, int namNhapHoc) {
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.lop = lop;
        this.cccd = cccd;
        this.diaChi = diaChi;
        this.nganh = nganh;
        this.namNhapHoc = namNhapHoc;
    }
}
