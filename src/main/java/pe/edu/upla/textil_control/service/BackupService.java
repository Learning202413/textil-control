package pe.edu.upla.textil_control.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Service;
import pe.edu.upla.textil_control.model.HistorialBackup;
import pe.edu.upla.textil_control.repository.HistorialBackupDTO;
import pe.edu.upla.textil_control.repository.HistorialBackupRepository;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class BackupService {

    @Autowired private HistorialBackupRepository backupRepo;
    @Autowired private JdbcTemplate jdbcTemplate;

    @Value("${app.backup.dir:respaldos_bd}")
    private String backupDirName;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Map<String, BackupJob> jobs = new ConcurrentHashMap<>();

    public static class BackupJob {
        public String estado;
        public String mensaje;
        public String nombreArchivo;
    }

    private Path obtenerDirectorioSeguro() {
        String userDir = System.getProperty("user.dir");
        Path path = Paths.get(userDir, backupDirName).toAbsolutePath();
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    public List<HistorialBackupDTO> listarTodos() {
        return backupRepo.listarTodos();
    }

    public BackupJob getEstadoJob(String jobId) {
        return jobs.get(jobId);
    }

    public String generarBackupAsincrono(Integer idUsuario) {
        String jobId = UUID.randomUUID().toString();
        BackupJob job = new BackupJob();
        job.estado = "IN_PROGRESS";
        jobs.put(jobId, job);

        executor.submit(() -> {
            try {
                Path backupPath = obtenerDirectorioSeguro();
                String fechaStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                String fileName = "backup_textil_" + fechaStr + ".sql";
                File file = backupPath.resolve(fileName).toFile();

                // 🔥 SOLUCIÓN: Tablas que NO deben viajar en el tiempo ni borrarse al restaurar
                List<String> tablasExcluidas = Arrays.asList("historial_backups", "sesiones_activas", "intentos_login");

                try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
                    writer.println("-- Backup Generado Automáticamente por Textil Control");
                    writer.println("-- Fecha: " + fechaStr);
                    writer.println("SET FOREIGN_KEY_CHECKS=0;\n");

                    List<Map<String, Object>> dbObjects = jdbcTemplate.queryForList("SHOW FULL TABLES");

                    // ==============================================================
                    // FASE 1: PROCESAR SOLO TABLAS BASE
                    // ==============================================================
                    for (Map<String, Object> dbObj : dbObjects) {
                        Object[] values = dbObj.values().toArray();
                        String name = values[0].toString();
                        String type = values[1].toString();

                        if ("BASE TABLE".equalsIgnoreCase(type)) {

                            // Si la tabla está en la lista de exclusión, la saltamos
                            if (tablasExcluidas.contains(name.toLowerCase())) {
                                continue;
                            }

                            writer.println("-- ---------------------------------------------------------");
                            writer.println("-- Estructura de tabla: " + name);
                            writer.println("-- ---------------------------------------------------------");
                            writer.println("DROP TABLE IF EXISTS `" + name + "`;");

                            String createTableSql = jdbcTemplate.queryForObject("SHOW CREATE TABLE `" + name + "`", (rs, rowNum) -> rs.getString(2));
                            writer.println(createTableSql + ";\n");

                            List<String> columnasInsertables = jdbcTemplate.queryForList(
                                    "SELECT COLUMN_NAME FROM information_schema.COLUMNS " +
                                            "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? " +
                                            "AND EXTRA NOT LIKE '%GENERATED%' ORDER BY ORDINAL_POSITION",
                                    String.class, name
                            );

                            if (columnasInsertables.isEmpty()) continue;

                            String colNombres = "`" + String.join("`, `", columnasInsertables) + "`";

                            writer.println("-- Datos de tabla: " + name);
                            jdbcTemplate.query("SELECT " + colNombres + " FROM `" + name + "`", rs -> {
                                StringBuilder insert = new StringBuilder("INSERT INTO `").append(name).append("` (").append(colNombres).append(") VALUES (");
                                for (int i = 1; i <= columnasInsertables.size(); i++) {
                                    Object value = rs.getObject(i);
                                    if (value == null) {
                                        insert.append("NULL");
                                    } else if (value instanceof Number) {
                                        insert.append(value.toString());
                                    } else if (value instanceof Boolean) {
                                        insert.append(((Boolean) value) ? "1" : "0");
                                    } else if (value instanceof java.sql.Timestamp) {
                                        String ts = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((java.sql.Timestamp) value);
                                        insert.append("'").append(ts).append("'");
                                    } else {
                                        String strValue = value.toString()
                                                .replace("\\", "\\\\")
                                                .replace("'", "''")
                                                .replace("\r", "\\r")
                                                .replace("\n", "\\n");
                                        insert.append("'").append(strValue).append("'");
                                    }
                                    if (i < columnasInsertables.size()) insert.append(", ");
                                }
                                insert.append(");");
                                writer.println(insert.toString());
                            });
                            writer.println();
                        }
                    }

                    // ==============================================================
                    // FASE 2: PROCESAR SOLO VISTAS
                    // ==============================================================
                    for (Map<String, Object> dbObj : dbObjects) {
                        Object[] values = dbObj.values().toArray();
                        String name = values[0].toString();
                        String type = values[1].toString();

                        if ("VIEW".equalsIgnoreCase(type)) {
                            writer.println("-- ---------------------------------------------------------");
                            writer.println("-- Estructura de vista: " + name);
                            writer.println("-- ---------------------------------------------------------");
                            writer.println("DROP VIEW IF EXISTS `" + name + "`;");

                            String createViewSql = jdbcTemplate.queryForObject("SHOW CREATE VIEW `" + name + "`", (rs, rowNum) -> rs.getString(2));
                            writer.println(createViewSql + ";\n");
                        }
                    }

                    writer.println("SET FOREIGN_KEY_CHECKS=1;");
                }

                HistorialBackup h = new HistorialBackup();
                h.setUsuarioSolicitante(idUsuario);
                h.setNombreArchivo(fileName);
                h.setTamanioBytes(file.length());
                h.setEstado("COMPLETADO");
                h.setObservaciones("Generado por exportación (JDBC) preservando tablas de auditoría.");
                backupRepo.save(h);

                job.nombreArchivo = fileName;
                job.estado = "COMPLETED";

            } catch (Exception e) {
                e.printStackTrace();
                job.estado = "FAILED";
                job.mensaje = "Error al exportar: " + e.getMessage();
            }
        });

        return jobId;
    }

    public boolean eliminarBackup(Integer idBackup, String fileName) throws Exception {
        Path filePath = obtenerDirectorioSeguro().resolve(fileName);
        if (Files.exists(filePath)) Files.delete(filePath);
        backupRepo.deleteById(idBackup);
        return true;
    }

    public Path obtenerRutaArchivo(String fileName) {
        return obtenerDirectorioSeguro().resolve(fileName);
    }

    public void restaurarBackup(String fileName) throws Exception {
        Path filePath = obtenerDirectorioSeguro().resolve(fileName);
        if (!Files.exists(filePath)) {
            throw new Exception("El archivo de backup físico no fue encontrado en el servidor.");
        }
        try (Connection conn = jdbcTemplate.getDataSource().getConnection()) {
            ScriptUtils.executeSqlScript(conn, new FileSystemResource(filePath.toFile()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error crítico al ejecutar el script de restauración: " + e.getMessage());
        }
    }
}