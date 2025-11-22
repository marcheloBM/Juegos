/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Cl.Burgos.Juegos.BD;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.*;

/**
 *
 * @author march
 */
public class DBSetup {
    private static final String DB_PATH = "Juegos.sqlite";
    private static final String SQL_SCRIPT_DEV = "src/Cl/Burgos/Juegos/SQL/Juegos.sqlite.sql";
    private static final String SQL_SCRIPT_JAR = "/Cl/Burgos/Juegos/SQL/Juegos.sqlite.sql";


    public static void inicializar() {
        try {
            File archivoBD = new File(DB_PATH);
            if (!archivoBD.exists()) {
                archivoBD.createNewFile();
                System.out.println("Base de datos creada: " + DB_PATH);
            }

            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH)) {
                if (!tablasExistentes(conn)) {
                    ejecutarScriptSQL(conn);
                } else {
                    System.out.println("Las tablas ya existen. No se ejecuto el script.");
                }
            }

        } catch (IOException | SQLException e) {
            System.err.println("Error al inicializar la base de datos: " + e.getMessage());
        }
    }

    private static boolean tablasExistentes(Connection conn) throws SQLException {
        String[] tablas = { "ps3", "ps2", "ps4", "psp", "psx", "pc" };
        DatabaseMetaData meta = conn.getMetaData();
        for (String tabla : tablas) {
            try (ResultSet rs = meta.getTables(null, null, tabla, null)) {
                if (!rs.next()) {
                    return false; // Falta al menos una tabla
                }
            }
        }
        return true;
    }

    private static void ejecutarScriptSQL(Connection conn) {
        String script = cargarScriptSQL();
        if (script == null) {
            System.err.println("No se pudo cargar el archivo SQL.");
            return;
        }

        String[] sentencias = script.split(";");
        try (Statement stmt = conn.createStatement()) {
            for (String sentencia : sentencias) {
                sentencia = sentencia.trim();
                if (!sentencia.isEmpty() &&
                    !sentencia.equalsIgnoreCase("BEGIN TRANSACTION") &&
                    !sentencia.equalsIgnoreCase("COMMIT")) {
                    stmt.execute(sentencia + ";");
                }
            }
            System.out.println("Script SQL ejecutado (tablas creadas si no existian).");
        } catch (SQLException e) {
            System.err.println("Error al ejecutar el script SQL: " + e.getMessage());
        }
    }

    private static String cargarScriptSQL() {
        // Intenta cargar desde ruta de desarrollo
        try {
            Path devPath = Paths.get(SQL_SCRIPT_DEV);
            return Files.readString(devPath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            // Si falla, intenta como recurso empaquetado
            try (InputStream input = DBSetup.class.getResourceAsStream(SQL_SCRIPT_JAR)) {
                if (input != null) {
                    return new String(input.readAllBytes(), StandardCharsets.UTF_8);
                }
            } catch (IOException ex) {
                System.err.println("Error al leer el recurso SQL: " + ex.getMessage());
            }
        }
        return null;
    }
}
