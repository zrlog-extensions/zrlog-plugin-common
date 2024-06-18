package com.zrlog.plugin;

import com.zrlog.plugin.data.codec.MsgPacket;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class PipeInfo {

    public PipeInfo(MsgPacket requestMsgPackage, MsgPacket responseMsgPacket, IMsgPacketCallBack iMsgPacketCallBack, Long createdAt) {
        this.requestMsgPackage = requestMsgPackage;
        this.responseMsgPacket = responseMsgPacket;
        this.iMsgPacketCallBack = iMsgPacketCallBack;
        this.cratedAt = createdAt;
    }

    private MsgPacket requestMsgPackage;
    private MsgPacket responseMsgPacket;

    private IMsgPacketCallBack iMsgPacketCallBack;
    private Long cratedAt;

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
}