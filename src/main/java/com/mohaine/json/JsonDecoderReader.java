package com.mohaine.json;

import com.mohaine.util.TaskTimer;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JsonDecoderReader {

    private char[] buffer = new char[50000];
    private int bufferOffset = 0;
    private int bufferEnd = 0;
    private int bufferHistory = 0;

    private boolean outOfData = false;
    private int lastReadOffset = -1;


    private int offset = 0;
    private Reader reader;
    private JsonConverterConfig config;


    public JsonDecoderReader(Reader reader) {
        this.reader = reader;
        config = new JsonConverterConfig();
    }

    public JsonDecoderReader(JsonConverterConfig config, Reader reader) {
        this.config = config;
        this.reader = reader;
    }

    public Object parseJson() throws IOException {
        bypassWhitespace();
        int startOffset = offset;

        if (!notDone()) {
            return null;
        }

        char startChar = readChar();
        if (startChar == '\"') {
            return parseString();
        } else if (startChar == '-' || (startChar >= '0' && startChar <= '9')) {
            return parseNumber();
        } else if (startChar == 't' || startChar == 'f') {
            return parseBoolean(startChar);
        } else if (startChar == 'n') {
            return parseNull();
        } else if (startChar == '[') {
            return parseArray();
        } else if (startChar == '{') {
            return parseObject();
        }
        throw new RuntimeException("JSON: invalid entry '" + startChar + "' at " + getLineChar(startOffset));
    }


    private void bypassWhitespace() throws IOException {
        while (notDone() && isWhitespace(readChar())) {
            offset++;
        }
    }

    private boolean notDone() throws IOException {
//        readyTimer.start();
//        try {
            return !(bufferOffset == bufferEnd && outOfData);
//        } finally {
//            readyTimer.stop();
//        }
    }

    private void readMoreIfNeeded() throws IOException {
        if (bufferOffset == bufferEnd && !outOfData) {
            bufferHistory += bufferEnd;
            int readSize = reader.read(buffer);
            bufferOffset = 0;
            if (readSize < 0) {
                outOfData = true;
                bufferEnd = 0;
            } else {
                bufferEnd = readSize;
            }
        }
    }

    private char readChar() throws IOException {
//        readTimer.start();
//        try {
            if (offset == lastReadOffset) {
                return buffer[lastReadOffset - bufferHistory];
            }
            readMoreIfNeeded();
            lastReadOffset = bufferOffset + bufferHistory;
            bufferOffset++;
            return buffer[lastReadOffset - bufferHistory];
//        } finally {
//            readTimer.stop();
//            readTimer.stop();
//        }
    }

    private String readString(int size, String start) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(start);
        while (notDone() && size > 0) {
            sb.append((char) readChar());
            offset++;
            size--;
        }
        return sb.toString();
    }

    private boolean validateStringAt(String string, int startIndex) {
//        if (jsonString.length() < startIndex + string.length()) {
//            return false;
//        }
//        for (int i = 0; i < string.length(); i++) {
//            if (jsonString.charAt(i + startIndex) != string.charAt(i)) {
//                return false;
//            }
//        }
        return true;
    }

    private String getLineChar(int offset) {
//        int lineCount = 1;
//        int lineCharCount = 0;
//
//        int lastChar = -1;
//        for (int i = 0; i <= offset; i++) {
//            char charAt = jsonString.charAt(i);
//            if (charAt == '\r') {
//                lineCharCount = 0;
//                lineCount++;
//            } else if (charAt == '\n') {
//                if (lastChar != '\r') {
//                    lineCharCount = 0;
//                    lineCount++;
//                }
//            } else {
//                lineCharCount++;
//            }
//
//            lastChar = charAt;
//
//        }
        return " OFFSET " + offset;
    }

    private boolean isWhitespace(char c) {
        return c == ' ' || c == '\r' || c == '\t' || c == '\n' || c == '\b' || c == '\f';
    }

    private Object parseObject() throws IOException {
        int startIndex = offset;
        offset++;
        JsonUnknownObject unknownObject = new JsonUnknownObject();
        while (notDone()) {
            bypassWhitespace();
            char startChar = readChar();
            if (startChar == '}') {
                offset++;
                return config.convertToObject(unknownObject);
            } else if (startChar == ',') {
                offset++;
                String name = parseName();
                unknownObject.setProperty(name, parseJson());
            } else {
                String name = parseName();
                unknownObject.setProperty(name, parseJson());
            }
        }

        throw new RuntimeException("JSON: invalid object. missing } at " + getLineChar(startIndex));
    }

    private String parseName() throws IOException {
        bypassWhitespace();
        String name = parseString();
        if (name == null) {
            throw new RuntimeException("JSON: invalid object. missing name at " + getLineChar(offset));
        }
        bypassWhitespace();

        char valueChar = readChar();
        if (valueChar != ':') {
            throw new RuntimeException("JSON: invalid object. missing : at " + getLineChar(offset));
        }
        offset++;
        bypassWhitespace();
        return name;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private List<?> parseArray() throws IOException {
        offset++;
        List values = new ArrayList();
        while (notDone()) {
            bypassWhitespace();
            char startChar = readChar();
            if (startChar == ']') {
                offset++;
                return values;
            } else if (startChar == ',') {
                offset++;
                values.add(parseJson());
            } else {
                values.add(parseJson());
            }
        }
        throw new RuntimeException("JSON: invalid array. missing ] at " + getLineChar(offset));
    }

    private Boolean parseNull() throws IOException {
        offset++;
        String nullString = readString(3, "n");
        if ("null".equals(nullString)) {
            return null;
        }
        throw new RuntimeException("JSON: invalid null at " + getLineChar(offset) + " : '" + nullString + "'");
    }

    private Boolean parseBoolean(char curChar) throws IOException {
        offset++;
        if (curChar == 'f') {
            String falseString = readString(4, "f");
            if ("false".equals(falseString)) {
                return Boolean.FALSE;
            }
        } else if (curChar == 't') {
            String trueString = readString(3, "t");
            if ("true".equals(trueString)) {
                return Boolean.TRUE;
            }
        }
        throw new RuntimeException("JSON: invalid boolean at " + getLineChar(offset));
    }

    private Number parseNumber() throws IOException {
        boolean hasDot = false;
        boolean hasE = false;

        StringBuffer number = new StringBuffer();
        while (notDone()) {
            char curChar = readChar();
            if (curChar == '-') {
                offset++;
                number.append(curChar);
            } else if (curChar == '.') {
                offset++;
                if (hasDot) {
                    throw new RuntimeException("JSON: Too many . in number at " + getLineChar(offset));
                }
                number.append(curChar);
                hasDot = true;
            } else if (curChar == 'E' || curChar == 'e') {
                offset++;
                if (hasE) {
                    throw new RuntimeException("JSON: Too many E in number at " + getLineChar(offset));
                }
                hasE = true;
                number.append('e');
            } else if (curChar >= '0' && curChar <= '9') {
                offset++;
                number.append(curChar);
            } else {
                break;
            }
        }
        if (hasDot || hasE) {
            return new Double(number.toString());
        } else {
            return new Long(number.toString());
        }
    }

    private String parseString() throws IOException {
        int startOffet = offset;
        // String
        StringBuffer sb = new StringBuffer(100);
        boolean lastWasEscape = false;
        offset++;
        while (notDone()) {
            char curChar = readChar();
            if (!lastWasEscape && curChar == '"') {
                offset++;
                return sb.toString();
            }
            if (lastWasEscape) {
                if (curChar == '"') {
                    sb.append(curChar);
                } else if (curChar == '\'') {
                    sb.append(curChar);
                } else if (curChar == '\\') {
                    sb.append(curChar);
                } else if (curChar == '/') {
                    sb.append(curChar);
                } else if (curChar == 'b') {
                    sb.append("\b");
                } else if (curChar == 'f') {
                    sb.append("\f");
                } else if (curChar == 'n') {
                    sb.append("\n");
                } else if (curChar == 'r') {
                    sb.append("\r");
                } else if (curChar == 't') {
                    sb.append("\t");
                } else if (curChar == 'u') {
                    String unicode = readString(4, "");
                    int value = Integer.parseInt(unicode, 16);
                    sb.append((char) value);
                }
                lastWasEscape = false;
            } else {
                lastWasEscape = curChar == '\\';
                if (!lastWasEscape) {
                    sb.append(curChar);
                }
            }
            offset++;
        }
        throw new RuntimeException("JSON Untermeniated String at " + getLineChar(startOffet));
    }
}
