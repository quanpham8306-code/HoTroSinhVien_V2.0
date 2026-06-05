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
    private String trangThai; // DAT, KHONG_DAT, CHUA_CO_DIEM

    public Diem(DangKyLopHocPhan dangKyLopHocPhan, double diemQuaTrinh, double diemCuoiKy, double diemHocPhan, String trangThai) {
        this.dangKyLopHocPhan = dangKyLopHocPhan;
        this.diemQuaTrinh = diemQuaTrinh;
        this.diemCuoiKy = diemCuoiKy;
        this.diemHocPhan = diemHocPhan;
        this.trangThai = trangThai;
    }

    private void setTrangThai(){
        if(diemHocPhan < 4)
            this.trangThai = "Trượt";
        else
            this.trangThai = "Qua môn";
    }
}
