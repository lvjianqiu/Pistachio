/*
 * Copyright 2014 Yahoo! Inc. Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or
 * agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and
 * limitations under the License. See accompanying LICENSE file.
 */

package com.yahoo.ads.pb.network.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.yahoo.ads.pb.PistachiosHandler;
import com.yahoo.ads.pb.util.ConfigurationManager;

/**
 * Receives a list of continent/city pairs from a {@link WorldClockClient} to
 * get the local times of the specified cities.
 */
public final class NettyPistachioServer {
    private static Logger logger = LoggerFactory.getLogger(NettyPistachioServer.class);

    static final boolean SSL = System.getProperty("ssl") != null;
    //static final int PORT = Integer.parseInt(System.getProperty("port", "8463"));
    static final int PORT = ConfigurationManager.getConfiguration().getInt("Network.Netty.Port", 9091);

    public static void main(String[] args) throws Exception {
        startServer(null);
    }

    public static void startServer(PistachiosHandler handler){
        EventLoopGroup bossGroup = null;
        EventLoopGroup workerGroup = null;
        try {
            // Configure SSL.
            final SslContext sslCtx;
            if (SSL) {
                SelfSignedCertificate ssc = new SelfSignedCertificate();
                sslCtx = SslContext.newServerContext(ssc.certificate(), ssc.privateKey());
            } else {
                sslCtx = null;
            }

            bossGroup = new NioEventLoopGroup(ConfigurationManager.getConfiguration().getInt("Network.Netty.BossThreads",1));
            workerGroup = new NioEventLoopGroup(ConfigurationManager.getConfiguration().getInt("Network.Netty.WorkerThreads",1));
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .handler(new LoggingHandler(LogLevel.INFO))
             .childHandler(new NettyPistachioServerInitializer(sslCtx, handler));

            b.bind(PORT).sync().channel().closeFuture().sync();
        } catch (Exception e) {
            logger.debug("error:", e);
        }finally {
            if (bossGroup != null)
                bossGroup.shutdownGracefully();
            if (workerGroup != null)
                workerGroup.shutdownGracefully();
        }
    }
}
