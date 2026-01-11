package com.compprog1282025.service;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;

import com.compprog1282025.model.Account;

public class AuthService {
    private static final String ACCOUNTS_CSV_PATH = "data/accounts.csv";

    private final Map<String, Account> accountMap = new HashMap<>();

    public void loadAccounts() throws IOException {
        Path path = Paths.get(ACCOUNTS_CSV_PATH);
        if (!Files.exists(path)) {
            System.err.println("Warning: accounts.csv not found.");
            return;
        }

        try (BufferedReader br = Files.newBufferedReader(path)) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 3) continue;

                String username = parts[0].trim();
                String hash = parts[1].trim();
                String role = parts[2].trim();
                accountMap.put(username, new Account(username, hash, role));
            }
        }
    }

    public boolean authenticate(String username, String password) {
        Account account = accountMap.get(username);
        if (account == null) return false;
        return account.getHashedPassword().equals(hashPassword(password));
    }

    public String getRole(String username) {
        Account account = accountMap.get(username);
        return account != null ? account.getRole() : null;
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashed = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashed) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}
