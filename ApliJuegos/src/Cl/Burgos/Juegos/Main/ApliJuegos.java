/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cl.Burgos.Juegos.Main;

import Cl.Burgos.Juegos.BD.DBSetup;
import Cl.Burgos.Juegos.FUN.*;
import Cl.Burgos.Juegos.GUI.*;
import javax.swing.JOptionPane;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


/**
 *
 * @author march
 */
public class ApliJuegos {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Inicio();
        //Para agregar Muschas imagenes a la BD
//        updateIMG();
    }
    public static void interzas(){
        DBSetup.inicializar();
        Directorio.crearDirec();
        new FrHome().setVisible(true);
    }
    public static void Inicio(){
        String repo = Confi.repositorio;
        String versionActual = Confi.versionActual;

        String ultimaVersion = GitHubReleaseGUI.obtenerUltimaVersion(repo);

        if (ultimaVersion == null) {
            JOptionPane.showMessageDialog(null, "⚠️ No se pudo verificar la versión.");
            //Inicia el programa si no se puede verificar
            interzas();
        } else if (ultimaVersion.equals(versionActual)) {
            // Estás usando la última versión publicada
            interzas();
        } else if (compararVersiones(versionActual, ultimaVersion) > 0) {
            // Estás usando una versión más nueva que la publicada
            JOptionPane.showMessageDialog(null, "🧪 Estás usando una versión en desarrollo (" + versionActual + ").");
            interzas();
        } else {
            // Hay una versión más nueva publicada
            JOptionPane.showMessageDialog(null, "🟢 Hay una nueva versión disponible: " + ultimaVersion);
            int respu = JOptionPane.showConfirmDialog(null, "¿Desea descargar la nueva versión?");
            if (respu == JOptionPane.YES_OPTION) {
                //Abrimos para descargar la nueva version
                GitHubReleaseGUI.main(new String[]{});
            } else {
                JOptionPane.showMessageDialog(null, "Intente mantener el programa actualizado.");
                //Si no queremos actualizar a la ultima Version
                interzas();
            }
        }
    }
    public static int compararVersiones(String v1, String v2) {
        String[] a = v1.split("\\.");
        String[] b = v2.split("\\.");
        int len = Math.max(a.length, b.length);
        for (int i = 0; i < len; i++) {
            int n1 = i < a.length ? Integer.parseInt(a[i]) : 0;
            int n2 = i < b.length ? Integer.parseInt(b[i]) : 0;
            if (n1 != n2) return Integer.compare(n1, n2);
        }
        return 0;
    }
    
    
    //para insertar muschas imagenes si faltan en BD
    private static void updateIMG(){
        String basePath = "F:\\IMG\\PS4"; // carpeta raíz original
        String url = "jdbc:sqlite:Juegos.sqlite"; // cambia según tu motor de BD

        try (Connection conn = DriverManager.getConnection(url)) {
            String sql = "UPDATE ps4 SET imagen=? WHERE codigo=?";
            PreparedStatement ps = conn.prepareStatement(sql);

            processDirectory(new File(basePath), ps);

            ps.close();
            conn.close();
            System.out.println("Imágenes copiadas y actualizadas correctamente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    private static void processDirectory(File dir, PreparedStatement ps) {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                processDirectory(file, ps); // recursivo
            } else if (file.getName().toLowerCase().endsWith(".jpg")) {
                String codigo = file.getName().substring(0, file.getName().lastIndexOf('.'));

                try {
                    boolean insertado = false;

                    // Intentar insertar directamente desde el archivo de origen
                    try (FileInputStream fis = new FileInputStream(file)) {
                        ps.setBinaryStream(1, fis, (int) file.length());
                        ps.setString(2, codigo);

                        int filas = ps.executeUpdate();
                        insertado = (filas > 0);
                    }

                    // ✅ Solo si se insertó en la BD, copiar al destino y eliminar el origen
                    if (insertado) {
                        String destino = System.getProperty("user.dir") + "/IMG/PS4/" + codigo + ".jpg";
                        CopiarArchivos(file.getAbsolutePath(), destino);

                        if (file.delete()) {
                            System.out.println("Imagen de origen eliminada: " + file.getAbsolutePath());
                        } else {
                            System.err.println("No se pudo eliminar la imagen de origen: " + file.getAbsolutePath());
                        }
                    }

                } catch (IOException | SQLException e) {
                    System.err.println("Error con archivo: " + file.getAbsolutePath());
                    e.printStackTrace();
                }
            }
        }
    }
    public static void CopiarArchivos(String origen, String destino) throws IOException {
        try (FileInputStream fis = new FileInputStream(origen);
             FileOutputStream fos = new FileOutputStream(destino)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
        }
    }


}
