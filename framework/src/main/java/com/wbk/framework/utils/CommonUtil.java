package com.wbk.framework.utils;

import java.util.List;

public class CommonUtil {

    public static boolean isNotEmpty(List list) {
        return list != null && list.size() > 0;
    }
}
