package com.yandex.practicum.utils;

public class AccessControl {
    protected static boolean changesAllowed = false;

    protected static boolean allow() {
        changesAllowed = true;
        return changesAllowed;
    }

    protected static boolean prohibit() {
        changesAllowed = false;
        return changesAllowed;
    }
}
