package PTPMUD.HoTroSinhVien.Repository;

import PTPMUD.HoTroSinhVien.Entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Integer> {
    Note findByTitleAndSinhVien_MaSv(String  title, String maSv );
    List<Note> findBySinhVien_MaSv(String MaSv);
}
