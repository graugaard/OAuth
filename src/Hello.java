/**
 * Created by jakob on 27/07/2016.
 */

import javax.ws.rs.*;

@Path("hello")
public class Hello {
    @GET
    public String hello() {
        return "Hello world!";
    }
}
