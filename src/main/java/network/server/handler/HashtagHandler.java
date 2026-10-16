package network.server.handler;

import model.Hashtag;
import network.shared.NetworkPacket;
import network.shared.RequestType;
import repository.HashtagRepository;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class HashtagHandler {

    private final HashtagRepository repository = new HashtagRepository();

    public void handle(NetworkPacket packet, ObjectOutputStream output) throws IOException {
        switch (packet.getRequestType()) {
            case SEARCH_HASHTAGS:
                searchHashtags(packet, output);
                break;

            case GET_POPULAR_HASHTAGS:
                sendPopularHashtags(output);
                break;
        }
    }

    private void searchHashtags(NetworkPacket packet, ObjectOutputStream output) {
        String keyword = (String) packet.getData();
        List<Hashtag> result = new ArrayList<>();
        for (Hashtag hashtag : repository.findAll()) {
            if (hashtag.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(hashtag);
            }
        }

        try {
            output.writeObject(new NetworkPacket(RequestType.SEARCH_HASHTAGS_RESPONSE, result));
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendPopularHashtags(ObjectOutputStream output) {
        List<Hashtag> hashtags = repository.findAll();
        hashtags.sort((h1, h2) -> Integer.compare(h2.getHashtagPosts().size(), h1.getHashtagPosts().size()));
        try {
            output.writeObject(new NetworkPacket(RequestType.GET_POPULAR_HASHTAGS_RESPONSE, hashtags));
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}