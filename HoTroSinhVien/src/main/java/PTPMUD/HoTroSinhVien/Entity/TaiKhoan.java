package PTPMUD.HoTroSinhVien.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "TaiKhoan")
public class TaiKhoan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idTaiKhoan;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role; // ADMIN, STUDENT

    @OneToOne
    @JoinColumn(name = "idSv")
    private SinhVien sinhVien;

    public TaiKhoan(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
}
