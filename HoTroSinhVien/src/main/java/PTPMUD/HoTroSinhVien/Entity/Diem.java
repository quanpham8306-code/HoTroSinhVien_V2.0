package PTPMUD.HoTroSinhVien.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "Diem")
public class Diem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idDiem;
    @OneToOne
    @JoinColumn(name = "idDangKy")
    private DangKyLopHocPhan dangKyLopHocPhan;
    @Min(0)
    @Max(10)
    private double diemQuaTrinh;
    @Min(0)
    @Max(10)
    private double diemCuoiKy;
    @Min(0)
    @Max(10)
    private double diemHocPhan;
    private String diemChu;
    @Column(columnDefinition = "NVARCHAR(100)")
    private String trangThai;

    public Diem(DangKyLopHocPhan dangKyLopHocPhan, double diemQuaTrinh, double diemCuoiKy) {
        this.dangKyLopHocPhan = dangKyLopHocPhan;
        this.diemQuaTrinh = diemQuaTrinh;
        this.diemCuoiKy = diemCuoiKy;
    }
    @PrePersist
    @PreUpdate
    private void beforeSave() {
        tinhDiemHocPhan();
        setDiemChu();
        setTrangThai();
    }
    private void tinhDiemHocPhan() {
        double diem = diemCuoiKy * 0.7f + diemQuaTrinh * 0.3f;

        this.diemHocPhan = BigDecimal.valueOf(diem)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
    private void setTrangThai(){
        if(diemHocPhan >= 4)
            this.trangThai = "Đạt";
        else
            this.trangThai = "Không đạt";
    }
    private String setDiemChu()
    {
        if (this.diemHocPhan >= 8.5) {
            diemChu = "A";
        } else if (this.diemHocPhan >= 7.0) {
            diemChu = "B";
        } else if (this.diemHocPhan >= 5.2) {
            diemChu = "C";
        } else if (this.diemHocPhan>= 4.0) {
            diemChu = "D";
        } else {
            diemChu = "F";
        }
        return diemChu;
    }
}
