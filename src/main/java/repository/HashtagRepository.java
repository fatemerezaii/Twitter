package repository;

import database.DatabaseManager;
import interfaces.IRepository;
import model.Hashtag;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HashtagRepository implements IRepository<Hashtag> {

    public boolean save(Hashtag hashtag) {
        String query = """
            INSERT INTO Hashtags (title)
            VALUES (?)
            """;
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, hashtag.getTitle());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    hashtag.setHashtagID(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private List<Integer> getPostsForHashtag(int hashtagId) {
        List<Integer> postIds = new ArrayList<>();
        String query = "SELECT postID FROM PostHashtags WHERE hashtagID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, hashtagId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    postIds.add(rs.getInt("postID"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return postIds;
    }

    @Override
    public boolean add(Hashtag hashtag) {
        return save(hashtag);
    }

    @Override
    public boolean update(Hashtag hashtag) {
        String query = """
            UPDATE Hashtags
            SET title = ?
            WHERE hashtagID = ?
            """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, hashtag.getTitle());
            stmt.setInt(2, hashtag.getHashtagID());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String query = "DELETE FROM Hashtags WHERE hashtagID=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Hashtag> findAll() {
        List<Hashtag> hashtags = new ArrayList<>();
        String query = "SELECT * FROM Hashtags";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Hashtag hashtag = new Hashtag(rs.getInt("hashtagID"), rs.getString("title"));
                hashtag.setHashtagPosts(getPostsForHashtag(hashtag.getHashtagID()));
                hashtags.add(hashtag);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hashtags;
    }

    @Override
    public Hashtag findById(int id) {
        String query = "SELECT * FROM Hashtags WHERE hashtagID=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Hashtag hashtag = new Hashtag(rs.getInt("hashtagID"), rs.getString("title"));
                hashtag.setHashtagPosts(getPostsForHashtag(hashtag.getHashtagID()));
                return hashtag;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}