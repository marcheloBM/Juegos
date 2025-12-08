package com.explorador;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

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

@WebServlet("/TablasServlet")
public class TablasServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<String> tablas = new ArrayList<>();

        try (Connection con = ConexionSQLite.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT name FROM sqlite_master WHERE type='table' ORDER BY name")) {

            while (rs.next()) {
                String nombre = rs.getString("name");
                // Filtramos tablas internas de SQLite
                if (!nombre.equalsIgnoreCase("sqlite_sequence")) {
                    tablas.add(nombre);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        request.setAttribute("tablas", tablas);
        RequestDispatcher rd = request.getRequestDispatcher("tablas.jsp");
        rd.forward(request, response);
    }
}
