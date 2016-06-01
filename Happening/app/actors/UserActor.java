package actors;

import controllers.*;
import controllers.Application.*;
import akka.actor.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import scala.concurrent.duration.Duration;
import java.util.concurrent.TimeUnit;


import scala.App;

public class UserActor extends UntypedActor {

    public static Props props(ActorRef out) {
        return Props.create(UserActor.class, out);
    }

    private final ActorRef out;

    public UserActor(ActorRef out) {
        this.out = out;
    }


    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof String) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree((String)message);
            System.out.println(json.get("place").textValue());
        }
        else if (message instanceof fetchTweetsMessage) {
            Application.fetchTweets("place").onRedeem(json -> {
                System.out.println("fetchTweets");
                out.tell(json.toString(), getSelf());
            });
        }
        else {
            System.out.println("unhandled message");
            unhandled(message);
        }
    }


    private final ActorSystem system = getContext().system();
    Cancellable cancellable = system.scheduler().schedule(Duration.Zero(),
            Duration.create(5, TimeUnit.SECONDS), getSelf(), new fetchTweetsMessage(),
            system.dispatcher(), null);
}

