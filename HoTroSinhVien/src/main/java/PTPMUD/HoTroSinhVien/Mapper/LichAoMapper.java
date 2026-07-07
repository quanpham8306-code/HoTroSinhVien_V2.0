package PTPMUD.HoTroSinhVien.Mapper;

import PTPMUD.HoTroSinhVien.DTO.Respone.LichAoDTO;
import PTPMUD.HoTroSinhVien.Entity.LichAo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
@Mapper(componentModel = "spring")
public interface LichAoMapper {
    LichAoDTO entityToDTO(LichAo lichAo);
}
