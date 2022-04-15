package com.test.demo.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author zhout 2022/4/5 6:49 下午
 */
@Slf4j
@Service
public class ConsumerService {

    /**
     * 消费消息(自动确认)
     *
     * @return true:消费成功，删除消息。false:保留消息
     */
    public boolean receiveAutoAck(String recordId, Map<String, String> body) {
        log.info("[consumer-autoAck] recordId={}, body={}", recordId, body);
        return true;
    }

    /**
     * 消费消息(方法执行完确认)
     */
    public void receive(String recordId, Map<String, String> body) {
        log.info("[consumer] recordId={}, body={}", recordId, body);
    }
}
