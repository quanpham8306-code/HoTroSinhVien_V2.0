package PTPMUD.HoTroSinhVien.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @JoinColumn(name = "id")
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
    private String trangThai; // DAT, KHONG_DAT, CHUA_CO_DIEM

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
        this.diemHocPhan = diemCuoiKy*0.7+diemHocPhan*0.3;
    }
    private void setTrangThai(){
        if(diemHocPhan < 4)
            this.trangThai = "Trượt";
        else
            this.trangThai = "Qua môn";
    }
    private String setDiemChu()
    {
        if (this.diemHocPhan >= 9.0) {
            diemChu = "A+";
        } else if (this.diemHocPhan >= 8.5) {
            diemChu = "A";
        } else if (this.diemHocPhan >= 7.8) {
            diemChu = "B+";
        } else if (this.diemHocPhan >= 7.0) {
            diemChu = "B";
        } else if (this.diemHocPhan >= 6.2) {
            diemChu = "C+";
        } else if (this.diemHocPhan >= 5.5) {
            diemChu = "C";
        } else if (this.diemHocPhan >= 5.0) {
            diemChu = "D+";
        } else if (this.diemHocPhan>= 4.0) {
            diemChu = "D";
        } else {
            diemChu = "F";
        }
        return diemChu;
    }
}
