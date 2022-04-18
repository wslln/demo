package com.test.demo.response;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
@ToString
public class Result<T> implements Serializable {

    private final int code = HttpStatus.OK.value();
    private final String message = HttpStatus.OK.getReasonPhrase();

    private final String requestId;

    private final T result;


    public Result(String requestId, T result) {
        this.requestId = requestId;
        this.result = result;
    }

    @SuppressWarnings("unchecked")
    public static <T> Result<T> ok() {
        return new Result<T>(UUID.randomUUID().toString(), (T) Collections.emptyList());
    }

    /**
     * 返回成功的数据
     */
    public static <T> Result<T> of(@NonNull T result) {
        return new Result<>(UUID.randomUUID().toString(), result);
    }

    /**
     * 返回成功的数据，带分页
     */
    public static <T> Result<PageData<T>> page(@NonNull List<T> data, Integer pageNumber, Integer pageSize, int totalCount) {
        PageData<T> page = PageData.page(pageNumber, pageSize, totalCount, data);
        return new Result<>(UUID.randomUUID().toString(), page);
    }

    /**
     * 返回成功的数据，分页数据为空的情况
     */
    public static <T> Result<PageData<T>> empty(Integer pageNumber, Integer pageSize) {
        PageData<T> page = PageData.empty(pageNumber, pageSize);
        return new Result<>(UUID.randomUUID().toString(), page);
    }
}
