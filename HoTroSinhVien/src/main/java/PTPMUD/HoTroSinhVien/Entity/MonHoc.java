package PTPMUD.HoTroSinhVien.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "MonHoc")
public class MonHoc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idMon;

    @Column(nullable =true)
    private String maMon;

    @Column(nullable = false,columnDefinition = "NVARCHAR(100)" )
    private String tenMonHoc;

    @Column(nullable = false)
    private int soTinChi;

    @OneToMany(mappedBy = "monHoc")
    private List<LopHocPhan> lopHocPhans = new ArrayList<>();

    public MonHoc(int soTinChi,  String tenMonHoc) {
        this.soTinChi = soTinChi;

        this.tenMonHoc = tenMonHoc;
    }
}
