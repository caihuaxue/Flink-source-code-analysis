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


/**
 * This file is based on source code from the Hadoop Project (http://hadoop.apache.org/), licensed by the Apache
 * Software Foundation (ASF) under the Apache License, Version 2.0. See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 */

package org.apache.flink.core.fs;

import org.apache.flink.annotation.Public;

/**
 * 文件辅助状态信息
 * Interface that represents the client side information for a file
 * independent of the file system.
 */
@Public
public interface FileStatus {

	/**
	 * Return the length of this file.
	 *
	 * @return the length of this file
	 */
	long getLen();

	/**
	 *Get the block size of the file.
	 *
	 * @return the number of bytes
	 */
	long getBlockSize();

	/**
	 * 获取文件的复制因子.
	 *
	 * @return the replication factor of a file.
	 */
	short getReplication();

	/**
	 * 获取文件的修改时间。
	 *
	 * @return the modification time of file in milliseconds since January 1, 1970 UTC.
	 */
	long getModificationTime();

	/**
	 * 获取文件的访问时间。
	 *
	 * @return the access time of file in milliseconds since January 1, 1970 UTC.
	 */
	long getAccessTime();

	/**
	 *检查此对象是否表示目录。
	 *
	 * @return <code>true</code> if this is a directory, <code>false</code> otherwise
	 */
	boolean isDir();

	/**
	 * 返回到FileStatus的相应路径。
	 * Returns the corresponding Path to the FileStatus.
	 *
	 * @return the corresponding Path to the FileStatus
	 */
	Path getPath();
}
