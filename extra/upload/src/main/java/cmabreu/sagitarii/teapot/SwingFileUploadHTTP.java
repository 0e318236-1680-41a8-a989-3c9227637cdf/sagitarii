package cmabreu.sagitarii.teapot;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.ParseException;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.text.MaskFormatter;

import cmabreu.sagitarii.teapot.comm.Uploader;
 
/**
 * A Swing application that uploads files to a HTTP server.
 * @author www.codejava.net
 *
 */
@SuppressWarnings("serial")
public class SwingFileUploadHTTP extends JFrame implements PropertyChangeListener {
    private JLabel targetTable = new JLabel("Target Table");
    private JTextField fieldTrgtTable = new JTextField(30);
    private JLabel experimentTag = new JLabel("Experiment Tag");
    private JFormattedTextField fieldExpTag;
    private JLabel workFolder = new JLabel("Work Folder");
    private JTextField fieldWorkFolder = new JTextField(30);
    private JFilePicker filePicker = new JFilePicker("CSV Data File", "Browse");
    private JButton buttonUpload = new JButton("Upload");
    private Configurator configurator;

    
    public SwingFileUploadHTTP( Configurator configurator ) {
        super("Sagitarii Upload Tool");
    	this.configurator = configurator;
 
        setResizable(false);
        setLayout( new GridBagLayout() );
        
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(5, 5, 5, 5);
 
        MaskFormatter mf1;
		try {
			mf1 = new MaskFormatter("HHHHHHHH-HHHH-HHH");
	        mf1.setPlaceholderCharacter('_');
	        fieldExpTag = new JFormattedTextField(mf1);  
	        fieldExpTag.setColumns(30);
		} catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Parse Exception", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
		}
        
        
        filePicker.setMode(JFilePicker.MODE_OPEN);
        buttonUpload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                buttonUploadActionPerformed(event);
            }
        });
 
        // add components to the frame
        constraints.gridx = 0;
        constraints.gridy = 0;
        add(targetTable, constraints);
 
        constraints.gridx = 1;
        add(fieldTrgtTable, constraints);

        
        constraints.gridx = 0;
        constraints.gridy = 2;
        add(experimentTag, constraints);
        
        constraints.gridx = 1;
        add(fieldExpTag, constraints);

        
        constraints.gridy = 3;
        constraints.gridx = 0;
        add(workFolder, constraints);
        
        constraints.gridx = 1;
        add(fieldWorkFolder, constraints);
        
        
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.weightx = 0.0;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.WEST;
        add(filePicker, constraints);
 
        constraints.gridy = 5;
        constraints.anchor = GridBagConstraints.CENTER;
        add(buttonUpload, constraints);
 
        pack();
        setLocationRelativeTo(null);    
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
 
    /**
     * handle click event of the Upload button
     */
    private void buttonUploadActionPerformed(ActionEvent event) {
        String trgtTable = fieldTrgtTable.getText();
        String expTag = fieldExpTag.getText();
        String workFolder = fieldWorkFolder.getText();
        String csvFile = filePicker.getSelectedFilePath();
 
        if (csvFile.equals("")) {
            JOptionPane.showMessageDialog(this, "Please choose a CSV file to upload", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        
        File file = new File( csvFile );
        if( file.exists() ){
        	if ( workFolder.equals("") ) {
        		workFolder = file.getAbsolutePath().replaceAll(file.getName(), "");
        		fieldWorkFolder.setText( workFolder );
        	}
        } else {
            JOptionPane.showMessageDialog(this, "Please choose a valid CSV file to upload", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        csvFile = file.getName();
        
        if (trgtTable.equals("")) {
            JOptionPane.showMessageDialog(this, "Please enter the Target Table", "Error", JOptionPane.ERROR_MESSAGE);
            fieldTrgtTable.requestFocus();
            return;
        }

        if (expTag.equals("")) {
            JOptionPane.showMessageDialog(this, "Please enter the Experiment Tag", "Error", JOptionPane.ERROR_MESSAGE);
            fieldExpTag.requestFocus();
            return;
        }
        
        try {
        	setVisible(false);
			new Uploader(configurator).uploadCSV(csvFile, trgtTable, expTag, workFolder, configurator.getSystemProperties() );
        } catch ( Exception e ) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        System.exit(0);
        
    }
 
    /**
     * Update the progress bar's state whenever the progress of upload changes.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
    	// 
    }
 
    /**
     * Launch the application
     */
    public void runUi() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
 
    }
}