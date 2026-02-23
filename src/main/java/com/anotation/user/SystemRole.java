package com.anotation.user;

/**
 * System-level role — stored in User entity.
 *
 * - USER : default role, can be assigned project roles
 * (Manager/Annotator/Reviewer)
 * - ADMIN : manages users & roles only (NOT project resources)
 */
public enum SystemRole {
    USER,
    ADMIN
}
