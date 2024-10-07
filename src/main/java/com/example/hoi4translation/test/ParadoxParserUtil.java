package com.example.hoi4translation.test;

import cn.hutool.core.text.CharPool;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class ParadoxParserUtil {
    private static final int EQUAL = '=';
    private static final int POUND = '#';
    private static final String splitter = "$$";

    public static JSONObject parse(List<String> contentList) {
        JSONObject json = new JSONObject();
        Stack<String> paths = new Stack<>();

        String content = contentList.stream()
                .map(line -> line.replaceAll("\t", " ")) // 空格代替制表符
                .map(line -> line.replaceAll("([\"\\s]\\w+?\")(\\w)", "$1 $2")) // 修复引号和字符串联
                .map(line -> line.replaceAll("\"([^\\\\]*?)\"", "\"$1\" ")) // 修复引号粘连 "Byung-il""Byung-soo""Byung-ok"
                .map(line -> line.replaceAll("(\\s=)(\\[.+])(\\s|$)", "$1\"$2\"$3")) // 修复方括号值 [From.GetID]
                .map(line -> StrUtil.subBefore(line, "#", false)) // 删除注释
                .map(StrUtil::trim) // 删除缩进
                .map(line -> line.replaceAll("<=", "= &lte;")) // 转义关系运算符
                .map(line -> line.replaceAll(">=", "= &gte;")) // 转义关系运算符
                .map(line -> line.replaceAll(">", "= &gt;")) // 转义关系运算符
                .map(line -> line.replaceAll("<", "= &lt;")) // 转义关系运算符
                .map(line -> line.replaceAll("==", "= &eqeq;")) // 转义关系运算符
                .filter(StrUtil::isNotBlank) // 过滤空行
                .collect(Collectors.joining("\n"));

        content += "\n"; // 添加额外空行

        StringBuilder token = new StringBuilder();

        boolean leftHand = true;
        boolean mayBeArray = false;
        boolean string = false;
        boolean escape = false;
        boolean comment = false;

        for (char ch : content.toCharArray()) {
            if (comment) {
                if (ch == CharPool.LF) {
                    comment = false;
                }
                continue;
            }
            switch (ch) {
                case EQUAL -> {
                    if (string) {
                        token.append(ch);
                        continue;
                    }
                    if (!leftHand) {
                        Stack<String> parts = new Stack<>();
                        Arrays.stream(StrUtil.trim(token).split(" ")).forEach(parts::push);

                        String key = parts.pop(); // 弹出栈顶元素作为第二项的键
                        String val = String.join(" ", parts).trim(); // 加入其他元素

                        correctPaths(paths, json, 1);
                        setValue(json, paths, val);

                        paths.pop();
                        paths.push(key.trim());
                        correctPaths(paths, json, 1);

                        token.setLength(0);
                        break;
                    }
                    if (token.length() > 0) {
                        String key = StrUtil.trim(token);
                        paths.push(key);
                        token.setLength(0);
                    }
                    correctPaths(paths, json, 1);
                    leftHand = false;
                    mayBeArray = false;
                }
                case CharPool.DELIM_START -> {
                    if (string) {
                        token.append(ch);
                        continue;
                    }
                    if (leftHand) {
                        if (token.length() > 0) {
                            token.append(' ');
                        } else {
                            paths.push("#");
                        }
                        correctPaths(paths, json, 1);
                    }
                    leftHand = true;
                    mayBeArray = true;
                }
                case CharPool.DELIM_END -> {
                    if (string) {
                        token.append(ch);
                        continue;
                    }
                    if (token.length() > 0) {
                        correctPaths(paths, json, 1);
                        if (mayBeArray) {
                            List<String> array = ReUtil.findAllGroup0("(?:[^\\s\"]+|\"[^\"]*\")+", StrUtil.trim(token));
                            setValue(json, paths, array);
                        } else {
                            setValue(json, paths, StrUtil.trim(token));
                            paths.pop();
                        }
                        token.setLength(0);
                    } else if (leftHand && mayBeArray) {
                        setValue(json, paths, new JSONObject());
                    }
                    if (!paths.empty()) {
                        paths.pop();
                    }
                    mayBeArray = false;
                    leftHand = true;
                }
                case CharPool.LF -> {
                    if (string) {
                        token.append(ch);
                        continue;
                    }
                    if (leftHand && (token.length() > 0)) {
                        token.append(' ');
                    } else if (token.length() > 0) {
                        correctPaths(paths, json, 1);
                        leftHand = true;
                        setValue(json, paths, StrUtil.trim(token));
                        token.setLength(0);
                        paths.pop();
                    }
                    comment = false;
                }
                case CharPool.DOUBLE_QUOTES -> {
                    if (escape) {
                        token.append(ch);
                        escape = false;
                        continue;
                    }
                    string = !string;
                    token.append(ch);
                }
                case POUND -> {
                    if (!string) {
                        comment = true;
                    } else {
                        token.append(ch);
                    }
                }
                case CharPool.BACKSLASH -> {
                    escape = true;
                    token.append(ch);
                    continue;
                }
                default -> {
                    if (comment) {
                        break;
                    }
                    if ((token.length() == 0) && ch == CharPool.SPACE) {
                        break;
                    }
                    token.append(ch);
                }
            }
            escape = false;
        }

        return json;
    }

    public static String generate(JSONObject json) {
        handleNest(json); // 递归遍历所有对象，查找isNest键，若找到则移除isNest键并将当前对象转为字符串
        String string = json.toStringPretty();

        String[] strings = string.split("\n");
        strings = ArrayUtil.sub(strings, 1, strings.length - 1);
        // TODO 转义:，解决字符串包含:时的错误
        string = Arrays.stream(strings)
                .map(line -> ReUtil.delPre("^\\s{4}", line))
                .map(line -> line.replaceAll(",$", "")) // 去除行末逗号
                .map(line -> line.replaceAll("\\s{4}", "\t")) // 空格转制表符
                .collect(Collectors.joining("\n"))
                .replaceAll("\\\\\"", "@@") // \"转@@
                .replaceAll("\"", "") // 去除双引号
                .replaceAll(": ", " = ") // 冒号转等号
                .replaceAll("\\\\\\\\@@", "\\\\\"") // \\@@转\"
                .replaceAll("@@", "\"") // @@转双引号
                .replaceAll("\\[", "{") // [转{
                .replaceAll("]", "}") // ]转}
                .replaceAll("\\$\\$[0-9a-fA-F]+", "") // 移除重复键值标识
                .replaceAll("= &gt;", ">") // 转义大于号
                .replaceAll("= &lt;", "<") // 转义小于号
                .replaceAll("%%", "[") // 转义左中括号
                .replaceAll("!!", "]") //转义右中括号
        ;

        return string;
    }

    private static void setValue(JSONObject json, Stack<String> paths, Object val) {
        JSONObject currentJson = json;

        for (int i = 0; i < paths.size(); i++) {
            String key = paths.get(i);

            if (i == paths.size() - 1) {
                currentJson.putOpt(key, val);
                if (val instanceof String && ((String) val).contains("\\\"")) {
                    String str = String.valueOf(val);
                    String str2 = StrUtil.sub(str, 1, -1);
                    String str3 = str2.replaceAll("\\\\\"", "\"");
                    JSONObject object = ParadoxParserUtil.parse(Arrays.asList(str3.split("\n")));
                    object.putOpt("isNest", true);
                    currentJson.putOpt(key, object);
                } else {
                    if (val instanceof String && ((String) val).matches("^\".*\"$") && ((String) val).contains("_") && !((String) val).contains(" ") && !((String) val).contains("/")) {
                        val = StrUtil.sub(String.valueOf(val), 1, -1);
                    }
                    currentJson.putOpt(key, val);
                }
            } else {
                if (!currentJson.containsKey(key)) {
                    currentJson.putOpt(key, new JSONObject());
                }
                currentJson = currentJson.getJSONObject(key);
            }
        }
    }

    private static void correctPaths(Stack<String> paths, JSONObject json, int num) {
        if (getObjectByPath(paths, json) != null) {
            String suffix = splitter + Integer.toHexString(num).toUpperCase();
            String lastPath = paths.pop();
            paths.push(StrUtil.subBefore(lastPath, splitter, false) + suffix);
            correctPaths(paths, json, num + 1);
        }
    }

    private static Object getObjectByPath(Stack<String> paths, JSONObject json) {
        String[] keys = paths.toArray(new String[0]);
        JSONObject currentObj = json;
        for (String key : keys) {
            if (currentObj.containsKey(key)) {
                Object obj = currentObj.get(key);
                if (obj instanceof JSONObject) {
                    currentObj = (JSONObject) obj;
                    if (keys[keys.length - 1].equals(key)) {
                        return obj;
                    }
                } else {
                    return obj;
                }
            } else {
                return null;
            }
        }
        return null;
    }

    private static JSONObject handleNest(JSONObject json) {
        for (String key : json.keySet()) {
            Object value = json.get(key);
            if (value instanceof String && ((String) value).contains("[")) {
                String v = String.valueOf(value).replaceAll("\\[", "%%").replaceAll("]", "!!");
                json.set(key, v);
            }
            if (value instanceof JSONObject nestedObject) {
                if (nestedObject.containsKey("isNest") && nestedObject.getBool("isNest")) {
                    nestedObject.remove("isNest");
                    String v = "\"" + generate(nestedObject).replaceAll("\n", " ").replaceAll("\"", "\\\\@@") + "\"";
                    json.set(key, v);
                } else {
                    json.set(key, handleNest(nestedObject));
                }
            }
        }
        return json;
    }
}
