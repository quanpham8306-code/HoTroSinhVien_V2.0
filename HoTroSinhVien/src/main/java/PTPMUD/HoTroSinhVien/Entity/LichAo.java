package PTPMUD.HoTroSinhVien.Entity;

import PTPMUD.HoTroSinhVien.Entity.SinhVien;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "LichAo")
public class LichAo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idLichAo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idSv", nullable = false)
    private SinhVien sinhVien;

    @Column(length = 1000)
    private String dsMaLopHP;

    public LichAo(String dsMaLopHP, SinhVien sinhVien) {
        this.dsMaLopHP = dsMaLopHP;
        this.sinhVien = sinhVien;
    }
}