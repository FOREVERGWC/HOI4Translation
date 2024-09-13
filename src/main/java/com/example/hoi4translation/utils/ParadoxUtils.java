package com.example.hoi4translation.utils;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.example.hoi4translation.domain.vo.StringVO;

import java.util.Arrays;
import java.util.List;

public class ParadoxUtils {
    public static List<StringVO> getString(JSONObject json, List<StringVO> list, String currentPath, String... targetKey) {
        json.forEach((key, value) -> processJsonObject(key, value, targetKey, list, currentPath));
        return list;
    }

    private static void processJsonObject(String key, Object value, String[] targetKey, List<StringVO> list, String currentPath) {
        if (value instanceof String stringValue && Arrays.stream(targetKey).anyMatch(target -> StrUtil.equals(key, target))) {
            if (ReUtil.isMatch("\"[^\"]*\"", stringValue) && !stringValue.contains("_")) {
                String val = StrUtil.removeSuffix(StrUtil.removePrefix(stringValue, "\""), "\"").trim();
                list.add(StringVO.builder().key(currentPath + "|" + key).original(val).build());
            }
        }

        String newPath = currentPath.isEmpty() ? key : currentPath + "|" + key;
        if (value instanceof JSONObject) {
            getString((JSONObject) value, list, newPath, targetKey);
        } else if (value instanceof JSONArray) {
            processJsonArray((JSONArray) value, list, newPath, targetKey);
        }
    }

    private static void processJsonArray(JSONArray jsonArray, List<StringVO> list, String currentPath, String... targetKey) {
        String path = StrUtil.subAfter(currentPath, "|", true);
        for (Object object : jsonArray) {
            String key = StrUtil.removeSuffix(StrUtil.removePrefix(String.valueOf(object), "\""), "\"").trim();
            if (Arrays.stream(targetKey).anyMatch(target -> StrUtil.equals(path, target)) || currentPath.contains("|ordered|")) {
                list.add(StringVO.builder().key(currentPath + "|" + key).original(key).build());
            }
        }
    }
}
