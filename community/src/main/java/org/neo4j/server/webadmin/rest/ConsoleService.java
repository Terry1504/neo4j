/**
 * Copyright (c) 2002-2010 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.neo4j.server.webadmin.rest;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.neo4j.server.database.Database;
import org.neo4j.server.rest.repr.BadInputException;
import org.neo4j.server.rest.repr.InputFormat;
import org.neo4j.server.rest.repr.OutputFormat;
import org.neo4j.server.rest.repr.ValueRepresentation;
import org.neo4j.server.webadmin.console.ScriptSession;
import org.neo4j.server.webadmin.rest.representations.ServiceDefinitionRepresentation;

@Path( ConsoleService.SERVICE_PATH )
public class ConsoleService implements AdvertisableService
{
    private static final String SERVICE_NAME = "console";
    static final String SERVICE_PATH = "server/console";
    private final SessionFactory sessionFactory;
    private final Database database;
    private final OutputFormat output;

    public ConsoleService( SessionFactory sessionFactory, Database database,
            OutputFormat output )
    {
        this.sessionFactory = sessionFactory;
        this.database = database;
        this.output = output;
    }

    public ConsoleService( @Context Database database,
                           @Context HttpServletRequest req,
                           @Context OutputFormat output )
    {
        this( new SessionFactoryImpl( req.getSession( true ) ), database, output );
    }

    Logger log = Logger.getLogger( ConsoleService.class );

    public String getName()
    {
        return SERVICE_NAME;
    }

    public String getServerPath()
    {
        return SERVICE_PATH;
    }

    @GET
    public Response getServiceDefinition( final @Context UriInfo uriInfo )
    {
        ServiceDefinitionRepresentation result = new ServiceDefinitionRepresentation( uriInfo.getBaseUri() + SERVICE_PATH );
        result.resourceUri( "exec", "" );

        try
        {
            return output.ok( result );
        } catch ( BadInputException e )
        {
            return output.badRequest( e );
        }
    }

    @POST
    public Response exec( @Context InputFormat input, String data )
    {
        Map<String, Object> args;
        try
        {
            args = input.readMap( data );
        }
        catch ( BadInputException e )
        {
            return output.badRequest( e );
        }

        if ( !args.containsKey( "command" ) )
        {
            return Response.status( Status.BAD_REQUEST ).entity(
                    "Expected command argument not present." ).build();
        }

        ScriptSession scriptSession = getSession( args );
        log.info( scriptSession.toString() );

        String result = scriptSession.evaluate( (String)args.get( "command" ) );

        try
        {
            return output.ok( ValueRepresentation.string( result ) );
        } catch ( BadInputException e )
        {
            return output.badRequest( e );
        }
    }

    private ScriptSession getSession( Map<String, Object> args )
    {
        return sessionFactory.createSession( (String)args.get( "engine" ), database );
    }
}
