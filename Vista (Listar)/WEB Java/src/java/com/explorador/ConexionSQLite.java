package com.explorador;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author march
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionSQLite {
    private static final String DB_URL = "jdbc:sqlite:D:/Users/march/Documents/GitHub/Juegos/Vista (Listar)/Juegos.sqlite"; 
    // Ajusta la ruta si tu BD está en otra carpeta

    public static Connection getConnection() throws SQLException {
        
        try {
            Class.forName("org.sqlite.JDBC"); // 👈 carga el driver
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se pudo cargar el driver JDBC de SQLite", e);
        }
        return DriverManager.getConnection(DB_URL);
    }
}
