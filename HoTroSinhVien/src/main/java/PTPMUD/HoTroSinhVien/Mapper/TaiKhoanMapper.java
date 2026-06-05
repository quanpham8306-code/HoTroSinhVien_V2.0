package PTPMUD.HoTroSinhVien.Mapper;

import PTPMUD.HoTroSinhVien.DTO.Respone.TaiKhoanDTO;
import PTPMUD.HoTroSinhVien.Entity.TaiKhoan;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TaiKhoanMapper {
    TaiKhoanDTO entityToDto(TaiKhoan entity);
    void updateTaiKhoan(@MappingTarget TaiKhoan oldTaiKhoan , TaiKhoan newTaiKhoan);
}
