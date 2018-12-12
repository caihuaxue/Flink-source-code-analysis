/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.runtime.io.network.buffer;

import org.apache.flink.core.memory.MemorySegment;

import org.apache.flink.shaded.netty4.io.netty.buffer.ByteBuf;
import org.apache.flink.shaded.netty4.io.netty.buffer.ByteBufAllocator;

import java.nio.ByteBuffer;

/**
 * Wrapper for pooled {@link MemorySegment} instances with reference counting.
 *
 * <p>This is similar to Netty's <tt>ByteBuf</tt> with some extensions and restricted to the methods
 * our use cases outside Netty handling use. In particular, we use two different indexes for read
 * and write operations, i.e. the <tt>reader</tt> and <tt>writer</tt> index (size of written data),
 * which specify three regions inside the memory segment:
 * <pre>
 *     +-------------------+----------------+----------------+
 *     | discardable bytes | readable bytes | writable bytes |
 *     +-------------------+----------------+----------------+
 *     |                   |                |                |
 *     0      <=      readerIndex  <=  writerIndex   <=  max capacity
 * </pre>
 *
 * <p>Our non-Netty usages of this <tt>Buffer</tt> class either rely on the underlying {@link
 * #getMemorySegment()} directly, or on {@link ByteBuffer} wrappers of this buffer which do not
 * modify either index, so the indices need to be updated manually via {@link #setReaderIndex(int)}
 * and {@link #setSize(int)}.
 */
public interface Buffer {

	/**
	 * 返回此缓冲区是否表示缓冲区或事件。
	 *
	 * @return <tt>true</tt> if this is a real buffer, <tt>false</tt> if this is an event
	 */
	boolean isBuffer();

	/**
	 * 标记此缓冲区以表示事件。
	 */
	void tagAsEvent();

	/**
	 * Returns the underlying memory segment. This method is dangerous since it ignores read only protections and omits
	 * slices. Use it only along the {@link #getMemorySegmentOffset()}.
	 *
	 * <p>This method will be removed in the future. For writing use {@link BufferBuilder}.
	 *
	 * @return the memory segment backing this buffer
	 */
	@Deprecated
	MemorySegment getMemorySegment();

	/**
	 * This method will be removed in the future. For writing use {@link BufferBuilder}.
	 *
	 * @return the offset where this (potential slice) {@link Buffer}'s data start in the underlying memory segment.
	 */
	@Deprecated
	int getMemorySegmentOffset();

	/**
	 * 获取缓冲区的回收器。
	 *
	 * @return buffer recycler
	 */
	BufferRecycler getRecycler();

	/**
	 * 释放这个缓冲区一次，即减少引用计数，如果引用计数达到，则循环使用缓冲区。
	 * Releases this buffer once, i.e. reduces the reference count and recycles the buffer if the
	 * reference count reaches <tt>0</tt>.
	 *
	 * @see #retainBuffer()
	 */
	void recycleBuffer();

	/**
	 * 返回此缓冲区是否已被回收。
	 *
	 * @return <tt>true</tt> if already recycled, <tt>false</tt> otherwise
	 */
	boolean isRecycled();

	/**
	 * 保留缓存区进一步地应用
	 * Retains this buffer for further use, increasing the reference counter by <tt>1</tt>.
	 *
	 * @return <tt>this</tt> instance (for chained calls)
	 *
	 * @see #recycleBuffer()
	 */
	Buffer retainBuffer();

	/**
	 * 返回此缓冲区的可读字节的只读切片。
	 * Returns a read-only slice of this buffer's readable bytes, i.e. between
	 * {@link #getReaderIndex()} and {@link #getSize()}.
	 *
	 * <p>Reader and writer indices as well as markers are not shared. Reference counters are
	 * shared but the slice is not {@link #retainBuffer() retained} automatically.
	 *
	 * @return a read-only sliced buffer
	 */
	Buffer readOnlySlice();

	/**
	 * Returns a read-only slice of this buffer.
	 *
	 * <p>Reader and writer indices as well as markers are not shared. Reference counters are
	 * shared but the slice is not {@link #retainBuffer() retained} automatically.
	 *
	 * @param index the index to start from
	 * @param length the length of the slice
	 *
	 * @return a read-only sliced buffer
	 */
	Buffer readOnlySlice(int index, int length);

	/**
	 * 得到缓冲区的最大容量
	 * Returns the maximum size of the buffer, i.e. the capacity of the underlying {@link MemorySegment}.
	 *
	 * @return size of the buffer
	 */
	int getMaxCapacity();

	/**
	 * 得到 缓冲区的读索引
	 * Returns the <tt>reader index</tt> of this buffer.
	 *
	 * <p>This is where readable (unconsumed) bytes start in the backing memory segment.
	 *
	 * @return reader index (from 0 (inclusive) to the size of the backing {@link MemorySegment}
	 * (inclusive))
	 */
	int getReaderIndex();

	/**
	 * 设置缓存区的读索引
	 * Sets the <tt>reader index</tt> of this buffer.
	 *
	 * @throws IndexOutOfBoundsException
	 * 		if the index is less than <tt>0</tt> or greater than {@link #getSize()}
	 */
	void setReaderIndex(int readerIndex) throws IndexOutOfBoundsException;

	/**
	 * 以非同步方式返回写入数据的大小，即此缓冲区的写入器索引。
	 * Returns the size of the written data, i.e. the <tt>writer index</tt>, of this buffer in an
	 * non-synchronized fashion.
	 *
	 * 这是可写字节在后备内存段中启动的地方。
	 * <p>This is where writable bytes start in the backing memory segment.
	 *
	 * @return writer index (from 0 (inclusive) to the size of the backing {@link MemorySegment}
	 * (inclusive))
	 */
	int getSizeUnsafe();

	/**
	 * Returns the size of the written data, i.e. the <tt>writer index</tt>, of this buffer.
	 *
	 * <p>This is where writable bytes start in the backing memory segment.
	 *
	 * @return writer index (from 0 (inclusive) to the size of the backing {@link MemorySegment}
	 * (inclusive))
	 */
	int getSize();

	/**
	 * Sets the size of the written data, i.e. the <tt>writer index</tt>, of this buffer.
	 *
	 * @throws IndexOutOfBoundsException
	 * 		if the index is less than {@link #getReaderIndex()} or greater than {@link #getMaxCapacity()}
	 */
	void setSize(int writerIndex);

	/**
	 * Returns the number of readable bytes (same as <tt>{@link #getSize()} -
	 * {@link #getReaderIndex()}</tt>).
	 */
	int readableBytes();

	/**
	 * Gets a new {@link ByteBuffer} instance wrapping this buffer's readable bytes, i.e. between
	 * {@link #getReaderIndex()} and {@link #getSize()}.
	 *
	 * <p>Please note that neither index is updated by the returned buffer.
	 *
	 * @return byte buffer sharing the contents of the underlying memory segment
	 */
	ByteBuffer getNioBufferReadable();

	/**
	 * Gets a new {@link ByteBuffer} instance wrapping this buffer's bytes.
	 *
	 * <p>Please note that neither <tt>read</tt> nor <tt>write</tt> index are updated by the
	 * returned buffer.
	 *
	 * @return byte buffer sharing the contents of the underlying memory segment
	 *
	 * @throws IndexOutOfBoundsException
	 * 		if the indexes are not without the buffer's bounds
	 * @see #getNioBufferReadable()
	 */
	ByteBuffer getNioBuffer(int index, int length) throws IndexOutOfBoundsException;

	/**
	 * Sets the buffer allocator for use in netty.
	 *
	 * @param allocator netty buffer allocator
	 */
	void setAllocator(ByteBufAllocator allocator);

	/**
	 * @return self as ByteBuf implementation.
	 */
	ByteBuf asByteBuf();
}
