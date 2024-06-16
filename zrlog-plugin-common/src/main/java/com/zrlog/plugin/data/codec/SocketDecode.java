package com.zrlog.plugin.data.codec;

import com.zrlog.plugin.IOSession;
import com.zrlog.plugin.RunConstants;
import com.zrlog.plugin.common.HexaConversionUtil;
import com.zrlog.plugin.common.LoggerUtil;
import com.zrlog.plugin.type.RunType;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

public class SocketDecode {

    private static final Logger LOGGER = LoggerUtil.getLogger(SocketDecode.class);

    private MsgPacket packet;
    private final ByteBuffer header = ByteBuffer.allocate(7);
    private ByteBuffer methodAndLengthAndContentType;
    private final Executor messageHandlerExecutor;

    public SocketDecode(Executor messageHandlerExecutor) {
        this.messageHandlerExecutor = messageHandlerExecutor;
        packet = new MsgPacket();
        packet.setDataLength(-1);
    }

    public MsgPacket getPacket() {
        return packet;
    }

    public void setPacket(MsgPacket packet) {
        this.packet = packet;
    }

    public boolean doDecode(final IOSession session) throws Exception {
        SocketChannel channel = (SocketChannel) session.getSystemAttr().get("_channel");

        if (!channel.isOpen() || channel.socket().isClosed()) {
            throw new EOFException();
        }
        boolean flag = false;
        if (packet.getDataLength() == -1) {
            //read header
            if (header.hasRemaining()) {
                int length = channel.read(header);
                if (length == -1) {
                    throw new IOException("connect closed");
                }
                if (header.hasRemaining()) {
                    return false;
                }
                byte[] data = header.array();
                if (data[0] != PackageVersion.V1.getVersion()) {
                    throw new RuntimeException("Unknown protocol version");
                }
                MsgPacketStatus msgPacketStatus = MsgPacketStatus.getMsgPacketStatus(data[1]);
                if (Objects.equals(msgPacketStatus, MsgPacketStatus.UNKNOWN)) {
                    throw new RuntimeException("Unknown package status");
                }
                packet.setStatus(msgPacketStatus);
                packet.setMethodLength(data[6]);
                packet.setMsgId(HexaConversionUtil.byteArrayToInt(HexaConversionUtil.subByts(data, 2, 4)));
                methodAndLengthAndContentType = ByteBuffer.allocate(packet.getMethodLength() + 4 + 1);
            }
            //read methodName
            if (methodAndLengthAndContentType != null && methodAndLengthAndContentType.hasRemaining()) {
                channel.read(methodAndLengthAndContentType);
                if (methodAndLengthAndContentType.hasRemaining()) {
                    return false;
                }
                byte[] data = methodAndLengthAndContentType.array();
                packet.setMethodStr(new String(HexaConversionUtil.subByts(data, 0, packet.getMethodLength())));
                packet.setDataLength(HexaConversionUtil.byteArrayToInt(HexaConversionUtil.subByts(data, packet.getMethodLength(), 4)));
                packet.setContentType(ContentType.getContentType(data[data.length - 1]));
                ByteBuffer dataBuffer = ByteBuffer.allocate(packet.getDataLength());
                packet.setData(dataBuffer);
            }
            //read data
            if (packet.getData() != null && packet.getData().hasRemaining()) {
                channel.read(packet.getData());
                flag = !packet.getData().hasRemaining();
            }
        } else {
            channel.read(packet.getData());
            flag = !packet.getData().hasRemaining();
        }
        if (flag) {
            if (RunConstants.runType == RunType.DEV) {
                LOGGER.info("recv <<< " + session.getAttr().get("count") + " " + packet);
            }
            messageHandlerExecutor.execute(() -> session.dispose(packet));
        }
        return flag;
    }

}
