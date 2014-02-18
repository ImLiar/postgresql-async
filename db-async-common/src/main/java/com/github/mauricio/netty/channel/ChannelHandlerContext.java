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
package com.github.mauricio.netty.channel;


import com.github.mauricio.netty.buffer.ByteBufAllocator;
import com.github.mauricio.netty.util.AttributeMap;
import com.github.mauricio.netty.util.concurrent.EventExecutor;

import java.net.SocketAddress;

/**
 * Enables a {@link ChannelHandler} to interact with its {@link ChannelPipeline}
 * and other handlers.  A handler can notify the next {@link ChannelHandler} in the {@link ChannelPipeline},
 * modify the {@link ChannelPipeline} it belongs to dynamically.
 *
 * <h3>Notify</h3>
 *
 * You can notify the closest handler in the
 * same {@link ChannelPipeline} by calling one of the various methods provided here.
 * Please refer to {@link ChannelPipeline} to understand how an event flows.
 *
 * <h3>Modifying a pipeline</h3>
 *
 * You can get the {@link ChannelPipeline} your handler belongs to by calling
 * {@link #pipeline()}.  A non-trivial application could insert, remove, or
 * replace handlers in the pipeline dynamically in runtime.
 *
 * <h3>Retrieving for later use</h3>
 *
 * You can keep the {@link com.github.mauricio.netty.channel.ChannelHandlerContext} for later use, such as
 * triggering an event outside the handler methods, even from a different thread.
 * <pre>
 * public class MyHandler extends {@link com.github.mauricio.netty.channel.ChannelDuplexHandler} {
 *
 *     <b>private {@link com.github.mauricio.netty.channel.ChannelHandlerContext} ctx;</b>
 *
 *     public void beforeAdd({@link com.github.mauricio.netty.channel.ChannelHandlerContext} ctx) {
 *         <b>this.ctx = ctx;</b>
 *     }
 *
 *     public void login(String username, password) {
 *         ctx.write(new LoginMessage(username, password));
 *     }
 *     ...
 * }
 * </pre>
 *
 * <h3>Storing stateful information</h3>
 *
 * {@link #attr(com.github.mauricio.netty.util.AttributeKey)} allow you to
 * store and access stateful information that is related with a handler and its
 * context.  Please refer to {@link ChannelHandler} to learn various recommended
 * ways to manage stateful information.
 *
 * <h3>A handler can have more than one context</h3>
 *
 * Please note that a {@link ChannelHandler} instance can be added to more than
 * one {@link ChannelPipeline}.  It means a single {@link ChannelHandler}
 * instance can have more than one {@link com.github.mauricio.netty.channel.ChannelHandlerContext} and therefore
 * the single instance can be invoked with different
 * {@link com.github.mauricio.netty.channel.ChannelHandlerContext}s if it is added to one or more
 * {@link ChannelPipeline}s more than once.
 * <p>
 * For example, the following handler will have as many independent attachments
 * as how many times it is added to pipelines, regardless if it is added to the
 * same pipeline multiple times or added to different pipelines multiple times:
 * <pre>
 * public class FactorialHandler extends {@link ChannelInboundHandlerAdapter}&lt{@link Integer}&gt {
 *
 *   private final {@link com.github.mauricio.netty.util.AttributeKey}&lt{@link Integer}&gt counter =
 *           new {@link com.github.mauricio.netty.util.AttributeKey}&lt{@link Integer}&gt("counter");
 *
 *   // This handler will receive a sequence of increasing integers starting
 *   // from 1.
 *   {@code @Override}
 *   public void channelRead({@link com.github.mauricio.netty.channel.ChannelHandlerContext} ctx, {@link Integer} integer) {
 *     {@link com.github.mauricio.netty.util.Attribute}&lt{@link Integer}&gt attr = ctx.getAttr(counter);
 *     Integer a = ctx.getAttr(counter).get();
 *
 *     if (a == null) {
 *       a = 1;
 *     }
 *
 *     attr.set(a * integer));
 *   }
 * }
 *
 * // Different context objects are given to "f1", "f2", "f3", and "f4" even if
 * // they refer to the same handler instance.  Because the FactorialHandler
 * // stores its state in a context object (as an attachment), the factorial is
 * // calculated correctly 4 times once the two pipelines (p1 and p2) are active.
 * FactorialHandler fh = new FactorialHandler();
 *
 * {@link ChannelPipeline} p1 = {@link java.nio.channels.Channels}.pipeline();
 * p1.addLast("f1", fh);
 * p1.addLast("f2", fh);
 *
 * {@link ChannelPipeline} p2 = {@link java.nio.channels.Channels}.pipeline();
 * p2.addLast("f3", fh);
 * p2.addLast("f4", fh);
 * </pre>
 *
 * <h3>Additional resources worth reading</h3>
 * <p>
 * Please refer to the {@link ChannelHandler}, and
 * {@link ChannelPipeline} to find out more about inbound and outbound operations,
 * what fundamental differences they have, how they flow in a  pipeline,  and how to handle
 * the operation in your application.
 */
public interface ChannelHandlerContext
         extends AttributeMap {

    /**
     * Return the {@link com.github.mauricio.netty.channel.Channel} which is bound to the {@link com.github.mauricio.netty.channel.ChannelHandlerContext}.
     */
    Channel channel();

    /**
     * The {@link com.github.mauricio.netty.util.concurrent.EventExecutor} that is used to dispatch the events. This can also be used to directly
     * submit tasks that get executed in the event loop. For more information please refer to the
     * {@link com.github.mauricio.netty.util.concurrent.EventExecutor} javadoc.
     */
    EventExecutor executor();

    /**
     * The unique name of the {@link com.github.mauricio.netty.channel.ChannelHandlerContext}.The name was used when then {@link ChannelHandler}
     * was added to the {@link ChannelPipeline}. This name can also be used to access the registered
     * {@link ChannelHandler} from the {@link ChannelPipeline}.
     */
    String name();

    /**
     * The {@link ChannelHandler} that is bound this {@link com.github.mauricio.netty.channel.ChannelHandlerContext}.
     */
    ChannelHandler handler();

    /**
     * Return {@code true} if the {@link ChannelHandler} which belongs to this {@link ChannelHandler} was removed
     * from the {@link ChannelPipeline}. Note that this method is only meant to be called from with in the
     * {@link EventLoop}.
     */
    boolean isRemoved();

    /**
     * A {@link com.github.mauricio.netty.channel.Channel} was registered to its {@link EventLoop}.
     *
     * This will result in having the  {@link com.github.mauricio.netty.channel.ChannelInboundHandler#channelRegistered(com.github.mauricio.netty.channel.ChannelHandlerContext)} method
     * called of the next  {@link com.github.mauricio.netty.channel.ChannelInboundHandler} contained in the  {@link ChannelPipeline} of the
     * {@link com.github.mauricio.netty.channel.Channel}.
     */
    ChannelHandlerContext fireChannelRegistered();

    /**
     * A {@link com.github.mauricio.netty.channel.Channel} was unregistered from its {@link EventLoop}.
     *
     * This will result in having the  {@link com.github.mauricio.netty.channel.ChannelInboundHandler#channelUnregistered(com.github.mauricio.netty.channel.ChannelHandlerContext)} method
     * called of the next  {@link com.github.mauricio.netty.channel.ChannelInboundHandler} contained in the  {@link ChannelPipeline} of the
     * {@link com.github.mauricio.netty.channel.Channel}.
     */
    @Deprecated
    ChannelHandlerContext fireChannelUnregistered();

    /**
     * A {@link com.github.mauricio.netty.channel.Channel} is active now, which means it is connected.
     *
     * This will result in having the  {@link com.github.mauricio.netty.channel.ChannelInboundHandler#channelActive(com.github.mauricio.netty.channel.ChannelHandlerContext)} method
     * called of the next  {@link com.github.mauricio.netty.channel.ChannelInboundHandler} contained in the  {@link ChannelPipeline} of the
     * {@link com.github.mauricio.netty.channel.Channel}.
     */
    ChannelHandlerContext fireChannelActive();

    /**
     * A {@link com.github.mauricio.netty.channel.Channel} is inactive now, which means it is closed.
     *
     * This will result in having the  {@link com.github.mauricio.netty.channel.ChannelInboundHandler#channelInactive(com.github.mauricio.netty.channel.ChannelHandlerContext)} method
     * called of the next  {@link com.github.mauricio.netty.channel.ChannelInboundHandler} contained in the  {@link ChannelPipeline} of the
     * {@link com.github.mauricio.netty.channel.Channel}.
     */
    ChannelHandlerContext fireChannelInactive();

    /**
     * A {@link com.github.mauricio.netty.channel.Channel} received an {@link Throwable} in one of its inbound operations.
     *
     * This will result in having the  {@link com.github.mauricio.netty.channel.ChannelInboundHandler#exceptionCaught(com.github.mauricio.netty.channel.ChannelHandlerContext, Throwable)}
     * method  called of the next  {@link com.github.mauricio.netty.channel.ChannelInboundHandler} contained in the  {@link ChannelPipeline} of the
     * {@link com.github.mauricio.netty.channel.Channel}.
     */
    ChannelHandlerContext fireExceptionCaught(Throwable cause);

    /**
     * A {@link com.github.mauricio.netty.channel.Channel} received an user defined event.
     *
     * This will result in having the  {@link com.github.mauricio.netty.channel.ChannelInboundHandler#userEventTriggered(com.github.mauricio.netty.channel.ChannelHandlerContext, Object)}
     * method  called of the next  {@link com.github.mauricio.netty.channel.ChannelInboundHandler} contained in the  {@link ChannelPipeline} of the
     * {@link com.github.mauricio.netty.channel.Channel}.
     */
    ChannelHandlerContext fireUserEventTriggered(Object event);

    /**
     * A {@link com.github.mauricio.netty.channel.Channel} received a message.
     *
     * This will result in having the {@link com.github.mauricio.netty.channel.ChannelInboundHandler#channelRead(com.github.mauricio.netty.channel.ChannelHandlerContext, Object)}
     * method  called of the next {@link com.github.mauricio.netty.channel.ChannelInboundHandler} contained in the  {@link ChannelPipeline} of the
     * {@link com.github.mauricio.netty.channel.Channel}.
     */
    ChannelHandlerContext fireChannelRead(Object msg);

    /**
     * Triggers an {@link com.github.mauricio.netty.channel.ChannelInboundHandler#channelWritabilityChanged(com.github.mauricio.netty.channel.ChannelHandlerContext)}
     * event to the next {@link com.github.mauricio.netty.channel.ChannelInboundHandler} in the {@link ChannelPipeline}.
     */
    ChannelHandlerContext fireChannelReadComplete();

    /**
     * Triggers an {@link com.github.mauricio.netty.channel.ChannelInboundHandler#channelWritabilityChanged(com.github.mauricio.netty.channel.ChannelHandlerContext)}
     * event to the next {@link com.github.mauricio.netty.channel.ChannelInboundHandler} in the {@link ChannelPipeline}.
     */
    ChannelHandlerContext fireChannelWritabilityChanged();

    /**
     * Request to bind to the given {@link java.net.SocketAddress} and notify the {@link com.github.mauricio.netty.channel.ChannelFuture} once the operation
     * completes, either because the operation was successful or because of an error.
     * <p>
     * This will result in having the
     * {@link com.github.mauricio.netty.channel.ChannelOutboundHandler#bind(com.github.mauricio.netty.channel.ChannelHandlerContext, java.net.SocketAddress, com.github.mauricio.netty.channel.ChannelPromise)} method
     * called of the next {@link com.github.mauricio.netty.channel.ChannelOutboundHandler} contained in the  {@link ChannelPipeline} of the
     * {@link com.github.mauricio.netty.channel.Channel}.
     */
    ChannelFuture bind(SocketAddress localAddress);

    /**
     * Request to connect to the given {@link java.net.SocketAddress} and notify the {@link com.github.mauricio.netty.channel.ChannelFuture} once the operation
     * completes, either because the operation was successful or because of an error.
     * <p>
     * If the connection fails because of a connection timeout, the {@link com.github.mauricio.netty.channel.ChannelFuture} will get failed with
     * a {@link ConnectTimeoutException}. If it fails because of connection refused a {@link java.net.ConnectException}
     * will be used.
     * <p>
     * This will result in having the
     * {@link com.github.mauricio.netty.channel.ChannelOutboundHandler#connect(com.github.mauricio.netty.channel.ChannelHandlerContext, java.net.SocketAddress, java.net.SocketAddress, com.github.mauricio.netty.channel.ChannelPromise)}
     * method called of the next {@link com.github.mauricio.netty.channel.ChannelOutboundHandler} contained in the  {@link ChannelPipeline} of the
     * {@link com.github.mauricio.netty.channel.Channel}.
     */
    ChannelFuture connect(SocketAddress remoteAddress);

    /**
     * Request to connect to the given {@link java.net.SocketAddress} while bind to the localAddress and notify the
     * {@link com.github.mauricio.netty.channel.ChannelFuture} once the operation completes, either because the operation was successful or because of
     * an error.
     * <p>
     * This will result in having the
     * {@link com.github.mauricio.netty.channel.ChannelOutboundHandler#connect(com.github.mauricio.netty.channel.ChannelHandlerContext, java.net.SocketAddress, java.net.SocketAddress, com.github.mauricio.netty.channel.ChannelPromise)}
     * method called of the next {@link com.github.mauricio.netty.channel.ChannelOutboundHandler} contained in the  {@link ChannelPipeline} of the
     * {@link com.github.mauricio.netty.channel.Channel}.
     */
    ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress);

    /**
     * Request to disconnect from the remote peer and notify the {@link com.github.mauricio.netty.channel.ChannelFuture} once the operation completes,
     * either because the operation was successful or because of an error.
     * <p>
     * This will result in having the
     * {@link com.github.mauricio.netty.channel.ChannelOutboundHandler#disconnect(com.github.mauricio.netty.channel.ChannelHandlerContext, com.github.mauricio.netty.channel.ChannelPromise)}
     * method called of the next {@link com.github.mauricio.netty.channel.ChannelOutboundHandler} contained in the  {@link ChannelPipeline} of the
     * {@link com.github.mauricio.netty.channel.Channel}.
     */
    ChannelFuture disconnect();

    /**
     * Request to close the {@link com.github.mauricio.netty.channel.Channel} and notify the {@link com.github.mauricio.netty.channel.ChannelFuture} once the operation completes,
     * either because the operation was successful or because of
     * an error.
     *
     * After it is closed it is not possible to reuse it again.
     * <p>
     * This will result in having the
     * {@link com.github.mauricio.netty.channel.ChannelOutboundHandler#close(com.github.mauricio.netty.channel.ChannelHandlerContext, com.github.mauricio.netty.channel.ChannelPromise)}
     * method called of the next {@link com.github.mauricio.netty.channel.ChannelOutboundHandler} contained in the  {@link ChannelPipeline} of the
     * {@link com.github.mauricio.netty.channel.Channel}.
     */
    ChannelFuture close();

    /**
     * Request to deregister from the previous assigned {@link com.github.mauricio.netty.util.concurrent.EventExecutor} and notify the
     * {@link com.github.mauricio.netty.channel.ChannelFuture} once the operation completes, either because the operation was successful or because of
     * an error.
     * <p>
     * This will result in having the
     * {@link com.github.mauricio.netty.channel.ChannelOutboundHandler#deregister(com.github.mauricio.netty.channel.ChannelHandlerContext, com.github.mauricio.netty.channel.ChannelPromise)}
     * method called of the next {@link com.github.mauricio.netty.channel.ChannelOutboundHandler} contained in the  {@link ChannelPipeline} of the
     * {@link com.github.mauricio.netty.channel.Channel}.
     *
     */
    @Deprecated
    ChannelFuture deregister();

    /**
     * Request to bind to the given {@link java.net.SocketAddress} and notify the {@link com.github.mauricio.netty.channel.ChannelFuture} once the operation
     * completes, either because the operation was successful or because of an error.
     *
     * The given {@link com.github.mauricio.netty.channel.ChannelPromise} will be notified.
     * <p>
     * This will result in having the
     * {@link com.github.mauricio.netty.channel.ChannelOutboundHandler#bind(com.github.mauricio.netty.channel.ChannelHandlerContext, java.net.SocketAddress, com.github.mauricio.netty.channel.ChannelPromise)} method
     * called of the next {@link com.github.mauricio.netty.channel.ChannelOutboundHandler} contained in the  {@link ChannelPipeline} of the
     * {@link com.github.mauricio.netty.channel.Channel}.
     */
    ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise);

    /**
     * Request to connect to the given {@link java.net.SocketAddress} and notify the {@link com.github.mauricio.netty.channel.ChannelFuture} once the operation
     * completes, either because the operation was successful or because of an error.
     *
     * The given {@link com.github.mauricio.netty.channel.ChannelFuture} will be notified.
     *
     * <p>
     * If the connection fails because of a connection timeout, the {@link com.github.mauricio.netty.channel.ChannelFuture} will get failed with
     * a {@link ConnectTimeoutException}. If it fails because of connection refused a {@link java.net.ConnectException}
     * will be used.
     * <p>
     * This will result in having the
     * {@link com.github.mauricio.netty.channel.ChannelOutboundHandler#connect(com.github.mauricio.netty.channel.ChannelHandlerContext, java.net.SocketAddress, java.net.SocketAddress, com.github.mauricio.netty.channel.ChannelPromise)}
     * method called of the next {@link com.github.mauricio.netty.channel.ChannelOutboundHandler} contained in the  {@link ChannelPipeline} of the
     * {@link com.github.mauricio.netty.channel.Channel}.
     */
    ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise);

    /**
     * Request to connect to the given {@link java.net.SocketAddress} while bind to the localAddress and notify the
     * {@link com.github.mauricio.netty.channel.ChannelFuture} once the operation completes, either because the operation was successful or because of
     * an error.
     *
     * The given {@link com.github.mauricio.netty.channel.ChannelPromise} will be notified and also returned.
     * <p>
     * This will result in having the
     * {@link com.github.mauricio.netty.channel.ChannelOutboundHandler#connect(com.github.mauricio.netty.channel.ChannelHandlerContext, java.net.SocketAddress, java.net.SocketAddress, com.github.mauricio.netty.channel.ChannelPromise)}
     * method called of the next {@link com.github.mauricio.netty.channel.ChannelOutboundHandler} contained in the  {@link ChannelPipeline} of the
     * {@link com.github.mauricio.netty.channel.Channel}.
     */
    ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise);

    /**
     * Request to disconnect from the remote peer and notify the {@link com.github.mauricio.netty.channel.ChannelFuture} once the operation completes,
     * either because the operation was successful or because of an error.
     *
     * The given {@link com.github.mauricio.netty.channel.ChannelPromise} will be notified.
     * <p>
     * This will result in having the
     * {@link com.github.mauricio.netty.channel.ChannelOutboundHandler#disconnect(com.github.mauricio.netty.channel.ChannelHandlerContext, com.github.mauricio.netty.channel.ChannelPromise)}
     * method called of the next {@link com.github.mauricio.netty.channel.ChannelOutboundHandler} contained in the  {@link ChannelPipeline} of the
     * {@link com.github.mauricio.netty.channel.Channel}.
     */
    ChannelFuture disconnect(ChannelPromise promise);

    /**
     * Request to close the {@link com.github.mauricio.netty.channel.Channel} and notify the {@link com.github.mauricio.netty.channel.ChannelFuture} once the operation completes,
     * either because the operation was successful or because of
     * an error.
     *
     * After it is closed it is not possible to reuse it again.
     * The given {@link com.github.mauricio.netty.channel.ChannelPromise} will be notified.
     * <p>
     * This will result in having the
     * {@link com.github.mauricio.netty.channel.ChannelOutboundHandler#close(com.github.mauricio.netty.channel.ChannelHandlerContext, com.github.mauricio.netty.channel.ChannelPromise)}
     * method called of the next {@link com.github.mauricio.netty.channel.ChannelOutboundHandler} contained in the  {@link ChannelPipeline} of the
     * {@link com.github.mauricio.netty.channel.Channel}.
     */
    ChannelFuture close(ChannelPromise promise);

    /**
     * Request to deregister from the previous assigned {@link com.github.mauricio.netty.util.concurrent.EventExecutor} and notify the
     * {@link com.github.mauricio.netty.channel.ChannelFuture} once the operation completes, either because the operation was successful or because of
     * an error.
     *
     * The given {@link com.github.mauricio.netty.channel.ChannelPromise} will be notified.
     * <p>
     * This will result in having the
     * {@link com.github.mauricio.netty.channel.ChannelOutboundHandler#deregister(com.github.mauricio.netty.channel.ChannelHandlerContext, com.github.mauricio.netty.channel.ChannelPromise)}
     * method called of the next {@link com.github.mauricio.netty.channel.ChannelOutboundHandler} contained in the  {@link ChannelPipeline} of the
     * {@link com.github.mauricio.netty.channel.Channel}.
     */
    @Deprecated
    ChannelFuture deregister(ChannelPromise promise);

    /**
     * Request to Read data from the {@link com.github.mauricio.netty.channel.Channel} into the first inbound buffer, triggers an
     * {@link com.github.mauricio.netty.channel.ChannelInboundHandler#channelRead(com.github.mauricio.netty.channel.ChannelHandlerContext, Object)} event if data was
     * read, and triggers a
     * {@link com.github.mauricio.netty.channel.ChannelInboundHandler#channelReadComplete(com.github.mauricio.netty.channel.ChannelHandlerContext) channelReadComplete} event so the
     * handler can decide to continue reading.  If there's a pending read operation already, this method does nothing.
     * <p>
     * This will result in having the
     * {@link com.github.mauricio.netty.channel.ChannelOutboundHandler#read(com.github.mauricio.netty.channel.ChannelHandlerContext)}
     * method called of the next {@link com.github.mauricio.netty.channel.ChannelOutboundHandler} contained in the  {@link ChannelPipeline} of the
     * {@link com.github.mauricio.netty.channel.Channel}.
     */
    ChannelHandlerContext read();

    /**
     * Request to write a message via this {@link com.github.mauricio.netty.channel.ChannelHandlerContext} through the {@link ChannelPipeline}.
     * This method will not request to actual flush, so be sure to call {@link #flush()}
     * once you want to request to flush all pending data to the actual transport.
     */
    ChannelFuture write(Object msg);

    /**
     * Request to write a message via this {@link com.github.mauricio.netty.channel.ChannelHandlerContext} through the {@link ChannelPipeline}.
     * This method will not request to actual flush, so be sure to call {@link #flush()}
     * once you want to request to flush all pending data to the actual transport.
     */
    ChannelFuture write(Object msg, ChannelPromise promise);

    /**
     * Request to flush all pending messages via this ChannelOutboundInvoker.
     */
    ChannelHandlerContext flush();

    /**
     * Shortcut for call {@link #write(Object, com.github.mauricio.netty.channel.ChannelPromise)} and {@link #flush()}.
     */
    ChannelFuture writeAndFlush(Object msg, ChannelPromise promise);

    /**
     * Shortcut for call {@link #write(Object)} and {@link #flush()}.
     */
    ChannelFuture writeAndFlush(Object msg);

    /**
     * Return the assigned {@link ChannelPipeline}
     */
    ChannelPipeline pipeline();

    /**
     * Return the assigned {@link com.github.mauricio.netty.buffer.ByteBufAllocator} which will be used to allocate {@link com.github.mauricio.netty.buffer.ByteBuf}s.
     */
    ByteBufAllocator alloc();

    /**
     * Return a new {@link com.github.mauricio.netty.channel.ChannelPromise}.
     */
    ChannelPromise newPromise();

    /**
     * Return an new {@link com.github.mauricio.netty.channel.ChannelProgressivePromise}
     */
    ChannelProgressivePromise newProgressivePromise();

    /**
     * Create a new {@link com.github.mauricio.netty.channel.ChannelFuture} which is marked as succeeded already. So {@link com.github.mauricio.netty.channel.ChannelFuture#isSuccess()}
     * will return {@code true}. All {@link com.github.mauricio.netty.util.concurrent.FutureListener} added to it will be notified directly. Also
     * every call of blocking methods will just return without blocking.
     */
    ChannelFuture newSucceededFuture();

    /**
     * Create a new {@link com.github.mauricio.netty.channel.ChannelFuture} which is marked as failed already. So {@link com.github.mauricio.netty.channel.ChannelFuture#isSuccess()}
     * will return {@code false}. All {@link com.github.mauricio.netty.util.concurrent.FutureListener} added to it will be notified directly. Also
     * every call of blocking methods will just return without blocking.
     */
    ChannelFuture newFailedFuture(Throwable cause);

    /**
     * Return a special ChannelPromise which can be reused for different operations.
     * <p>
     * It's only supported to use
     * it for {@link com.github.mauricio.netty.channel.ChannelHandlerContext#write(Object, com.github.mauricio.netty.channel.ChannelPromise)}.
     * </p>
     * <p>
     * Be aware that the returned {@link com.github.mauricio.netty.channel.ChannelPromise} will not support most operations and should only be used
     * if you want to save an object allocation for every write operation. You will not be able to detect if the
     * operation  was complete, only if it failed as the implementation will call
     * {@link ChannelPipeline#fireExceptionCaught(Throwable)} in this case.
     * </p>
     * <strong>Be aware this is an expert feature and should be used with care!</strong>
     */
    ChannelPromise voidPromise();

}
