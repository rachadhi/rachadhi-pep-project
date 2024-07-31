package DAO;
import Model.Message;
import Util.ConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class MessageDAO {

   
    public List<Message> getAllMessages() { 
        List<Message> msg = new ArrayList<>();
        Connection connection = null;
        PreparedStatement pstmt = null;
        String sql = "SELECT * FROM Message";
        try {connection = ConnectionUtil.getConnection();
            pstmt = connection.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                msg.add(new Message(rs.getInt("message_id"),
                        rs.getInt("posted_by"),
                        rs.getString("message_text"),
                        rs.getLong("time_posted_epoch")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msg;
    }
    
   
    public Message getMessageById(int msg_Id) { 
        Connection connection = null;
        PreparedStatement pstmt = null;
        String sql = "SELECT * FROM Message WHERE message_id = ?";
        try {connection = ConnectionUtil.getConnection();
            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, msg_Id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Message(rs.getInt("message_id"),
                        rs.getInt("posted_by"),
                        rs.getString("message_text"),
                        rs.getLong("time_posted_epoch"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public boolean delete(int msg_Id) {  
        Connection connection = null;
        PreparedStatement pstmt = null;
        String sql = "DELETE FROM Message WHERE message_id = ?";
        try {connection  = ConnectionUtil.getConnection();
            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, msg_Id);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Message insertMessage(Message m) throws SQLException {
        Connection connection = null;
        PreparedStatement pstmt = null;
        String sql = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";
        try {connection  = ConnectionUtil.getConnection();
            pstmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
    
            pstmt.setInt(1, m.getPosted_by());
            pstmt.setString(2, m.getMessage_text());
            pstmt.setLong(3, m.getTime_posted_epoch());
    
            int insert = pstmt.executeUpdate();
            System.out.println(insert + " row(s) inserted !!");
    
            if (insert > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int msgId = rs.getInt(1);
                    return new Message(msgId, m.getPosted_by(), m.getMessage_text(),
                            m.getTime_posted_epoch());
                } else {
                    throw new Exception("Failed.");
                }
            } else {
                throw new Exception("Failed");
            }
        } catch (Exception e) {
            throw new SQLException("Error creating message: " + e.getMessage());
        }
    }
    
    
    public boolean updateMessageText(int msg_id, String newMsgText) { 
        Connection connection = null;
        PreparedStatement pstmt = null;
        String sql = "UPDATE Message SET message_text = ? WHERE message_id = ?";
        try {connection  = ConnectionUtil.getConnection();
             pstmt = connection.prepareStatement(sql);
             pstmt.setString(1, newMsgText);
             pstmt.setInt(2, msg_id);
            int r = pstmt.executeUpdate();
            return r > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    
    public List<Message> getAllMessagesByAccountId(int useraccountid) { 
        Connection connection = null;
        PreparedStatement pstmt = null;
        List<Message> msg = new ArrayList<>();
        String sql = "SELECT * FROM Message WHERE posted_by = ?";
        try {connection = ConnectionUtil.getConnection();
             pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, useraccountid);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                msg.add(new Message(rs.getInt("message_id"),
                        rs.getInt("posted_by"),
                        rs.getString("message_text"),
                        rs.getLong("time_posted_epoch")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msg;
    }

    
}
