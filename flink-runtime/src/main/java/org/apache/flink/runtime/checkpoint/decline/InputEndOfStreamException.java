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
 * 表示检查点被拒绝的异常，因为其中一个输入流在对齐完成之前到达了终点。
 * Exception indicating that a checkpoint was declined because one of the input
 * stream reached its end before the alignment was complete.
 */
public final class InputEndOfStreamException extends CheckpointDeclineException {

	private static final long serialVersionUID = 1L;

	public InputEndOfStreamException() {
		super("Checkpoint was declined because one input stream is finished");
	}
}
