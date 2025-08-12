package dao;

import domain.Tarea;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO simple que implementa operaciones requeridas.
 * Usa PreparedStatement y maneja transacciones cuando corresponde.
 */
public class TareaDAO {

    public void crearTablaSiNoExiste() throws Exception {
        String sql = """ 
        IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='tarea' AND xtype='U')
        CREATE TABLE tarea (
            id BIGINT IDENTITY(1,1) PRIMARY KEY,
            titulo NVARCHAR(255) NOT NULL,
            prioridad INT NOT NULL,
            especial BIT NOT NULL,
            fecha DATETIME2 NULL,
            hecho BIT NOT NULL DEFAULT 0,
            creado DATETIME2 NOT NULL,
            actualizado DATETIME2 NOT NULL,
            eliminado BIT NOT NULL DEFAULT 0
        );
        """;

        try (Connection c = Conexion.getConnection();
             Statement s = c.createStatement()) {
            s.execute(sql);
        }
    }

    public Tarea insertar(Tarea t) throws Exception {
        String sql = "INSERT INTO tarea(titulo,prioridad,especial,fecha,hecho,creado,actualizado,eliminado) VALUES (?,?,?,?,?,?,?,0);";
        try (Connection c = Conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            c.setAutoCommit(false);
            ps.setString(1, t.getTitulo());
            ps.setInt(2, t.getPrioridad());
            ps.setBoolean(3, t.isEspecial());
            if (t.getFecha() != null) ps.setTimestamp(4, Timestamp.valueOf(t.getFecha()));
            else ps.setNull(4, Types.TIMESTAMP);
            ps.setBoolean(5, t.isHecho());
            ps.setTimestamp(6, Timestamp.valueOf(t.getCreado()));
            ps.setTimestamp(7, Timestamp.valueOf(t.getActualizado()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    t.setId(rs.getLong(1));
                }
            }
            c.commit();
            return t;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<Tarea> listarVisibles() throws Exception {
        String sql = "SELECT id,titulo,prioridad,especial,fecha,hecho,creado,actualizado FROM tarea WHERE eliminado=0 ORDER BY id ASC";
        try (Connection c = Conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Tarea> lista = new ArrayList<>();
            while (rs.next()) {
                Tarea t = new Tarea();
                t.setId(rs.getLong("id"));
                t.setTitulo(rs.getString("titulo"));
                t.setPrioridad(rs.getInt("prioridad"));
                t.setEspecial(rs.getBoolean("especial"));
                Timestamp ts = rs.getTimestamp("fecha");
                if (ts != null) t.setFecha(ts.toLocalDateTime());
                t.setHecho(rs.getBoolean("hecho"));
                // creado/actualizado se pueden asignar si se desea
                lista.add(t);
            }
            return lista;
        }
    }

    public void alternarHecho(long id, boolean nuevoEstado) throws Exception {
        String sql = "UPDATE tarea SET hecho=?, actualizado=? WHERE id=?";
        try (Connection c = Conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBoolean(1, nuevoEstado);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(3, id);
            ps.executeUpdate();
        }
    }

    public void marcarEliminado(long id) throws Exception {
        String sql = "UPDATE tarea SET eliminado=1, actualizado=? WHERE id=?";
        try (Connection c = Conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    public Tarea buscarPorId(long id) throws Exception {
        String sql = "SELECT id,titulo,prioridad,especial,fecha,hecho,creado,actualizado FROM tarea WHERE id=?";
        try (Connection c = Conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Tarea t = new Tarea();
                    t.setId(rs.getLong("id"));
                    t.setTitulo(rs.getString("titulo"));
                    t.setPrioridad(rs.getInt("prioridad"));
                    t.setEspecial(rs.getBoolean("especial"));
                    Timestamp ts = rs.getTimestamp("fecha");
                    if (ts != null) t.setFecha(ts.toLocalDateTime());
                    t.setHecho(rs.getBoolean("hecho"));
                    return t;
                } else return null;
            }
        }
    }
}

