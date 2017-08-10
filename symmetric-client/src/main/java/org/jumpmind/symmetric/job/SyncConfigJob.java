/**
 * Licensed to JumpMind Inc under one or more contributor
 * license agreements.  See the NOTICE file distributed
 * with this work for additional information regarding
 * copyright ownership.  JumpMind Inc licenses this file
 * to you under the GNU General Public License, version 3.0 (GPLv3)
 * (the "License"); you may not use this file except in compliance
 * with the License.
 *
 * You should have received a copy of the GNU General Public License,
 * version 3.0 (GPLv3) along with this library; if not, see
 * <http://www.gnu.org/licenses/>.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jumpmind.symmetric.job;

import org.jumpmind.symmetric.ISymmetricEngine;
import org.jumpmind.symmetric.common.ParameterConstants;
import org.jumpmind.symmetric.service.ClusterConstants;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/*
 * Background job that checks to see if config needs to be synced from registration server
 */
public class SyncConfigJob extends AbstractJob {

    public SyncConfigJob(ISymmetricEngine engine, ThreadPoolTaskScheduler taskScheduler) {
        super("job.sync.config", engine, taskScheduler);
    }

    @Override
    public boolean isAutoStartConfigured() {
        return engine.getParameterService().is(ParameterConstants.START_SYNC_CONFIG_JOB);
    }
    
    @Override
    public String getClusterLockName() {
        return ClusterConstants.SYNC_CONFIG;
    }
    
    @Override
    public void doJob(boolean force) throws Exception {
        engine.getPullService().pullConfigData(false);
    }

}