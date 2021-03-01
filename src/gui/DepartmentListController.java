package gui;


import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable {

	@FXML
	private TableView<Department> tableViewDepartment;
	
	@FXML
	private ObservableList<Department> obsList;
	
	@FXML
	private TableColumn<Department, Integer> tableColumnId;
	
	@FXML
	private TableColumn<Department, String> tableColumnName;
	
	@FXML
	private Button btNewDepartment;
	
	private DepartmentService departmentService;
	
	public void setDepartmentService(DepartmentService departmentService) {
		
		this.departmentService = departmentService;
	}
	
	@FXML
	public void onBtNewDepartmentAction() {
		
		System.out.println("onBtNewDepartmentAction");
	}
	
	private void initializeNodes() {

		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		
		initializeNodes();
		
		Stage stage = (Stage) Main.getMainScene().getWindow();
		
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
	}

	public void updateTableView() {
		
		if (departmentService == null) {
			
			throw new IllegalStateException("Service was null"); 
		}
		
		List<Department> list = departmentService.findAll();
		
		obsList = FXCollections.observableArrayList(list);
		
		tableViewDepartment.setItems(obsList);
	}
}
