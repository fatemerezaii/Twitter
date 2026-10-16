package repository;

import database.DatabaseManager;
import interfaces.IRepository;
import model.accounts.users.NormalUser;
import model.accounts.users.User;
import model.accounts.users.premiumUsers.BlueUser;
import model.accounts.users.premiumUsers.GoldUser;
import model.accounts.users.premiumUsers.PremiumUser;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository implements IRepository<User> {
    @Override
    public boolean add(User user) {
        String type;
        if (user instanceof GoldUser) {
            type = "GOLD";
        } else if (user instanceof BlueUser) {
            type = "BLUE";
        } else {
            type = "NORMAL";
        }
        String query = "INSERT INTO Users " + "(username,password,fullName,email,phoneNumber,birthday,userType,credit,token,biography,blocked,accSign,expirationDate)" + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getPhoneNumber());
            if (user.getBirthday() != null) {
                stmt.setDate(6, new java.sql.Date(user.getBirthday().getTime()));
            } else {
            }
            stmt.setNull(6, Types.DATE);
            stmt.setString(7, type);
            stmt.setInt(8, user.getCredit());
            stmt.setInt(9, user.getToken());
            stmt.setString(10, user.getBiography());
            stmt.setBoolean(11, user.isBlocked());
            stmt.setString(12, user.getAccSign());

            if (user instanceof PremiumUser) {
                PremiumUser premiumUser = (PremiumUser) user;
                if (premiumUser.getExpirationDate() != null) {
                    stmt.setDate(13, new java.sql.Date(premiumUser.getExpirationDate().getTime()));
                } else {
                    stmt.setNull(13, Types.DATE);
                }
            } else {
                stmt.setNull(13, Types.DATE);
            }

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    user.setAccID(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public User findById(int id) {
        String query = "SELECT * FROM Users WHERE accID=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User login(String usernameOrEmail, String password) {
        String query = "SELECT * FROM Users " + "WHERE (username=? OR email=?) " + "AND password=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, usernameOrEmail);
            stmt.setString(2, usernameOrEmail);
            stmt.setString(3, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM Users";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public boolean update(User user) {
        String type;
        if (user instanceof GoldUser) {
            type = "GOLD";
        } else if (user instanceof BlueUser) {
            type = "BLUE";
        } else {
            type = "NORMAL";
        }
        String query = "UPDATE Users SET " + "credit=?, token=?, biography=?, blocked=?, userType=?, accSign=?, expirationDate=? " + "WHERE accID=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, user.getCredit());
            stmt.setInt(2, user.getToken());
            stmt.setString(3, user.getBiography());
            stmt.setBoolean(4, user.isBlocked());
            stmt.setString(5, type);
            stmt.setString(6, user.getAccSign());

            if (user instanceof PremiumUser premiumUser && premiumUser.getExpirationDate() != null) {
                stmt.setDate(7, new java.sql.Date(premiumUser.getExpirationDate().getTime()));
            } else {
                stmt.setNull(7, Types.DATE);
            }

            stmt.setInt(8, user.getAccID());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateProfile(User user) {
        String query = "UPDATE Users SET " + "fullName=?, email=?, phoneNumber=?, biography=?, password=? " + "WHERE accID=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPhoneNumber());
            stmt.setString(4, user.getBiography());
            stmt.setString(5, user.getPassword());
            stmt.setInt(6, user.getAccID());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String query = "DELETE FROM Users WHERE accID=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean followUser(int followerId, int followingId) {
        String query = "INSERT IGNORE INTO UserFollows(followerId,followingId) VALUES(?,?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, followerId);
            stmt.setInt(2, followingId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean unfollowUser(int followerId, int followingId) {
        String query = "DELETE FROM UserFollows WHERE followerId=? AND followingId=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, followerId);
            stmt.setInt(2, followingId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean existsUsername(String username) {
        String query = "SELECT accID FROM Users WHERE username=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean existsEmail(String email) {
        String query = "SELECT accID FROM Users WHERE email=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("accID");
        String fullName = rs.getString("fullName");
        String phone = rs.getString("phoneNumber");
        String username = rs.getString("username");
        java.util.Date birthday = rs.getDate("birthday");
        String cover = rs.getString("profileCoverPath");
        String password = rs.getString("password");
        String email = rs.getString("email");
        java.util.Date signUp = rs.getTimestamp("signUpDate");
        boolean isAdmin = rs.getBoolean("isAdmin");
        int credit = rs.getInt("credit");
        int token = rs.getInt("token");
        String biography = rs.getString("biography");
        boolean blocked = rs.getBoolean("blocked");
        String accSign = rs.getString("accSign");
        String type = rs.getString("userType");
        java.util.Date expiration = rs.getDate("expirationDate");
        User user;
        switch (type) {
            case "GOLD": user = new GoldUser(id,fullName, phone, username, birthday, cover, password, email, signUp, isAdmin, credit, token, biography, blocked, accSign, expiration);
                break;

            case "BLUE":
                user = new BlueUser(id, fullName, phone, username, birthday, cover, password, email, signUp, isAdmin, credit, token, biography, blocked, accSign, expiration);
                break;

            default:
                user = new NormalUser(id, fullName, phone, username, birthday, cover, password, email, signUp, isAdmin, credit, token, biography, blocked, accSign);
        }

        user.setFollowers(getFollowList(id, "followingId", "followerId"));
        user.setFollowings(getFollowList(id, "followerId", "followingId"));
        return user;
    }

    private List<Integer> getFollowList(int id, String matchColumn, String selectColumn) {
        List<Integer> list = new ArrayList<>();
        String query = "SELECT " + selectColumn + " FROM UserFollows WHERE " + matchColumn + "=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(rs.getInt(selectColumn));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<String> getFollowersUsernames(int userId) {
        List<String> usernames = new ArrayList<>();
        User user = findById(userId);
        if (user == null || user.getFollowers() == null) {
            return usernames;
        }
        for (Integer id : user.getFollowers()) {
            User follower = findById(id);
            if (follower != null) {
                usernames.add(follower.getUsername());
            }
        }
        return usernames;
    }

    public List<String> getFollowingsUsernames(int userId) {
        List<String> usernames = new ArrayList<>();
        User user = findById(userId);
        if (user == null || user.getFollowings() == null) {
            return usernames;
        }
        for (Integer id : user.getFollowings()) {
            User following = findById(id);
            if (following != null) {
                usernames.add(following.getUsername());
            }
        }
        return usernames;
    }

    public boolean blockUser(int id, boolean blocked) {
        String sql = "UPDATE Users SET blocked=? WHERE accID=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, blocked);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}