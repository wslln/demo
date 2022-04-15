package com.test.demo.config;

import com.google.common.collect.ImmutableMap;
import com.test.demo.mq.ListenerMessage;
import com.test.demo.mq.ListenerMessage2;
import com.test.demo.mq.ListenerMessage3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.ErrorHandler;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhout 2022/4/5 4:43 下午
 */
@Slf4j
//@Configuration
public class RedisStreamConfig {

    public static final String QUEUE = "z_queue";
    public static final String[] GROUPS = {"z_group", "z_group_2", "z_group_3"};
    public static final String[] CONSUMERS = {"consumer_1", "consumer_2", "consumer_3", "consumer_4"};
    public static final String MANUAL_ACK_GROUP = GROUPS[1];

    private final ListenerMessage listenerMessage;

    private final ListenerMessage2 listenerMessage2;

    private final ListenerMessage3 listenerMessage3;

    private final RedisTemplate<String, Object> redisTemplate;

    private final ThreadPoolTaskExecutor streamConsumerExecutor;

    public RedisStreamConfig(ListenerMessage listenerMessage, ListenerMessage2 listenerMessage2, ListenerMessage3 listenerMessage3, RedisTemplate<String, Object> redisTemplate, ThreadPoolTaskExecutor streamConsumerExecutor) {
        this.listenerMessage = listenerMessage;
        this.listenerMessage2 = listenerMessage2;
        this.listenerMessage3 = listenerMessage3;
        this.redisTemplate = redisTemplate;
        this.streamConsumerExecutor = streamConsumerExecutor;
    }

    /**
     * 启动项目，创建队列并绑定消费组
     */
    @PostConstruct
    public void initQueue() {
        Boolean hasKey = redisTemplate.hasKey(QUEUE);
        if (hasKey == null || !hasKey) {
            Map<String, Object> map = ImmutableMap.<String, Object>builder().put("1", 1).build();
            RecordId recordId = redisTemplate.opsForStream().add(QUEUE, map);
            // 将初始化的值删除
            redisTemplate.opsForStream().delete(QUEUE, recordId);
        }

        StreamInfo.XInfoGroups groups = redisTemplate.opsForStream().groups(QUEUE);
        List<String> groupNames = groups.stream().map(StreamInfo.XInfoGroup::groupName).collect(Collectors.toList());

        for (String group : GROUPS) {
            if (!groupNames.contains(group)) {
                redisTemplate.opsForStream().createGroup(QUEUE, group);
            }
        }
    }

    /**
     * 配置对象
     */
    @Bean
    public StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ?> streamMessageListenerContainerOptions() {
        return StreamMessageListenerContainer
                .StreamMessageListenerContainerOptions
                .builder()
                // 一次性最多拉取多少条消息
                .batchSize(10)
                // 执行消息轮询的执行器
                .executor(this.streamConsumerExecutor)
                // 消息消费异常的handler
                .errorHandler(new ErrorHandler() {
                    @Override
                    public void handleError(Throwable t) {
                        // throw new RuntimeException(t);
                        t.printStackTrace();
                        log.error("[MQ Handler Exception] " + t.getMessage());
                    }
                })
                // 序列化器
                .serializer(new StringRedisSerializer())
                // 超时时间，设置为0，表示不超时（超时后会抛出异常）
                .pollTimeout(Duration.ofSeconds(10))
                .build();
    }

    /**
     * 根据配置对象创建监听容器对象
     */
    @Bean
    public StreamMessageListenerContainer<String, ?> streamMessageListenerContainer(RedisConnectionFactory factory) {
        StreamMessageListenerContainer<String, ?> listenerContainer = StreamMessageListenerContainer.create(factory, streamMessageListenerContainerOptions());
        listenerContainer.start();
        return listenerContainer;
    }

    /**
     * 订阅者1，消费组group1，收到消息后自动确认，与订阅者2为竞争关系，消息仅被其中一个消费
     */
    @Bean
    public Subscription subscription(StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer) {
        return streamMessageListenerContainer.receiveAutoAck(
                Consumer.from(GROUPS[0], CONSUMERS[0]),
                StreamOffset.create(QUEUE, ReadOffset.lastConsumed()),
                listenerMessage
        );
    }

    /**
     * 订阅者2，消费组group1，收到消息后自动确认，与订阅者1为竞争关系，消息仅被其中一个消费
     */
    @Bean
    public Subscription subscription2(StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer) {
        return streamMessageListenerContainer.receiveAutoAck(
                Consumer.from(GROUPS[0], CONSUMERS[1]),
                StreamOffset.create(QUEUE, ReadOffset.lastConsumed()),
                listenerMessage2
        );
    }

    /**
     * 订阅者3，消费组group2，收到消息后不自动确认，手动确认，需要用户选择合适的时机确认，与订阅者1和2非竞争关系，即使消息被订阅者1或2消费，亦可消费
     * 当某个消息被ACK，PEL列表就会减少
     */
    @Bean
    public Subscription subscription3(StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer) {
        return streamMessageListenerContainer.receive(
                Consumer.from(MANUAL_ACK_GROUP, CONSUMERS[2]),
                StreamOffset.create(QUEUE, ReadOffset.lastConsumed()),
                // new ListenerMessage3(GROUPS[1], redisTemplate)
                listenerMessage3
        );
    }

    /*
     * 消息迁移（备用）
     * 订阅者4
     * 如果忘记确认（ACK），则PEL列表会不断增长占用内存
     * 如果服务器宕机，重启连接后将再次收到PEL中的消息ID列表
     */
    //    @Bean
    //    public Subscription subscription4(StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer) {
    //        return streamMessageListenerContainer.receive(
    //                Consumer.from(GROUPS[2], CONSUMERS[3]),
    //                StreamOffset.create(QUEUE, ReadOffset.lastConsumed()),
    //                new ListenerMessage4()
    //        );
    //    }
}
