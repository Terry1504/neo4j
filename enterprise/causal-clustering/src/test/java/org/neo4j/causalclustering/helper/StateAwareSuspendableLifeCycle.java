/*
 * Copyright (c) 2002-2018 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j Enterprise Edition. The included source
 * code can be redistributed and/or modified under the terms of the
 * GNU AFFERO GENERAL PUBLIC LICENSE Version 3
 * (http://www.fsf.org/licensing/licenses/agpl-3.0.html) with the
 * Commons Clause, as found in the associated LICENSE.txt file.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * Neo4j object code can be licensed independently from the source
 * under separate terms from the AGPL. Inquiries can be directed to:
 * licensing@neo4j.com
 *
 * More information is also available at:
 * https://neo4j.com/licensing/
 */
package org.neo4j.causalclustering.helper;

import org.neo4j.causalclustering.helper.SuspendableLifecycleStateTestHelpers.LifeCycleState;
import org.neo4j.logging.Log;

public class StateAwareSuspendableLifeCycle extends SuspendableLifeCycle
{
    public LifeCycleState status;

    StateAwareSuspendableLifeCycle( Log debugLog )
    {
        super( debugLog );
    }

    @Override
    protected void start0()
    {
        status = LifeCycleState.Start;
    }

    @Override
    protected void stop0()
    {
        status = LifeCycleState.Stop;
    }

    @Override
    protected void shutdown0()
    {
        status = LifeCycleState.Shutdown;
    }

    @Override
    protected void init0()
    {
        status = LifeCycleState.Init;
    }
}
