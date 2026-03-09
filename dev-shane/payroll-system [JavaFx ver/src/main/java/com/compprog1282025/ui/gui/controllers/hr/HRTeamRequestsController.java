package com.compprog1282025.ui.gui.controllers.hr;

import com.compprog1282025.model.employee.Request;
import com.compprog1282025.model.user.EffectiveRole;
import com.compprog1282025.model.user.RoleResolver;
import com.compprog1282025.model.user.Session;
import com.compprog1282025.service.InvalidAccessException;
import com.compprog1282025.service.RequestService;

import java.util.List;

public class HRTeamRequestsController {
    private final RequestService requestService;

    public HRTeamRequestsController(RequestService requestService) {
        this.requestService = requestService;
    }

    public List<Request> getPendingLeaveRequests() {
        return requestService.getPendingLeaveRequests();
    }

    public List<Request> getRequestHistory() {
        return requestService.getRequestHistory();
    }

    public void approveRequest(String requestId, Session session) throws InvalidAccessException {
        ensureHR(session);
        requestService.approveRequest(requestId, session);
    }

    public void rejectRequest(String requestId, Session session) throws InvalidAccessException {
        ensureHR(session);
        requestService.rejectRequest(requestId, session);
    }

    public void load(Session session) {
        getPendingLeaveRequests();
        getRequestHistory();
    }

    private void ensureHR(Session session) throws InvalidAccessException {
        if (RoleResolver.resolve(session) != EffectiveRole.HR) {
            throw new InvalidAccessException("Only HR users can approve or reject team requests.");
        }
    }
}


