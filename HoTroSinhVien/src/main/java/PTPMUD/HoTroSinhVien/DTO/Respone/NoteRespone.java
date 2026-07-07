package PTPMUD.HoTroSinhVien.DTO.Respone;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NoteRespone {
    private int id;
    private String title;
    private LocalDate date;
    private String tag;
    private String note;
}
