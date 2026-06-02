package com.yuyue.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.yuyue.constant.ArticleConstant.SSE_RECONNECT_TIME_MS;
import static com.yuyue.constant.ArticleConstant.SSE_TIMEOUT_MS;

/**
 * SSE Emitter 管理器
 *
 */
@Component
@Slf4j
public class SseEmitterManager {

    /**
     * 存储所有的 SseEmitter
     */
    private final Map<String, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    /**
     * 创建 SseEmitter
     *
     * @param taskId 任务ID
     * @return SseEmitter
     */
    public SseEmitter createEmitter(String taskId) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);

        // 设置超时回调
        // 使用 remove(key, value) 防止旧连接的回调误删新连接的 emitter
        emitter.onTimeout(() -> {
            log.warn("SSE 连接超时, taskId={}", taskId);
            emitterMap.remove(taskId, emitter);
        });

        // 设置完成回调
        emitter.onCompletion(() -> {
            log.info("SSE 连接完成, taskId={}", taskId);
            emitterMap.remove(taskId, emitter);
        });

        // 设置错误回调
        emitter.onError((e) -> {
            // 客户端断开（ClientAbortException / IOException）是正常行为
            if (e instanceof IOException
                    || e.getClass().getName().contains("ClientAbortException")) {
                log.warn("SSE 连接断开（客户端主动关闭）, taskId={}", taskId);
            } else {
                log.error("SSE 连接错误, taskId={}", taskId, e);
            }
            emitterMap.remove(taskId, emitter);
        });

        emitterMap.put(taskId, emitter);
        log.info("SSE 连接已创建, taskId={}", taskId);

        return emitter;
    }

    /**
     * 发送消息
     *
     * @param taskId  任务ID
     * @param message 消息内容
     */
    public void send(String taskId, String message) {
        SseEmitter emitter = emitterMap.get(taskId);
        if (emitter == null) {
            log.warn("SSE Emitter 不存在, taskId={}", taskId);
            return;
        }

        try {
            emitter.send(SseEmitter.event()
                    .data(message)
                    .reconnectTime(SSE_RECONNECT_TIME_MS));
            log.debug("SSE 消息发送成功, taskId={}, message={}", taskId, message);
        } catch (Exception e) {
            // 客户端主动断开连接是正常行为，不记录完整堆栈
            log.warn("SSE 消息发送失败（客户端可能已断开）, taskId={}, reason={}",
                    taskId, e.getMessage());
            // 不在此处移除 emitter，由 onError/onCompletion 回调统一清理
        }
    }

    /**
     * 完成连接
     *
     * @param taskId 任务ID
     */
    public void complete(String taskId) {
        SseEmitter emitter = emitterMap.get(taskId);
        if (emitter == null) {
            log.warn("SSE Emitter 不存在, taskId={}", taskId);
            return;
        }

        try {
            emitter.complete();
            log.info("SSE 连接已完成, taskId={}", taskId);
        } catch (Exception e) {
            log.warn("SSE 连接完成失败（客户端可能已断开）, taskId={}", taskId);
        } finally {
            emitterMap.remove(taskId);
        }
    }

    /**
     * 检查 Emitter 是否存在
     *
     * @param taskId 任务ID
     * @return 是否存在
     */
    public boolean exists(String taskId) {
        return emitterMap.containsKey(taskId);
    }
}
