package PTPMUD.HoTroSinhVien.Mapper;

import PTPMUD.HoTroSinhVien.DTO.Respone.LopHocPhanDTO;
import PTPMUD.HoTroSinhVien.Entity.LopHocPhan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LopHocPhanMapper {
    LopHocPhan dtoToEntity(LopHocPhanDTO dto);
    @Mapping(source = "monHoc.tenMonHoc", target = "tenMonHoc")
    LopHocPhanDTO entityToDto(LopHocPhan lopHocPhan);
    void updateLPH(@MappingTarget LopHocPhan oldLPH, LopHocPhan newLPH);
    List<LopHocPhanDTO> entityToDTO(List<LopHocPhan> lph);
}
