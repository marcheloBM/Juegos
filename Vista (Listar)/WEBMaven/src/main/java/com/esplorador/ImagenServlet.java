/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.esplorador;

/**
 *
 * @author march
 */
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/ImagenServlet")
public class ImagenServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String tabla = request.getParameter("tabla");
        String id = request.getParameter("id");

        if (tabla == null || id == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Faltan parámetros 'tabla' o 'id'");
            return;
        }

        try (Connection con = ConexionSQLite.getConnection();
             Statement st = con.createStatement();
             ResultSet rsMeta = st.executeQuery("SELECT * FROM " + tabla + " LIMIT 1")) {

            ResultSetMetaData meta = rsMeta.getMetaData();
            String idColumna = meta.getColumnName(1); // primera columna como ID

            String sql = "SELECT imagen FROM " + tabla + " WHERE " + idColumna + " = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, id);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    byte[] imgBytes = rs.getBytes("imagen");
                    if (imgBytes != null && imgBytes.length > 0) {
                        response.setContentType("image/jpeg"); // ajusta si usas PNG
                        response.setContentLength(imgBytes.length);
                        response.getOutputStream().write(imgBytes);
                    } else {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Imagen no encontrada");
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Registro no encontrado");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al acceder a la base de datos");
        }
    }
}