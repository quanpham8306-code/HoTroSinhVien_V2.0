package PTPMUD.HoTroSinhVien.Entity;

import PTPMUD.HoTroSinhVien.Security.crypto.StringCryptoConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "Note")
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(unique = true)
    @Convert(converter = StringCryptoConverter.class)
    private String title;
    private LocalDate date;
    @Convert(converter = StringCryptoConverter.class)
    private String tag;
    @Convert(converter = StringCryptoConverter.class)
    private String note;
    @ManyToOne
    @JoinColumn(name = "id_sv")
    private SinhVien sinhVien;
}
