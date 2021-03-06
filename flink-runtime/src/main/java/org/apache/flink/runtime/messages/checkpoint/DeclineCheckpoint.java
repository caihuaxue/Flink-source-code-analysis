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

package org.apache.flink.runtime.messages.checkpoint;

import org.apache.flink.api.common.JobID;
import org.apache.flink.runtime.checkpoint.decline.AlignmentLimitExceededException;
import org.apache.flink.runtime.checkpoint.decline.CheckpointDeclineOnCancellationBarrierException;
import org.apache.flink.runtime.checkpoint.decline.CheckpointDeclineSubsumedException;
import org.apache.flink.runtime.checkpoint.decline.CheckpointDeclineTaskNotCheckpointingException;
import org.apache.flink.runtime.checkpoint.decline.CheckpointDeclineTaskNotReadyException;
import org.apache.flink.runtime.checkpoint.decline.InputEndOfStreamException;
import org.apache.flink.runtime.executiongraph.ExecutionAttemptID;
import org.apache.flink.util.SerializedThrowable;

/**
 * 拒绝Checkpoint
 * This message is sent from the {@link org.apache.flink.runtime.taskmanager.TaskManager} to the
 * {@link org.apache.flink.runtime.jobmanager.JobManager} to tell the checkpoint coordinator
 * that a checkpoint request could not be heeded. This can happen if a Task is already in
 * RUNNING state but is internally not yet ready to perform checkpoints.
 */
public class DeclineCheckpoint extends AbstractCheckpointMessage implements java.io.Serializable {

	private static final long serialVersionUID = 2094094662279578953L;

	/** The reason why the checkpoint was declined */
	private final Throwable reason;

	public DeclineCheckpoint(JobID job, ExecutionAttemptID taskExecutionId, long checkpointId) {
		this(job, taskExecutionId, checkpointId, null);
	}

	public DeclineCheckpoint(JobID job, ExecutionAttemptID taskExecutionId, long checkpointId, Throwable reason) {
		super(job, taskExecutionId, checkpointId);
		
		if (reason == null ||
			reason.getClass() == AlignmentLimitExceededException.class ||
			reason.getClass() == CheckpointDeclineOnCancellationBarrierException.class ||
			reason.getClass() == CheckpointDeclineSubsumedException.class ||
			reason.getClass() == CheckpointDeclineTaskNotCheckpointingException.class ||
			reason.getClass() == CheckpointDeclineTaskNotReadyException.class ||
			reason.getClass() == InputEndOfStreamException.class)
		{
			// null or known common exceptions that cannot reference any dynamically loaded code
			this.reason = reason;
		} else {
			// some other exception. replace with a serialized throwable, to be on the safe side
			this.reason = new SerializedThrowable(reason);
		}
	}

	// --------------------------------------------------------------------------------------------

	/**
	 * 获取检查点被拒绝的原因。
	 * 
	 * @return The reason why the checkpoint was declined
	 */
	public Throwable getReason() {
		return reason;
	}

	// --------------------------------------------------------------------------------------------

	@Override
	public String toString() {
		return String.format("Declined Checkpoint %d for (%s/%s): %s",
				getCheckpointId(), getJob(), getTaskExecutionId(), reason);
	}
}
