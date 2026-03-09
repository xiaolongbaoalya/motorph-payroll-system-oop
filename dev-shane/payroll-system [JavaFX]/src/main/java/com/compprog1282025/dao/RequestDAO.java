package com.compprog1282025.dao;

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
    public static final String REQUEST_CSV_PATH = "data/requests.csv";
    private static final String LEAVE_TYPE = "LEAVE";
    private static final String DEFAULT_LEAVE_START_TIME = "00:00";
    private static final String DEFAULT_LEAVE_END_TIME = "23:59";

    private final List<Request> requestList;

    public RequestDAO() {
        this.requestList = new ArrayList<>();
        java.io.File directory = new java.io.File("data");
        if (!directory.exists()) {
            directory.mkdir();
        }
        loadData();
    }

    @Override
    public void loadData() {
        requestList.clear();
        boolean dataNormalized = false;

        try (CSVReader reader = new CSVReader(new FileReader(REQUEST_CSV_PATH))) {
            String[] line;
            reader.readNext();
            while ((line = reader.readNext()) != null) {
                if (line.length < 13) {
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

                if (normalizeLeaveTimes(req)) {
                    dataNormalized = true;
                }

                requestList.add(req);
            }
        } catch (Exception e) {
            System.out.println("No existing requests file found. Creating new list.");
        }

        if (dataNormalized) {
            saveData();
        }
    }

    @Override
    public void saveData() {
        try (CSVWriter writer = new CSVWriter(new FileWriter(REQUEST_CSV_PATH))) {
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
                saveData();
                return;
            }
        }
        throw new IllegalArgumentException("Request not found: " + request.getRequestId());
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

    private boolean normalizeLeaveTimes(Request request) {
        if (request == null || !LEAVE_TYPE.equalsIgnoreCase(orEmpty(request.getType()))) {
            return false;
        }

        boolean changed = false;
        if (isBlank(request.getStartTime())) {
            request.setStartTime(DEFAULT_LEAVE_START_TIME);
            changed = true;
        }
        if (isBlank(request.getEndTime())) {
            request.setEndTime(DEFAULT_LEAVE_END_TIME);
            changed = true;
        }
        return changed;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
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