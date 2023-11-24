package viewmodel;

import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class SignUpController {
    @FXML
    private TextField EnterFirstName, EnterLastName, EmailText, DOBText, ZipCodeText, PhoneNumberText;
    @FXML
    private Button RegisterButton;
    @FXML
    private Button logInButton;
    @FXML
    private Text firstNameError, lastNameError, emailError, dobError, zipCodeError, InvalidPhoneNumber, SucessfulReg;

    @FXML
    protected void initialize() {
        hideErrorMessages();
    }
//still needs work
    private void hideErrorMessages() {
        firstNameError.setOpacity(0.0);
        lastNameError.setOpacity(0.0);
        emailError.setOpacity(0.0);
        dobError.setOpacity(0.0);
        zipCodeError.setOpacity(0.0);
        InvalidPhoneNumber.setOpacity(0.0);
        SucessfulReg.setOpacity(0.0);
    }

    @FXML
    protected void ClickButton() {
        CheckFirstName();
        CheckLastName();
        CheckEmail();
        CheckDOB();
        EnterZipCode();
        CheckPhoneNumber();

        checkAllFieldsValid();

        if (isAllValid()) {
            createNewAccount(null); // Pass null or an appropriate ActionEvent
        }
    }

    private boolean isValid(String input, String pattern) {
        return input.matches(pattern);
    }

    private void checkAllFieldsValid() {
        if (isAllValid()) {
            SucessfulReg.setOpacity(1.0);
        } else {
            SucessfulReg.setOpacity(0.0);
        }
    }

    private boolean isAllValid() {
        return firstNameError.getOpacity() == 0.0 && lastNameError.getOpacity() == 0.0 &&
                emailError.getOpacity() == 0.0 && dobError.getOpacity() == 0.0 &&
                zipCodeError.getOpacity() == 0.0 && InvalidPhoneNumber.getOpacity() == 0.0;
    }
    //checks to make sure the first name has the correct pattern. it covers both lower case and upper case letters for words whos letters range from 2 to 25
    //if the pattern fails, the error text will lose its opacity and be visible
    @FXML
    protected void CheckFirstName() {
        if(!isValid(EnterFirstName.getText(), "[A-Za-z]{2,25}")){
            firstNameError.setOpacity(1.0);
        } else {
            firstNameError.setOpacity(0.0);
        }
    }
    //checks to make sure the last name has the correct pattern. it covers both lower case and upper case letters for words whos letters range from 2 to 25
    //if the pattern fails, the error text will lose its opacity and be visible
    @FXML
    protected void CheckLastName() {
        if(!isValid(EnterLastName.getText(), "[A-Za-z]{2,25}")){
            lastNameError.setOpacity(1.0);
        } else {
            lastNameError.setOpacity(0.0);
        }
    }
    //checks to make sure the the email has the correct pattern. it covers both upper/lower case words, numbers from 0-9 followed by any special characters like %+-
    //also checks to make sure it only accepts emails from the @farmingdale.edu
    //if the pattern fails, the error text will lose its opacity and be visible
    @FXML
    protected void CheckEmail() {
        if(!isValid(EmailText.getText(), "[a-zA-Z0-9._%+-]+@farmingdale\\.edu")){
            emailError.setOpacity(1.0);
        } else {
            emailError.setOpacity(0.0);
        }
    }
    //checks to make sure the date of birth is valid and the user has to make sure to include a slash for month/day/year
    //if the pattern fails, the error text will lose its opacity and be visible
    @FXML
    protected void CheckDOB() {
        if(!isValid(DOBText.getText(), "\\d{2}/\\d{2}/\\d{4}")){
            dobError.setOpacity(1.0);
        } else {
            dobError.setOpacity(0.0);
        }
    }
    //checks to make sure the zipcode follows the same pattern within the 5 number parameter
//if the pattern fails, the error text will lose its opacity and be visible
    @FXML
    protected void EnterZipCode() {
        if(!isValid(ZipCodeText.getText(), "\\d{5}")){
            zipCodeError.setOpacity(1.0);
        } else {
            zipCodeError.setOpacity(0.0);
        }
    }
    //checks to make sure the phone number follows the same pattern as intended 3 numbers for the area code followed by a dash, 3 numbers for the middle number followed by another slash and then finally the last 4 digits
    //if the pattern fails, the error text will lose its opacity and be visible
    @FXML
    protected void CheckPhoneNumber(){
        if(!isValid(PhoneNumberText.getText(), "\\d{3}-\\d{3}-\\d{4}")){
            InvalidPhoneNumber.setOpacity(1.0);
        }
        else{
            InvalidPhoneNumber.setOpacity(0.0);
        }
    }

    public void createNewAccount(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Registration successful!");
        alert.showAndWait();
    }

    public void goBack(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

