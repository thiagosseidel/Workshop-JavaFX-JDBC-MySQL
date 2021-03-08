package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable {

	private Seller entity;

	private SellerService service;

	private DepartmentService departmentService;

	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	@FXML
	private TextField txtEmail;

	@FXML
	private DatePicker dpBirthDate;

	@FXML
	private TextField txtBaseSalary;

	@FXML
	private ComboBox<Department> comboBoxDepartment;

	@FXML
	private Label labelErrorName;

	@FXML
	private Label labelErrorEmail;

	@FXML
	private Label labelErrorBirthDate;

	@FXML
	private Label labelErrorBaseSalary;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;

	@FXML
	private ObservableList<Department> obsListDepartment;

	public void setSeller(Seller entity) {

		this.entity = entity;

		updateFormData();
	}

	public void setServices(SellerService service, DepartmentService departmentService) {

		this.service = service;
		this.departmentService = departmentService;
	}

	public void subscribeDataChangeListener(DataChangeListener listener) {

		if (listener != null) {

			dataChangeListeners.add(listener);
		}
	}

	@FXML
	public void onBtSaveAction(ActionEvent event) {

		if (service == null) {

			throw new IllegalStateException("Service was null");
		}

		try {

			updateEntityValues();

			service.saveOrUpdate(entity);

			notifyDataChangeListeners();

			Utils.currentStage(event).close();
		} catch (ValidationException e) {

			setErrorMessages(e.getErrors());
		} catch (DbException e) {

			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void notifyDataChangeListeners() {

		for (DataChangeListener listener : dataChangeListeners) {

			listener.onDataChanged();
		}
	}

	private void updateEntityValues() {

		if (entity == null) {

			entity = new Seller();
		}

		ValidationException exception = new ValidationException("Validation error.");

		entity.setId(Utils.tryParseToInt(txtId.getText()));

		if (txtName.getText() == null || txtName.getText().trim().isEmpty()) {

			exception.addError("Name", "Field can't be empty");
		}

		entity.setName(txtName.getText());
		
		
		if (txtEmail.getText() == null || txtEmail.getText().trim().isEmpty()) {

			exception.addError("Email", "Field can't be empty");
		}

		entity.setEmail(txtEmail.getText());
		
		
		if (dpBirthDate.getValue() == null) {
			
			exception.addError("BirthDate", "Field can't be empty");
		}
		else {

			Instant inst = Instant.from(dpBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));
			
			entity.setBirthDate(Date.from(inst));	
		}


		if (txtBaseSalary.getText() == null || txtBaseSalary.getText().trim().isEmpty()) {

			exception.addError("BaseSalary", "Field can't be empty");
		}

		entity.setBaseSalary(Utils.tryParseToDouble(txtBaseSalary.getText()));
		
		
		entity.setDepartment(comboBoxDepartment.getValue());
		
		if (!exception.getErrors().isEmpty()) {

			throw exception;
		}
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {

		Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rs) {

		initializeNodes();
	}

	private void initializeNodes() {

		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 70);
		Constraints.setTextFieldDouble(txtBaseSalary);
		Constraints.setTextFieldMaxLength(txtEmail, 60);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
	}

	public void updateFormData() {

		if (entity == null) {

			throw new IllegalStateException("Entity was null");
		}

		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
		txtEmail.setText(entity.getEmail());

		Locale.setDefault(Locale.US);

		txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));

		if (entity.getBirthDate() != null) {

			dpBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
		}
		
		if (entity.getDepartment() == null) {
			
			comboBoxDepartment.getSelectionModel().selectFirst();
		}
		else {
		
			comboBoxDepartment.setValue(entity.getDepartment());
		}
		
	}

	public void loadAssociatedObjects() {

		if (departmentService == null) {

			throw new IllegalStateException("DepartmentService was null");
		}

		List<Department> list = departmentService.findAll();

		obsListDepartment = FXCollections.observableArrayList(list);

		comboBoxDepartment.setItems(obsListDepartment);
		
		initializeComboBoxDepartment();
	}

	private void initializeComboBoxDepartment() {
	
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
		
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
			
				setText(empty ? "" : item.getName());
			}
		};
		
		comboBoxDepartment.setCellFactory(factory);
		comboBoxDepartment.setButtonCell(factory.call(null));
	}

	private void setErrorMessages(Map<String, String> errors) {

		Set<String> fields = errors.keySet();

		labelErrorName.setText("");
		labelErrorEmail.setText("");
		labelErrorBaseSalary.setText("");
		labelErrorBirthDate.setText("");
		
		if (fields.contains("Name")) {

			labelErrorName.setText(errors.get("Name"));
		}
		
		if (fields.contains("Email")) {

			labelErrorEmail.setText(errors.get("Email"));
		}
		
		if (fields.contains("BaseSalary")) {

			labelErrorBaseSalary.setText(errors.get("BaseSalary"));
		}
		
		if (fields.contains("BirthDate")) {

			labelErrorBirthDate.setText(errors.get("BirthDate"));
		}
	}
}
