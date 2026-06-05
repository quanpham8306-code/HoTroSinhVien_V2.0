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
@Table(name = "ThoiKhoaBieu")
public class ThoiKhoaBieu {

    public static final String CHINH_THUC = "CHINH_THUC";
    public static final String LICH_AO = "LICH_AO";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idTKB;

    @Column(nullable = false, length = 30)
    private String loaiLich; // CHINH_THUC, LICH_AO

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idSv", nullable = false)
    private SinhVien sinhVien;

    @OneToMany()
    @JoinColumn(name = "id_lop_hp")
    private List <LopHocPhan> lopHocPhan;


    public ThoiKhoaBieu(String loaiLich) {
        this.loaiLich = loaiLich;
    }





}
