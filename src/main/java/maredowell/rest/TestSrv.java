package maredowell.rest;

import javax.ws.rs.core.Response;

/**
 * Created by AlexKotsc on 02-03-2015.
 */
/*@Path("/")*/
public class TestSrv {

    /*@GET*/
    public Response getNode(){
        //return "Hejsa verden!";
        return Response.status(200).entity("Hejsa verden").build();
    }
}
