package Service;
import java.sql.SQLException;
import java.util.Optional;
import DAO.AccountDAO;
import Model.Account;


public class AccountService {
    private final AccountDAO accountDAO;

    public AccountService() {
        this.accountDAO = new AccountDAO();
    }

    
    public Account AccountByUsername(String username) {
        return accountDAO.getAccountByUsername(username);
    }

    public Account RegisterAccount(String username, String password) throws SQLException { 
        return accountDAO.RegisterNewAccount(username, password);
    }

    public Optional<Account> retrieveAccountById(int userId) { 
        return accountDAO.retrieveById(userId);
    }    
    
    
    public Account authenticate_check(String username, String password) {
       
        Account userAccount = accountDAO.getAccountByUsername(username);       
        if (userAccount != null && userAccount.getPassword().equals(password)) {
            return userAccount;
        }

        return null; 
    }
}
