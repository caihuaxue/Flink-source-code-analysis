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

package org.apache.flink.runtime.checkpoint.decline;

/**
 * 由于在未决检查点的屏障之前在输入上接收到更新的检查点屏障，因此拒绝检查点的异常。
 * Exception indicating that a checkpoint was declined because a newer checkpoint
 * barrier was received on an input before the pending checkpoint's barrier. 
 */
public final class CheckpointDeclineSubsumedException extends CheckpointDeclineException {

	private static final long serialVersionUID = 1L;

	public CheckpointDeclineSubsumedException(long newCheckpointId) {
		super("Checkpoint was canceled because a barrier from newer checkpoint " + newCheckpointId + " was received.");
	}
}
