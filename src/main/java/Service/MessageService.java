package Service;
import DAO.MessageDAO;
import Model.Message;
import java.util.List;
import java.sql.SQLException;


public class MessageService {
    private final MessageDAO messageDAO;
   private final AccountService accountService;

    public MessageService(AccountService accountService, MessageDAO messageDAO) {
        this.messageDAO = new MessageDAO();
        this.accountService = accountService;
    }

    
    public List<Message> AllMessages() {
        return messageDAO.getAllMessages();
    }

   
    public Message getMsgById(int msg_id) { 
        return messageDAO.getMessageById(msg_id);
    } 
  

    
    public boolean deleteMsg(int msg_id) { 
        return messageDAO.delete(msg_id);
    }

    public Message createNewMsg(Message msg) throws SQLException {      
        return messageDAO.insertMessage(msg);
    }
    
    public boolean updateMsgText(int msg_Id, String newMsgText) { 
        
        if (newMsgText == null || newMsgText.isBlank()) {
            return false;
        }

        
        Message current_Msg = messageDAO.getMessageById(msg_Id);
        if (current_Msg == null) {
            return false; 
        }

       
        return messageDAO.updateMessageText(msg_Id, newMsgText);
    }

    
    public List<Message> retrieveAllMessages(int userAccountId) { 
        return messageDAO.getAllMessagesByAccountId(userAccountId);
    }
}
