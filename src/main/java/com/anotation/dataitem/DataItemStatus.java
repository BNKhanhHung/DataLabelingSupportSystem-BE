package com.anotation.dataitem;

public enum DataItemStatus {
    NEW, // Vừa được upload, chưa giao cho ai
    ASSIGNED, // Đã giao cho Annotator
    ANNOTATED, // Annotator đã hoàn thành gán nhãn
    REVIEWED // Reviewer đã duyệt xong
}
