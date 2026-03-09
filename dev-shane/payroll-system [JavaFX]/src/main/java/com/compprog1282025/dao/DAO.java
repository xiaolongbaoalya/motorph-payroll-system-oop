package com.compprog1282025.dao;

import java.util.ArrayList;
import java.util.List;

public interface DAO<T, ID> {
	
	void loadData();
	void saveData();
	void insert(T entity);
	void update(T entity);
	void delete(ID id);
	T findById(ID id);
	List<T> getAll();
}
