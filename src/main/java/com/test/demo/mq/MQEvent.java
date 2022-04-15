package com.test.demo.mq;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 生产者与消费者通信事件
 *
 * @author zhout 2022/4/6 10:39 上午
 */
@Getter
@AllArgsConstructor
public enum MQEvent {
    /**
     * 员工被删除事件
     */
    USER_DEL
}
