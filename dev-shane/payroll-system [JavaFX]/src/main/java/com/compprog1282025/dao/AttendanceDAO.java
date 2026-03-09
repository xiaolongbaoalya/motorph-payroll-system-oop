package com.compprog1282025.dao;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.compprog1282025.model.employee.*;
import com.compprog1282025.service.DateTimeUtil;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public class AttendanceDAO implements DAO<Attendance, String> {
	public static final String ATTENDANCE_CSV_PATH = "data/attendance.csv";
	private List<Attendance> attendanceList;

    public AttendanceDAO() {
        this.attendanceList = new ArrayList<>();
        loadData(); // CRITICAL: This must be called to populate the list
    }
	@Override
	public void loadData() {
		attendanceList.clear();
		try (CSVReader reader = new CSVReader(new FileReader(ATTENDANCE_CSV_PATH))) {
			String[] line;
			String[] headers = reader.readNext();
			if(headers.length == 6) {
				while ((line = reader.readNext()) != null) {
					int employeeNumber = Integer.parseInt(line[0]);
					String lastName = line[1];
					String firstName = line[2];
					LocalDate date = DateTimeUtil.convertStringToDate(line[3]);
					LocalTime timeIn = DateTimeUtil.convertStringToTime(line[4]);
					LocalTime timeOut = DateTimeUtil.convertStringToTime(line[5]);
					String attendanceId = String.format("%d_%s_%s", employeeNumber, line[3], line[4]);
					this.attendanceList.add(new Attendance(attendanceId, employeeNumber, firstName, lastName, date, timeIn, timeOut));
				}
			} else {
				while ((line = reader.readNext()) != null) {
					String attendanceId = line[0];
					int employeeNumber = Integer.parseInt(line[1]);
					String lastName = line[2];
					String firstName = line[3];
					LocalDate date = DateTimeUtil.convertStringToDate(line[4]);
					LocalTime timeIn = DateTimeUtil.convertStringToTime(line[5]);
					LocalTime timeOut = DateTimeUtil.convertStringToTime(line[6]);
					this.attendanceList.add(new Attendance(attendanceId, employeeNumber, firstName, lastName, date, timeIn, timeOut));
				}
			}
			
		} catch (Exception e) {
			System.err.println("Warning: Error loading attendance data");
			e.printStackTrace();
		}
	}

    @Override
    public void saveData() {
        try (CSVWriter writer = new CSVWriter(new FileWriter(ATTENDANCE_CSV_PATH))) {
            writer.writeNext(new String[]{"Attendance ID", "Employee #", "Last Name", "First Name", "Date", "Log In", "Log Out"});

            for (Attendance att : attendanceList) {
                // Check if TimeOut is null before trying to convert it
                String timeOutStr = (att.getTimeOut() != null)
                        ? DateTimeUtil.convertTimeToString(att.getTimeOut())
                        : ""; // Save as empty string in CSV if null

                writer.writeNext(new String[]{
                        att.getAttendanceId(),
                        Integer.toString(att.getEmployeeNumber()),
                        att.getLastName(),
                        att.getFirstName(),
                        DateTimeUtil.convertDateToString(att.getDate()),
                        DateTimeUtil.convertTimeToString(att.getTimeIn()),
                        timeOutStr // Use the safe string here
                });
            }
            System.out.println("Successfully wrote to: " + ATTENDANCE_CSV_PATH);
        } catch (IOException e) {
            System.err.println("Warning: Error saving attendance data");
        }
    }
	
	@Override
	public void insert(Attendance attendance) {
		attendanceList.add(attendance);
		// write into file only if time out is complete / not null
		if(attendance.getTimeOut() != null) {
			try (CSVWriter writer = new com.opencsv.CSVWriter(new FileWriter(ATTENDANCE_CSV_PATH, true))) {
				writer.writeNext(new String[]{
						attendance.getAttendanceId(),
		                String.valueOf(attendance.getEmployeeNumber()),
		                attendance.getLastName(),
		                attendance.getFirstName(),
		                DateTimeUtil.convertDateToString(attendance.getDate()),
		                DateTimeUtil.convertTimeToString(attendance.getTimeIn()),
		                DateTimeUtil.convertTimeToString(attendance.getTimeOut())
		            });
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	@Override
	public void update(Attendance attendance) {
		for (int i = 0; i < attendanceList.size(); i++) {
			if(attendanceList.get(i).getAttendanceId().equalsIgnoreCase(attendance.getAttendanceId())) {
				System.out.println("employee found");
				attendanceList.set(i, attendance);
				break;
			}
		}
		saveData();
	}
	
	public void deleteByEmployeeNumber(int employeeNumber) {
		attendanceList.removeIf(att -> att.getEmployeeNumber() == employeeNumber);
		saveData();
	}

	@Override
	public void delete(String attendanceId) {
		for (int i = 0; i < attendanceList.size(); i++) {
			if(attendanceList.get(i).getAttendanceId().equalsIgnoreCase(attendanceId.trim())) {
				attendanceList.remove(i);
				break;
			}
		}
		saveData();
	}
	
	@Override
	public List<Attendance> getAll() {
		return attendanceList;
	}
	
	@Override
	public Attendance findById(String attendanceId) {
		for(int i = 0; i < attendanceList.size(); i++) {
			Attendance current = attendanceList.get(i);
			if(current.getAttendanceId().equalsIgnoreCase(attendanceId.trim())) {
				return current;
			}
		}
		return null;
	}
	
	public List<Attendance> filterById(String fullName) {
		List<Attendance> filterList = new ArrayList<>();
		for (int i = 0; i < this.attendanceList.size(); i++) {
			Attendance current = attendanceList.get(i);
			String currFullName = current.getFirstName() + " " + current.getLastName();
			if (currFullName.trim().equalsIgnoreCase(fullName.trim())) {
				filterList.add(current);
			}
		}
		return filterList;
	}
}

