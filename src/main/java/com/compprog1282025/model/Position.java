package com.compprog1282025.model;

public class Position {
    private String position;
    private Employee supervisor;

    public Position(String position, Employee supervisor) {
        this.position = position;
        this.supervisor = supervisor;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Employee getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(Employee supervisor) {
        this.supervisor = supervisor;
    }

    @Override
    public String toString() {
        return position;
    }
}
