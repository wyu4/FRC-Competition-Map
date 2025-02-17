package com.FRCCompetitionMap.Requests.FRC.ParsedData;

import com.FRCCompetitionMap.Requests.RequestTuple;

public class ParsedTuple<A> {
    private final Integer code;
    private final A parsed;

    public ParsedTuple(RequestTuple result, A parsed) {
        this(result.getCode(), parsed);
    }

    public ParsedTuple(Integer code, A parsed) {
        this.code = code;
        this.parsed = parsed;
    }

    public Integer getCode() {
        return code;
    }

    public A getParsed() {
        return parsed;
    }

    @Override
    public String toString() {
        return "{code:%s,class:%s,parsed:%s}".formatted(code, parsed.getClass(), parsed);
    }
}
