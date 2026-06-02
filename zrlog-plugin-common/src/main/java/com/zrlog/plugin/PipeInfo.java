package com.zrlog.plugin;

import com.zrlog.plugin.common.PluginExecutionTimeouts;
import com.zrlog.plugin.data.codec.MsgPacket;

public class PipeInfo {

    public PipeInfo(MsgPacket requestMsgPackage, MsgPacket responseMsgPacket, IMsgPacketCallBack iMsgPacketCallBack, Long createdAt) {
        this(requestMsgPackage, responseMsgPacket, iMsgPacketCallBack, createdAt,
                createdAt + PluginExecutionTimeouts.DEFAULT_EXECUTION_TIMEOUT.toMillis());
    }

    public PipeInfo(MsgPacket requestMsgPackage,
                    MsgPacket responseMsgPacket,
                    IMsgPacketCallBack iMsgPacketCallBack,
                    Long createdAt,
                    Long expireAt) {
        this.requestMsgPackage = requestMsgPackage;
        this.responseMsgPacket = responseMsgPacket;
        this.iMsgPacketCallBack = iMsgPacketCallBack;
        this.cratedAt = createdAt;
        this.expireAt = expireAt;
    }

    private MsgPacket requestMsgPackage;
    private MsgPacket responseMsgPacket;

    private IMsgPacketCallBack iMsgPacketCallBack;
    private Long cratedAt;
    private Long expireAt;

    public MsgPacket getResponseMsgPacket() {
        return responseMsgPacket;
    }

    public void setResponseMsgPacket(MsgPacket responseMsgPacket) {
        this.responseMsgPacket = responseMsgPacket;
    }

    public MsgPacket getRequestMsgPackage() {
        return requestMsgPackage;
    }

    public void setRequestMsgPackage(MsgPacket requestMsgPackage) {
        this.requestMsgPackage = requestMsgPackage;
    }

    public IMsgPacketCallBack getiMsgPacketCallBack() {
        return iMsgPacketCallBack;
    }

    public void setiMsgPacketCallBack(IMsgPacketCallBack iMsgPacketCallBack) {
        this.iMsgPacketCallBack = iMsgPacketCallBack;
    }

    public Long getCratedAt() {
        return cratedAt;
    }

    public void setCratedAt(Long cratedAt) {
        this.cratedAt = cratedAt;
    }

    public Long getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(Long expireAt) {
        this.expireAt = expireAt;
    }

    public void extendExpireAt(Long expireAt) {
        if (expireAt == null) {
            return;
        }
        if (this.expireAt == null || this.expireAt < expireAt) {
            this.expireAt = expireAt;
        }
    }
}
