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

    public List<Request> getPendingLeaveRequests(Session session) throws InvalidAccessException {
        return requestService.getPendingLeaveRequests(session);
    }

    public List<Request> getRequestHistory(Session session) throws InvalidAccessException {
        return requestService.getRequestHistory(session);
    }

    public void approveRequest(String requestId, Session session) throws InvalidAccessException {
        ensureHR(session);
        requestService.approveRequest(requestId, session);
    }

    public void rejectRequest(String requestId, Session session) throws InvalidAccessException {
        ensureHR(session);
        requestService.rejectRequest(requestId, session);
    }

    public void load(Session session) throws InvalidAccessException {
        getPendingLeaveRequests(session);
        getRequestHistory(session);
    }

    private void ensureHR(Session session) throws InvalidAccessException {
        if (RoleResolver.resolve(session) != EffectiveRole.HR && RoleResolver.resolve(session) != EffectiveRole.ADMIN) {
            throw new InvalidAccessException("Only HR or Admin users can approve or reject team requests.");
        }
    }
}