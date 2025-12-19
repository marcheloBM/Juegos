/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Cl.Burgos.Juegos.DAO;

import Cl.Burgos.Juegos.BD.BD;
import Cl.Burgos.Juegos.ENT.ClPsvita;
import Cl.Burgos.Juegos.FUN.Log;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author march
 */
public class DAOPsvita {
    public boolean sqlInsert(ClPsvita clPsvita) {
        Connection con = BD.getInstance().conectar();
        String insert = "insert into psvita(codigo,nombre,region,disco,actualizacion,dlc,formato,tipoJuego,imagen) values (?,?,?,?,?,?,?,?,?)";
        PreparedStatement ps = null;
        try{
            ps = con.prepareStatement(insert);
            ps.setString(1, clPsvita.getCodigo());
            ps.setString(2, clPsvita.getNombre());
            ps.setString(3, clPsvita.getRegion());
            ps.setString(4, clPsvita.getDisco());
            ps.setString(5, clPsvita.getUpdate());
            ps.setBoolean(6, clPsvita.isDlc());
            ps.setString(7, clPsvita.getFormato());
            ps.setString(8, clPsvita.getTipoJuego());
            
            // Manejo de la imagen
            InputStream fi = null;
            String ruta = clPsvita.getRuta();

            if (ruta != null && !ruta.trim().isEmpty()) {
                File file = new File(ruta);
                if (file.exists()) {
                    fi = new FileInputStream(file);
                } else {
                    // Si la ruta no existe, usar imagen por defecto desde el JAR
                    fi = getClass().getResourceAsStream("/Cl/Burgos/Juegos/IMG/Sin Imagen.jpg");
                }
            } else {
                // Si la ruta está vacía, también usar imagen por defecto
                fi = getClass().getResourceAsStream("/Cl/Burgos/Juegos/IMG/Sin Imagen.jpg");
            }

            // Asignar el stream al campo binario
            ps.setBinaryStream(9, fi);

            // Ejecutar
            ps.execute();
            return true;
        }catch(Exception ex){
            Logger.getLogger(DAOPsvita.class.getName()).log(Level.SEVERE, null, ex);
            Log.log(ex.getMessage());
            return false;
        }
    }
    
    public boolean sqlUpdate(ClPsvita clPsvita){	
        Connection con = BD.getInstance().conectar();
        String insert = "update psvita set codigo=?, nombre=?, region=?, disco=?, actualizacion=?, dlc=?, formato=?, tipoJuego=?, imagen=? where IdPsvita=?;";
        String insert2 = "update psvita set codigo=?, nombre=?, region=?, disco=?, actualizacion=?, dlc=?, formato=?, tipoJuego=? where IdPsvita=?;";
        FileInputStream fi = null;
        PreparedStatement ps = null;
        try{
            if(clPsvita.getRuta().length()!=0){
                File file = new File(clPsvita.getRuta());
                fi = new FileInputStream(file);

                ps = con.prepareStatement(insert);
                ps.setString(1, clPsvita.getCodigo());
                ps.setString(2, clPsvita.getNombre());
                ps.setString(3, clPsvita.getRegion());
                ps.setString(4, clPsvita.getDisco());
                ps.setString(5, clPsvita.getUpdate());
                ps.setBoolean(6, clPsvita.isDlc());
                ps.setString(7, clPsvita.getFormato());
                ps.setString(8, clPsvita.getTipoJuego());
                ps.setBinaryStream(9, fi);
                ps.setInt(10, clPsvita.getId());
                
            }else{
                ps = con.prepareStatement(insert2);
                 ps.setString(1, clPsvita.getCodigo());
                ps.setString(2, clPsvita.getNombre());
                ps.setString(3, clPsvita.getRegion());
                ps.setString(4, clPsvita.getDisco());
                ps.setString(5, clPsvita.getUpdate());
                ps.setBoolean(6, clPsvita.isDlc());
                ps.setString(7, clPsvita.getFormato());
                ps.setString(8, clPsvita.getTipoJuego());
                ps.setInt(9, clPsvita.getId());
            }
            
            ps.executeUpdate();
            return true;
        }catch(Exception ex){
            Logger.getLogger(DAOPsvita.class.getName()).log(Level.SEVERE, null, ex);
            Log.log(ex.getMessage());
            System.out.println(ex.getMessage());
            return false;
        }
    }
    
    public boolean sqlDelete(ClPsvita clPsvita){
        Connection con = BD.getInstance().conectar();
        PreparedStatement ps = null;
        String stSql =  "delete from psvita where IdPsvita=?;";
        try {
            
             ps = con.prepareStatement(stSql);
            ps.setInt(1, clPsvita.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DAOPsvita.class.getName()).log(Level.SEVERE, null, ex);
            Log.log(ex.getMessage());
//            log.info(ex.getMessage());
        }
        return false;
    }
    
    public List<ClPsvita> leerPsvita() {
        List<ClPsvita> lista=new ArrayList<>();
        String strConsulta;
        
        strConsulta="select IdPsvita,codigo,nombre,region,disco,actualizacion,dlc,formato,tipoJuego,imagen from psvita order by nombre asc;";
        
        try{
         ResultSet rs=BD.getInstance().sqlSelect(strConsulta);
         if(rs==null)return null;
         while(rs.next()){
             ClPsvita c = new ClPsvita(rs.getInt("IdPsvita"), rs.getString("codigo"), rs.getString("nombre"), 
                     rs.getString("region"), rs.getString("disco"), rs.getString("actualizacion"), rs.getBoolean("dlc"), 
                     rs.getString("formato"), rs.getString("tipoJuego"), rs.getBytes("imagen"));
              lista.add(c);
         }
         
        } catch (SQLException ex) {
            Logger.getLogger(DAOPsvita.class.getName()).log(Level.SEVERE, null, ex);
            Log.log(ex.getMessage());
        } catch (Exception ex) {
            Logger.getLogger(DAOPsvita.class.getName()).log(Level.SEVERE, null, ex);
            Log.log(ex.getMessage());
        }
        return lista;
    }
    
    public List<ClPsvita> leerBuscar(ClPsvita clPsvita) {
        List<ClPsvita> lista=new ArrayList<>();
        String strConsulta;
        String resp=null;
        
        if(clPsvita.getCodigo().length()>1){
            resp="codigo='"+clPsvita.getCodigo()+"'";
        }
        if(clPsvita.getNombre().length()>1){
            resp="nombre like '%"+clPsvita.getNombre()+"%'";
        }
        if(clPsvita.getCodigo().length()>1 && clPsvita.getNombre().length()>1){
            resp="codigo='"+clPsvita.getCodigo()+"' and nombre like '%"+clPsvita.getNombre()+"%'";
        }
        strConsulta="select IdPsvita,codigo,nombre,region,disco,actualizacion,dlc,formato,tipoJuego,imagen from psvita where "+resp+" order by nombre asc";
        
        try{
         ResultSet rs=BD.getInstance().sqlSelect(strConsulta);
         if(rs==null)return null;
         while(rs.next()){
             ClPsvita c = new ClPsvita(rs.getInt("IdPsvita"), rs.getString("codigo"), rs.getString("nombre"), 
                     rs.getString("region"), rs.getString("disco"), rs.getString("actualizacion"), rs.getBoolean("dlc"), 
                     rs.getString("formato"), rs.getString("tipoJuego"), rs.getBytes("imagen"));
              lista.add(c);
         }
         
        } catch (SQLException ex) {
            Logger.getLogger(DAOPsvita.class.getName()).log(Level.SEVERE, null, ex);
            Log.log(ex.getMessage());
        } catch (Exception ex) {
            Logger.getLogger(DAOPsvita.class.getName()).log(Level.SEVERE, null, ex);
            Log.log(ex.getMessage());
        }
        return lista;
    }
    
    public List<ClPsvita> leerPsvita2(ClPsvita clPsvita) {
        List<ClPsvita> lista=new ArrayList<>();
        String strConsulta;
        
        strConsulta="select IdPsvita,codigo,nombre,region,disco,actualizacion,dlc,formato,tipoJuego,imagen from psvita where IdPsvita="+clPsvita.getId();
        
        try{
         ResultSet rs=BD.getInstance().sqlSelect(strConsulta);
         if(rs==null)return null;
         while(rs.next()){
             ClPsvita c = new ClPsvita(rs.getInt("IdPsvita"), rs.getString("codigo"), rs.getString("nombre"), 
                     rs.getString("region"), rs.getString("disco"), rs.getString("actualizacion"), rs.getBoolean("dlc"), 
                     rs.getString("formato"), rs.getString("tipoJuego"), rs.getBytes("imagen"));
              lista.add(c);
         }
         
        } catch (SQLException ex) {
            Logger.getLogger(DAOPsvita.class.getName()).log(Level.SEVERE, null, ex);
            Log.log(ex.getMessage());
        } catch (Exception ex) {
            Logger.getLogger(DAOPsvita.class.getName()).log(Level.SEVERE, null, ex);
            Log.log(ex.getMessage());
        }
        return lista;
    }
    
    public int Cuantos() {
        String strConsulta;
        int num = 0;
        strConsulta="select count(*) as cuantos from psvita where codigo LIKE 'SCPV%';";
        
        try{
         ResultSet rs=BD.getInstance().sqlSelect(strConsulta);
//         if(rs==null)return null;
         while(rs.next()){
             num=rs.getInt("cuantos");
         }
         
        } catch (SQLException ex) {
            Logger.getLogger(DAOPsvita.class.getName()).log(Level.SEVERE, null, ex);
            Log.log(ex.getMessage());
        } catch (Exception ex) {
            Logger.getLogger(DAOPsvita.class.getName()).log(Level.SEVERE, null, ex);
            Log.log(ex.getMessage());
        }
        return num;
    }
    
    public int CuantosTotal() {
        String strConsulta;
        int num = 0;
        strConsulta="select count(*) as cuantos from psvita;";
        
        try{
         ResultSet rs=BD.getInstance().sqlSelect(strConsulta);
//         if(rs==null)return null;
         while(rs.next()){
             num=rs.getInt("cuantos");
         }
         
        } catch (SQLException ex) {
            Logger.getLogger(DAOPsvita.class.getName()).log(Level.SEVERE, null, ex);
            Log.log(ex.getMessage());
        } catch (Exception ex) {
            Logger.getLogger(DAOPsvita.class.getName()).log(Level.SEVERE, null, ex);
            Log.log(ex.getMessage());
        }
        return num;
    }
    
    public void CrearTablaPDFPSvita(String dest,String nombreArc){
        try {
            // Creamos un documento pdf con iText
            PdfWriter pdfWriter = new PdfWriter(dest+"./"+nombreArc+".pdf");
            PdfDocument pdfDoc = new PdfDocument(pdfWriter);
            Document doc = new Document(pdfDoc, PageSize.LETTER);
            //Arriba,Abajo,derecha,izqueda
            doc.setMargins(80, 20, 20, 20);
            
            PdfFont font1 = PdfFontFactory.createFont(StandardFonts.TIMES_ITALIC);
//            PdfFont font2 = PdfFontFactory.createFont(FontConstants.TIMES_ITALIC);
            
            Paragraph parrafo1 = new Paragraph(nombreArc).setFont(font1);
            // Cambiamos el tamaño de fuente del parrafo 2 lo hacemos mas pequeño
            parrafo1.setFontSize(12f);
            
            // Creamos unas tablas
            float[] anchos = {10f, 50f, 50f, 50f, 50f, 50f, 50f, 50f, 60f};
            Table tabla1 = new Table(anchos);
            Table tabla2 = new Table(anchos);
            
            // Agregamos contenido a las tablas
//            tabla1.addCell("ID");
            tabla1.addCell("Codigo");
            tabla1.addCell("Nombre");
            tabla1.addCell("Region");
            tabla1.addCell("Disco");
            tabla1.addCell("Update");
            tabla1.addCell("DLC");
            tabla1.addCell("Formato");
            tabla1.addCell("Tipo Juego");
            tabla1.addCell("Imagen");
            List<ClPsvita> lista=new DAOPsvita().leerPsvita();
            for (int i = 0; i < lista.size(); i++) {
//                tabla1.addCell(Integer.toString(lista.get(i).getId()));
                tabla1.addCell(lista.get(i).getCodigo());
                tabla1.addCell(lista.get(i).getNombre());
                tabla1.addCell(lista.get(i).getRegion());
                tabla1.addCell(lista.get(i).getDisco());
                tabla1.addCell(lista.get(i).getUpdate());
                tabla1.addCell(siyno(lista.get(i).isDlc()));
                tabla1.addCell(lista.get(i).getFormato());
                tabla1.addCell(lista.get(i).getTipoJuego());
                Image img;
                if(lista.get(i).getImagen() != null){
                    img = new Image(ImageDataFactory.create(lista.get(i).getImagen()));
                    img.scaleToFit(60, 60);
                }else{
                    // Imagen por defecto desde archivo local
                    String ruta = "./src/Cl/Burgos/Juegos/IMG/Sin Imagen.jpg";
                    try {
                        byte[] bytes = Files.readAllBytes(Paths.get(ruta));
                        img = new Image(ImageDataFactory.create(bytes));
                        img.scaleToFit(60, 60);
                    } catch (IOException e) {
                        System.err.println("No se pudo cargar la imagen por defecto: " + e.getMessage());
                        Log.log("No se pudo cargar la imagen por defecto: " + e.getMessage());
                        img = null; // o puedes usar una imagen vacía si lo prefieres
                    }
                }
                tabla1.addCell(img);
                
            }
            
            Paragraph parrafo2 = new Paragraph("Comentario:");
            // Cambiamos el tamaño de fuente del parrafo 2 lo hacemos mas pequeño
            parrafo2.setFontSize(12f);
            
            doc.add(parrafo1);
            
            doc.add(tabla1);
            doc.add(parrafo2);
            doc.add(tabla2);
            
            doc.close();
            
        } catch (IOException ex) {
            Logger.getLogger(DAOPsvita.class.getName()).log(Level.SEVERE, null, ex);
            Log.log(ex.getMessage());
        }finally{
            try {
                JOptionPane.showMessageDialog(null, "Archivo Creado");
                File objetofile = new File (dest+"./"+nombreArc+".pdf");
                Desktop.getDesktop().open(objetofile);
            } catch (IOException ex) {
                Logger.getLogger(DAOPsvita.class.getName()).log(Level.SEVERE, null, ex);
                Log.log(ex.getMessage());
            }
                
            }
    }
    
    public String siyno(boolean b){
        String rep="";
        if(b==true){ rep="Si";}
        if(b==false){ rep="No";}
        return rep;
    }
}
