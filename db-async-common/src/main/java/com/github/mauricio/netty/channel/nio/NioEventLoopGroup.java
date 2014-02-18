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
package com.github.mauricio.netty.channel.nio;

import com.github.mauricio.netty.channel.MultithreadEventLoopGroup;
import com.github.mauricio.netty.util.concurrent.EventExecutor;

import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.ThreadFactory;

/**
 * {@link MultithreadEventLoopGroup} implementations which is used for NIO {@link java.nio.channels.Selector} based {@link com.github.mauricio.netty.channel.Channel}s.
 */
public class NioEventLoopGroup extends MultithreadEventLoopGroup {

    /**
     * Create a new instance using the default number of threads, the default {@link java.util.concurrent.ThreadFactory} and
     * the {@link java.nio.channels.spi.SelectorProvider} which is returned by {@link java.nio.channels.spi.SelectorProvider#provider()}.
     */
    public NioEventLoopGroup() {
        this(0);
    }

    /**
     * Create a new instance using the specified number of threads, {@link java.util.concurrent.ThreadFactory} and the
     * {@link java.nio.channels.spi.SelectorProvider} which is returned by {@link java.nio.channels.spi.SelectorProvider#provider()}.
     */
    public NioEventLoopGroup(int nThreads) {
        this(nThreads, null);
    }

    /**
     * Create a new instance using the specified number of threads, the given {@link java.util.concurrent.ThreadFactory} and the
     * {@link java.nio.channels.spi.SelectorProvider} which is returned by {@link java.nio.channels.spi.SelectorProvider#provider()}.
     */
    public NioEventLoopGroup(int nThreads, ThreadFactory threadFactory) {
        this(nThreads, threadFactory, SelectorProvider.provider());
    }

    /**
     * Create a new instance using the specified number of threads, the given {@link java.util.concurrent.ThreadFactory} and the given
     * {@link java.nio.channels.spi.SelectorProvider}.
     */
    public NioEventLoopGroup(
            int nThreads, ThreadFactory threadFactory, final SelectorProvider selectorProvider) {
        super(nThreads, threadFactory, selectorProvider);
    }

    /**
     * Sets the percentage of the desired amount of time spent for I/O in the child event loops.  The default value is
     * {@code 50}, which means the event loop will try to spend the same amount of time for I/O as for non-I/O tasks.
     */
    public void setIoRatio(int ioRatio) {
        for (EventExecutor e: children()) {
            ((NioEventLoop) e).setIoRatio(ioRatio);
        }
    }

    /**
     * Replaces the current {@link java.nio.channels.Selector}s of the child event loops with newly created {@link java.nio.channels.Selector}s to work
     * around the  infamous epoll 100% CPU bug.
     */
    public void rebuildSelectors() {
        for (EventExecutor e: children()) {
            ((NioEventLoop) e).rebuildSelector();
        }
    }

    @Override
    protected EventExecutor newChild(
            ThreadFactory threadFactory, Object... args) throws Exception {
        return new NioEventLoop(this, threadFactory, (SelectorProvider) args[0]);
    }
}
