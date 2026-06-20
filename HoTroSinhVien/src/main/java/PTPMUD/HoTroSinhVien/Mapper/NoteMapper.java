package PTPMUD.HoTroSinhVien.Mapper;

import PTPMUD.HoTroSinhVien.DTO.Request.NoteRequest;
import PTPMUD.HoTroSinhVien.DTO.Respone.NoteRespone;
import PTPMUD.HoTroSinhVien.Entity.Note;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NoteMapper {
    NoteRespone entityToDto(Note entity);
    Note dtoToEntity(NoteRequest dto);
    List<NoteRespone> entityToDto(List<Note> entity);
    Note updateNote(@MappingTarget Note old, NoteRequest dto);
}
