/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.channel;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

/**
 * A special {@link io.netty.channel.ChannelInboundHandler} which offers an easy way to initialize a {@link io.netty.channel.Channel} once it was
 * registered to its {@link io.netty.channel.EventLoop}.
 *
 * Implementations are most often used in the context of {@link io.netty.bootstrap.Bootstrap#handler(io.netty.channel.ChannelHandler)} ,
 * {@link io.netty.bootstrap.ServerBootstrap#handler(io.netty.channel.ChannelHandler)} and {@link io.netty.bootstrap.ServerBootstrap#childHandler(io.netty.channel.ChannelHandler)} to
 * setup the {@link ChannelPipeline} of a {@link io.netty.channel.Channel}.
 *
 * <pre>
 *
 * public class MyChannelInitializer extends {@link io.netty.channel.ChannelInitializer} {
 *     public void initChannel({@link io.netty.channel.Channel} channel) {
 *         channel.pipeline().addLast("myHandler", new MyHandler());
 *     }
 * }
 *
 * {@link io.netty.bootstrap.ServerBootstrap} bootstrap = ...;
 * ...
 * bootstrap.childHandler(new MyChannelInitializer());
 * ...
 * </pre>
 * Be aware that this class is marked as {@link io.netty.channel.ChannelHandler.Sharable} and so the implementation must be safe to be re-used.
 *
 * @param <C>   A sub-type of {@link io.netty.channel.Channel}
 */
@Sharable
public abstract class ChannelInitializer<C extends Channel> extends ChannelInboundHandlerAdapter {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ChannelInitializer.class);

    /**
     * This method will be called once the {@link io.netty.channel.Channel} was registered. After the method returns this instance
     * will be removed from the {@link ChannelPipeline} of the {@link io.netty.channel.Channel}.
     *
     * @param ch            the {@link io.netty.channel.Channel} which was registered.
     * @throws Exception    is thrown if an error occurs. In that case the {@link io.netty.channel.Channel} will be closed.
     */
    protected abstract void initChannel(C ch) throws Exception;

    @Override
    @SuppressWarnings("unchecked")
    public final void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        ChannelPipeline pipeline = ctx.pipeline();
        boolean success = false;
        try {
            initChannel((C) ctx.channel());
            pipeline.remove(this);
            ctx.fireChannelRegistered();
            success = true;
        } catch (Throwable t) {
            logger.warn("Failed to initialize a channel. Closing: " + ctx.channel(), t);
        } finally {
            if (pipeline.context(this) != null) {
                pipeline.remove(this);
            }
            if (!success) {
                ctx.close();
            }
        }
    }
}
