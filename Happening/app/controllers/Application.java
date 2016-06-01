package controllers;

import play.*;
import play.mvc.*;
import play.mvc.WebSocket;
import akka.actor.*;
import views.html.*;
import actors.*;

public class Application extends Controller {

    public Result index() {
        return ok(index.render("Happening"));
    }

    public WebSocket<String> ws() {

        return WebSocket.withActor(UserActor::props);
    }

}
