package com.zrlog.plugin;

import com.google.gson.Gson;
import com.zrlog.plugin.api.IActionHandler;
import com.zrlog.plugin.common.IOUtil;
import com.zrlog.plugin.common.IdUtil;
import com.zrlog.plugin.common.LoggerUtil;
import com.zrlog.plugin.common.PluginExecutionTimeouts;
import com.zrlog.plugin.data.codec.*;
import com.zrlog.plugin.data.codec.convert.JsonConvertMsgBody;
import com.zrlog.plugin.message.CapabilityInvokeRequest;
import com.zrlog.plugin.message.NotificationRequest;
import com.zrlog.plugin.message.Plugin;
import com.zrlog.plugin.message.SchedulerQueryRequest;
import com.zrlog.plugin.message.SchedulerUpdateRequest;
import com.zrlog.plugin.render.IRenderHandler;
import com.zrlog.plugin.type.ActionType;

import java.io.File;
import java.nio.channels.Channel;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IOSession {

    private static final Logger LOGGER = LoggerUtil.getLogger(IOSession.class);

    private final Map<String, Object> attr = new ConcurrentHashMap<>();
    private final Map<Integer, PipeInfo> pipeMap = new ConcurrentHashMap<>();
    private final Map<String, Object> systemAttr = new ConcurrentHashMap<>();
    private final IActionHandler actionHandler;
    private Plugin plugin;
    private final AtomicLong sendMsgCounter = new AtomicLong(0);
    private final AtomicLong receiveMsgCounter = new AtomicLong(0);
    private final MsgPacketDispose msgPacketDispose = new MsgPacketDispose();
    private final IRenderHandler renderHandler;
    private final SocketEncode socketEncode;
    private static final ReentrantLock lock = new ReentrantLock();
    private static ClearIdlMsgPacketRunnable clearIdlMsgPacketRunnable;
    private static ScheduledExecutorService executor;

    public IOSession(SocketChannel channel, Selector selector, SocketCodec socketCodec, IActionHandler actionHandler, IRenderHandler renderHandler) {
        systemAttr.put("_channel", channel);
        systemAttr.put("_selector", selector);
        systemAttr.put("_decode", socketCodec.getSocketDecode());
        systemAttr.put("_encode", socketCodec.getSocketEncode());
        systemAttr.put("_actionHandler", actionHandler);

        this.socketEncode = socketCodec.getSocketEncode();
        this.actionHandler = actionHandler;
        this.renderHandler = renderHandler;
        lock.lock();
        try {
            if (Objects.isNull(executor)) {
                executor = Executors.newSingleThreadScheduledExecutor();
                clearIdlMsgPacketRunnable = new ClearIdlMsgPacketRunnable();
                executor.scheduleAtFixedRate(clearIdlMsgPacketRunnable, 0, 1, TimeUnit.SECONDS);
            }
        } finally {
            lock.unlock();
        }
        clearIdlMsgPacketRunnable.addTask(pipeMap);
    }

    public AtomicLong getSendMsgCounter() {
        return sendMsgCounter;
    }

    public AtomicLong getReceiveMsgCounter() {
        return receiveMsgCounter;
    }

    public IOSession(SocketChannel channel, Selector selector, SocketCodec socketCodec, IActionHandler actionHandler) {
        this(channel, selector, socketCodec, actionHandler, null);
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }

    public <T> T getResponseSync(ContentType contentType, Object data, ActionType actionType, Class<T> clazz) {
        int msgId = IdUtil.getInt();
        MsgPacketStatus status = MsgPacketStatus.SEND_REQUEST;
        MsgPacket msgPacket = new MsgPacket(data, contentType, status, msgId, actionType.name());
        sendMsg(msgPacket);
        MsgPacket response = getResponseMsgPacketByMsgId(msgId);
        if (Objects.isNull(response)) {
            return null;
        }
        if (response.getStatus() == MsgPacketStatus.RESPONSE_SUCCESS) {
            if (response.getContentType() == ContentType.JSON) {
                return new JsonConvertMsgBody().toObj(response.getData(), clazz);
            }
        } else {
            throw new RuntimeException("some error");
        }
        throw new RuntimeException("unSupport response " + response.getContentType());
    }

    public void sendMsg(ContentType contentType, Object data, String methodStr, int msgId, MsgPacketStatus status, IMsgPacketCallBack callBack) {
        MsgPacket msgPacket = new MsgPacket(data, contentType, status, msgId, methodStr);
        sendMsg(msgPacket, callBack);
    }

    public void sendMsg(ContentType contentType, Object data, String methodStr, int msgId, MsgPacketStatus status) {
        MsgPacket msgPacket = new MsgPacket(data, contentType, status, msgId, methodStr);
        sendMsg(msgPacket, null);
    }

    public void sendMsg(MsgPacket msgPacket, IMsgPacketCallBack callBack) {
        sendMsg(msgPacket, callBack, PluginExecutionTimeouts.DEFAULT_EXECUTION_TIMEOUT);
    }

    public void sendMsg(MsgPacket msgPacket, IMsgPacketCallBack callBack, Duration responseTimeout) {
        try {
            if (msgPacket.getStatus() == MsgPacketStatus.SEND_REQUEST) {
                long now = System.currentTimeMillis();
                pipeMap.put(msgPacket.getMsgId(),
                        new PipeInfo(msgPacket, null, callBack, now, now + responseTimeout(responseTimeout).toMillis()));
            }
            socketEncode.doEncode(this, msgPacket);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "", e);
        }
    }

    public void sendMsg(MsgPacket msgPacket) {
        sendMsg(msgPacket, null);
    }

    public void sendJsonMsg(Object data, String method, int id, MsgPacketStatus status) {
        sendMsg(ContentType.JSON, data, method, id, status, null);
    }

    public void sendJsonMsg(Object data, String method, int id, MsgPacketStatus status, IMsgPacketCallBack callBack) {
        sendMsg(ContentType.JSON, data, method, id, status, callBack);
    }

    public void responseHtml(String templatePath, Map dataMap, String method, int id, IMsgPacketCallBack callBack) {
        if (renderHandler != null) {
            sendMsg(ContentType.HTML, renderHandler.render(templatePath, getPlugin(), dataMap), method, id, MsgPacketStatus.RESPONSE_SUCCESS, callBack);
        } else {
            sendMsg(ContentType.HTML, IOUtil.getStringInputStream(IOSession.class.getResourceAsStream(templatePath)), method, id, MsgPacketStatus.RESPONSE_SUCCESS, callBack);
        }
    }

    public void responseHtmlStr(String htmlString, String method, int id) {
        sendMsg(ContentType.HTML, htmlString, method, id, MsgPacketStatus.RESPONSE_SUCCESS, null);
    }

    public void responseXmlStr(String htmlString, String method, int id) {
        sendMsg(ContentType.XML, htmlString, method, id, MsgPacketStatus.RESPONSE_SUCCESS, null);
    }

    public void responseHtmlStr(String htmlString, String method, int id, IMsgPacketCallBack callBack) {
        sendMsg(ContentType.HTML, htmlString, method, id, MsgPacketStatus.RESPONSE_SUCCESS, callBack);
    }

    public void responseHtml(String templatePath, Map dataMap, String method, int id) {
        responseHtml(templatePath, dataMap, method, id, null);
    }

    public void sendFileMsg(File file, int id, MsgPacketStatus status) {
        sendMsg(ContentType.FILE, file, ActionType.HTTP_ATTACHMENT_FILE.name(), id, status, null);
    }

    public int requestService(String name, Map map, IMsgPacketCallBack msgPacketCallBack) {
        return requestService(name, map, msgPacketCallBack, PluginExecutionTimeouts.DEFAULT_EXECUTION_TIMEOUT);
    }

    public int requestService(String name, Map map, IMsgPacketCallBack msgPacketCallBack, Duration responseTimeout) {
        int msgId = IdUtil.getInt();
        map.put("name", name);
        MsgPacket msgPacket = new MsgPacket(map, ContentType.JSON, MsgPacketStatus.SEND_REQUEST, msgId, ActionType.SERVICE.name());
        sendMsg(msgPacket, msgPacketCallBack, responseTimeout);
        return msgId;
    }

    public int requestCapability(String pluginId, String capabilityKey, Map<String, Object> payload, IMsgPacketCallBack msgPacketCallBack) {
        int msgId = IdUtil.getInt();
        CapabilityInvokeRequest request = new CapabilityInvokeRequest();
        request.setPluginId(pluginId);
        request.setCapabilityKey(capabilityKey);
        request.setPayload(payload);
        MsgPacket msgPacket = new MsgPacket(request, ContentType.JSON, MsgPacketStatus.SEND_REQUEST, msgId, ActionType.CAPABILITY_INVOKE.name());
        sendMsg(msgPacket, msgPacketCallBack);
        return msgId;
    }

    public int publishNotification(NotificationRequest request, IMsgPacketCallBack msgPacketCallBack) {
        int msgId = IdUtil.getInt();
        MsgPacket msgPacket = new MsgPacket(request, ContentType.JSON, MsgPacketStatus.SEND_REQUEST, msgId, ActionType.NOTIFICATION_PUBLISH.name());
        sendMsg(msgPacket, msgPacketCallBack);
        return msgId;
    }

    public int querySchedule(String capabilityKey, IMsgPacketCallBack msgPacketCallBack) {
        int msgId = IdUtil.getInt();
        SchedulerQueryRequest request = new SchedulerQueryRequest();
        request.setCapabilityKey(capabilityKey);
        MsgPacket msgPacket = new MsgPacket(request, ContentType.JSON, MsgPacketStatus.SEND_REQUEST, msgId, ActionType.SCHEDULER_QUERY.name());
        sendMsg(msgPacket, msgPacketCallBack);
        return msgId;
    }

    public int querySchedule(String capabilityKey) {
        return querySchedule(capabilityKey, null);
    }

    /**
     * @deprecated Scheduler writes are managed by the runtime scheduler center.
     */
    @Deprecated
    public int updateSchedule(String capabilityKey, String cron, Boolean enabled, IMsgPacketCallBack msgPacketCallBack) {
        int msgId = IdUtil.getInt();
        SchedulerUpdateRequest request = new SchedulerUpdateRequest();
        request.setCapabilityKey(capabilityKey);
        request.setCron(cron);
        request.setEnabled(enabled);
        MsgPacket msgPacket = new MsgPacket(request, ContentType.JSON, MsgPacketStatus.SEND_REQUEST, msgId, ActionType.SCHEDULER_UPDATE.name());
        sendMsg(msgPacket, msgPacketCallBack);
        return msgId;
    }

    /**
     * @deprecated Scheduler writes are managed by the runtime scheduler center.
     */
    @Deprecated
    public int updateSchedule(String capabilityKey, String cron, Boolean enabled) {
        return updateSchedule(capabilityKey, cron, enabled, null);
    }

    /**
     * @deprecated Scheduler writes are managed by the runtime scheduler center.
     */
    @Deprecated
    public int updateSchedule(String capabilityKey, String cron, IMsgPacketCallBack msgPacketCallBack) {
        return updateSchedule(capabilityKey, cron, null, msgPacketCallBack);
    }

    /**
     * @deprecated Scheduler writes are managed by the runtime scheduler center.
     */
    @Deprecated
    public int updateSchedule(String capabilityKey, String cron) {
        return updateSchedule(capabilityKey, cron, null, null);
    }

    /**
     * @deprecated Scheduler writes are managed by the runtime scheduler center.
     */
    @Deprecated
    public int updateScheduleEnabled(String capabilityKey, boolean enabled, IMsgPacketCallBack msgPacketCallBack) {
        return updateSchedule(capabilityKey, null, enabled, msgPacketCallBack);
    }

    /**
     * @deprecated Scheduler writes are managed by the runtime scheduler center.
     */
    @Deprecated
    public int updateScheduleEnabled(String capabilityKey, boolean enabled) {
        return updateScheduleEnabled(capabilityKey, enabled, null);
    }

    public int requestService(String name, Map map) {
        return requestService(name, map, null);
    }

    public <T> T callService(String name, Map map, Class<T> clazz) {
        int messageId = requestService(name, map);
        return new Gson().fromJson(new String(getResponseMsgPacketByMsgId(messageId).getData().array()), clazz);
    }

    public void dispose(MsgPacket msgPacket) {
        try {
            if (msgPacket.getStatus() == MsgPacketStatus.RESPONSE_SUCCESS || msgPacket.getStatus() == MsgPacketStatus.RESPONSE_ERROR) {
                PipeInfo pipeInfo = pipeMap.get(msgPacket.getMsgId());
                if (pipeInfo != null) {
                    IMsgPacketCallBack callBack = pipeMap.get(msgPacket.getMsgId()).getiMsgPacketCallBack();
                    pipeInfo.setResponseMsgPacket(msgPacket);
                    if (callBack != null) {
                        try {
                            callBack.handler(msgPacket);
                        } finally {
                            clearIdlMsgPacketRunnable.removePipeByMsgId(msgPacket.getMsgId());
                        }
                        // 不进行多次处理
                        return;
                    }
                    return;
                }
            }
            msgPacketDispose.handler(this, msgPacket, actionHandler);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "handle error", e);
        }
    }

    public void close() {
        try {
            ((Channel) systemAttr.get("_channel")).close();
            clearIdlMsgPacketRunnable.removePipeMap(pipeMap);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "", e);
        }
    }

    public Map<Integer, PipeInfo> getPipeMap() {
        return pipeMap;
    }

    public Map<String, Object> getSystemAttr() {
        return systemAttr;
    }

    public Map<String, Object> getAttr() {
        return attr;
    }

    public MsgPacket getRequestMsgPacketByMsgId(int msgId) {
        PipeInfo pipeInfo = pipeMap.get(msgId);
        if (Objects.isNull(pipeInfo)) {
            return null;
        }
        return pipeInfo.getRequestMsgPackage();
    }

    public MsgPacket getResponseMsgPacketByMsgId(int msgId) {
        return getResponseMsgPacketByMsgId(msgId, PluginExecutionTimeouts.DEFAULT_EXECUTION_TIMEOUT);
    }

    public MsgPacket getResponseMsgPacketByMsgId(int msgId, Duration readTimeout) {
        Duration timeoutDuration = responseTimeout(readTimeout);
        long timeout = timeoutDuration.toMillis();
        extendPipeTimeout(msgId, timeoutDuration);
        try {
            int sleepSeek = 10;
            while (true) {
                if (timeout <= 0) {
                    return null;
                }
                try {
                    Thread.sleep(sleepSeek);
                } catch (InterruptedException e) {
                    LOGGER.log(Level.SEVERE, "", e);
                }
                timeout -= sleepSeek;
                PipeInfo pipeInfo = pipeMap.get(msgId);
                if (Objects.isNull(pipeInfo)) {
                    return null;
                }
                MsgPacket msgPacket = pipeInfo.getResponseMsgPacket();
                if (msgPacket != null) {
                    return msgPacket;
                }
            }
        } finally {
            clearMessageCacheByMsgId(msgId);
        }
    }

    public void clearMessageCacheByMsgId(int msgId) {
        clearIdlMsgPacketRunnable.removePipeByMsgId(msgId);
    }

    private Duration responseTimeout(Duration readTimeout) {
        if (readTimeout == null || readTimeout.toMillis() <= 0) {
            return PluginExecutionTimeouts.DEFAULT_EXECUTION_TIMEOUT;
        }
        return readTimeout;
    }

    private void extendPipeTimeout(int msgId, Duration readTimeout) {
        PipeInfo pipeInfo = pipeMap.get(msgId);
        if (pipeInfo != null) {
            pipeInfo.extendExpireAt(System.currentTimeMillis() + readTimeout.toMillis());
        }
    }
}
