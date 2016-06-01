package actors;

import controllers.*;
import controllers.Application.*;
import akka.actor.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import scala.concurrent.duration.Duration;
import java.util.concurrent.TimeUnit;
import java.util.Optional;



import scala.App;

public class UserActor extends UntypedActor {

    public static Props props(ActorRef out) {
        return Props.create(UserActor.class, out);
    }

    public Optional<String> optQuery = Optional.empty();

    private final ActorRef out;

    public UserActor(ActorRef out) {
        this.out = out;
    }


    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof String) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree((String)message);
            optQuery = Optional.of(json.get("place").textValue());
            fetchTweets(json.get("place").textValue());
        }
        else if (message instanceof fetchTweetsMessage) {
            optQuery.ifPresent(this::fetchTweets);
        }
        else {
            System.out.println("unhandled message");
            unhandled(message);
        }
    }

    private void fetchTweets(String place) {
        Application.fetchTweets(place).onRedeem(json -> {
            System.out.println("fetchTweets");
            out.tell(json.toString(), getSelf());
        });
    }


    private final ActorSystem system = getContext().system();
    Cancellable cancellable = system.scheduler().schedule(Duration.Zero(),
            Duration.create(5, TimeUnit.SECONDS), getSelf(), new fetchTweetsMessage(),
            system.dispatcher(), null);
}

