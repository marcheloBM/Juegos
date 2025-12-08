/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.explorador;

/**
 *
 * @author march
 */
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/RegistrosServlet")
public class RegistrosServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String tabla = request.getParameter("tabla");
        List<Map<String, Object>> registros = new ArrayList<>();
        List<String> columnas = new ArrayList<>();

        try (Connection con = ConexionSQLite.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM " + tabla)) {

            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();
            for (int i = 1; i <= colCount; i++) {
                columnas.add(meta.getColumnName(i));
            }

            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                for (String col : columnas) {
                    fila.put(col, rs.getObject(col));
                }
                registros.add(fila);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        request.setAttribute("tabla", tabla);
        request.setAttribute("columnas", columnas);
        request.setAttribute("registros", registros);
        RequestDispatcher rd = request.getRequestDispatcher("registros.jsp");
        rd.forward(request, response);
    }
}
