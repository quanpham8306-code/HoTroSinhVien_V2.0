package PTPMUD.HoTroSinhVien.Controller.Student;

import PTPMUD.HoTroSinhVien.DTO.Request.CreateDiemDTO;
import PTPMUD.HoTroSinhVien.DTO.Request.NoteRequest;
import PTPMUD.HoTroSinhVien.DTO.ResponseObject;
import PTPMUD.HoTroSinhVien.Entity.DangKyLopHocPhan;
import PTPMUD.HoTroSinhVien.Entity.Diem;
import PTPMUD.HoTroSinhVien.Entity.Note;
import PTPMUD.HoTroSinhVien.Entity.SinhVien;
import PTPMUD.HoTroSinhVien.Mapper.NoteMapper;
import PTPMUD.HoTroSinhVien.Repository.NoteRepository;
import PTPMUD.HoTroSinhVien.Repository.SinhVienRepository;
import PTPMUD.HoTroSinhVien.Security.crypto.StringCryptoConverter;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping(path = "/api/student/note")
public class StudentNoteController {

    NoteRepository noteRepository;
    NoteMapper noteMapper;
    SinhVienRepository sinhVienRepository;
    @GetMapping("/me")
    ResponseEntity<ResponseObject> getMyNotes(Authentication authentication){
        List<Note> notes = noteRepository.findBySinhVien_MaSv(authentication.getName());
        return (notes != null) ?
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("ok","Query student successfully.",noteMapper.entityToDto(notes))
                ):
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("false","Can not found student. ","")
                );
    }
    @GetMapping("/{title}")
    ResponseEntity<ResponseObject> getMyNoteByTitle(
            Authentication authentication,
            @PathVariable String title
    ){
        Note note = noteRepository.findByTitleAndSinhVien_MaSv(title,authentication.getName());
        return (note != null) ?
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("ok","Query student successfully.",noteMapper.entityToDto(note))
                ):
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("false","Can not found student. ","")
                );
    }
    @PostMapping("/addNote")
    ResponseEntity<ResponseObject> addNote(
            Authentication authentication,
            @RequestBody NoteRequest noteRequest
    ){
        Note oldNote = noteRepository.findByTitleAndSinhVien_MaSv(noteRequest.getTitle(),authentication.getName());

        if(oldNote != null){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ResponseObject("false","Note already exists.","")
            );
        }
        else {
            Note note = noteMapper.dtoToEntity(noteRequest);
            SinhVien sinhVien = sinhVienRepository.findByMaSv(authentication.getName());
            note.setSinhVien(sinhVien);
            noteRepository.save(note);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(
                            "ok",
                            "Note added successfully.",
                            ""
                    )
            );
        }
    }


    @PutMapping("/update")
    ResponseEntity<ResponseObject> updateNote(
            Authentication authentication,
            @RequestBody NoteRequest noteRequest
    ) {
        Note oldNote = noteRepository.findByTitleAndSinhVien_MaSv(noteRequest.getTitle(),authentication.getName());
        if(oldNote==null){
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new ResponseObject("false","Note not exists.","")
            );
        }
        else {
            noteMapper.updateNote(oldNote, noteRequest);
            noteRepository.save(oldNote);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(
                            "ok",
                            "Note already update",
                            ""
                    )
            );
        }
    }
    @DeleteMapping("/delete")
    @Transactional
    ResponseEntity<ResponseObject> deleteNote(
            Authentication authentication,
            @RequestBody NoteRequest noteRequest
    ){
        Note oldNote = noteRepository.findById(noteRequest.getId());
        if(oldNote==null){
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new ResponseObject("false","Note not exists.","")
            );
        }
        else {
            noteRepository.delete(oldNote);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(
                            "ok",
                            "Note delete successfully",
                            ""
                    )
            );
        }
    }
}