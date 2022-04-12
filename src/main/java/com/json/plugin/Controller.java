package com.json.plugin;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/api")
public class Controller {

    @GET
    @Path("/json")
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({MediaType.APPLICATION_JSON})
    public Response toJson(@QueryParam("id") String params)
    {
        return Response.ok(params).build();
    }
}

//@FormParam("issue") String issue, @FormParam("description") String description