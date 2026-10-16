package com.citypulse.trip.entity;

/**
 * Vai trò của thành viên trong chuyến đi:
 * - Owner: toàn quyền quản lý, mời/xóa thành viên, duyệt/từ chối đề xuất, sửa chuyến đi.
 * - Contributor: có quyền xem chuyến đi và đề xuất địa điểm.
 * - Viewer: chỉ có quyền xem chuyến đi và thành viên.
 */
public enum TripRole {
    Owner,
    Contributor,
    Viewer;

    public boolean canPropose() {
        return this == Owner || this == Contributor;
    }

    public boolean isOwner() {
        return this == Owner;
    }
}

