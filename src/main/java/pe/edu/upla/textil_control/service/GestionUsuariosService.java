package pe.edu.upla.textil_control.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upla.textil_control.model.Rol;
import pe.edu.upla.textil_control.model.Usuario;
import pe.edu.upla.textil_control.repository.RolRepository;
import pe.edu.upla.textil_control.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;

/**
 * GestionUsuariosService — CORREGIDO.
 *
 * Problemas que corrige:
 *  1. Se usa JdbcTemplate para INSERT y UPDATE en vez de usuarioRepository.save()
 *     porque la entidad tiene fecha_crea y fecha_mod con insertable/updatable=false,
 *     y reprocesos_acum que JPA intentaba actualizar a null en operaciones parciales.
 *  2. El método actualizar() para activar/desactivar solo actualiza el campo 'activo'
 *     en la BD sin tocar ningún otro campo, exactamente como hace el original.
 *  3. La lógica de negocio es idéntica al GestionUsuariosServlet.java original.
 */
@Service
public class GestionUsuariosService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordService passwordService;

    /**
     * JdbcTemplate para operaciones de escritura (INSERT/UPDATE).
     * Evita problemas de Hibernate con columnas NOT NULL que no están
     * en el entity o son managed por la BD (timestamps).
     */
    @Autowired
    private JdbcTemplate jdbc;

    // ── LISTAR ────────────────────────────────────────────────

    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = usuarioRepository.findAllByOrderByNombreAsc();
        for (Usuario u : usuarios) {
            rolRepository.findById(u.getIdRol())
                         .ifPresent(r -> u.setNombreRol(r.getNombreRol()));
        }
        return usuarios;
    }

    // ── BUSCAR POR ID ─────────────────────────────────────────

    public Usuario buscarPorId(Integer id) {
        Optional<Usuario> opt = usuarioRepository.findById(id);
        opt.ifPresent(u ->
            rolRepository.findById(u.getIdRol())
                         .ifPresent(r -> u.setNombreRol(r.getNombreRol()))
        );
        return opt.orElse(null);
    }

    // ── INSERTAR ──────────────────────────────────────────────

    /**
     * Inserta nuevo usuario usando JdbcTemplate para tener control total
     * sobre las columnas — evita que Hibernate intente insertar valores
     * en columnas con DEFAULT que maneja la BD.
     */
    public void insertar(Usuario u) {
        String hashPassword = passwordService.encriptar(u.getPassword());
        jdbc.update("""
                INSERT INTO usuarios
                    (username, password, nombre, apellido, email, id_rol, activo,
                     horario_restringido, horario_dias, horario_inicio, horario_fin,
                     reprocesos_acum)
                VALUES (?, ?, ?, ?, ?, ?, 1, ?, ?, ?, ?, 0)
                """,
                u.getUsername().trim(),
                hashPassword,
                u.getNombre().trim(),
                u.getApellido().trim(),
                u.getEmail().trim(),
                u.getIdRol(),
                u.getHorarioRestringido() != null ? u.getHorarioRestringido() : true,
                u.getHorarioDias(),
                u.getHorarioInicio(),
                u.getHorarioFin()
        );
    }

    // ── ACTUALIZAR (edición completa desde el modal) ──────────

    /**
     * Actualiza todos los campos editables del usuario.
     * Si password viene vacío, no lo cambia — igual que el original.
     * Nunca toca: reprocesos_acum, fecha_crea, fecha_mod.
     */
    public boolean actualizar(Usuario u) {
        boolean cambiaPassword = (u.getPassword() != null
                                  && u.getPassword().trim().length() >= 6);

        if (cambiaPassword) {
            int filas = jdbc.update("""
                    UPDATE usuarios SET
                        nombre              = ?,
                        apellido            = ?,
                        email               = ?,
                        id_rol              = ?,
                        activo              = ?,
                        horario_restringido = ?,
                        horario_dias        = ?,
                        horario_inicio      = ?,
                        horario_fin         = ?,
                        password            = ?
                    WHERE id_usuario = ?
                    """,
                    u.getNombre(),
                    u.getApellido(),
                    u.getEmail(),
                    u.getIdRol(),
                    u.getActivo(),
                    u.getHorarioRestringido() != null ? u.getHorarioRestringido() : true,
                    u.getHorarioDias(),
                    u.getHorarioInicio(),
                    u.getHorarioFin(),
                    passwordService.encriptar(u.getPassword().trim()),
                    u.getIdUsuario()
            );
            return filas > 0;
        } else {
            int filas = jdbc.update("""
                    UPDATE usuarios SET
                        nombre              = ?,
                        apellido            = ?,
                        email               = ?,
                        id_rol              = ?,
                        activo              = ?,
                        horario_restringido = ?,
                        horario_dias        = ?,
                        horario_inicio      = ?,
                        horario_fin         = ?
                    WHERE id_usuario = ?
                    """,
                    u.getNombre(),
                    u.getApellido(),
                    u.getEmail(),
                    u.getIdRol(),
                    u.getActivo(),
                    u.getHorarioRestringido() != null ? u.getHorarioRestringido() : true,
                    u.getHorarioDias(),
                    u.getHorarioInicio(),
                    u.getHorarioFin(),
                    u.getIdUsuario()
            );
            return filas > 0;
        }
    }

    // ── CAMBIAR ESTADO (activar / desactivar) ─────────────────

    /**
     * Solo cambia el campo 'activo' — no toca ningún otro campo.
     * Equivalente exacto a cambiarEstado() del original.
     * Usar este método desde el controller para activar/desactivar
     * en vez de cargar el usuario completo y llamar actualizar().
     */
    public boolean cambiarEstado(Integer idUsuario, boolean activar) {
        int filas = jdbc.update(
                "UPDATE usuarios SET activo = ? WHERE id_usuario = ?",
                activar ? 1 : 0,
                idUsuario
        );
        return filas > 0;
    }

    // ── ELIMINAR ──────────────────────────────────────────────

    @Transactional
    public boolean eliminar(Integer idUsuario) {
        if (tieneActividades(idUsuario)) return false;
        usuarioRepository.deleteEspecialidadesByUsuario(idUsuario);
        usuarioRepository.deleteById(idUsuario);
        return true;
    }

    // ── VERIFICAR ACTIVIDADES ─────────────────────────────────

    public boolean tieneActividades(Integer idUsuario) {
        return usuarioRepository.countOrdenesTrabajoByResponsable(idUsuario) > 0
            || usuarioRepository.countTelasByRegistrador(idUsuario) > 0;
    }
}
