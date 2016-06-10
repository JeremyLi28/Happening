package actors;

import controllers.*;
import controllers.Application.*;
import akka.actor.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.JSONException;
import org.json.JSONObject;
import scala.concurrent.duration.Duration;
import java.util.concurrent.TimeUnit;
import java.util.*;





import scala.App;
import twitter4j.TwitterException;
import twitter4j.json.DataObjectFactory;

public class UserActor extends UntypedActor {

    public static Props props(ActorRef out) {
        return Props.create(UserActor.class, out);
    }

    public Optional<String> optQuery = Optional.empty();

    private final ActorRef out;
    private static final Set<Long> tweetId = new HashSet<>();


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
//            optQuery.ifPresent(this::fetchTweets);
            fetchTweets("Irvine");
        }
        else {
            System.out.println("unhandled message");
            unhandled(message);
        }
    }

    private void fetchTweets (String place) throws TwitterException, JSONException {
//        Application.fetchTweets(place).onRedeem(json -> {
//            out.tell(json.toString(), getSelf());
//        });
        List<JSONObject> tweets = Application.fetchTweet();
        for(JSONObject tweet: tweets) {
            out.tell(tweet.toString(), getSelf());
        }
    }


    private final ActorSystem system = getContext().system();
    Cancellable cancellable = system.scheduler().schedule(Duration.Zero(),
            Duration.create(5, TimeUnit.SECONDS), getSelf(), new fetchTweetsMessage(),
            system.dispatcher(), null);

    @Override
    public void postStop() {
        cancellable.cancel();
    }
}

