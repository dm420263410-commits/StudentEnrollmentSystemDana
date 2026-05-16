package controllers;

import dao.CourseDAO;
import dao.EnrollmentDAO;
import dao.StudentDAO;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Course;
import models.Enrollment;
import models.Student;

public class EnrollmentController implements Initializable {

    @FXML
    private ComboBox<Integer> studentsComboBox;
    @FXML
    private ComboBox<Integer> coursesComboBox;
    @FXML
    private DatePicker enrollmentDate;
    @FXML
    private TableView<Enrollment> table;
    @FXML
    private TableColumn<Enrollment, Integer> enrollmentIdTC;
    @FXML
    private TableColumn<Enrollment, Integer> studentIdTC;
    @FXML
    private TableColumn<Enrollment, Integer> courseIdTC;
    @FXML
    private TableColumn<Enrollment, String> enrollmentDateTC;
    @FXML
    private TableColumn<Enrollment, String> studentNameTC;
    @FXML
    private TableColumn<Enrollment, String> courseNameTC;
    
    StudentDAO studentDAO = new StudentDAO();
    CourseDAO courseDAO = new CourseDAO();
    EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // إعداد أعمدة الجدول
        enrollmentIdTC.setCellValueFactory(new PropertyValueFactory<>("enrollmentId"));
        studentIdTC.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        courseIdTC.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        enrollmentDateTC.setCellValueFactory(new PropertyValueFactory<>("enrollmentDate"));
        studentNameTC.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        courseNameTC.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        
        // تعبئة الـ ComboBoxes بأرقام الطلاب والمواد
        List<Integer> studentIds = studentDAO.getAllStudentsIds();
        studentsComboBox.getItems().addAll(studentIds);
        
        List<Integer> courseIds = courseDAO.getAllCoursesIds();
        coursesComboBox.getItems().addAll(courseIds);
        
        // عند اختيار صف من الجدول، يتم تعبئة الحقول
        table.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue == null) return;
                studentsComboBox.setValue(newValue.getStudentId());
                coursesComboBox.setValue(newValue.getCourseId());
                enrollmentDate.setValue(newValue.getEnrollmentDate());
            }
        );
    }
    
    @FXML
    private void viewHandle(ActionEvent event) {
        List<Enrollment> enrollments = enrollmentDAO.findAll();
        table.getItems().setAll(enrollments);
        showInfoAlert("Refreshed", "Enrollments list has been refreshed");
    }
    
    @FXML
    private void enrollHandle(ActionEvent event) {
        if (enrollmentValidator()) {
            Integer studentId = studentsComboBox.getValue();
            Integer courseId = coursesComboBox.getValue();
            
            Student student = studentDAO.findById(studentId);
            Course course = courseDAO.findById(courseId);
            LocalDate date = enrollmentDate.getValue();
            
            Enrollment e = new Enrollment(student, course, date);
            boolean success = enrollmentDAO.insertOne(e);
            
            if (success) {
                clear();
                viewHandle(event);
                showInfoAlert("Success", "Enrollment Added Successfully");
            } else {
                showWarningAlert("Duplicate Enrollment", "Cannot Enroll",
                    "This student is already enrolled in this course");
            }
        } else {
            showWarningAlert("Invalid Input", "Missing Data",
                "Please select student ID, course ID, and enrollment date");
        }
    }
    
    @FXML
    private void updateHandle(ActionEvent event) {
        Enrollment e = table.getSelectionModel().getSelectedItem();
        if (e == null) {
            showWarningAlert("No Selection", "No Record Selected",
                "Please select an enrollment record from the table");
        } else if (enrollmentValidator()) {
            Student student = studentDAO.findById(studentsComboBox.getValue());
            Course course = courseDAO.findById(coursesComboBox.getValue());
            
            e.setStudent(student);
            e.setCourse(course);
            e.setEnrollmentDate(enrollmentDate.getValue());
            
            boolean success = enrollmentDAO.updateOne(e);
            if (success) {
                showInfoAlert("Success", "Enrollment Updated Successfully");
                clear();
                viewHandle(event);
            } else {
                showWarningAlert("Update Failed", "Cannot Update",
                    "The new student-course combination may already exist");
            }
        } else {
            showWarningAlert("Invalid Input", "Missing Data",
                "Please fill all fields to update");
        }
    }
    
    @FXML
    private void deleteHandle(ActionEvent event) {
        Enrollment e = table.getSelectionModel().getSelectedItem();
        if (e == null) {
            showWarningAlert("No Selection", "No Record Selected",
                "Please select an enrollment record from the table");
        } else {
            if (showConfirmationAlert("Delete Confirmation", "Are you sure?",
                "Do you want to delete this enrollment record?")) {
                enrollmentDAO.deleteOne(e);
                viewHandle(event);
                clear();
            }
        }
    }
    
    private boolean enrollmentValidator() {
        return studentsComboBox.getValue() != null && 
               coursesComboBox.getValue() != null && 
               enrollmentDate.getValue() != null;
    }
    
    private void clear() {
        studentsComboBox.setValue(null);
        coursesComboBox.setValue(null);
        enrollmentDate.setValue(null);
        table.getSelectionModel().clearSelection();
    }
    
    private void showWarningAlert(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private boolean showConfirmationAlert(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}