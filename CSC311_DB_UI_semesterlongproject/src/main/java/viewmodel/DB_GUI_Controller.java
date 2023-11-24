package viewmodel;

import java.io.IOException;


import dao.DbConnectivityClass;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Person;
import service.MyLogger;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class DB_GUI_Controller implements Initializable {

    // JavaFX UI elements
    @FXML
    TextField first_name, last_name, department, major, email, imageURL;
    @FXML
    ImageView img_view;
    @FXML
    MenuBar menuBar;
    @FXML
    private TableView<Person> tv;
    @FXML
    private TableColumn<Person, Integer> tv_id;
    @FXML
    private TableColumn<Person, String> tv_fn, tv_ln, tv_department, tv_major, tv_email;
    @FXML
    private Button deleteButton;
    @FXML
    private Button clearButton;
    @FXML
    private Button editButton;
    @FXML
    private Button addBtn;
    @FXML
    private TextField AddStatus;
    @FXML
    private TextField DeleteStatus;
    @FXML
    private TextField EditStatus;
    @FXML
    ComboBox<Major> MajorComboBox;
    private final DbConnectivityClass cnUtil = new DbConnectivityClass();
    private final ObservableList<Person> data = cnUtil.getData();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            tv_id.setCellValueFactory(new PropertyValueFactory<>("id"));
            tv_fn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            tv_ln.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            tv_department.setCellValueFactory(new PropertyValueFactory<>("department"));
            tv_major.setCellValueFactory(new PropertyValueFactory<>("major"));
            tv_email.setCellValueFactory(new PropertyValueFactory<>("email"));
            tv.setItems(data);

            editButton.disableProperty().bind(tv.getSelectionModel().selectedItemProperty().isNull());
            deleteButton.disableProperty().bind(tv.getSelectionModel().selectedItemProperty().isNull());
            addBtn.disableProperty().bind(Bindings.createBooleanBinding(() ->
                            first_name.getText().isEmpty() || last_name.getText().isEmpty() ||
                                    department.getText().isEmpty() || MajorComboBox.getValue() == null ||
                                    email.getText().isEmpty(), first_name.textProperty(), last_name.textProperty(),
                    department.textProperty(), MajorComboBox.valueProperty(), email.textProperty()));

            // Populate ComboBox for major
           MajorComboBox.setItems(FXCollections.observableArrayList(Major.values()));
            System.out.println("ComboBox Items: " + MajorComboBox.getItems()); // Debugging line

        } catch (Exception e) {
            MyLogger.makeLog("Exception occurred during initialization: " + e.getMessage());
        }
    }


    @FXML
    protected void addNewRecord() {
        if (validateFields()) {
            // Retrieve the selected Major from the ComboBox
            Major selectedMajor = MajorComboBox.getValue();

            // Check if the selected major is not null
            if (selectedMajor != null) {
                // Create a new Person object with the selected major
                Person p = new Person(first_name.getText(), last_name.getText(), department.getText(),
                        selectedMajor.name(), email.getText(), imageURL.getText());

                // Insert the new Person into the database and update UI
                cnUtil.insertUser(p);
                cnUtil.retrieveId(p);
                p.setId(cnUtil.retrieveId(p));
                data.add(p);

                // Clear the form and update status
                clearForm();
                AddStatus.setText("Record added successfully.");
                AddStatus.setOpacity(1.0);
                DeleteStatus.setOpacity(0.0);
                EditStatus.setOpacity(0.0);
            } else {
                // If major is not selected, set an error message
                AddStatus.setText("Please select a major.");
                AddStatus.setOpacity(1.0);
                DeleteStatus.setOpacity(0.0);
                EditStatus.setOpacity(0.0);
            }
        } else {
            AddStatus.setText("Please fill in all fields correctly.");
            AddStatus.setOpacity(1.0);
            DeleteStatus.setOpacity(0.0);
            EditStatus.setOpacity(0.0);
        }
    }

    private boolean validateFields() {
        // Validation logic for the form fields
        return !first_name.getText().trim().isEmpty() &&
                !last_name.getText().trim().isEmpty() &&
                !department.getText().trim().isEmpty() &&
                MajorComboBox.getValue() != null &&
                !email.getText().trim().isEmpty();
    }



    @FXML
    protected void clearForm() {
        first_name.setText("");
        last_name.setText("");
        department.setText("");
        MajorComboBox.setValue(null); // Clear ComboBox selection
        email.setText("");
        imageURL.setText("");
    }

    @FXML
    protected void logOut(ActionEvent actionEvent) {
        try {
            // Load the login.fxml file
            Parent root = loadLoginFXML();
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").getFile());
            Stage window = (Stage) menuBar.getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            MyLogger.makeLog("Error occurred while logging out: " + e.getMessage());
        }
    }

    @FXML
    protected void closeApplication() {
        Platform.exit();
    }

    @FXML
    protected void displayAbout() {
        try {
            // Load the about.fxml file for displaying information about the application
            Parent root = FXMLLoader.load(getClass().getResource("/view/about.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(root, 600, 500);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (Exception e) {
            MyLogger.makeLog("Exception occurred while displaying about: " + e.getMessage());
        }
    }

    @FXML
    protected void editRecord() {
        // Get the selected person from the table
        Person selectedPerson = tv.getSelectionModel().getSelectedItem();

        if (selectedPerson != null) {
            int index = data.indexOf(selectedPerson);

            // Ensure MajorComboBox has a selected value
            if (MajorComboBox.getValue() == null) {
                EditStatus.setText("Please select a major.");
                EditStatus.setOpacity(1.0);
                return;
            }

            // Create a new Person object with the actual ID and updated values
            Person updatedPerson = new Person(selectedPerson.getId(), first_name.getText(), last_name.getText(),
                    department.getText(), MajorComboBox.getValue().name(), email.getText(),
                    imageURL.getText());

            // Update the person in the database and in the ObservableList
            cnUtil.editUser(selectedPerson.getId(), updatedPerson);
            data.set(index, updatedPerson);
            tv.refresh();

            // Update status messages
            EditStatus.setText("Record updated successfully.");
            EditStatus.setOpacity(1.0);
            DeleteStatus.setOpacity(0.0);
            AddStatus.setOpacity(0.0);
        } else {
            EditStatus.setText("No record selected for editing.");
            EditStatus.setOpacity(1.0);
        }
    }

    @FXML
    protected void deleteRecord() {
        // Get the selected person from the table
        Person p = tv.getSelectionModel().getSelectedItem();
        int index = data.indexOf(p);

        // Delete the person from the database
        cnUtil.deleteRecord(p);

        // Remove the person from the data list
        data.remove(index);
        tv.getSelectionModel().select(index);
        DeleteStatus.setOpacity(1.0);
        EditStatus.setOpacity(0.0);
        AddStatus.setOpacity(0.0);
    }

    @FXML
    protected void showImage() {
        // Show a file chooser dialog to select an image
        File file = (new FileChooser()).showOpenDialog(img_view.getScene().getWindow());
        if (file != null) {
            img_view.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    protected void addRecord() {
        // Show a dialog to add a new user
        showSomeone();
    }

    @FXML
    protected void selectedItemTV(MouseEvent mouseEvent) {
        Person p = tv.getSelectionModel().getSelectedItem();
        first_name.setText(p.getFirstName());
        last_name.setText(p.getLastName());
        department.setText(p.getDepartment());
        MajorComboBox.setValue(Major.valueOf(p.getMajor())); // Set ComboBox value
        email.setText(p.getEmail());
        imageURL.setText(p.getImageURL());
    }

    public void lightTheme(ActionEvent actionEvent) {
        try {
            // Change the application theme to light
            Scene scene = menuBar.getScene();
            Stage stage = (Stage) scene.getWindow();
            stage.getScene().getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
            System.out.println("light " + scene.getStylesheets());

        } catch (Exception e) {
            MyLogger.makeLog("Exception occurred while changing to light theme: " + e.getMessage());
        }
    }

    public void darkTheme(ActionEvent actionEvent) {
        try {
            // Change the application theme to dark
            Stage stage = (Stage) menuBar.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/css/darkTheme.css").toExternalForm());
        } catch (Exception e) {
            MyLogger.makeLog("Exception occurred while changing to dark theme: " + e.getMessage());
        }
    }

    public void showSomeone() {
        // Show a dialog to add a new user with name, last name, email, and major selection
        Dialog<Results> dialog = new Dialog<>();
        dialog.setTitle("New User");
        dialog.setHeaderText("Please specify…");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField textField1 = new TextField("Name");
        TextField textField2 = new TextField("Last Name");
        TextField textField3 = new TextField("Email ");
        ObservableList<Major> options = FXCollections.observableArrayList(Major.values());
        ComboBox<Major> comboBox = new ComboBox<>(options);
        comboBox.getSelectionModel().selectFirst();
        dialogPane.setContent(new VBox(8, textField1, textField2, textField3, comboBox));
        Platform.runLater(textField1::requestFocus);
        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                return new Results(textField1.getText(), textField2.getText(), comboBox.getValue());
            }
            return null;
        });
        Optional<Results> optionalResult = dialog.showAndWait();
        optionalResult.ifPresent((Results results) -> {
            MyLogger.makeLog(
                    results.fname + " " + results.lname + " " + results.major);
        });
    }

    private Parent loadLoginFXML() throws IOException {
        return FXMLLoader.load(getClass().getResource("/view/login.fxml"));
    }

    private static enum Major { Business, CSC, CPIS }

    private static class Results {

        String fname;
        String lname;
        Major major;

        public Results(String name, String date, Major venue) {
            this.fname = name;
            this.lname = date;
            this.major = venue;
        }
    }
}
