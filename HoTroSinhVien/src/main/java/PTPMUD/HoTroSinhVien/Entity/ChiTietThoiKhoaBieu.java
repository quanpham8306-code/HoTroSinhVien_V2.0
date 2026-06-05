package PTPMUD.HoTroSinhVien.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ChiTietThoiKhoaBieu")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChiTietThoiKhoaBieu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idChiTietTKB;

    @ManyToOne
    @JoinColumn(name = "idTKB")
    private ThoiKhoaBieu thoiKhoaBieu;

    @ManyToOne
    @JoinColumn(name = "idLopHP")
    private LopHocPhan lopHocPhan;
}