package com.compprog1282025.dao;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.compprog1282025.model.employee.Compensation;
import com.compprog1282025.model.employee.ContactInfo;
import com.compprog1282025.model.employee.Employee;
import com.compprog1282025.model.employee.GovernmentID;
import com.compprog1282025.model.employee.Leave;
import com.compprog1282025.model.employee.Position;
import com.compprog1282025.service.DateTimeUtil;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public class LeaveDAO implements DAO<Leave, String>{
	public static final String LEAVE_CSV_PATH = "data/leave.csv";
	private List<Leave> leaveList;
	
	public LeaveDAO() {
		this.leaveList = new ArrayList<>();
	}
	
	@Override
	public void loadData() {
		leaveList.clear();
		String filepath = LEAVE_CSV_PATH;
		try (CSVReader reader = new CSVReader(new FileReader(filepath))) {
			String[] line;
			reader.readNext();
			while ((line = reader.readNext()) != null) {
				String leaveId = line[0];
				int empNum = Integer.valueOf(line[1]);
				LocalDate leaveDate = DateTimeUtil.convertStringToDate(line[2]);
				String reason = line[3];
				String status = line[4];
				int approvingStaffNum = Integer.valueOf(line[5]);
				LocalDate approvalDate = DateTimeUtil.convertStringToDate(line[6]);
				this.leaveList.add(new Leave(leaveId, empNum, leaveDate, reason, status, approvingStaffNum, approvalDate));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void saveData() {
		try (CSVWriter writer = new com.opencsv.CSVWriter(new FileWriter(LEAVE_CSV_PATH))) {
			
			// Write header
	        writer.writeNext(new String[]{"Employee #", "Leave Date", "Status", "Approving Staff #", "Approval Date"});
	        
	        for (Leave leave : leaveList) {
	            writer.writeNext(new String[]{
	            	leave.getLeaveId(),
	                Integer.toString(leave.getEmployeeNumber()),
	                DateTimeUtil.convertDateToString(leave.getLeaveDate()),
	                leave.getLeaveReason(),
	                leave.getStatus(),
	                Integer.toString(leave.getApprovingStaffNumber()),
	                DateTimeUtil.convertDateToString(leave.getApprovalDate())
	            });
	        }

	        System.out.println("Successfully wrote to: " + LEAVE_CSV_PATH);
	        } catch (IOException e) {
	        	e.printStackTrace();
	        }
	}
	
	@Override
	public void insert(Leave leave) {
		leaveList.add(leave);
		try (CSVWriter writer = new com.opencsv.CSVWriter(new FileWriter(LEAVE_CSV_PATH, true))) {
	            writer.writeNext(new String[]{
	            	leave.getLeaveId(),
	                Integer.toString(leave.getEmployeeNumber()),
	                DateTimeUtil.convertDateToString(leave.getLeaveDate()),
	                leave.getLeaveReason(),
	                leave.getStatus(),
	                Integer.toString(leave.getApprovingStaffNumber()),
	                DateTimeUtil.convertDateToString(leave.getApprovalDate())
	            });
	        System.out.println("Successfully wrote to: " + LEAVE_CSV_PATH);
        } catch (IOException e) {
        	e.printStackTrace();
        }
	}
	
	@Override
	public void update(Leave leave) {
		// loop through userList to find user
		// if employeeNumber found, replace
		for (int i = 0; i < leaveList.size(); i++) {
			if(leaveList.get(i).getLeaveId().equalsIgnoreCase(leave.getLeaveId())) {
				leaveList.set(i, leave);
				break;
			}
		}
		saveData();
	}
	
	@Override
	public void delete(String leaveId) {
		for (int i = 0; i < leaveList.size(); i++) {
			if(leaveList.get(i).getLeaveId().equalsIgnoreCase(leaveId)) {
				leaveList.remove(i);
				break;
			}
		}
		saveData();
	}
	
	@Override
	public List<Leave> getAll() {
		return leaveList;
	}
	
	@Override
	public Leave findById(String leaveId) {
		for(int i = 0; i < leaveList.size(); i++) {
			Leave current = leaveList.get(i);
			if(current.getLeaveId().equalsIgnoreCase(leaveId)) {
				return current;
			}
		}
		return null;
	}
	

}
