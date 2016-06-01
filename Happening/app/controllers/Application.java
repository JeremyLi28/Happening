package controllers;

import play.*;
import play.libs.Json;
import play.mvc.*;
import play.mvc.WebSocket;
import akka.actor.*;
import views.html.*;
import actors.*;
import java.io.Serializable;
import com.fasterxml.jackson.databind.JsonNode;
import javax.inject.Inject;
import play.mvc.*;
import play.libs.ws.*;
import play.libs.F.Function;
import play.libs.F.Promise;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import static java.util.stream.Collectors.toList;
import static utils.Streams.stream;
import java.util.List;
import java.util.Random;




public class Application extends Controller {


    public static class fetchTweetsMessage implements Serializable {}

    public Result index() {
        return ok(index.render("Happening"));
    }

    public WebSocket<String> ws() {

        return WebSocket.withActor(UserActor::props);
    }

    public static Promise<JsonNode> fetchTweets(String place) {
        Promise<WSResponse> responsePromise = WS.url("http://twitter-search-proxy.herokuapp.com/search/tweets").setQueryParameter("q", place).get();
        return responsePromise
                .filter(response -> response.getStatus() == Http.Status.OK)
                .map(response -> filterTweetsWithLocation(response.asJson()))
                .recover(Application::errorResponse);
    }

    private static JsonNode filterTweetsWithLocation(JsonNode jsonNode) {
//create a stream view of the jsonNode iterator
        List<JsonNode> newJsonList = stream(jsonNode.findPath("statuses"))
                //map the stream of json to update the values to have the geo-info
                .map(json -> setCoordinates((ObjectNode) json))
                .collect(toList());

        ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
        objectNode.putArray("statuses").addAll(newJsonList);
        return objectNode;

    }

    private static ObjectNode setCoordinates(ObjectNode nextStatus) {
        nextStatus.putArray("coordinates").add(randomLat()).add(randomLon());
        return nextStatus;
    }

    private static Random rand = new java.util.Random();

    private static double randomLat() {
        return (rand.nextDouble() * (-117.822395+117.859957)) -117.859957;
    }

    private static double randomLon() {
        return (rand.nextDouble() * (33.654339 - 33.632010)) + 33.632010;

    }

    private static JsonNode errorResponse(Throwable ignored) {
        return Json.newObject().put("error", "Could not fetch the tweets");
    }

}
