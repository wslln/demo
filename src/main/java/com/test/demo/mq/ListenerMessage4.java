package com.test.demo.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamListener;

/**
 * 消息转移用
 *
 * @author zhout 2022/4/5 6:15 下午
 */
@Slf4j
public class ListenerMessage4 implements StreamListener<String, MapRecord<String, String, String>> {

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        // 接收到消息
        log.info("监听：ListenerMessage4 ==> recordId={}, stream={}, value={}", message.getId(), message.getStream(), message.getValue());
    }
}
