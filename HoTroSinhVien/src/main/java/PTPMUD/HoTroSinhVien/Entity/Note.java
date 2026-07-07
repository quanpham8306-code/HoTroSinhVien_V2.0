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
    @Column(columnDefinition = "NVARCHAR(100)" )
    @Convert(converter = StringCryptoConverter.class)
    private String title;
    private LocalDate date;
    @Convert(converter = StringCryptoConverter.class)
    @Column(columnDefinition = "NVARCHAR(100)" )
    private String tag;
    @Convert(converter = StringCryptoConverter.class)
    @Column(columnDefinition = "NVARCHAR(2000)" )
    private String note;
    @ManyToOne
    @JoinColumn(name = "idSv")
    private SinhVien sinhVien;
}
