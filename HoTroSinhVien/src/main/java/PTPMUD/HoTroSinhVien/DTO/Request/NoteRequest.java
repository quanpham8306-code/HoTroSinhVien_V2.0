package PTPMUD.HoTroSinhVien.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NoteRequest {
    private String title;
    private LocalDate date;
    private String tag;
    private String note;
}
