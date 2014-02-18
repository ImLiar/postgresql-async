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
package io.netty.channel.socket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;

import java.net.InetSocketAddress;

/**
 * A TCP/IP socket {@link io.netty.channel.Channel}.
 */
public interface SocketChannel extends Channel {
    @Override
    ServerSocketChannel parent();

    @Override
    SocketChannelConfig config();
    @Override
    InetSocketAddress localAddress();
    @Override
    InetSocketAddress remoteAddress();

    /**
     * Returns {@code true} if and only if the remote peer shut down its output so that no more
     * data is received from this channel.  Note that the semantic of this method is different from
     * that of {@link java.net.Socket#shutdownInput()} and {@link java.net.Socket#isInputShutdown()}.
     */
    boolean isInputShutdown();

    /**
     * @see java.net.Socket#isOutputShutdown()
     */
    boolean isOutputShutdown();

    /**
     * @see java.net.Socket#shutdownOutput()
     */
    ChannelFuture shutdownOutput();

    /**
     * @see java.net.Socket#shutdownOutput()
     *
     * Will notify the given {@link io.netty.channel.ChannelPromise}
     */
    ChannelFuture shutdownOutput(ChannelPromise future);
}
