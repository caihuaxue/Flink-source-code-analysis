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

package org.apache.flink.core.fs;

import org.apache.flink.annotation.Public;

import java.io.IOException;

/**
 * A BlockLocation lists hosts, offset and length of block.
 */
@Public
public interface BlockLocation extends Comparable<BlockLocation> {

	/**
	 * 内存块放置的hosts
	 * Get the list of hosts (hostname) hosting this block.
	 *
	 * @return A list of hosts (hostname) hosting this block.
	 * @throws IOException
	 *         thrown if the list of hosts could not be retrieved
	 */
	String[] getHosts() throws IOException;

	/**
	 * 获取与此块关联的文件的开始偏移量。
	 * Get the start offset of the file associated with this block.
	 *
	 * @return The start offset of the file associated with this block.
	 */
	long getOffset();

	/**
	 * 获取块的长度。
	 *
	 * @return the length of the block
	 */
	long getLength();
}
