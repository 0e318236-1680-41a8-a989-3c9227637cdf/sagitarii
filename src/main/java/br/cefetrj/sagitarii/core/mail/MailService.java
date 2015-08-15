package br.cefetrj.sagitarii.core.mail;

import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.cefetrj.sagitarii.core.types.UserType;
import br.cefetrj.sagitarii.persistence.entity.Experiment;
import br.cefetrj.sagitarii.persistence.entity.User;
import br.cefetrj.sagitarii.persistence.services.UserService;

public class MailService {
	private Logger logger = LogManager.getLogger( this.getClass().getName() );
	
	public void notifyExperimentFinished( Experiment experiment ) throws Exception {
		logger.debug("notify " + experiment.getOwner().getUserMail() + ": experiment finished.");
		Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        String msgBody = "Your experiment " + experiment.getTagExec() + " has finished.<br>";
        msgBody += "Start date/time: " + experiment.getLastExecutionDate() + "<br>";
        msgBody += "Finish date/time: " + experiment.getFinishDateTime() + "<br>";

        try {
        	MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("sagitarii@eic.cefet-rj.br", "Sagitarii Mail Service"));
            msg.addRecipient(Message.RecipientType.TO,
                             new InternetAddress(experiment.getOwner().getUserMail(), experiment.getOwner().getFullName() ));
            msg.setSubject("Sagitarii Notification: Experiment "+experiment.getTagExec()+" Finished");
            msg.setText(msgBody, "utf-8", "html");
            Transport.send(msg);

        } catch ( Exception e) {
			logger.error( e.getMessage() );
		}
	}
	

	public void sendUserRequest( User user ) throws Exception {
		logger.debug("new user request " + user.getFullName() );
		Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        String msgBody = "A new user is requesting access to Sagitarii system.<br>";
        msgBody += "Full Name: " + user.getFullName() + "<br>";
        msgBody += "Login Name: " + user.getLoginName() + "<br>";
        msgBody += "Mail Address: " + user.getUserMail() + "<br>";

        try {
        	MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("sagitarii@eic.cefet-rj.br", "Sagitarii Mail Service"));
            
            UserService us = new UserService();
            List<User> users = us.getList( UserType.ADMIN );
            
            for ( User admin : users ) {
            	msg.addRecipient(Message.RecipientType.TO,
                             new InternetAddress(admin.getUserMail(), admin.getFullName() ));
            }
            
            msg.setSubject("User Access Request: "+ user.getFullName() );
            msg.setText(msgBody, "utf-8", "html");
            Transport.send(msg);

        } catch ( Exception e) {
			logger.error( e.getMessage() );
		}
	}
	

	public void notifyUserChange( User user ) throws Exception {
		logger.debug("send user change notification " + user.getFullName() );
		Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        String msgBody = "An administrator updated your data.<br>";
        msgBody += "Full Name: " + user.getFullName() + "<br>";
        msgBody += "Login Name: " + user.getLoginName() + "<br>";
        msgBody += "Mail Address: " + user.getUserMail() + "<br>";
        msgBody += "Type: " + user.getType() + "<br>";

        try {
        	MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("sagitarii@eic.cefet-rj.br", "Sagitarii Mail Service"));
            
            UserService us = new UserService();
            List<User> users = us.getList( UserType.ADMIN );
            
            for ( User admin : users ) {
            	msg.addRecipient(Message.RecipientType.TO,
                             new InternetAddress(admin.getUserMail(), admin.getFullName() ));
            }
            
            msg.setSubject("Your user data was updated");
            msg.setText(msgBody, "utf-8", "html");
            Transport.send(msg);

        } catch ( Exception e) {
			logger.error( e.getMessage() );
		}
	}
	
	
}
