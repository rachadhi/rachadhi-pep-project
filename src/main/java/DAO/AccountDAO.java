package DAO;
import Model.Account;
import Util.ConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;


public class AccountDAO {

    
    public Account getAccountByUsername(String username) {
       Connection connection = null;
       PreparedStatement pstmt = null;
        String sql = "SELECT * FROM Account WHERE username = ?";
        try {connection = ConnectionUtil.getConnection();
             pstmt = connection.prepareStatement(sql);
             pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Account(rs.getInt("account_id"),
                        rs.getString("username"),
                        rs.getString("password"));
            }
        } 
    catch (Exception e) {
            e.printStackTrace();
        }
        return null;  

    }


    public Account RegisterNewAccount(String username, String password) throws SQLException { 
        Connection connection = null;
        PreparedStatement pstmt = null;
        String sql = "INSERT INTO Account (username, password) VALUES (?, ?)";
        connection = ConnectionUtil.getConnection();
              pstmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            int row = pstmt.executeUpdate();
        

            if (row > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int acc_Id = rs.getInt(1);
                    return new Account(acc_Id, username, password);
                }
            }      
               
            return null;        
    } 
   

    public Optional<Account> retrieveById(int user_Id) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        String sql = "SELECT * FROM accounts WHERE id = ?";
        try {connection = ConnectionUtil.getConnection();
            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, user_Id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Account userAccount = new Account(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password")
                       
                );
                return Optional.of(userAccount);
            }
        
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
      
   
}
