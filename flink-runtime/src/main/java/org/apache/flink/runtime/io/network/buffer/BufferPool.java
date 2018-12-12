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

import java.io.IOException;

/**
 * 动态大小的缓冲池。
 * A dynamically sized buffer pool.
 */
public interface BufferPool extends BufferProvider, BufferRecycler {

	/**
	 * 销毁这个缓存池
	 * Destroys this buffer pool.
	 * 如果不是所有的缓冲器都可用，它们一被回收就是被懒回收。
	 * <p>If not all buffers are available, they are recycled lazily as soon as they are recycled.
	 */
	void lazyDestroy();

	/**
	 * 检查这个缓冲池是否已经被销毁
	 * Checks whether this buffer pool has been destroyed.
	 */
	@Override
	boolean isDestroyed();

	/**
	 * 缓冲池里的memory segments数量
	 * Returns the number of guaranteed (minimum number of) memory segments of this buffer pool.
	 */
	int getNumberOfRequiredMemorySegments();

	/**
	 * 缓冲池里的memory segments的最大数量
	 * Returns the maximum number of memory segments this buffer pool should use.
	 *
	 * @return maximum number of memory segments to use or <tt>-1</tt> if unlimited
	 */
	int getMaxNumberOfMemorySegments();

	/**
	 * 缓冲池的当前容量大小
	 * Returns the current size of this buffer pool.
	 *
	 * <p>The size of the buffer pool can change dynamically at runtime.
	 */
	int getNumBuffers();

	/**
	 * 设置当前缓冲池的容量大小
	 * Sets the current size of this buffer pool.
	 *
	 * <p>The size needs to be greater or equal to the guaranteed number of memory segments.
	 */
	void setNumBuffers(int numBuffers) throws IOException;

	/**
	 * 得到当前缓冲池占用着的memory segments数目
	 * Returns the number memory segments, which are currently held by this buffer pool.
	 */
	int getNumberOfAvailableMemorySegments();

	/**
	 * Returns the number of used buffers of this buffer pool.
	 */
	int bestEffortGetNumOfUsedBuffers();
}
