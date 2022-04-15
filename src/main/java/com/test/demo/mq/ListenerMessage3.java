package com.test.demo.mq;

import com.test.demo.config.RedisStreamConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;


/**
 * 在消费完成后 确认已消费
 *
 * @author zhout 2022/4/5 6:06 下午
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ListenerMessage3 implements StreamListener<String, MapRecord<String, String, String>> {

    private final RedisStream redisStream;

    private final ConsumerService consumerService;

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        String recordId = message.getId().getValue();
        try {
            log.info("ListenerMessage3-手动ack ==> recordId={}, streamKey={}, value={}", recordId, message.getStream(), message.getValue());
            consumerService.receive(recordId, message.getValue());

        } catch (Exception e) {
            log.warn("ListenerMessage3 Error：json:{}", message);
            log.warn(e.getMessage(), e);
        } finally {
            // 消费完成后确认消费（ACK）
            redisStream.ack(message.getStream(), RedisStreamConfig.MANUAL_ACK_GROUP, recordId);
        }
    }
}
