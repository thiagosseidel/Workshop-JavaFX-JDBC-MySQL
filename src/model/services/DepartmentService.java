package model.services;

import java.util.ArrayList;
import java.util.List;

import model.entities.Department;

public class DepartmentService {

	public List<Department> findAll() {
		
		List<Department> listResult = new ArrayList<Department>();
		
		listResult.add(new Department(1, "Books"));
		listResult.add(new Department(2, "Computers"));
		listResult.add(new Department(3, "Electronics"));
		
		return listResult;
	}
}
