package PTPMUD.HoTroSinhVien.Mapper;

import PTPMUD.HoTroSinhVien.DTO.Request.CreateSinhVienDTO;
import PTPMUD.HoTroSinhVien.DTO.Respone.SinhVienDTO;
import PTPMUD.HoTroSinhVien.Entity.SinhVien;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SinhVienMapper {
    SinhVienDTO entityToDto(SinhVien sinhVien);
    SinhVien dtoToEntity(CreateSinhVienDTO dto);
    void updateSinhVien(@MappingTarget SinhVien oldSinhVien, CreateSinhVienDTO newSinhVien);
}
