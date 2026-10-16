package repository;

import database.DatabaseManager;
import interfaces.IRepository;
import model.Post;
import model.files.File;
import model.files.Image;
import model.files.Video;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostRepository implements IRepository<Post> {

    @Override
    public boolean add(Post post) {
        return save(post);
    }

    public boolean save(Post post) {
        String query = "INSERT INTO Posts (posterID, text, mediaPath, mediaType, imageFormat, videoQuality, videoFormat, videoTime, parentPostID, isLocked) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, post.getPosterID());
            stmt.setString(2, post.getText());

            if (post.getMedia() != null) {
                File media = post.getMedia();
                stmt.setString(3, media.getFilePath());

                if (media instanceof Image) {
                    stmt.setString(4, "IMAGE");
                    stmt.setString(5, ((Image) media).getImgFormat().name());
                    stmt.setNull(6, Types.VARCHAR);
                    stmt.setNull(7, Types.VARCHAR);
                    stmt.setInt(8, 0);
                } else if (media instanceof Video) {
                    Video video = (Video) media;
                    stmt.setString(4, "VIDEO");
                    stmt.setNull(5, Types.VARCHAR);
                    stmt.setString(6, video.getQuality().name());
                    stmt.setString(7, video.getVideoFormat().name());
                    stmt.setInt(8, video.getVideoTime());
                }
            } else {
                stmt.setNull(3, Types.VARCHAR);
                stmt.setNull(4, Types.VARCHAR);
                stmt.setNull(5, Types.VARCHAR);
                stmt.setNull(6, Types.VARCHAR);
                stmt.setNull(7, Types.VARCHAR);
                stmt.setInt(8, 0);
            }

            if (post.getParentPostID() == 0) {
                stmt.setNull(9, Types.INTEGER);
            } else {
                stmt.setInt(9, post.getParentPostID());
            }
            stmt.setBoolean(10, post.isLocked());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        post.setPostID(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Post findById(int postId) {
        String query = "SELECT * FROM Posts WHERE postID = ? AND isDeleted = FALSE";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, postId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPost(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean likePost(int postId, int userId) {
        String insertLike = "INSERT IGNORE INTO PostLikes (postID, userID) VALUES (?, ?)";
        String updateCount = "UPDATE Posts SET likes = likes + 1 WHERE postID = ?";

        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt1 = conn.prepareStatement(insertLike);
                 PreparedStatement stmt2 = conn.prepareStatement(updateCount)) {

                stmt1.setInt(1, postId);
                stmt1.setInt(2, userId);
                int r1 = stmt1.executeUpdate();

                if (r1 > 0) {
                    stmt2.setInt(1, postId);
                    stmt2.executeUpdate();
                    conn.commit();
                    return true;
                }
                conn.rollback();
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Post> getReplies(int parentPostId) {
        List<Post> replies = new ArrayList<>();
        String query = "SELECT * FROM Posts WHERE parentPostID = ? AND isDeleted = FALSE ORDER BY postDate DESC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, parentPostId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    replies.add(mapResultSetToPost(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return replies;
    }

    private Post mapResultSetToPost(ResultSet rs) throws SQLException {
        int id = rs.getInt("postID");
        int posterId = rs.getInt("posterID");
        String text = rs.getString("text");
        java.util.Date date = rs.getTimestamp("postDate");

        Post post = new Post(id, posterId, text, date);
        post.setParentPostID(rs.getInt("parentPostID"));
        post.setViews(rs.getInt("views"));
        post.setLikes(rs.getInt("likes"));
        post.setLocked(rs.getBoolean("isLocked"));
        post.setDeleted(rs.getBoolean("isDeleted"));
        post.setEdited(rs.getBoolean("isEdited"));
        String mediaPath = rs.getString("mediaPath");
        String mediaType = rs.getString("mediaType");
        if (mediaPath != null && mediaType != null) {
            if ("IMAGE".equals(mediaType)) {
                String fmt = rs.getString("imageFormat");
                post.setMedia(new Image(mediaPath, Image.ImgFormat.valueOf(fmt)));
            } else if ("VIDEO".equals(mediaType)) {
                String qlt = rs.getString("videoQuality");
                String fmt = rs.getString("videoFormat");
                int time = rs.getInt("videoTime");
                post.setMedia(new Video(mediaPath, Video.Quality.valueOf(qlt), Video.VideoFormat.valueOf(fmt), time));
            }
        }
        post.setLikers(getLikersList(id));
        return post;
    }

    private List<Integer> getLikersList(int postId) {
        List<Integer> likers = new ArrayList<>();
        String query = "SELECT userID FROM PostLikes WHERE postID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, postId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    likers.add(rs.getInt("userID"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return likers;
    }

    @Override
    public boolean update(Post post) {

        String query = """
        UPDATE Posts
        SET
            text=?,
            mediaPath=?,
            mediaType=?,
            imageFormat=?,
            videoQuality=?,
            videoFormat=?,
            videoTime=?,
            parentPostID=?,
            isLocked=?,
            isDeleted=?,
            isEdited=?,
            likes=?,
            views=?
        WHERE postID=?
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, post.getText());

            if (post.getMedia() != null) {

                File media = post.getMedia();

                stmt.setString(2, media.getFilePath());

                if (media instanceof Image image) {

                    stmt.setString(3, "IMAGE");
                    stmt.setString(4, image.getImgFormat().name());
                    stmt.setNull(5, Types.VARCHAR);
                    stmt.setNull(6, Types.VARCHAR);
                    stmt.setInt(7, 0);

                } else {

                    Video video = (Video) media;

                    stmt.setString(3, "VIDEO");
                    stmt.setNull(4, Types.VARCHAR);
                    stmt.setString(5, video.getQuality().name());
                    stmt.setString(6, video.getVideoFormat().name());
                    stmt.setInt(7, video.getVideoTime());
                }

            } else {

                stmt.setNull(2, Types.VARCHAR);
                stmt.setNull(3, Types.VARCHAR);
                stmt.setNull(4, Types.VARCHAR);
                stmt.setNull(5, Types.VARCHAR);
                stmt.setNull(6, Types.VARCHAR);
                stmt.setInt(7, 0);
            }

            stmt.setInt(8, post.getParentPostID());
            stmt.setBoolean(9, post.isLocked());
            stmt.setBoolean(10, post.isDeleted());
            stmt.setBoolean(11, post.isEdited());
            stmt.setInt(12, post.getLikes());
            stmt.setInt(13, post.getViews());
            stmt.setInt(14, post.getPostID());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean delete(int postId) {

        String query =
                "UPDATE Posts SET isDeleted = TRUE WHERE postID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, postId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public List<Post> findAll() {

        List<Post> posts = new ArrayList<>();

        String query =
                "SELECT * FROM Posts WHERE isDeleted = FALSE ORDER BY postDate DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                posts.add(mapResultSetToPost(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return posts;
    }

    public List<Post> findByPosterId(int posterId) {

        List<Post> posts = new ArrayList<>();

        String query =
                "SELECT * FROM Posts WHERE posterID=? AND isDeleted=FALSE ORDER BY postDate DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, posterId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                posts.add(mapResultSetToPost(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return posts;
    }

    public void increaseView(int postId) {

        String query =
                "UPDATE Posts SET views = views + 1 WHERE postID=?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, postId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean lockPost(int postId) {

        String query =
                "UPDATE Posts SET isLocked = TRUE WHERE postID=?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, postId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean unlockPost(int postId) {

        String query =
                "UPDATE Posts SET isLocked = FALSE WHERE postID=?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, postId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<Post> search(String keyword) {

        List<Post> posts = new ArrayList<>();

        String query =
                "SELECT * FROM Posts WHERE text LIKE ? AND isDeleted=FALSE";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + keyword + "%");

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                posts.add(mapResultSetToPost(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return posts;
    }

    public boolean addReply(int parentPostId, String replyText) {
        Post parent = findById(parentPostId);
        if (parent == null) return false;
        Post reply = new Post(0, parent.getPosterID(), replyText, new java.util.Date());
        reply.setParentPostID(parentPostId);

        return save(reply);
    }

    public List<Post> findReplies(int parentPostId) {
        return getReplies(parentPostId);
    }

    public List<Post> getPostsByUser(int userId) {

        List<Post> posts = new ArrayList<>();

        String sql =
                "SELECT * FROM Posts " +
                        "WHERE posterID=? AND isDeleted=FALSE AND isLocked=FALSE";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                posts.add(mapResultSetToPost(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return posts;
    }

    public boolean unlikePost(int postId, int userId) {

        Post post = findById(postId);

        if (post == null)
            return false;

        post.getLikers().remove(Integer.valueOf(userId));

        post.setLikes(post.getLikers().size());

        return update(post);
    }

    public List<Post> findRepliesByUser(int userId) {

        List<Post> posts = new ArrayList<>();

        String sql =
                "SELECT * FROM Posts " +
                        "WHERE posterID=? " +
                        "AND parentPostID IS NOT NULL " +
                        "AND isDeleted=FALSE " +
                        "ORDER BY postDate DESC";


        try(Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {


            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();


            while(rs.next()) {
                posts.add(mapResultSetToPost(rs));
            }


        } catch(SQLException e) {
            e.printStackTrace();
        }


        return posts;
    }



    public List<Post> findMediaByUser(int userId) {

        List<Post> posts = new ArrayList<>();

        String sql =
                "SELECT * FROM Posts " +
                        "WHERE posterID=? " +
                        "AND mediaPath IS NOT NULL " +
                        "AND isDeleted=FALSE " +
                        "ORDER BY postDate DESC";


        try(Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {


            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();


            while(rs.next()) {
                posts.add(mapResultSetToPost(rs));
            }


        } catch(SQLException e) {
            e.printStackTrace();
        }


        return posts;
    }

    public List<Post> getPopularPosts() {

        List<Post> posts = new ArrayList<>();

        String sql =
                "SELECT * FROM Posts " +
                        "WHERE isDeleted = FALSE " +
                        "ORDER BY likes DESC, views DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                posts.add(mapResultSetToPost(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return posts;
    }
}