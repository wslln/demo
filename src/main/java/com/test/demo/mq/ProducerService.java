package com.test.demo.mq;

import com.google.common.collect.Maps;
import com.test.demo.config.RedisStreamConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

/**
 * @author zhout 2022/4/5 9:56 下午
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProducerService {

    private final RedisStream redisStream;

    /**
     * 生产消息
     */
    public RecordId send(MQEvent event, Object data) {
        if (Objects.isNull(event) || Objects.isNull(data)) {
            log.info("未生产消息,参数有误 event={}, data={}", event, data);
            return null;
        }
        Map<String, Object> map = Maps.newHashMap();
        map.put("event", event.name());
        map.put("data", data);
        return redisStream.add(RedisStreamConfig.QUEUE, map);
    }
}
