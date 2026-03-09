package com.compprog1282025.dao;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.compprog1282025.model.employee.Request;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public class RequestDAO implements DAO<Request, String> {
    public static final String REQUEST_CSV_PATH = "data/request.csv";
    private static final String LEGACY_REQUEST_CSV_PATH = "data/requests.csv";

    private final List<Request> requestList;

    public RequestDAO() {
        this.requestList = new ArrayList<>();
        File directory = new File("data");
        if (!directory.exists()) {
            directory.mkdir();
        }
        loadData();
    }

    @Override
    public void loadData() {
        requestList.clear();

        File source = resolveReadFile();
        if (source == null) {
            return;
        }

        try (CSVReader reader = new CSVReader(new FileReader(source))) {
            String[] line;
            reader.readNext(); // Skip header
            while ((line = reader.readNext()) != null) {
                if (line.length < 11) {
                    continue;
                }

                Request req = new Request();
                req.setRequestId(safeGet(line, 0));
                req.setEmployeeNumber(parseInt(safeGet(line, 1)));
                req.setType(safeGet(line, 2));
                req.setCategory(safeGet(line, 3));

                String start = safeGet(line, 4);
                if (!start.isEmpty()) {
                    req.setStartDate(LocalDate.parse(start));
                }

                String end = safeGet(line, 5);
                if (!end.isEmpty()) {
                    req.setEndDate(LocalDate.parse(end));
                }

                req.setStartTime(safeGet(line, 6));
                req.setEndTime(safeGet(line, 7));
                req.setReason(safeGet(line, 8));
                req.setStatus(safeGet(line, 9));

                String filed = safeGet(line, 10);
                if (!filed.isEmpty()) {
                    req.setDateFiled(LocalDate.parse(filed));
                }

                req.setApprovedByFullName(safeGet(line, 11));
                req.setApprovedByEmployeeNo(safeGet(line, 12));

                requestList.add(req);
            }
        } catch (Exception e) {
            System.out.println("No existing request file found. Creating new list.");
        }
    }

    @Override
    public void saveData() {
        // Primary output
        writeToFile(new File(REQUEST_CSV_PATH));

        // Backward compatibility output so older checks/scripts still see updates.
        writeToFile(new File(LEGACY_REQUEST_CSV_PATH));
    }

    private void writeToFile(File file) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
            writer.writeNext(new String[]{
                    "ID", "EmpNo", "Type", "Category", "Start", "End", "T-Start", "T-End",
                    "Reason", "Status", "DateFiled", "approved_by_full_name", "approved_by_employee_no"
            });

            for (Request req : requestList) {
                writer.writeNext(new String[]{
                        orEmpty(req.getRequestId()),
                        String.valueOf(req.getEmployeeNumber()),
                        orEmpty(req.getType()),
                        orEmpty(req.getCategory()),
                        req.getStartDate() != null ? req.getStartDate().toString() : "",
                        req.getEndDate() != null ? req.getEndDate().toString() : "",
                        orEmpty(req.getStartTime()),
                        orEmpty(req.getEndTime()),
                        orEmpty(req.getReason()),
                        orEmpty(req.getStatus()),
                        req.getDateFiled() != null ? req.getDateFiled().toString() : "",
                        orEmpty(req.getApprovedByFullName()),
                        orEmpty(req.getApprovedByEmployeeNo())
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File resolveReadFile() {
        File primary = new File(REQUEST_CSV_PATH);
        if (primary.exists()) {
            return primary;
        }

        File legacy = new File(LEGACY_REQUEST_CSV_PATH);
        if (legacy.exists()) {
            return legacy;
        }

        return null;
    }

    @Override
    public void insert(Request request) {
        requestList.add(request);
        saveData();
    }

    @Override
    public void update(Request request) {
        for (int i = 0; i < requestList.size(); i++) {
            if (requestList.get(i).getRequestId().equals(request.getRequestId())) {
                requestList.set(i, request);
                break;
            }
        }
        saveData();
    }

    public void deleteByEmployeeNumber(int employeeNumber) {
        requestList.removeIf(req -> req.getEmployeeNumber() == employeeNumber);
        saveData();
    }

    @Override
    public void delete(String requestId) {
        requestList.removeIf(req -> req.getRequestId().equals(requestId));
        saveData();
    }

    @Override
    public List<Request> getAll() {
        return requestList;
    }

    @Override
    public Request findById(String requestId) {
        return requestList.stream()
                .filter(req -> req.getRequestId().equals(requestId))
                .findFirst()
                .orElse(null);
    }

    private static String safeGet(String[] row, int index) {
        return index < row.length && row[index] != null ? row[index] : "";
    }

    private static int parseInt(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }
        return Integer.parseInt(value.trim());
    }

    private static String orEmpty(String value) {
        return value == null ? "" : value;
    }
}
