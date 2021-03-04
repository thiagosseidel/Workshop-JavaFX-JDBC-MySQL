package gui;

import java.net.URL;
import java.util.ResourceBundle;

import db.DbException;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {

	private Department entity;
	
	private DepartmentService service;
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	public void setDepartment(Department entity) {
		
		this.entity = entity;
		
		updateFormData();
	}
	
	public void setDepartmentService(DepartmentService service) {
		
		this.service = service;
	}
	
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		
		if (service == null) {
			
			throw new IllegalStateException("Service was null");		
		}
		
		updateEntityValues();
		
		try {
			
			service.saveOrUpdate(entity);
			
			Utils.currentStage(event).close();
		}
		catch(DbException e) {
			
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	private void updateEntityValues() {
		
		if (entity == null) {
			
			entity = new Department();
		}
		
		entity.setId(Utils.tryParseToInt(txtId.getText()));
		entity.setName(txtName.getText());
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
		Constraints.setTextFieldMaxLength(txtName, 30);
	}
	
	public void updateFormData() {
		
		if (entity == null) {
			
			throw new IllegalStateException("Entity was null");
		}
		
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
	}
}
