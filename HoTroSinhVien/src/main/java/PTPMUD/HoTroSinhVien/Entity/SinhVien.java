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

    @Column(nullable = false)
    private String hoTen;

    private LocalDate ngaySinh;

    private Boolean gioiTinh;
    @Email
    private String email;

    private String soDienThoai;

    private String lop;

    private String cccd;

    private String diaChi;

    @OneToOne(mappedBy = "sinhVien")
    private TaiKhoan taiKhoan;

    public SinhVien(String hoTen, LocalDate ngaySinh, Boolean gioiTinh, String email, String soDienThoai, String lop, String cccd, String diaChi) {
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.lop = lop;
        this.cccd = cccd;
        this.diaChi = diaChi;
    }

}
