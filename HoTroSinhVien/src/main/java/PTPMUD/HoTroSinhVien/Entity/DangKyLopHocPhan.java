package PTPMUD.HoTroSinhVien.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Data
@NoArgsConstructor
public class DangKyLopHocPhan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "id_sv")
    private SinhVien sinhVien;

    @ManyToOne
    @JoinColumn(name = "id_lop_hp")
    private LopHocPhan lopHocPhan;

    public DangKyLopHocPhan(SinhVien sinhVien, LopHocPhan lopHocPhan) {
        this.sinhVien = sinhVien;
        this.lopHocPhan = lopHocPhan;
    }
}
