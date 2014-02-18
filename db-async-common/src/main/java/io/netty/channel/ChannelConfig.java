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

import io.netty.buffer.ByteBufAllocator;

import java.util.Map;

/**
 * A set of configuration properties of a {@link io.netty.channel.Channel}.
 * <p>
 * Please down-cast to more specific configuration type such as
 * {@link io.netty.channel.socket.SocketChannelConfig} or use {@link #setOptions(java.util.Map)} to set the
 * transport-specific properties:
 * <pre>
 * {@link io.netty.channel.Channel} ch = ...;
 * {@link io.netty.channel.socket.SocketChannelConfig} cfg = <strong>({@link io.netty.channel.socket.SocketChannelConfig}) ch.getConfig();</strong>
 * cfg.setTcpNoDelay(false);
 * </pre>
 *
 * <h3>Option map</h3>
 *
 * An option map property is a dynamic write-only property which allows
 * the configuration of a {@link io.netty.channel.Channel} without down-casting its associated
 * {@link io.netty.channel.ChannelConfig}.  To update an option map, please call {@link #setOptions(java.util.Map)}.
 * <p>
 * All {@link io.netty.channel.ChannelConfig} has the following options:
 *
 * <table border="1" cellspacing="0" cellpadding="6">
 * <tr>
 * <th>Name</th><th>Associated setter method</th>
 * </tr><tr>
 * <td>{@link io.netty.channel.ChannelOption#CONNECT_TIMEOUT_MILLIS}</td><td>{@link #setConnectTimeoutMillis(int)}</td>
 * </tr><tr>
 * <td>{@link io.netty.channel.ChannelOption#WRITE_SPIN_COUNT}</td><td>{@link #setWriteSpinCount(int)}</td>
 * </tr><tr>
 * <td>{@link io.netty.channel.ChannelOption#ALLOCATOR}</td><td>{@link #setAllocator(io.netty.buffer.ByteBufAllocator)}</td>
 * </tr><tr>
 * <td>{@link io.netty.channel.ChannelOption#AUTO_READ}</td><td>{@link #setAutoRead(boolean)}</td>
 * </tr>
 * </table>
 * <p>
 * More options are available in the sub-types of {@link io.netty.channel.ChannelConfig}.  For
 * example, you can configure the parameters which are specific to a TCP/IP
 * socket as explained in {@link io.netty.channel.socket.SocketChannelConfig}.
 */
public interface ChannelConfig {

    /**
     * Return all set {@link io.netty.channel.ChannelOption}'s.
     */
    Map<ChannelOption<?>, Object> getOptions();

    /**
     * Sets the configuration properties from the specified {@link java.util.Map}.
     */
    boolean setOptions(Map<ChannelOption<?>, ?> options);

    /**
     * Return the value of the given {@link io.netty.channel.ChannelOption}
     */
    <T> T getOption(ChannelOption<T> option);

    /**
     * Sets a configuration property with the specified name and value.
     * To override this method properly, you must call the super class:
     * <pre>
     * public boolean setOption(ChannelOption&lt;T&gt; option, T value) {
     *     if (super.setOption(option, value)) {
     *         return true;
     *     }
     *
     *     if (option.equals(additionalOption)) {
     *         ....
     *         return true;
     *     }
     *
     *     return false;
     * }
     * </pre>
     *
     * @return {@code true} if and only if the property has been set
     */
    <T> boolean setOption(ChannelOption<T> option, T value);

    /**
     * Returns the connect timeout of the channel in milliseconds.  If the
     * {@link io.netty.channel.Channel} does not support connect operation, this property is not
     * used at all, and therefore will be ignored.
     *
     * @return the connect timeout in milliseconds.  {@code 0} if disabled.
     */
    int getConnectTimeoutMillis();

    /**
     * Sets the connect timeout of the channel in milliseconds.  If the
     * {@link io.netty.channel.Channel} does not support connect operation, this property is not
     * used at all, and therefore will be ignored.
     *
     * @param connectTimeoutMillis the connect timeout in milliseconds.
     *                             {@code 0} to disable.
     */
    ChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis);

    /**
     * Returns the maximum number of messages to read per read loop.
     * a {@link io.netty.channel.ChannelInboundHandler#channelRead(io.netty.channel.ChannelHandlerContext, Object) channelRead()} event.
     * If this value is greater than 1, an event loop might attempt to read multiple times to procure multiple messages.
     */
    int getMaxMessagesPerRead();

    /**
     * Sets the maximum number of messages to read per read loop.
     * If this value is greater than 1, an event loop might attempt to read multiple times to procure multiple messages.
     */
    ChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead);

    /**
     * Returns the maximum loop count for a write operation until
     * {@link java.nio.channels.WritableByteChannel#write(java.nio.ByteBuffer)} returns a non-zero value.
     * It is similar to what a spin lock is used for in concurrency programming.
     * It improves memory utilization and write throughput depending on
     * the platform that JVM runs on.  The default value is {@code 16}.
     */
    int getWriteSpinCount();

    /**
     * Sets the maximum loop count for a write operation until
     * {@link java.nio.channels.WritableByteChannel#write(java.nio.ByteBuffer)} returns a non-zero value.
     * It is similar to what a spin lock is used for in concurrency programming.
     * It improves memory utilization and write throughput depending on
     * the platform that JVM runs on.  The default value is {@code 16}.
     *
     * @throws IllegalArgumentException
     *         if the specified value is {@code 0} or less than {@code 0}
     */
    ChannelConfig setWriteSpinCount(int writeSpinCount);

    /**
     * Returns {@link io.netty.buffer.ByteBufAllocator} which is used for the channel
     * to allocate buffers.
     */
    ByteBufAllocator getAllocator();

    /**
     * Set the {@link io.netty.buffer.ByteBufAllocator} which is used for the channel
     * to allocate buffers.
     */
    ChannelConfig setAllocator(ByteBufAllocator allocator);

    /**
     * Returns {@link io.netty.channel.RecvByteBufAllocator} which is used for the channel
     * to allocate receive buffers.
     */
    RecvByteBufAllocator getRecvByteBufAllocator();

    /**
     * Set the {@link io.netty.buffer.ByteBufAllocator} which is used for the channel
     * to allocate receive buffers.
     */
    ChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator);

    /**
     * Returns {@code true} if and only if {@link io.netty.channel.ChannelHandlerContext#read()} will be invoked automatically so that
     * a user application doesn't need to call it at all. The default value is {@code true}.
     */
    boolean isAutoRead();

    /**
     * Sets if {@link io.netty.channel.ChannelHandlerContext#read()} will be invoked automatically so that a user application doesn't
     * need to call it at all. The default value is {@code true}.
     */
    ChannelConfig setAutoRead(boolean autoRead);

    /**
     * @deprecated From version 5.0, {@link io.netty.channel.Channel} will not be closed on write failure.
     *
     * Returns {@code true} if and only if the {@link io.netty.channel.Channel} will be closed automatically and immediately on
     * write failure.  The default is {@code false}.
     */
    @Deprecated
    boolean isAutoClose();

    /**
     * @deprecated From version 5.0, {@link io.netty.channel.Channel} will not be closed on write failure.
     *
     * Sets whether the {@link io.netty.channel.Channel} should be closed automatically and immediately on write faillure.
     * The default is {@code false}.
     */
    @Deprecated
    ChannelConfig setAutoClose(boolean autoClose);

    /**
     * Returns the high water mark of the write buffer.  If the number of bytes
     * queued in the write buffer exceeds this value, {@link io.netty.channel.Channel#isWritable()}
     * will start to return {@code false}.
     */
    int getWriteBufferHighWaterMark();

    /**
     * Sets the high water mark of the write buffer.  If the number of bytes
     * queued in the write buffer exceeds this value, {@link io.netty.channel.Channel#isWritable()}
     * will start to return {@code false}.
     */
    ChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark);

    /**
     * Returns the low water mark of the write buffer.  Once the number of bytes
     * queued in the write buffer exceeded the
     * {@linkplain #setWriteBufferHighWaterMark(int) high water mark} and then
     * dropped down below this value, {@link io.netty.channel.Channel#isWritable()} will start to return
     * {@code true} again.
     */
    int getWriteBufferLowWaterMark();

    /**
     * Sets the low water mark of the write buffer.  Once the number of bytes
     * queued in the write buffer exceeded the
     * {@linkplain #setWriteBufferHighWaterMark(int) high water mark} and then
     * dropped down below this value, {@link io.netty.channel.Channel#isWritable()} will start to return
     * {@code true} again.
     */
    ChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark);

    /**
     * Returns {@link io.netty.channel.MessageSizeEstimator} which is used for the channel
     * to detect the size of a message.
     */
    MessageSizeEstimator getMessageSizeEstimator();

    /**
     * Set the {@link io.netty.buffer.ByteBufAllocator} which is used for the channel
     * to detect the size of a message.
     */
    ChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator);
}
