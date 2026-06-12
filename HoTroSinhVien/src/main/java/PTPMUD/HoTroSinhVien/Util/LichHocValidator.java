package PTPMUD.HoTroSinhVien.Util;

import PTPMUD.HoTroSinhVien.Entity.LopHocPhan;

import java.util.List;

public final class LichHocValidator {

    private LichHocValidator() {
    }

    public static boolean trungLich(LopHocPhan a, LopHocPhan b) {
        if (a == null || b == null) {
            return false;
        }
        if (a.getThu() == b.getThu()) {
            return false;
        }
        if (a.getGioBatDau() == null || a.getGioKetThuc() == null
                || b.getGioBatDau() == null || b.getGioKetThuc() == null) {
            return false;
        }

        boolean trungGio = a.getGioBatDau().isBefore(b.getGioKetThuc())
                && b.getGioBatDau().isBefore(a.getGioKetThuc());

        return trungGio && trungNgay(a, b);
    }

    public static LopHocPhan timLopBiTrung(List<LopHocPhan> danhSachLop, LopHocPhan lopMoi) {
        return danhSachLop.stream()
                .filter(lop -> trungLich(lop, lopMoi))
                .findFirst()
                .orElse(null);
    }

    private static boolean trungNgay(LopHocPhan a, LopHocPhan b) {
        if (a.getNgayBatDau() == null || a.getNgayKetThuc() == null
                || b.getNgayBatDau() == null || b.getNgayKetThuc() == null) {
            return true;
        }
        return !a.getNgayKetThuc().isBefore(b.getNgayBatDau())
                && !b.getNgayKetThuc().isBefore(a.getNgayBatDau());
    }
}
