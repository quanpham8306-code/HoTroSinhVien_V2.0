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
public class ChiTietThoiKhoaBieu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idChiTietThoiKhoaBieu;

    @ManyToOne
    @JoinColumn(name = "idTKB")
    private ThoiKhoaBieu thoiKhoaBieu;

    @ManyToOne
    @JoinColumn(name = "idLopHP")
    private LopHocPhan lopHocPhan;

    public ChiTietThoiKhoaBieu(ThoiKhoaBieu thoiKhoaBieu, LopHocPhan lopHocPhan) {
        this.thoiKhoaBieu = thoiKhoaBieu;
        this.lopHocPhan = lopHocPhan;
    }
}
