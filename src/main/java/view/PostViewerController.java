package view;

public class PostViewerController {
    private static int currentPostId;

    public static void setCurrentPostId(int id) {
        currentPostId = id;
    }

    public static int getCurrentPostId() {
        return currentPostId;
    }
}
