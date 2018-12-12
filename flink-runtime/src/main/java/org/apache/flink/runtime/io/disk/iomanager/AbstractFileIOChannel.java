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

package org.apache.flink.runtime.io.disk.iomanager;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.flink.util.Preconditions;

public abstract class AbstractFileIOChannel implements FileIOChannel {

	/** Logger object for channel and its subclasses */
	protected static final Logger LOG = LoggerFactory.getLogger(FileIOChannel.class);
	
	/** The ID of the underlying channel. */
	protected final FileIOChannel.ID id;
	
	/** 用于文件访问的NIO文件通道 */
	protected final FileChannel fileChannel;
	
	
	/**
	 * 为给定ID所指示的路径创建一个新的通道。
	 * 通道将IO请求分发给定的请求队列进行处理。
	 * 
	 * @param channelID The id describing the path of the file that the channel accessed.
	 * @param writeEnabled Flag describing whether the channel should be opened in read/write mode, rather
	 *                     than in read-only mode.
	 * @throws IOException Thrown, if the channel could no be opened.
	 */
	protected AbstractFileIOChannel(FileIOChannel.ID channelID, boolean writeEnabled) throws IOException {
		this.id = Preconditions.checkNotNull(channelID);
		
		try {
			@SuppressWarnings("resource")
			RandomAccessFile file = new RandomAccessFile(id.getPath(), writeEnabled ? "rw" : "r");
			this.fileChannel = file.getChannel();
		}
		catch (IOException e) {
			throw new IOException("Channel to path '" + channelID.getPath() + "' could not be opened.", e);
		}
	}
	
	// --------------------------------------------------------------------------------------------

	@Override
	public final FileIOChannel.ID getChannelID() {
		return this.id;
	}
	
	@Override
	public long getSize() throws IOException {
		FileChannel channel = fileChannel;
		return channel == null ? 0 : channel.size();
	}
	
	@Override
	public abstract boolean isClosed();
	
	@Override
	public abstract void close() throws IOException;

	@Override
	public void deleteChannel() {
		if (!isClosed() || this.fileChannel.isOpen()) {
			throw new IllegalStateException("Cannot delete a channel that is open.");
		}
	
		// make a best effort to delete the file. Don't report exceptions.
		try {
			File f = new File(this.id.getPath());
			if (f.exists()) {
				f.delete();
			}
		} catch (Throwable t) {}
	}
	
	@Override
	public void closeAndDelete() throws IOException {
		try {
			close();
		} finally {
			deleteChannel();
		}
	}

	@Override
	public FileChannel getNioFileChannel() {
		return fileChannel;
	}
}
