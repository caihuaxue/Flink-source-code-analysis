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

import org.apache.flink.annotation.PublicEvolving;

/**
 * An enumeration defining the kind and characteristics of a {@link FileSystem}.
 */
@PublicEvolving
public enum FileSystemKind {

	/**
	 * 真正的文件系统，包含文件和目录
	 * An actual file system, with files and directories.
	 */
	FILE_SYSTEM,

	/**
	 * 对象存储，文件对应着目录
	 * There are not really directories, but a directory-like structure may be mimicked
	 * by hierarchical naming of files.
	 */
	OBJECT_STORE
}
