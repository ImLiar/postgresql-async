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
package io.netty.buffer;

/**
 * Implementations are responsible to allocate buffers. Implementations of this interface are expected to be
 * thread-safe.
 */
public interface ByteBufAllocator {

    /**
     * Allocate a {@link io.netty.buffer.ByteBuf}. If it is a direct or heap buffer
     * depends on the actual implementation.
     */
    ByteBuf buffer();

    /**
     * Allocate a {@link io.netty.buffer.ByteBuf} with the given initial capacity.
     * If it is a direct or heap buffer depends on the actual implementation.
     */
    ByteBuf buffer(int initialCapacity);

    /**
     * Allocate a {@link io.netty.buffer.ByteBuf} with the given initial capacity and the given
     * maximal capacity. If it is a direct or heap buffer depends on the actual
     * implementation.
     */
    ByteBuf buffer(int initialCapacity, int maxCapacity);

    /**
     * Allocate a {@link io.netty.buffer.ByteBuf} whose initial capacity is 0, preferably a direct buffer which is suitable for I/O.
     */
    ByteBuf ioBuffer();

    /**
     * Allocate a {@link io.netty.buffer.ByteBuf}, preferably a direct buffer which is suitable for I/O.
     */
    ByteBuf ioBuffer(int initialCapacity);

    /**
     * Allocate a {@link io.netty.buffer.ByteBuf}, preferably a direct buffer which is suitable for I/O.
     */
    ByteBuf ioBuffer(int initialCapacity, int maxCapacity);

    /**
     * Allocate a heap {@link io.netty.buffer.ByteBuf}.
     */
    ByteBuf heapBuffer();

    /**
     * Allocate a heap {@link io.netty.buffer.ByteBuf} with the given initial capacity.
     */
    ByteBuf heapBuffer(int initialCapacity);

    /**
     * Allocate a heap {@link io.netty.buffer.ByteBuf} with the given initial capacity and the given
     * maximal capacity.
     */
    ByteBuf heapBuffer(int initialCapacity, int maxCapacity);

    /**
     * Allocate a direct {@link io.netty.buffer.ByteBuf}.
     */
    ByteBuf directBuffer();

    /**
     * Allocate a direct {@link io.netty.buffer.ByteBuf} with the given initial capacity.
     */
    ByteBuf directBuffer(int initialCapacity);

    /**
     * Allocate a direct {@link io.netty.buffer.ByteBuf} with the given initial capacity and the given
     * maximal capacity.
     */
    ByteBuf directBuffer(int initialCapacity, int maxCapacity);

    /**
     * Allocate a {@link io.netty.buffer.CompositeByteBuf}.
     * If it is a direct or heap buffer depends on the actual implementation.
     */
    CompositeByteBuf compositeBuffer();

    /**
     * Allocate a {@link io.netty.buffer.CompositeByteBuf} with the given maximum number of components that can be stored in it.
     * If it is a direct or heap buffer depends on the actual implementation.
     */
    CompositeByteBuf compositeBuffer(int maxNumComponents);

    /**
     * Allocate a heap {@link io.netty.buffer.CompositeByteBuf}.
     */
    CompositeByteBuf compositeHeapBuffer();

    /**
     * Allocate a heap {@link io.netty.buffer.CompositeByteBuf} with the given maximum number of components that can be stored in it.
     */
    CompositeByteBuf compositeHeapBuffer(int maxNumComponents);

    /**
     * Allocate a direct {@link io.netty.buffer.CompositeByteBuf}.
     */
    CompositeByteBuf compositeDirectBuffer();

    /**
     * Allocate a direct {@link io.netty.buffer.CompositeByteBuf} with the given maximum number of components that can be stored in it.
     */
    CompositeByteBuf compositeDirectBuffer(int maxNumComponents);

    /**
     * Returns {@code true} if direct {@link io.netty.buffer.ByteBuf}'s are pooled
     */
    boolean isDirectBufferPooled();
}
