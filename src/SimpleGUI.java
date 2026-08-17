import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class SimpleGUI implements ActionListener {

    //Main Menu GUI Initialization
    JFrame frame = new JFrame();
    JButton encryptFileButton = new JButton("Encrypt File");
    JButton decryptFileButton = new JButton("Decrypt File");
    JLabel title = new JLabel();

    //gettingFileInput GUI Initialization
    JButton getFileButton = new JButton();
    JButton backButton = new JButton("Back to Main Menu");
    JFileChooser fileChooser = new JFileChooser();

    //Password GUI Initialization
    JLabel passwordLabel = new JLabel("Create Password:");
    JLabel confirmLabel = new JLabel("Confirm Password:");
    JPasswordField passwordField = new JPasswordField();
    JPasswordField confirmPasswordField = new JPasswordField();
    JButton enterButton = new JButton("Enter");
    JLabel text = new JLabel();

    private File selectedFile;
    private String password;
    String whatAreWeDoing;

    SimpleGUI (){

        //Main Page GUI with Encryption and Decryption Buttons
        title.setBounds(60,75,444,111);
        title.setFont(new Font(null,Font.ITALIC,40));

        encryptFileButton.setBounds(182,320,200,30);
        encryptFileButton.addActionListener(this);
        encryptFileButton.setFocusable(false);

        decryptFileButton.setBounds(182,370,200,30);
        decryptFileButton.addActionListener(this);
        decryptFileButton.setFocusable(false);

        //gettingFileButton GUI
        getFileButton.setBounds(182,300,200,30);
        getFileButton.addActionListener(this);
        getFileButton.setFocusable(false);

        //Password GUI
        passwordLabel.setBounds(150,280,200,30);
        confirmLabel.setBounds(150,330,200,30);
        passwordField.setBounds(282,280,200,30);
        confirmPasswordField.setBounds(282,330,200,30);
        text.setBounds(182,360,200,25);

        enterButton.setBounds(247,380,70,30);
        enterButton.addActionListener(this);
        enterButton.setFocusable(false);

        //Back Button to go back to Main Menu
        backButton.setBounds(15,444,150,25);
        backButton.addActionListener(this);
        backButton.setFocusable(false);

        //Setting the Frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(564,564);
        frame.setLayout(null);
        frame.setVisible(true);

        mainPage();
    }


    //Setting up the Main Page
    void mainPage() {
        title.setText("Forest's File Encryptor");
        frame.add(title);
        frame.add(encryptFileButton);
        frame.add(decryptFileButton);
        frame.remove(backButton);
        whatAreWeDoing = "";
    }


    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource()==encryptFileButton){
            encryptionPage();
        }
        if (event.getSource()==decryptFileButton){
            decryptionPage();
        }
        if (event.getSource()==getFileButton){
            try {
                gettingFileInput();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        if (event.getSource()==backButton){
            newPage();
            mainPage();
        }
        if (event.getSource()==enterButton){
            switch (whatAreWeDoing) {
                case ("Encrypting"):
                    if (String.valueOf(passwordField.getPassword()).equals(String.valueOf(confirmPasswordField.getPassword()))) {
                        password = String.valueOf(passwordField.getPassword());
                        try {
                            FileEncryption.encryptor(selectedFile, password);
                            text.setText("Encrypted File has been created!");
                        } catch (Exception e) {
                            System.out.println("Something went horribly wrong: " + e);
                        }
                    } else if (String.valueOf(passwordField.getPassword()).isEmpty()) {
                        text.setText("Please enter a password");
                    } else {
                        text.setText("Passwords do not match");
                    }
                    break;
                case ("Decrypting"):
                    FileEncryption.decryptor(selectedFile, String.valueOf(passwordField.getPassword()), text);
                    break;
                default:
                    System.out.println("Yeah this wasn't supposed to happen."); break;
            }
        }
    }

    void encryptionPage(){
        newPage();
        getFileButton.setText("Get File to Encrypt");
        title.setText("Encryptor");
        frame.add(title);
        frame.add(getFileButton);
        whatAreWeDoing = "Encrypting";
    }

    void decryptionPage(){
        newPage();
        getFileButton.setText("Get File to Decrypt");
        title.setText("Decryptor");
        frame.add(title);
        frame.add(getFileButton);
        whatAreWeDoing = "Decrypting";
    }

    void gettingFileInput() {
        int response = fileChooser.showOpenDialog(null);
        if (response == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            if (selectedFile.exists()) {
                if (whatAreWeDoing.equals("Encrypting")) {
                    creatingPasswordPage();
                } else if (whatAreWeDoing.equals("Decrypting")) {
                    enterPasswordPage();
                }
            }
            else {
                text.setText("Could not find file");
                System.out.println("Bruh you had one job");
            }
        }

    }

    void creatingPasswordPage() {
        newPage();
        passwordLabel.setText("Create Password");
        frame.add(passwordLabel);
        frame.add(passwordField);
        frame.add(confirmLabel);
        frame.add(confirmPasswordField);
        frame.add(enterButton);
    }

    void enterPasswordPage() {
        newPage();
        passwordLabel.setText("Input Password");
        frame.add(passwordLabel);
        frame.add(passwordField);
        frame.add(enterButton);
    }

    void newPage(){
        frame.getContentPane().removeAll();
        frame.revalidate();
        frame.repaint();
        frame.add(backButton);
        text.setText("");
        frame.add(text);
        passwordField.setText("");
        confirmPasswordField.setText("");
    }
}
