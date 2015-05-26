package cmabreu.sagitarii.core.mail;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cmabreu.sagitarii.persistence.entity.Experiment;

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
	

	
	
	
}
