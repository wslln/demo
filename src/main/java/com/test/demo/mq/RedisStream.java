package com.test.demo.mq;

import lombok.NonNull;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * @author zhout 2022/4/5 4:20 下午
 */
@Component
class RedisStream {

    private final RedisTemplate<String, Object> redisTemplate;

    private final StreamOperations<String, Object, Object> streamOperations;

    public RedisStream(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.streamOperations = redisTemplate.opsForStream();
    }

    /**
     * 生产消息
     * XADD key * hkey1 hval1 [hkey2 hval2...]
     * key不存在，创建键为key的Stream流，并往流里添加消息
     * key存在，往流里添加消息
     */
    public RecordId add(String key, Map<String, Object> map) {
        return streamOperations.add(key, map);
    }

    public <V> RecordId add(ObjectRecord<String, V> record){
        return streamOperations.add(record);
    }

    /**
     * 查看Stream的详情
     * XINFO STREAM key
     */
    public StreamInfo.XInfoStream info(String key) {
        return streamOperations.info(key);
    }

    /**
     * 查看Stream的消息个数
     * XLEN key
     */
    public Long size(String key) {
        return streamOperations.size(key);
    }

    /**
     * 查询消息
     * XRANGE key start end [COUNT count]
     * range：表示查询区间，比如区间(消息ID，消息ID2)，查询消息ID到消息ID2之间的消息，特殊值("-","+")表示流中可能的最小ID和最大ID
     * Range.unbounded()：查询所有
     * Range.closed(消息ID，消息ID2)：查询[消息ID，消息ID2]
     * Range.open(消息ID，消息ID2)：查询(消息ID，消息ID2)
     * limit：表示查询出来后限制显示个数
     * Limit.limit().count(限制个数)
     */
    public List<MapRecord<String, Object, Object>> range(String key, Range<String> range, RedisZSetCommands.Limit limit) {
        return streamOperations.range(key, range, limit);
    }

    /**
     * 查询消息
     * XREVRANGE key end start [COUNT count]
     * xReverseRange用法跟xRange一样，只是最后显示的时候是反序的，即消息ID从大到小显示
     */
    public List<MapRecord<String, Object, Object>> reverseRange(String key, Range<String> range, RedisZSetCommands.Limit limit) {
        return streamOperations.reverseRange(key, range, limit);
    }

    /**
     * 批量删除消息
     * XDEL key ID [ID ...]
     */
    public Long delete(String key, String... recordIds) {
        return streamOperations.delete(key, recordIds);
    }

    /**
     * 修剪/保留消息
     * XTRIM key MAXLEN | MINID [~] count
     * count：保留消息个数，当count是具体的消息ID时，表示移除ID小于count这个ID的所有消息
     * approximateTrimming：近似
     * 等于false时，表示精确保留count个个数的消息，不多不少只能是count
     * 等于true时，表示近似保留count个个数的消息，不能少于count，但可以稍微多余count(前提条件是数据量多于200个)
     */
    public Long trim(String key, long count) {
        return streamOperations.trim(key, count);
    }

    /**
     * 创建消费组
     * XGROUP CREATE key groupname id-or-$
     * XGROUP SETID key groupname id-or-$ (消费组已创建，重新设置读取消息顺序)
     * id为0表示组从stream的第一条数据开始读，
     * id为$表示组从新的消息开始读取。(默认)
     */
    public String createGroup(String key, String group) {
        return this.createGroup(key, ReadOffset.latest(), group);
    }

    public String createGroup(String key, ReadOffset offset, String group) {
        return streamOperations.createGroup(key, offset, group);
    }

    /**
     * 销毁消费组
     * XGROUP DESTROY key groupname
     */
    public Boolean destroyGroup(String key, String group) {
        return streamOperations.destroyGroup(key, group);
    }

    /**
     * 查看消费组详情
     * XINFO GROUPS key
     */
    public StreamInfo.XInfoGroups infoGroups(String key) {
        return streamOperations.groups(key);
    }

    /**
     * 读取消息
     * XREAD [COUNT count] [BLOCK milliseconds] STREAMS key[key ...] id[id ...]
     * 从一个或者多个流中读取数据
     * 特殊ID=0-0：从队列最先添加的消息读取
     * 特殊ID=$：只接收从我们阻塞的那一刻开始通过XADD添加到流的消息，对已经添加的历史消息不感兴趣
     * 在阻塞模式中，可以使用$，表示最新的消息ID。（在非阻塞模式下$无意义）。
     */
    @SafeVarargs
    public final List<MapRecord<String, Object, Object>> read(StreamReadOptions options, StreamOffset<String>... offsets) {
        return streamOperations.read(options, offsets);
    }

    /**
     * 读取消息，强制带消费组、消费者
     * XREADGROUP GROUP group consumer [COUNT count] [BLOCK milliseconds] [NOACK] STREAMS key[key ...] ID[ID ...]
     * 特殊符号 0-0：表示从pending列表重新读取消息，不支持阻塞，无法读取的过程自动ack
     * 特殊符号 > ：表示只接收比消费者晚创建的消息，之前的消息不管
     * 特殊符号 $ ：在xReadGroup中使用是无意义的，报错提示：ERR The $ ID is meaningless in the context of XREADGROUP
     */
    @SafeVarargs
    public final List<MapRecord<String, Object, Object>> readGroup(Consumer consumer, StreamReadOptions options, StreamOffset<String>... offsets) {
        return streamOperations.read(consumer, options, offsets);
    }

    /**
     * 消费者详情
     * XINFO CONSUMERS key group
     */
    public StreamInfo.XInfoConsumers infoConsumers(String key, String group) {
        return streamOperations.consumers(key, group);
    }

    /**
     * 删除消费者
     * XGROUP DELCONSUMER key groupname consumername
     */
    public Boolean groupDelConsumer(String key, Consumer consumer) {
        return streamOperations.deleteConsumer(key, consumer);
    }

    /**
     * 查看指定消费者的待处理列表
     */
    public PendingMessages pending(String key, Consumer consumer) {
        return this.pending(key, consumer, Range.unbounded(), -1L);
    }

    /**
     * Pending Entries List (PEL)
     * XPENDING key group [consumer] [start end count]
     * 查看指定消费组的待处理列表
     */
    public PendingMessagesSummary pending(String key, String group) {
        return streamOperations.pending(key, group);
    }

    public PendingMessages pending(String key, Consumer consumer, Range<?> range, long count) {
        return streamOperations.pending(key, consumer, range, count);
    }

    /**
     * 消息确认(从PEL中删除一条或多条消息)
     * XACK key group ID[ID ...]
     */
    public Long ack(String key, String group, String... recordIds) {
        return streamOperations.acknowledge(key, group, recordIds);
    }

    /**
     * 消息转移
     * XCLAIM key group consumer min-idle-time ID[ID ...]
     * idleTime：转移条件，进入PEL列表的时间大于空闲时间
     */
    public List<ByteRecord> claim(String key, String group, String consumer, long idleTime, String recordId) {
        return this.claim(key, group, consumer, idleTime, RecordId.of(recordId));
    }

    public List<ByteRecord> claim(String key, String group, String consumer, long idleTime, RecordId... recordIds) {
        return redisTemplate.execute(new RedisCallback<List<ByteRecord>>() {
            @Override
            public List<ByteRecord> doInRedis(@NonNull RedisConnection redisConnection) throws DataAccessException {
                return redisConnection.streamCommands().xClaim(key.getBytes(), group, consumer, Duration.ofSeconds(idleTime), recordIds);
            }
        });
    }
}
