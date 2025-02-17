package com.FRCCompetitionMap.Requests;

/**
 * A class that can store the error code and the content of the GET requests.
 */
public class RequestTuple {
    private final Integer code;
    private final String content;

    public RequestTuple(Integer code, String content) {
        this.code = code;
        this.content = content;
    }

    public Integer getCode() {
        return code;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "{code:%s,content:%s}".formatted(code, content);
    }
}
