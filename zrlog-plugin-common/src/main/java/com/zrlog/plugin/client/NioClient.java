package com.zrlog.plugin.client;

import com.zrlog.plugin.IOSession;
import com.zrlog.plugin.api.*;
import com.zrlog.plugin.common.ConfigKit;
import com.zrlog.plugin.common.IOUtil;
import com.zrlog.plugin.common.IdUtil;
import com.zrlog.plugin.common.LoggerUtil;
import com.zrlog.plugin.data.codec.MsgPacketStatus;
import com.zrlog.plugin.data.codec.SocketCodec;
import com.zrlog.plugin.data.codec.SocketDecode;
import com.zrlog.plugin.data.codec.SocketEncode;
import com.zrlog.plugin.message.Plugin;
import com.zrlog.plugin.render.IRenderHandler;
import com.zrlog.plugin.type.ActionType;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class NioClient {


    private final IConnectHandler connectHandler;
    private final IRenderHandler renderHandler;
    private final IActionHandler actionHandler;

    public NioClient() {
        this(null);
    }

    public NioClient(IActionHandler actionHandler) {
        this(null, null, actionHandler);
    }

    public NioClient(IConnectHandler connectHandler, IRenderHandler renderHandler) {
        this(connectHandler, renderHandler, null);
    }

    public NioClient(IConnectHandler connectHandler, IRenderHandler renderHandler, IActionHandler actionHandler) {
        this.connectHandler = connectHandler;
        this.renderHandler = renderHandler;
        if (actionHandler == null) {
            this.actionHandler = new ClientActionHandler();
        } else {
            this.actionHandler = actionHandler;
        }
    }

    public void connectServer(String[] args, List<Class<?>> classList, Class<? extends IPluginAction> pluginAction) throws IOException {
        connectServer(args, classList, pluginAction, new ArrayList<>());
    }

    public void connectServer(String[] args, List<Class<?>> classList, Class<? extends IPluginAction> pluginAction, Class<? extends IPluginService> service) throws IOException {
        List<Class<? extends IPluginService>> serviceList = new ArrayList<>();
        serviceList.add(service);
        connectServer(args, classList, pluginAction, serviceList);
    }

    public void connectServer(String[] args, List<Class<?>> classList, Class<? extends IPluginAction> pluginAction, List<Class<? extends IPluginService>> serviceList) throws IOException {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL %4$s %5$s%6$s%n");
        Plugin plugin = new Plugin();
        String propertiesPath = "/plugin.properties";
        try (InputStream in = NioClient.class.getResourceAsStream(propertiesPath)) {
            if (in == null) {
                throw new IOException("not found properties file " + propertiesPath);
            }
            Properties properties = new Properties();
            properties.load(in);
            plugin.setVersion(properties.getProperty("version", ""));
            plugin.setName(properties.getProperty("name", ""));
            plugin.setDesc(properties.getProperty("desc", ""));
            if (properties.get("dependentService") != null) {
                plugin.setDependentService(new LinkedHashSet<>(Arrays.asList(properties.get("dependentService").toString().split(","))));
            }
            if (properties.get("paths") != null) {
                plugin.setPaths(new LinkedHashSet<>(Arrays.asList(properties.get("paths").toString().split(","))));
            }
            if (properties.get("actions") != null) {
                plugin.setActions(new LinkedHashSet<>(Arrays.asList(properties.get("actions").toString().split(","))));
            }
            plugin.setShortName(properties.getProperty("shortName", ""));
            plugin.setAuthor(properties.getProperty("author", ""));
            plugin.setIndexPage(properties.getProperty("indexPage", ""));
            try (InputStream inputStream = NioClient.class.getResourceAsStream("/preview-image.base64")) {
                if (inputStream != null) {
                    plugin.setPreviewImageBase64(new String(IOUtil.getByteByInputStream(inputStream)));
                } else {
                    plugin.setPreviewImageBase64("");
                }
            }
        }
        if (serviceList != null && !serviceList.isEmpty()) {
            for (Class<? extends IPluginService> serviceClass : serviceList) {
                Service service = serviceClass.getAnnotation(Service.class);
                if (service == null) {
                    throw new RuntimeException("forget add @Service in the Class " + serviceClass);
                }
                plugin.getServices().add(service.value());
            }
        }
        //parse args
        int serverPort = ConfigKit.getServerPort();
        if (args != null && args.length > 0) {
            serverPort = Integer.parseInt(args[0]);
        }
        if (args != null && args.length > 1) {
            plugin.setId(args[1]);
        }
        if (Objects.isNull(plugin.getId())) {
            plugin.setId(UUID.randomUUID().toString());
        }
        InetSocketAddress serverAddress = new InetSocketAddress("127.0.0.1", serverPort);
        connectServer(serverAddress, classList, plugin, pluginAction, serviceList);
    }

    private void connectServer(InetSocketAddress serverAddress, List<Class<?>> classList, Plugin plugin, Class<? extends IPluginAction> pluginAction, List<Class<? extends IPluginService>> serviceList) {
        try (SocketChannel socketChannel = SocketChannel.open()) {
            socketChannel.configureBlocking(false);
            Selector selector = Selector.open();

            Set<SelectionKey> selectionKeys;
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            socketChannel.connect(serverAddress);
            IOSession session = null;
            SocketDecode socketDecode = null;
            while (selector.isOpen()) {
                selector.select();
                selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    try {
                        SelectionKey selectionKey = iterator.next();
                        SocketChannel channel = (SocketChannel) selectionKey.channel();
                        if (selectionKey.isConnectable()) {
                            if (channel.isConnectionPending()) {
                                channel.finishConnect();
                            }
                            socketDecode = new SocketDecode(Executors.newFixedThreadPool(4));
                            session = new IOSession(channel, selector, new SocketCodec(new SocketEncode(), socketDecode), actionHandler, renderHandler);
                            session.setPlugin(plugin);
                            session.getAttr().put("_actionClassList", classList);
                            session.getAttr().put("_pluginClass", pluginAction);
                            session.getAttr().put("_pluginServices", serviceList);
                            if (connectHandler != null) {
                                session.getAttr().put("_connectHandle", connectHandler);
                            }
                            session.sendJsonMsg(plugin, ActionType.INIT_CONNECT.name(), IdUtil.getInt(), MsgPacketStatus.SEND_REQUEST);
                        } else if (selectionKey.isReadable()) {
                            if (Objects.isNull(session)) {
                                throw new RuntimeException("socketDecode is null");
                            }
                            while (!socketDecode.doDecode(session)) {
                                Thread.sleep(20);
                            }
                        }
                    } catch (Exception e) {
                        exitPlugin(e);
                    } finally {
                        iterator.remove();
                    }
                }
                //selectionKeys.clear();
            }
        } catch (Exception e) {
            exitPlugin(e);
        }
    }

    private void exitPlugin(Exception e) {
        LoggerUtil.getLogger(NioClient.class).log(Level.SEVERE, "", e);
        System.exit(0);
    }
}