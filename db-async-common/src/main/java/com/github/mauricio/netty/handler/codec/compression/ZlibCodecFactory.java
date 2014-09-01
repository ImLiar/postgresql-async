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
package com.github.mauricio.netty.handler.codec.compression;

import com.github.mauricio.netty.util.internal.PlatformDependent;
import com.github.mauricio.netty.util.internal.SystemPropertyUtil;
import com.github.mauricio.netty.util.internal.logging.InternalLogger;
import com.github.mauricio.netty.util.internal.logging.InternalLoggerFactory;

/**
 * Creates a new {@link ZlibEncoder} and a new {@link ZlibDecoder}.
 */
public final class ZlibCodecFactory {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ZlibCodecFactory.class);

    private static final boolean noJdkZlibDecoder;

    static {
        noJdkZlibDecoder = SystemPropertyUtil.getBoolean("io.netty.noJdkZlibDecoder", true);
        logger.debug("-Dio.netty.noJdkZlibDecoder: {}", noJdkZlibDecoder);
    }

    public static ZlibEncoder newZlibEncoder(int compressionLevel) {
        if (PlatformDependent.javaVersion() < 7) {
            return new JZlibEncoder(compressionLevel);
        } else {
            return new JdkZlibEncoder(compressionLevel);
        }
    }

    public static ZlibEncoder newZlibEncoder(ZlibWrapper wrapper) {
        if (PlatformDependent.javaVersion() < 7) {
            return new JZlibEncoder(wrapper);
        } else {
            return new JdkZlibEncoder(wrapper);
        }
    }

    public static ZlibEncoder newZlibEncoder(ZlibWrapper wrapper, int compressionLevel) {
        if (PlatformDependent.javaVersion() < 7) {
            return new JZlibEncoder(wrapper, compressionLevel);
        } else {
            return new JdkZlibEncoder(wrapper, compressionLevel);
        }
    }

    public static ZlibEncoder newZlibEncoder(ZlibWrapper wrapper, int compressionLevel, int windowBits, int memLevel) {
        if (PlatformDependent.javaVersion() < 7) {
            return new JZlibEncoder(wrapper, compressionLevel, windowBits, memLevel);
        } else {
            return new JdkZlibEncoder(wrapper, compressionLevel);
        }
    }

    public static ZlibEncoder newZlibEncoder(byte[] dictionary) {
        if (PlatformDependent.javaVersion() < 7) {
            return new JZlibEncoder(dictionary);
        } else {
            return new JdkZlibEncoder(dictionary);
        }
    }

    public static ZlibEncoder newZlibEncoder(int compressionLevel, byte[] dictionary) {
        if (PlatformDependent.javaVersion() < 7) {
            return new JZlibEncoder(compressionLevel, dictionary);
        } else {
            return new JdkZlibEncoder(compressionLevel, dictionary);
        }
    }

    public static ZlibEncoder newZlibEncoder(int compressionLevel, int windowBits, int memLevel, byte[] dictionary) {
        if (PlatformDependent.javaVersion() < 7) {
            return new JZlibEncoder(compressionLevel, windowBits, memLevel, dictionary);
        } else {
            return new JdkZlibEncoder(compressionLevel, dictionary);
        }
    }

    public static ZlibDecoder newZlibDecoder() {
        if (PlatformDependent.javaVersion() < 7 || noJdkZlibDecoder) {
            return new JZlibDecoder();
        } else {
            return new JdkZlibDecoder();
        }
    }

    public static ZlibDecoder newZlibDecoder(ZlibWrapper wrapper) {
        if (PlatformDependent.javaVersion() < 7 || noJdkZlibDecoder) {
            return new JZlibDecoder(wrapper);
        } else {
            return new JdkZlibDecoder(wrapper);
        }
    }

    public static ZlibDecoder newZlibDecoder(byte[] dictionary) {
        if (PlatformDependent.javaVersion() < 7 || noJdkZlibDecoder) {
            return new JZlibDecoder(dictionary);
        } else {
            return new JdkZlibDecoder(dictionary);
        }
    }

    private ZlibCodecFactory() {
        // Unused
    }
}
