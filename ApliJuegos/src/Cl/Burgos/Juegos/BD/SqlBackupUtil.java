/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Cl.Burgos.Juegos.BD;

import java.util.List;
import java.util.function.Function;

/**
 *
 * @author march
 */
public class SqlBackupUtil {

    // Escapa comillas simples
    public static String esc(String s) {
        return s == null ? null : s.replace("'", "''");
    }

    // Convierte valor a SQL seguro
    public static String sqlVal(Object v) {
        if (v == null) return "NULL";
        if (v instanceof Number) return v.toString();
        if (v instanceof Boolean) return ((Boolean) v) ? "1" : "0";
        return "'" + esc(v.toString()) + "'";
    }

    // Método genérico para cualquier lista
    public static <T> String generarInsertMultiple(String tabla,
                                                   String[] columnas,
                                                   List<T> lista,
                                                   Function<T, Object[]> mapper) {
        if (lista == null || lista.isEmpty()) return "-- sin datos para " + tabla;

        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(tabla).append(" (");
        sb.append(String.join(", ", columnas));
        sb.append(") VALUES\n");

        for (int i = 0; i < lista.size(); i++) {
            Object[] valores = mapper.apply(lista.get(i));
            sb.append("(");
            for (int j = 0; j < valores.length; j++) {
                sb.append(sqlVal(valores[j]));
                if (j < valores.length - 1) sb.append(", ");
            }
            sb.append(")");
            if (i < lista.size() - 1) {
                sb.append(",\n");
            } else {
                sb.append(";\n");
            }
        }
        return sb.toString();
    }
}

