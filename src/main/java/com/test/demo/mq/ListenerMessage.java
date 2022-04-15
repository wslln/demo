package com.test.demo.mq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

/**
 * 监听消息，与ListenerMessage2竞争
 *
 * @author zhout 2022/4/5 6:01 下午
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ListenerMessage implements StreamListener<String, MapRecord<String, String, String>> {

    private final RedisStream redisStream;

    private final ConsumerService consumerService;

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        log.info("ListenerMessage1-监听： ==> recordId={}, stream={}, value={}", message.getId(), message.getStream(), message.getValue());
        String recordId = message.getId().getValue();

        boolean canDelMessage = false;
        try {
            canDelMessage = consumerService.receiveAutoAck(recordId, message.getValue());
        } catch (Exception e) {
            log.error("ListenerMessage1 Error：json:{}", message);
            log.error(e.getMessage(), e);
        }

        if (canDelMessage) {
            // 消息确认完后消息实际上没有在redis中消失，这也是redis stream中的一个特性，如果要想真正的删除该消息，需要进行指定字段ID删除
            redisStream.delete(message.getStream(), recordId);
        }
    }
}