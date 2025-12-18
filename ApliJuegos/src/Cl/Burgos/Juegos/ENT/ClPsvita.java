/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Cl.Burgos.Juegos.ENT;

/**
 *
 * @author march
 */
public class ClPsvita {
    private int id;
    private String codigo;
    private String nombre;
    private String region;
    private String disco;
    private String update;
    private boolean dlc;
    private String formato;
    private String tipoJuego;
    private byte[] imagen;
    private String ruta;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDisco() {
        return disco;
    }

    public void setDisco(String disco) {
        this.disco = disco;
    }

    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    public boolean isDlc() {
        return dlc;
    }

    public void setDlc(boolean dlc) {
        this.dlc = dlc;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getTipoJuego() {
        return tipoJuego;
    }

    public void setTipoJuego(String tipoJuego) {
        this.tipoJuego = tipoJuego;
    }
    //Insertar
    public ClPsvita(String codigo, String nombre, String region, String disco, String update, boolean dlc, String formato, String tipoJuego, String ruta) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.region = region;
        this.disco = disco;
        this.update = update;
        this.dlc = dlc;
        this.formato = formato;
        this.tipoJuego = tipoJuego;
        this.ruta = ruta;
    }
    //Listar
    public ClPsvita(int id, String codigo, String nombre, String region, String disco, String update, boolean dlc, String formato, String tipoJuego, byte[] imagen) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.region = region;
        this.disco = disco;
        this.update = update;
        this.dlc = dlc;
        this.formato = formato;
        this.tipoJuego = tipoJuego;
        this.imagen = imagen;
    }
    //Buscar
    public ClPsvita(int id, String codigo, String nombre, String region, String disco, String update, boolean dlc, String formato, String tipoJuego, String ruta) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.region = region;
        this.disco = disco;
        this.update = update;
        this.dlc = dlc;
        this.formato = formato;
        this.tipoJuego = tipoJuego;
        this.ruta = ruta;
    }
    //Eliminar
    public ClPsvita(int id) {
        this.id = id;
    }
    //Buscar
    public ClPsvita(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }
    
    

    
}
