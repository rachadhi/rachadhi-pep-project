package Controller;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import DAO.MessageDAO;
import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {

    private final AccountService accountService;
    private final MessageService messageService; 
    private static final Logger logger = LoggerFactory.getLogger(SocialMediaController.class);
    public SocialMediaController() {
        this.accountService = new AccountService();      
        this.messageService = new MessageService(new AccountService(), new MessageDAO());
         
      } 
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.get("example-endpoint", this::exampleHandler);
        app.post("/register", this::registerNewAccount); 
        app.post("/login", this::userLoginProcess);
        app.post("/messages", this::createNewMessage);      
        app.get("/messages", this::retrieveAllMessages);
        app.get("/messages/{message_id}", this::retrieveMessageById);  
        app.delete("/messages/{message_id}", this::deleteMessageBYMessageId);     
        app.patch("/messages/{message_id}", this::updateMessageById);    
        app.get("/accounts/{account_id}/messages", this::retrieveAllMessagesByAccountId);  

        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void exampleHandler(Context context) {
        context.json("sample text");
    }

    //new User registrations
    public void registerNewAccount(Context ctx) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Account usernewAccount = objectMapper.readValue(ctx.body(), Account.class);
            
            String username = usernewAccount.getUsername();
            String password = usernewAccount.getPassword();

            if (username.isBlank() || password.length() < 4) {
                ctx.status(400);
                return;
            }
           
            if (accountService.AccountByUsername(username) != null) {
                ctx.status(400);
                return;
            }
           
            Account newaccountregister = accountService.RegisterAccount(username, password);

            if (newaccountregister != null) {
                ctx.json(newaccountregister);
            } else {               
               ctx.status(500);
            }

        } catch (Exception e) {
            ctx.status(400);
        }
    }

     //process User logins
    private void userLoginProcess(Context ctx) {
        Account loginRequestaccount = ctx.bodyAsClass(Account.class);   
        Account authUserAccount = accountService.authenticate_check(loginRequestaccount.getUsername(), loginRequestaccount.getPassword());
        if (authUserAccount != null) {
            ctx.json(authUserAccount);
            } else {
             ctx.status(401);
            }
    }
  
    //creation of new messages
    private void createNewMessage(Context ctx) {
        try {
            logger.info("createMessage request with body", ctx.body());
        
            ObjectMapper mapper = new ObjectMapper();
            Message new_Msg = mapper.readValue(ctx.body(), Message.class);

        
            if (new_Msg.getMessage_text() == null || new_Msg.getMessage_text().isBlank()) {
            logger.error("Message not found");
            ctx.status(400);
            return;
            }
            if (new_Msg.getMessage_text().length() > 255) {
            logger.error("Message text greater than 255 length");
            ctx.status(400);
            return;
            }

        
            int postedBy = new_Msg.getPosted_by();
            if (accountService.retrieveAccountById(postedBy) == null) {
            logger.error("User not found");
            ctx.status(400);
            return;
            }    

        
           Message New_Message = messageService.createNewMsg(new_Msg);
           ctx.status(200).json(New_Message);

        
           } catch (IllegalArgumentException e) {
           logger.error("Invalid request content", e);
           ctx.status(400);
        } catch (Exception e) {
          logger.error("Creating message error", e);
          ctx.status(400);
        }
    }   
   

    //retrieve all messages
    private void retrieveAllMessages(Context ctx) {
        ctx.json(messageService.AllMessages());
    }

    //retrieve a message by its ID
    private void retrieveMessageById(Context ctx) {
        String msg_id = ctx.pathParam("message_id");
        int Msg_Id;
        try {
            Msg_Id = Integer.parseInt(msg_id);
        } catch (Exception e) {
            ctx.status(400);
            return;
        }       
       
        Message msg = messageService.getMsgById(Msg_Id);
        if (msg != null) {
            ctx.json(msg);
        } else {
            ctx.json("");
        }
    }   

    //Delete a message identified by a message ID.    
    private void deleteMessageBYMessageId(Context ctx) {
        int msgId;
        String MsgIdStr = ctx.pathParam("message_id");        
        try {
            msgId = Integer.parseInt(MsgIdStr);
        } catch (Exception e) {
            ctx.status(400);
            return;
        }
    
        Message deleted_Msg = messageService.getMsgById(msgId);
        if (deleted_Msg != null) {
            if (messageService.deleteMsg(msgId)) {
                ctx.status(200).json(deleted_Msg); 
            } else {
                ctx.status(500);
            }
        } else {
            ctx.status(200); 
        }
    }


    public void updateMessageById(Context ctx) {
        int msg_Id = Integer.parseInt(ctx.pathParam("message_id"));
        String content_Body = ctx.body();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Message revised_msg = objectMapper.readValue(content_Body, Message.class);
            
            String update_text = revised_msg.getMessage_text();
            if (update_text == null || update_text.isEmpty() || update_text.length() > 255) {
                ctx.status(400);
                return;
            }
            
            Message current_msg = messageService.getMsgById(msg_Id);
            if (current_msg == null) {
                ctx.status(400);
                return;
            }
            
            boolean checkupdatemsg = messageService.updateMsgText(msg_Id, update_text);
            if (checkupdatemsg) {                
                current_msg.setMessage_text(update_text); 
                ctx.json(current_msg);
                ctx.status(200);
            } else {
                ctx.status(400);
            }
        } catch (Exception e) {
            ctx.status(400);
        }
    }
    
     
    //retrieve all messages written by a particular user
    private void retrieveAllMessagesByAccountId(Context ctx) {
   
    String user_account_id = ctx.pathParam("account_id");
    int acc_id;
    try {
        acc_id = Integer.parseInt(user_account_id);
    } catch (Exception e) {
        ctx.status(400);
        return;
    }    
    List<Message> msg = messageService.retrieveAllMessages(acc_id);
    ctx.json(msg); 
}    
  


}
