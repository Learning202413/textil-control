-- Backup Generado Automáticamente por Textil Control
-- Fecha: 20260614_123148
SET FOREIGN_KEY_CHECKS=0;

-- ---------------------------------------------------------
-- Estructura de tabla: asignaciones_carga
-- ---------------------------------------------------------
DROP TABLE IF EXISTS `asignaciones_carga`;
CREATE TABLE `asignaciones_carga` (
  `id_asignacion` int NOT NULL AUTO_INCREMENT,
  `id_ot` int NOT NULL COMMENT 'FK → orden_trabajo (HU13)',
  `id_pieza` int DEFAULT NULL COMMENT 'FK → piezas_modelo; NULL para tareas de ENSAMBLAJE',
  `id_fase` int NOT NULL COMMENT 'FK → fases_produccion',
  `id_maquinista` int DEFAULT NULL COMMENT 'FK → usuarios rol=MAQUINISTA; NULL=sin asignar aún',
  `estado_fase` enum('PENDIENTE','EN_PROCESO','COMPLETADA','BLOQUEADA') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDIENTE',
  `fecha_asignacion` timestamp NULL DEFAULT NULL COMMENT 'Fecha en que se asignó el maquinista',
  `fecha_completado` timestamp NULL DEFAULT NULL COMMENT 'Fecha en que la fase se marcó COMPLETADA',
  `cantidad_piezas` int NOT NULL DEFAULT '1',
  `piezas_completadas` int DEFAULT NULL,
  `tipo_tarea` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `id_asignacion_padre` int DEFAULT NULL COMMENT 'Apunta a la tarea original',
  PRIMARY KEY (`id_asignacion`) /*T![clustered_index] CLUSTERED */,
  KEY `fk_ac_pieza` (`id_pieza`),
  KEY `fk_ac_fase` (`id_fase`),
  KEY `fk_ac_maq` (`id_maquinista`),
  KEY `idx_ac_ot` (`id_ot`),
  KEY `idx_ac_maq` (`id_maquinista`),
  KEY `idx_ac_estado` (`estado_fase`),
  KEY `idx_ac_padre` (`id_asignacion_padre`),
  UNIQUE KEY `uq_ac_ot_pieza_fase_normal` (`id_ot`,`id_pieza`,`id_fase`,`tipo_tarea`),
  UNIQUE KEY `uq_padre_reposicion` (`id_asignacion_padre`,`tipo_tarea`),
  CONSTRAINT `fk_ac_ot` FOREIGN KEY (`id_ot`) REFERENCES `orden_trabajo` (`id_ot`) ON DELETE CASCADE,
  CONSTRAINT `fk_ac_pieza` FOREIGN KEY (`id_pieza`) REFERENCES `piezas_modelo` (`id_pieza`) ON DELETE CASCADE,
  CONSTRAINT `fk_ac_fase` FOREIGN KEY (`id_fase`) REFERENCES `fases_produccion` (`id_fase`),
  CONSTRAINT `fk_ac_maq` FOREIGN KEY (`id_maquinista`) REFERENCES `usuarios` (`id_usuario`),
  CONSTRAINT `fk_ac_padre` FOREIGN KEY (`id_asignacion_padre`) REFERENCES `asignaciones_carga` (`id_asignacion`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci AUTO_INCREMENT=360001 COMMENT='HU05: Asignaciones de cargas de trabajo por fase y pieza';

-- Datos de tabla: asignaciones_carga
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (120007, 210001, 300001, 1, 210001, 'COMPLETADA', '2026-05-25 12:57:17', '2026-05-25 12:57:34', 40, 40, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (120008, 210001, 300001, 5, 210001, 'COMPLETADA', '2026-05-25 12:58:30', '2026-05-25 12:58:47', 40, 40, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (120009, 210001, 300002, 1, 210001, 'COMPLETADA', '2026-05-25 12:57:08', '2026-05-25 12:57:42', 40, 40, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (120010, 210001, 300002, 5, 210001, 'COMPLETADA', '2026-05-25 12:58:22', '2026-05-25 12:58:55', 40, 40, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (120011, 210001, 300003, 1, 210001, 'COMPLETADA', '2026-05-25 12:56:58', '2026-05-25 12:57:51', 20, 20, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (120012, 210001, 300003, 5, 210001, 'COMPLETADA', '2026-05-25 12:58:10', '2026-05-25 12:59:04', 20, 20, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (120013, 210001, NULL, 6, 210001, 'COMPLETADA', '2026-05-25 12:59:54', '2026-05-25 13:12:32', 20, 20, 'ENSAMBLAJE', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (150001, 1, 330001, 1, 210001, 'COMPLETADA', '2026-05-25 13:31:27', '2026-05-25 13:31:45', 300, 300, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (150002, 1, 330001, 2, 210001, 'COMPLETADA', '2026-05-25 13:35:48', '2026-05-25 13:36:15', 300, 300, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (150003, 1, NULL, 6, 210001, 'COMPLETADA', '2026-05-26 04:26:22', '2026-05-26 16:27:30', 300, 300, 'ENSAMBLAJE', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (150004, 2, 330001, 1, 210001, 'COMPLETADA', '2026-05-25 14:14:49', '2026-05-25 14:15:07', 150, 148, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (150005, 2, 330001, 2, 210001, 'COMPLETADA', '2026-05-25 14:17:59', '2026-05-25 14:18:28', 150, 140, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (150006, 2, 330001, 1, 210001, 'COMPLETADA', '2026-05-25 14:16:35', '2026-05-25 14:16:57', 2, 2, 'REPOSICION', 150004);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (150007, 2, 330001, 2, 210001, 'COMPLETADA', '2026-05-25 14:20:09', '2026-05-25 14:20:31', 10, 10, 'REPOSICION', 150005);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (150008, 2, NULL, 6, 210001, 'COMPLETADA', '2026-05-25 14:21:13', '2026-05-25 14:21:36', 150, 150, 'ENSAMBLAJE', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (150009, 3, 330001, 1, 210001, 'COMPLETADA', '2026-05-25 14:46:22', '2026-05-25 14:47:05', 80, 80, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (150010, 3, 330001, 2, 210001, 'COMPLETADA', '2026-05-25 14:47:39', '2026-05-25 14:47:59', 80, 70, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (150011, 3, 330001, 2, 210001, 'COMPLETADA', '2026-05-26 04:27:16', '2026-05-26 06:11:07', 10, 10, 'REPOSICION', 150010);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (150012, 30001, 240001, 1, 210001, 'COMPLETADA', '2026-05-25 15:49:03', '2026-05-25 16:11:03', 600, 600, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (150013, 30001, 240001, 3, 210001, 'EN_PROCESO', '2026-05-25 16:53:48', NULL, 600, NULL, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (150014, 30001, 240001, 5, NULL, 'PENDIENTE', NULL, NULL, 600, NULL, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (150015, 30001, 240002, 1, 210001, 'COMPLETADA', '2026-05-25 15:11:26', '2026-05-25 15:12:19', 100, 98, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (150016, 30001, 240002, 3, 210001, 'COMPLETADA', '2026-05-25 15:35:59', '2026-05-25 15:48:25', 100, 100, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (150017, 30001, 240002, 5, 210001, 'COMPLETADA', '2026-05-25 15:48:50', '2026-05-25 15:49:25', 100, 90, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (150018, 30001, 240003, 1, NULL, 'PENDIENTE', NULL, NULL, 100, NULL, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (150019, 30001, 240003, 2, NULL, 'PENDIENTE', NULL, NULL, 100, NULL, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (150020, 30001, 240004, 1, NULL, 'PENDIENTE', NULL, NULL, 200, NULL, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (150021, 30001, 240004, 2, NULL, 'PENDIENTE', NULL, NULL, 200, NULL, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (150022, 60001, 330001, 1, NULL, 'PENDIENTE', NULL, NULL, 148, NULL, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (150023, 60001, 330001, 2, NULL, 'PENDIENTE', NULL, NULL, 148, NULL, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (150024, 30001, 240002, 1, 210001, 'COMPLETADA', '2026-05-25 15:34:37', '2026-05-25 15:35:29', 2, 2, 'REPOSICION', 150015);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (150025, 30001, 240002, 5, 210001, 'COMPLETADA', '2026-05-25 15:50:28', '2026-05-25 15:50:42', 10, 10, 'REPOSICION', 150017);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (150026, 240001, 330002, 1, 6, 'COMPLETADA', '2026-05-26 21:56:29', '2026-05-26 21:57:46', 100, 98, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (150027, 240001, 330002, 3, NULL, 'PENDIENTE', NULL, NULL, 100, NULL, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (150028, 240001, 330003, 3, NULL, 'PENDIENTE', NULL, NULL, 100, NULL, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (180001, 120001, 240001, 1, NULL, 'PENDIENTE', NULL, NULL, 2052, NULL, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (180002, 120001, 240001, 3, NULL, 'PENDIENTE', NULL, NULL, 2052, NULL, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (180003, 120001, 240001, 5, NULL, 'PENDIENTE', NULL, NULL, 2052, NULL, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (180004, 120001, 240002, 1, NULL, 'PENDIENTE', NULL, NULL, 342, NULL, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (180005, 120001, 240002, 3, NULL, 'PENDIENTE', NULL, NULL, 342, NULL, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (180006, 120001, 240002, 5, NULL, 'PENDIENTE', NULL, NULL, 342, NULL, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (180007, 120001, 240003, 1, NULL, 'PENDIENTE', NULL, NULL, 342, NULL, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (180008, 120001, 240003, 2, NULL, 'PENDIENTE', NULL, NULL, 342, NULL, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (180009, 120001, 240004, 1, NULL, 'PENDIENTE', NULL, NULL, 684, NULL, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (180010, 120001, 240004, 2, NULL, 'PENDIENTE', NULL, NULL, 684, NULL, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (180011, 3, NULL, 6, NULL, 'PENDIENTE', NULL, NULL, 80, NULL, 'ENSAMBLAJE', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (210001, 240001, 330002, 1, NULL, 'PENDIENTE', NULL, NULL, 2, 0, 'REPOSICION', 150026);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (240001, 270001, 360001, 1, NULL, 'PENDIENTE', NULL, NULL, 120, NULL, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (240002, 270001, 360001, 3, NULL, 'PENDIENTE', NULL, NULL, 120, NULL, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (240003, 270001, 360001, 5, NULL, 'PENDIENTE', NULL, NULL, 120, NULL, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (240004, 270001, 360002, 1, NULL, 'PENDIENTE', NULL, NULL, 120, NULL, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (240005, 270001, 360002, 3, NULL, 'PENDIENTE', NULL, NULL, 120, NULL, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (240006, 270001, 360002, 5, NULL, 'PENDIENTE', NULL, NULL, 120, NULL, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (240007, 270001, 360003, 1, NULL, 'PENDIENTE', NULL, NULL, 240, NULL, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (240008, 270001, 360003, 3, NULL, 'PENDIENTE', NULL, NULL, 240, NULL, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (240009, 270001, 360003, 5, NULL, 'PENDIENTE', NULL, NULL, 240, NULL, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (240010, 270001, 360004, 1, NULL, 'PENDIENTE', NULL, NULL, 120, NULL, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (240011, 270001, 360004, 2, NULL, 'PENDIENTE', NULL, NULL, 120, NULL, 'NORMAL', NULL);
INSERT INTO `asignaciones_carga` (`id_asignacion`, `id_ot`, `id_pieza`, `id_fase`, `id_maquinista`, `estado_fase`, `fecha_asignacion`, `fecha_completado`, `cantidad_piezas`, `piezas_completadas`, `tipo_tarea`, `id_asignacion_padre`) VALUES (240012, 270001, 360004, 5, NULL, 'PENDIENTE', NULL, NULL, 120, NULL, 'NORMAL', NULL);

-- ---------------------------------------------------------
-- Estructura de tabla: catalogo_telas
-- ---------------------------------------------------------
DROP TABLE IF EXISTS `catalogo_telas`;
CREATE TABLE `catalogo_telas` (
  `id_catalogo` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `composicion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `proveedor_base` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `requiere_reposo` tinyint(1) DEFAULT '0',
  `fecha_registro` timestamp DEFAULT CURRENT_TIMESTAMP,
  `tiempo_reposo` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id_catalogo`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci AUTO_INCREMENT=450001;

-- Datos de tabla: catalogo_telas
INSERT INTO `catalogo_telas` (`id_catalogo`, `nombre`, `composicion`, `proveedor_base`, `requiere_reposo`, `fecha_registro`, `tiempo_reposo`) VALUES (1, 'Franela Reactiva 30/1', '100% Algodón', 'Textiles del Sur S.A.', 0, '2026-05-07 06:07:52', 0);
INSERT INTO `catalogo_telas` (`id_catalogo`, `nombre`, `composicion`, `proveedor_base`, `requiere_reposo`, `fecha_registro`, `tiempo_reposo`) VALUES (90001, 'Elástico 4 vías premium', 'Spandex / Nylon', 'Importaciones ABCD', 1, '2026-05-12 13:46:59', 10);
INSERT INTO `catalogo_telas` (`id_catalogo`, `nombre`, `composicion`, `proveedor_base`, `requiere_reposo`, `fecha_registro`, `tiempo_reposo`) VALUES (180001, 'elastico', 'Spandex', 'Cintia', 0, '2026-05-17 15:32:48', 0);
INSERT INTO `catalogo_telas` (`id_catalogo`, `nombre`, `composicion`, `proveedor_base`, `requiere_reposo`, `fecha_registro`, `tiempo_reposo`) VALUES (210001, 'prueba', 'prueba', 'prueba', 1, '2026-05-25 16:54:37', 160);
INSERT INTO `catalogo_telas` (`id_catalogo`, `nombre`, `composicion`, `proveedor_base`, `requiere_reposo`, `fecha_registro`, `tiempo_reposo`) VALUES (300001, 'Spting', 'boots', 'boots', 1, '2026-06-11 15:15:14', 20);
INSERT INTO `catalogo_telas` (`id_catalogo`, `nombre`, `composicion`, `proveedor_base`, `requiere_reposo`, `fecha_registro`, `tiempo_reposo`) VALUES (330001, 'ax', 'ax', 'ax', 0, '2026-06-14 16:59:50', 0);

-- ---------------------------------------------------------
-- Estructura de tabla: conciliacion_despacho
-- ---------------------------------------------------------
DROP TABLE IF EXISTS `conciliacion_despacho`;
CREATE TABLE `conciliacion_despacho` (
  `id_conciliacion` int NOT NULL AUTO_INCREMENT,
  `id_ot` int NOT NULL COMMENT 'FK → orden_trabajo (estado=FINALIZADA)',
  `cantidad_final` int NOT NULL COMMENT 'Conteo físico real del almacén',
  `diferencia` int NOT NULL COMMENT 'cantidad_final − orden_trabajo.cantidad_est (negativo = merma)',
  `estado` enum('PENDIENTE','CONCILIADO_OK','MERMA_DETECTADA','DESPACHADO') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDIENTE',
  `id_responsable` int NOT NULL COMMENT 'FK → usuarios (SUPERVISOR o ADMIN que concilia)',
  `fecha_conciliacion` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'CUS 7.2: fecha de conciliación',
  `fecha_despacho` timestamp NULL DEFAULT NULL COMMENT 'CUS 7.4: fecha en que se confirma el despacho',
  `observaciones` text COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cantidad_ensamblaje` int DEFAULT NULL COMMENT 'Prendas reales reportadas',
  `id_asignacion_ensamblaje` int DEFAULT NULL COMMENT 'FK a asignaciones_carga',
  PRIMARY KEY (`id_conciliacion`) /*T![clustered_index] CLUSTERED */,
  UNIQUE KEY `uq_cd_ot` (`id_ot`) COMMENT 'Una sola conciliación por OT',
  KEY `fk_cd_resp` (`id_responsable`),
  KEY `idx_cd_estado` (`estado`),
  KEY `idx_cd_ensamblaje` (`id_asignacion_ensamblaje`),
  CONSTRAINT `fk_cd_ot` FOREIGN KEY (`id_ot`) REFERENCES `orden_trabajo` (`id_ot`),
  CONSTRAINT `fk_cd_resp` FOREIGN KEY (`id_responsable`) REFERENCES `usuarios` (`id_usuario`),
  CONSTRAINT `fk_cd_ensamblaje` FOREIGN KEY (`id_asignacion_ensamblaje`) REFERENCES `asignaciones_carga` (`id_asignacion`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci AUTO_INCREMENT=240001 COMMENT='HU07: Conciliación de inventario físico vs estimado y despacho';

-- Datos de tabla: conciliacion_despacho
INSERT INTO `conciliacion_despacho` (`id_conciliacion`, `id_ot`, `cantidad_final`, `diferencia`, `estado`, `id_responsable`, `fecha_conciliacion`, `fecha_despacho`, `observaciones`, `cantidad_ensamblaje`, `id_asignacion_ensamblaje`) VALUES (60001, 210001, 20, 0, 'DESPACHADO', 1, '2026-05-25 13:22:53', '2026-05-25 13:23:02', 'Ninguna', 20, 120013);
INSERT INTO `conciliacion_despacho` (`id_conciliacion`, `id_ot`, `cantidad_final`, `diferencia`, `estado`, `id_responsable`, `fecha_conciliacion`, `fecha_despacho`, `observaciones`, `cantidad_ensamblaje`, `id_asignacion_ensamblaje`) VALUES (60002, 2, 150, 0, 'DESPACHADO', 1, '2026-05-25 16:36:41', '2026-05-25 16:47:01', 'NINGUNA', 150, 150008);
INSERT INTO `conciliacion_despacho` (`id_conciliacion`, `id_ot`, `cantidad_final`, `diferencia`, `estado`, `id_responsable`, `fecha_conciliacion`, `fecha_despacho`, `observaciones`, `cantidad_ensamblaje`, `id_asignacion_ensamblaje`) VALUES (90001, 150002, 5, 0, 'DESPACHADO', 1, '2026-05-26 10:25:37', '2026-05-26 10:26:27', '', NULL, NULL);
INSERT INTO `conciliacion_despacho` (`id_conciliacion`, `id_ot`, `cantidad_final`, `diferencia`, `estado`, `id_responsable`, `fecha_conciliacion`, `fecha_despacho`, `observaciones`, `cantidad_ensamblaje`, `id_asignacion_ensamblaje`) VALUES (120001, 1, 300, 0, 'PENDIENTE', 3, '2026-05-26 16:27:34', NULL, NULL, 300, 150003);

-- ---------------------------------------------------------
-- Estructura de tabla: defectos_reproceso
-- ---------------------------------------------------------
DROP TABLE IF EXISTS `defectos_reproceso`;
CREATE TABLE `defectos_reproceso` (
  `id_defecto` int NOT NULL AUTO_INCREMENT,
  `id_ot` int NOT NULL COMMENT 'FK → orden_trabajo',
  `id_pieza` int DEFAULT NULL COMMENT 'FK → piezas_modelo (opcional: a nivel pieza)',
  `id_maquinista` int NOT NULL COMMENT 'FK → usuarios (maquinista responsable)',
  `tipo_falla` enum('ERROR_COSTURA','SALTO_PUNTADA','MANCHA_SUCIEDAD','TENSION_INCORRECTA','CORTE_IRREGULAR','ENSAMBLAJE','OTRO') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'OTRO',
  `observaciones` text COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_registro` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `estado` enum('PENDIENTE','REGISTRADO','CORREGIDO') COLLATE utf8mb4_unicode_ci DEFAULT 'REGISTRADO',
  `cantidad_faltante` int DEFAULT '1',
  `id_asignacion` int DEFAULT NULL,
  `genera_reposicion` tinyint(1) NOT NULL DEFAULT '0' COMMENT '1 = este defecto tiene una tarea de reposición asociada en asignaciones_carga',
  PRIMARY KEY (`id_defecto`) /*T![clustered_index] CLUSTERED */,
  KEY `fk_dr_ot` (`id_ot`),
  KEY `fk_dr_pieza` (`id_pieza`),
  KEY `fk_dr_maq` (`id_maquinista`),
  KEY `idx_dr_ot` (`id_ot`),
  KEY `idx_dr_maq` (`id_maquinista`),
  KEY `idx_dr_fecha` (`fecha_registro`),
  KEY `idx_estado` (`estado`),
  KEY `idx_asignacion` (`id_asignacion`),
  CONSTRAINT `fk_dr_ot` FOREIGN KEY (`id_ot`) REFERENCES `orden_trabajo` (`id_ot`),
  CONSTRAINT `fk_dr_pieza` FOREIGN KEY (`id_pieza`) REFERENCES `piezas_modelo` (`id_pieza`),
  CONSTRAINT `fk_dr_maq` FOREIGN KEY (`id_maquinista`) REFERENCES `usuarios` (`id_usuario`),
  CONSTRAINT `fk_defecto_asignacion` FOREIGN KEY (`id_asignacion`) REFERENCES `asignaciones_carga` (`id_asignacion`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci AUTO_INCREMENT=180001 COMMENT='HU06: Defectos y reprocesos por OT y maquinista responsable';

-- Datos de tabla: defectos_reproceso
INSERT INTO `defectos_reproceso` (`id_defecto`, `id_ot`, `id_pieza`, `id_maquinista`, `tipo_falla`, `observaciones`, `fecha_registro`, `estado`, `cantidad_faltante`, `id_asignacion`, `genera_reposicion`) VALUES (30001, 2, 330001, 210001, 'ERROR_COSTURA', 'Ninguna', '2026-05-25 14:15:06', 'REGISTRADO', 2, 150004, 1);
INSERT INTO `defectos_reproceso` (`id_defecto`, `id_ot`, `id_pieza`, `id_maquinista`, `tipo_falla`, `observaciones`, `fecha_registro`, `estado`, `cantidad_faltante`, `id_asignacion`, `genera_reposicion`) VALUES (30002, 2, 330001, 210001, 'MANCHA_SUCIEDAD', 'Descuido', '2026-05-25 14:18:27', 'REGISTRADO', 10, 150005, 1);
INSERT INTO `defectos_reproceso` (`id_defecto`, `id_ot`, `id_pieza`, `id_maquinista`, `tipo_falla`, `observaciones`, `fecha_registro`, `estado`, `cantidad_faltante`, `id_asignacion`, `genera_reposicion`) VALUES (30003, 3, 330001, 210001, 'SALTO_PUNTADA', 'prueba', '2026-05-25 14:47:58', 'REGISTRADO', 10, 150010, 1);
INSERT INTO `defectos_reproceso` (`id_defecto`, `id_ot`, `id_pieza`, `id_maquinista`, `tipo_falla`, `observaciones`, `fecha_registro`, `estado`, `cantidad_faltante`, `id_asignacion`, `genera_reposicion`) VALUES (30004, 30001, 240002, 210001, 'ERROR_COSTURA', 'ju', '2026-05-25 15:12:18', 'REGISTRADO', 2, 150015, 1);
INSERT INTO `defectos_reproceso` (`id_defecto`, `id_ot`, `id_pieza`, `id_maquinista`, `tipo_falla`, `observaciones`, `fecha_registro`, `estado`, `cantidad_faltante`, `id_asignacion`, `genera_reposicion`) VALUES (30005, 30001, 240002, 210001, 'ERROR_COSTURA', 'ds', '2026-05-25 15:49:24', 'REGISTRADO', 10, 150017, 1);
INSERT INTO `defectos_reproceso` (`id_defecto`, `id_ot`, `id_pieza`, `id_maquinista`, `tipo_falla`, `observaciones`, `fecha_registro`, `estado`, `cantidad_faltante`, `id_asignacion`, `genera_reposicion`) VALUES (30006, 30001, 240001, 210001, 'OTRO', 'Producción incompleta reportada por el maquinista. Faltan 100 unidades.', '2026-05-25 16:11:02', 'CORREGIDO', 0, 150012, 0);
INSERT INTO `defectos_reproceso` (`id_defecto`, `id_ot`, `id_pieza`, `id_maquinista`, `tipo_falla`, `observaciones`, `fecha_registro`, `estado`, `cantidad_faltante`, `id_asignacion`, `genera_reposicion`) VALUES (60001, 240001, 330002, 6, 'ERROR_COSTURA', '', '2026-05-26 21:57:46', 'REGISTRADO', 2, 150026, 1);

-- ---------------------------------------------------------
-- Estructura de tabla: especialidades
-- ---------------------------------------------------------
DROP TABLE IF EXISTS `especialidades`;
CREATE TABLE `especialidades` (
  `id_especialidad` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `descripcion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id_especialidad`) /*T![clustered_index] CLUSTERED */,
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci AUTO_INCREMENT=330001;

-- Datos de tabla: especialidades
INSERT INTO `especialidades` (`id_especialidad`, `nombre`, `descripcion`) VALUES (1, 'ORILLADO', 'Acabado de bordes completos');
INSERT INTO `especialidades` (`id_especialidad`, `nombre`, `descripcion`) VALUES (2, 'REMALLADO', 'Unión de piezas con remalladora');
INSERT INTO `especialidades` (`id_especialidad`, `nombre`, `descripcion`) VALUES (3, 'PESPUNTADO', 'Costura decorativa y refuerzo');
INSERT INTO `especialidades` (`id_especialidad`, `nombre`, `descripcion`) VALUES (5, 'PLANCHADO', 'Planchado y presentación final');
INSERT INTO `especialidades` (`id_especialidad`, `nombre`, `descripcion`) VALUES (90001, 'Punta Estrella', 'Esta es una prueba para ver si funciona.');
INSERT INTO `especialidades` (`id_especialidad`, `nombre`, `descripcion`) VALUES (180001, 'TRAZADO', 'Trazado de lineas');
INSERT INTO `especialidades` (`id_especialidad`, `nombre`, `descripcion`) VALUES (210001, 'PRUEBA', 'PRUEBA EN SPRING BOOT');
INSERT INTO `especialidades` (`id_especialidad`, `nombre`, `descripcion`) VALUES (210002, 'Prueba2', '');

-- ---------------------------------------------------------
-- Estructura de tabla: fallas_tela
-- ---------------------------------------------------------
DROP TABLE IF EXISTS `fallas_tela`;
CREATE TABLE `fallas_tela` (
  `id_falla` int NOT NULL AUTO_INCREMENT,
  `id_tela` int NOT NULL,
  `id_tizador` int NOT NULL,
  `tipo_falla` enum('MANCHA','HUECO','DEFECTO_TEJIDO') COLLATE utf8mb4_unicode_ci NOT NULL,
  `posicion_rollo` int NOT NULL,
  `posicion_metro` decimal(38,2) DEFAULT NULL,
  `ancho_cm` decimal(38,2) DEFAULT NULL,
  `largo_cm` decimal(38,2) DEFAULT NULL,
  `descripcion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `es_area_no_apta` tinyint(1) NOT NULL DEFAULT '1',
  `fecha_registro` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_falla`) /*T![clustered_index] CLUSTERED */,
  KEY `fk_falla_tela` (`id_tela`),
  KEY `fk_falla_tizador` (`id_tizador`),
  KEY `idx_falla_tela` (`id_tela`),
  KEY `idx_falla_tipo` (`tipo_falla`),
  CONSTRAINT `fk_falla_tela` FOREIGN KEY (`id_tela`) REFERENCES `telas` (`id_tela`),
  CONSTRAINT `fk_falla_tizador` FOREIGN KEY (`id_tizador`) REFERENCES `usuarios` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci AUTO_INCREMENT=150001;

-- Datos de tabla: fallas_tela
INSERT INTO `fallas_tela` (`id_falla`, `id_tela`, `id_tizador`, `tipo_falla`, `posicion_rollo`, `posicion_metro`, `ancho_cm`, `largo_cm`, `descripcion`, `es_area_no_apta`, `fecha_registro`) VALUES (1, 2, 1, 'MANCHA', 1, 2.50, 5.00, 3.00, 'Mancha de aceite en borde derecho del rollo 1', 1, '2026-05-17 22:05:09');
INSERT INTO `fallas_tela` (`id_falla`, `id_tela`, `id_tizador`, `tipo_falla`, `posicion_rollo`, `posicion_metro`, `ancho_cm`, `largo_cm`, `descripcion`, `es_area_no_apta`, `fecha_registro`) VALUES (2, 2, 1, 'HUECO', 1, 7.80, 2.00, 2.00, 'Hueco pequeño en zona central', 1, '2026-05-17 22:05:09');
INSERT INTO `fallas_tela` (`id_falla`, `id_tela`, `id_tizador`, `tipo_falla`, `posicion_rollo`, `posicion_metro`, `ancho_cm`, `largo_cm`, `descripcion`, `es_area_no_apta`, `fecha_registro`) VALUES (3, 60001, 1, 'DEFECTO_TEJIDO', 2, 1.00, 5.00, 6.00, '', 0, '2026-05-17 22:08:21');
INSERT INTO `fallas_tela` (`id_falla`, `id_tela`, `id_tizador`, `tipo_falla`, `posicion_rollo`, `posicion_metro`, `ancho_cm`, `largo_cm`, `descripcion`, `es_area_no_apta`, `fecha_registro`) VALUES (30001, 2, 1, 'HUECO', 1, 0.70, 2.00, 3.00, '', 1, '2026-06-12 20:10:04');

-- ---------------------------------------------------------
-- Estructura de tabla: fases_produccion
-- ---------------------------------------------------------
DROP TABLE IF EXISTS `fases_produccion`;
CREATE TABLE `fases_produccion` (
  `id_fase` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `orden` int NOT NULL COMMENT 'Secuencia: 1=Corte, 2=Orillado, ... N=última fase',
  `descripcion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id_fase`) /*T![clustered_index] CLUSTERED */,
  UNIQUE KEY `uq_fase_orden` (`orden`),
  UNIQUE KEY `uq_fase_nombre` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci AUTO_INCREMENT=180001 COMMENT='HU05: Catálogo de fases de confección en orden secuencial';

-- Datos de tabla: fases_produccion
INSERT INTO `fases_produccion` (`id_fase`, `nombre`, `orden`, `descripcion`) VALUES (1, 'CORTE', 1, 'Corte de tela según el tizado. Especialidad: CORTE');
INSERT INTO `fases_produccion` (`id_fase`, `nombre`, `orden`, `descripcion`) VALUES (2, 'ORILLADO', 2, 'Acabado de bordes de cada pieza. Especialidad: ORILLADO');
INSERT INTO `fases_produccion` (`id_fase`, `nombre`, `orden`, `descripcion`) VALUES (3, 'REMALLADO', 3, 'Unión de piezas con remalladora. Especialidad: REMALLADO');
INSERT INTO `fases_produccion` (`id_fase`, `nombre`, `orden`, `descripcion`) VALUES (4, 'PESPUNTADO', 4, 'Costura decorativa y de refuerzo. Especialidad: PESPUNTADO');
INSERT INTO `fases_produccion` (`id_fase`, `nombre`, `orden`, `descripcion`) VALUES (5, 'PLANCHADO', 5, 'Planchado y presentación final. Especialidad: PLANCHADO');
INSERT INTO `fases_produccion` (`id_fase`, `nombre`, `orden`, `descripcion`) VALUES (6, 'ENSAMBLAJE', 6, 'Ensamblaje final de prendas. Une todas las piezas. Fase Gatekeeper de la OT.');

-- ---------------------------------------------------------
-- Estructura de tabla: fotos_tela
-- ---------------------------------------------------------
DROP TABLE IF EXISTS `fotos_tela`;
CREATE TABLE `fotos_tela` (
  `id_foto` int NOT NULL AUTO_INCREMENT,
  `id_tela` int NOT NULL COMMENT 'Tela a la que pertenece la foto',
  `nombre_archivo` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Nombre del archivo guardado en disco',
  `ruta_relativa` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_subida` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_foto`) /*T![clustered_index] CLUSTERED */,
  KEY `fk_foto_tela` (`id_tela`),
  KEY `idx_foto_tela` (`id_tela`),
  CONSTRAINT `fk_foto_tela` FOREIGN KEY (`id_tela`) REFERENCES `telas` (`id_tela`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci AUTO_INCREMENT=270001;

-- Datos de tabla: fotos_tela
INSERT INTO `fotos_tela` (`id_foto`, `id_tela`, `nombre_archivo`, `ruta_relativa`, `fecha_subida`) VALUES (60002, 30001, 'd65980b3-9b69-4d0f-ad1f-dd3a647c21e7.jpg', 'uploads/telas/30001/d65980b3-9b69-4d0f-ad1f-dd3a647c21e7.jpg', '2026-05-23 22:20:20');
INSERT INTO `fotos_tela` (`id_foto`, `id_tela`, `nombre_archivo`, `ruta_relativa`, `fecha_subida`) VALUES (60003, 180001, '1e2f6881-5f44-4ae8-9e9b-95ddf77a2ae3.jpg', 'uploads/telas/180001/1e2f6881-5f44-4ae8-9e9b-95ddf77a2ae3.jpg', '2026-05-23 22:57:02');
INSERT INTO `fotos_tela` (`id_foto`, `id_tela`, `nombre_archivo`, `ruta_relativa`, `fecha_subida`) VALUES (90001, 240001, 'a5c98d32-1384-4d7d-b66e-36988865e23d.jpg', 'uploads/telas/240001/a5c98d32-1384-4d7d-b66e-36988865e23d.jpg', '2026-05-24 23:09:19');
INSERT INTO `fotos_tela` (`id_foto`, `id_tela`, `nombre_archivo`, `ruta_relativa`, `fecha_subida`) VALUES (120001, 300001, '31cc81c9-7caa-491e-ba16-972fa9566cb7.jpg', 'uploads/telas/300001/31cc81c9-7caa-491e-ba16-972fa9566cb7.jpg', '2026-05-26 14:39:43');
INSERT INTO `fotos_tela` (`id_foto`, `id_tela`, `nombre_archivo`, `ruta_relativa`, `fecha_subida`) VALUES (150001, 360001, '2a9bb6e8-32b3-4252-b978-75a0638cdefd.png', 'uploads/telas/360001/2a9bb6e8-32b3-4252-b978-75a0638cdefd.png', '2026-06-11 17:19:13');

-- ---------------------------------------------------------
-- Estructura de tabla: mermas
-- ---------------------------------------------------------
DROP TABLE IF EXISTS `mermas`;
CREATE TABLE `mermas` (
  `id_merma` int NOT NULL AUTO_INCREMENT,
  `id_tela` int NOT NULL COMMENT 'Tela sobre la que se genera la merma',
  `id_ot` int NOT NULL COMMENT 'Orden de trabajo/corte asociada',
  `id_tizador` int NOT NULL COMMENT 'Tizador que registra',
  `fase` enum('TIZADO','CORTE') COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Fase donde se genera la merma',
  `peso_utilizado_kg` decimal(10,2) DEFAULT NULL,
  `peso_merma_kg` decimal(10,2) DEFAULT NULL,
  `observaciones` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_registro` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `porcentaje_merma` decimal(10,2) GENERATED ALWAYS AS (case when `peso_utilizado_kg` > 0 then round((`peso_merma_kg` / `peso_utilizado_kg`) * 100, 3) else 0 end) VIRTUAL COMMENT 'CA1 HU04: % merma calculado automáticamente',
  PRIMARY KEY (`id_merma`) /*T![clustered_index] CLUSTERED */,
  KEY `fk_merma_tela` (`id_tela`),
  KEY `fk_merma_ot` (`id_ot`),
  KEY `fk_merma_tizador` (`id_tizador`),
  KEY `idx_merma_tela` (`id_tela`),
  KEY `idx_merma_ot` (`id_ot`),
  CONSTRAINT `fk_merma_tela` FOREIGN KEY (`id_tela`) REFERENCES `telas` (`id_tela`),
  CONSTRAINT `fk_merma_ot` FOREIGN KEY (`id_ot`) REFERENCES `orden_trabajo` (`id_ot`),
  CONSTRAINT `fk_merma_tizador` FOREIGN KEY (`id_tizador`) REFERENCES `usuarios` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci AUTO_INCREMENT=120001 COMMENT='HU04: Merma generada en tizado y corte por orden';

-- Datos de tabla: mermas

-- ---------------------------------------------------------
-- Estructura de tabla: modelos_prenda
-- ---------------------------------------------------------
DROP TABLE IF EXISTS `modelos_prenda`;
CREATE TABLE `modelos_prenda` (
  `id_modelo` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `temporada` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_registro` timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_modelo`) /*T![clustered_index] CLUSTERED */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci AUTO_INCREMENT=330002;

-- Datos de tabla: modelos_prenda
INSERT INTO `modelos_prenda` (`id_modelo`, `nombre`, `temporada`, `fecha_registro`) VALUES (1, 'Corset Clásico Victoria', 'Otoño-Invierno 2026', '2026-05-07 06:07:53');
INSERT INTO `modelos_prenda` (`id_modelo`, `nombre`, `temporada`, `fecha_registro`) VALUES (2, 'Top Corset Venus', 'Verano 2026', '2026-05-07 06:07:53');
INSERT INTO `modelos_prenda` (`id_modelo`, `nombre`, `temporada`, `fecha_registro`) VALUES (120002, 'Polo', 'Verano 2026', '2026-05-25 05:46:25');
INSERT INTO `modelos_prenda` (`id_modelo`, `nombre`, `temporada`, `fecha_registro`) VALUES (150003, 'Gorro', 'Invierno', '2026-05-25 12:30:24');
INSERT INTO `modelos_prenda` (`id_modelo`, `nombre`, `temporada`, `fecha_registro`) VALUES (180002, 'prueba1', 'prueba1', '2026-05-25 16:55:07');
INSERT INTO `modelos_prenda` (`id_modelo`, `nombre`, `temporada`, `fecha_registro`) VALUES (210002, 'Casaca ', 'Invierno', '2026-05-26 04:09:45');
INSERT INTO `modelos_prenda` (`id_modelo`, `nombre`, `temporada`, `fecha_registro`) VALUES (240002, 'Spritng', 'boot', '2026-06-09 18:28:34');

-- ---------------------------------------------------------
-- Estructura de tabla: notificaciones
-- ---------------------------------------------------------
DROP TABLE IF EXISTS `notificaciones`;
CREATE TABLE `notificaciones` (
  `id_notificacion` int NOT NULL AUTO_INCREMENT,
  `titulo` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `mensaje` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tipo` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `id_referencia` int DEFAULT NULL,
  `para_rol` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `leida` tinyint(1) DEFAULT '0',
  `fecha_creacion` timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_notificacion`) /*T![clustered_index] CLUSTERED */,
  KEY `idx_para_rol` (`para_rol`),
  KEY `idx_leida` (`leida`),
  KEY `fk_notificacion_asignacion` (`id_referencia`),
  CONSTRAINT `fk_notificacion_asignacion` FOREIGN KEY (`id_referencia`) REFERENCES `asignaciones_carga` (`id_asignacion`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci AUTO_INCREMENT=210001;

-- Datos de tabla: notificaciones
INSERT INTO `notificaciones` (`id_notificacion`, `titulo`, `mensaje`, `tipo`, `id_referencia`, `para_rol`, `leida`, `fecha_creacion`) VALUES (1, '✅ Tarea completada', 'El maquinista Betsy Aldo completó la fase ''ORILLADO'' de la pieza ''Espalda ajustable'' para la OT OT-2026-0011 (Cantidad: 40/40).', 'TAREA_COMPLETADA', NULL, 'ADMINISTRADOR,JEFE_PRODUCCION,SUPERVISOR', 1, '2026-05-24 23:48:47');
INSERT INTO `notificaciones` (`id_notificacion`, `titulo`, `mensaje`, `tipo`, `id_referencia`, `para_rol`, `leida`, `fecha_creacion`) VALUES (2, '✅ Tarea completada', 'El maquinista Betsy Aldo completó la fase ''CORTE'' de la pieza ''Panel central'' para la OT OT-2026-0011 (Cantidad: 20/20).', 'TAREA_COMPLETADA', NULL, 'ADMINISTRADOR,JEFE_PRODUCCION,SUPERVISOR', 1, '2026-05-25 00:03:24');
INSERT INTO `notificaciones` (`id_notificacion`, `titulo`, `mensaje`, `tipo`, `id_referencia`, `para_rol`, `leida`, `fecha_creacion`) VALUES (3, '✅ Tarea completada', 'El maquinista Betsy Aldo completó la fase ''CORTE'' de la pieza ''Copa delantera derecha'' para la OT OT-2026-0011 (Cantidad: 20/20).', 'TAREA_COMPLETADA', NULL, 'ADMINISTRADOR,JEFE_PRODUCCION,SUPERVISOR', 1, '2026-05-25 00:06:23');
INSERT INTO `notificaciones` (`id_notificacion`, `titulo`, `mensaje`, `tipo`, `id_referencia`, `para_rol`, `leida`, `fecha_creacion`) VALUES (4, '✅ Tarea completada', 'El maquinista Betsy Aldo completó la fase ''CORTE'' de la pieza ''Tirantes elásticos'' para la OT OT-2026-0010 (Cantidad: 40/40).', 'TAREA_COMPLETADA', NULL, 'ADMINISTRADOR,JEFE_PRODUCCION,SUPERVISOR', 1, '2026-05-25 00:11:50');
INSERT INTO `notificaciones` (`id_notificacion`, `titulo`, `mensaje`, `tipo`, `id_referencia`, `para_rol`, `leida`, `fecha_creacion`) VALUES (5, '✅ Tarea completada', 'El maquinista Betsy Aldo completó la fase ''ORILLADO'' de la pieza ''Tirantes elásticos'' para la OT OT-2026-0010 (Cantidad: 40/40).', 'TAREA_COMPLETADA', NULL, 'ADMINISTRADOR,JEFE_PRODUCCION,SUPERVISOR', 1, '2026-05-25 00:20:21');
INSERT INTO `notificaciones` (`id_notificacion`, `titulo`, `mensaje`, `tipo`, `id_referencia`, `para_rol`, `leida`, `fecha_creacion`) VALUES (6, '✅ Tarea completada', 'El maquinista Betsy Aldo completó la fase ''CORTE'' de la pieza ''Cuerpo tubular'' para la OT OT-2026-0006 (Cantidad: 148/150).', 'TAREA_COMPLETADA', NULL, 'ADMINISTRADOR,JEFE_PRODUCCION,SUPERVISOR', 1, '2026-05-25 01:22:32');
INSERT INTO `notificaciones` (`id_notificacion`, `titulo`, `mensaje`, `tipo`, `id_referencia`, `para_rol`, `leida`, `fecha_creacion`) VALUES (7, '✅ Tarea completada', 'El maquinista Betsy Aldo completó la fase ''CORTE'' de la pieza ''Copa delantera derecha'' para la OT OT-2026-0008 (Cantidad: 130/150).', 'TAREA_COMPLETADA', NULL, 'ADMINISTRADOR,JEFE_PRODUCCION,SUPERVISOR', 1, '2026-05-25 01:25:36');
INSERT INTO `notificaciones` (`id_notificacion`, `titulo`, `mensaje`, `tipo`, `id_referencia`, `para_rol`, `leida`, `fecha_creacion`) VALUES (8, '✅ Tarea completada', 'El maquinista Betsy Aldo completó la fase ''ORILLADO'' de la pieza ''Panel central'' para la OT OT-2026-0011 (Cantidad: 18/20).', 'TAREA_COMPLETADA', NULL, 'ADMINISTRADOR,JEFE_PRODUCCION,SUPERVISOR', 1, '2026-05-25 01:27:27');
INSERT INTO `notificaciones` (`id_notificacion`, `titulo`, `mensaje`, `tipo`, `id_referencia`, `para_rol`, `leida`, `fecha_creacion`) VALUES (9, '✅ Tarea completada', 'El maquinista Betsy Aldo completó la fase ''CORTE'' de la pieza ''Copa delantera izquierda'' para la OT OT-2026-0005 (Cantidad: 888/888).', 'TAREA_COMPLETADA', NULL, 'ADMINISTRADOR,JEFE_PRODUCCION,SUPERVISOR', 1, '2026-05-25 01:29:43');
INSERT INTO `notificaciones` (`id_notificacion`, `titulo`, `mensaje`, `tipo`, `id_referencia`, `para_rol`, `leida`, `fecha_creacion`) VALUES (10, '✅ Tarea completada', 'El maquinista Betsy Aldo completó la fase ''REMALLADO'' de la pieza ''Copa delantera derecha'' para la OT OT-2026-0011 (Cantidad: 18/20).', 'TAREA_COMPLETADA', NULL, 'ADMINISTRADOR,JEFE_PRODUCCION,SUPERVISOR', 1, '2026-05-25 01:37:28');
INSERT INTO `notificaciones` (`id_notificacion`, `titulo`, `mensaje`, `tipo`, `id_referencia`, `para_rol`, `leida`, `fecha_creacion`) VALUES (11, '✅ Tarea completada', 'El maquinista Betsy Aldo completó la fase ''CORTE'' de la pieza ''Copa delantera derecha'' para la OT OT-2026-0004 (Cantidad: 99/100).', 'TAREA_COMPLETADA', NULL, 'ADMINISTRADOR,JEFE_PRODUCCION,SUPERVISOR', 1, '2026-05-25 01:53:34');
INSERT INTO `notificaciones` (`id_notificacion`, `titulo`, `mensaje`, `tipo`, `id_referencia`, `para_rol`, `leida`, `fecha_creacion`) VALUES (12, '✅ Tarea completada', 'El maquinista Betsy Aldo completó la fase ''CORTE'' de la pieza ''Cuerpo tubular'' para la OT OT-2026-0001 (Cantidad: 290/300).', 'TAREA_COMPLETADA', NULL, 'ADMINISTRADOR,JEFE_PRODUCCION,SUPERVISOR', 1, '2026-05-25 02:17:41');
INSERT INTO `notificaciones` (`id_notificacion`, `titulo`, `mensaje`, `tipo`, `id_referencia`, `para_rol`, `leida`, `fecha_creacion`) VALUES (13, '✅ Tarea completada', 'El maquinista Betsy Aldo completó la fase ''CORTE'' de la pieza ''Cuerpo tubular'' para la OT OT-2026-0002 (Cantidad: 100/150).', 'TAREA_COMPLETADA', NULL, 'ADMINISTRADOR,JEFE_PRODUCCION,SUPERVISOR', 1, '2026-05-25 02:34:32');
INSERT INTO `notificaciones` (`id_notificacion`, `titulo`, `mensaje`, `tipo`, `id_referencia`, `para_rol`, `leida`, `fecha_creacion`) VALUES (14, '✅ Tarea completada', 'El maquinista Betsy Aldo completó la fase ''CORTE'' de la pieza ''Copa delantera derecha'' para la OT OT-2026-0005 (Cantidad: 140/148).', 'TAREA_COMPLETADA', NULL, 'ADMINISTRADOR,JEFE_PRODUCCION,SUPERVISOR', 1, '2026-05-25 03:04:18');
INSERT INTO `notificaciones` (`id_notificacion`, `titulo`, `mensaje`, `tipo`, `id_referencia`, `para_rol`, `leida`, `fecha_creacion`) VALUES (30001, '✅ Tarea completada', 'El maquinista Betsy Aldo completó la fase ''CORTE'' de ''mangas'' para la OT OT-2026-0012 (40/40 prendas).', 'TAREA_COMPLETADA', 120007, 'ADMINISTRADOR,JEFE_PRODUCCION,SUPERVISOR', 1, '2026-05-25 12:57:36');
INSERT INTO `notificaciones` (`id_notificacion`, `titulo`, `mensaje`, `tipo`, `id_referencia`, `para_rol`, `leida`, `fecha_creacion`) VALUES (30002, '✅ Tarea completada', 'El maquinista Betsy Aldo completó la fase ''CORTE'' de ''cuerpo'' para la OT OT-2026-0012 (40/40 prendas).', 'TAREA_COMPLETADA', 120009, 'ADMINISTRADOR,JEFE_PRODUCCION,SUPERVISOR', 1, '2026-05-25 12:57:45');
INSERT INTO `notificaciones` (`id_notificacion`, `titulo`, `mensaje`, `tipo`, `id_referencia`, `para_rol`, `leida`, `fecha_creacion`) VALUES (30003, '✅ Tarea completada', 'El maquinista Betsy Aldo completó la fase ''CORTE'' de ''cuello'' para la OT OT-2026-0012 (20/20 prendas).', 'TAREA_COMPLETADA', 120011, 'ADMINISTRADOR,JEFE_PRODUCCION,SUPERVISOR', 1, '2026-05-25 12:57:54');
INSERT INTO `notificaciones` (`id_notificacion`, `titulo`, `mensaje`, `tipo`, `id_referencia`, `para_rol`, `leida`, `fecha_creacion`) VALUES (30004, '✅ Tarea completada', 'El maquinista Betsy Aldo completó la fase ''PLANCHADO'' de ''mangas'' para la OT OT-2026-0012 (40/40 prendas).', 'TAREA_COMPLETADA', 120008, 'ADMINISTRADOR,JEFE_PRODUCCION,SUPERVISOR', 1, '2026-05-25 12:58:50');
INSERT INTO `notificaciones` (`id_notificacion`, `titulo`, `mensaje`, `tipo`, `id_referencia`, `para_rol`, `leida`, `fecha_creacion`) VALUES (30005, '✅ Tarea completada', 'El maquinista Betsy Aldo completó la fase ''PLANCHADO'' de ''cuerpo'' para la OT OT-2026-0012 (40/40 prendas).', 'TAREA_COMPLETADA', 120010, 'ADMINISTRADOR,JEFE_PRODUCCION,SUPERVISOR', 1, '2026-05-25 12:58:58');
INSERT INTO `notificaciones` (`id_notificacion`, `titulo`, `mensaje`, `tipo`, `id_referencia`, `para_rol`, `leida`, `fecha_creacion`) VALUES (30006, '✅ Tarea completada', 'El maquinista Betsy Aldo completó la fase ''PLANCHADO'' de ''cuello'' para la OT OT-2026-0012 (20/20 prendas).', 'TAREA_COMPLETADA', 120012, 'ADMINISTRADOR,JEFE_PRODUCCION,SUPERVISOR', 1, '2026-05-25 12:59:09');
INSERT INTO `notificaciones` (`id_notificacion`, `titulo`, `mensaje`, `tipo`, `id_referencia`, `para_rol`, `leida`, `fecha_creacion`) VALUES (60001, '✅ Tarea completada', 'El maquinista Betsy Aldo completó la fase ''ENSAMBLAJE'' de ''null'' para la OT OT-2026-0012 (20/20 prendas).', 'TAREA_COMPLETADA', 120013, 'ADMINISTRADOR,JEFE_PRODUCCION,SUPERVISOR', 1, '2026-05-25 13:12:36');
INSERT INTO `notificaciones` (`id_notificacion`, `titulo`, `mensaje`, `tipo`, `id_referencia`, `para_rol`, `leida`, `fecha_creacion`) VALUES (60002, '✅ Tarea completada', 'El maquinista Betsy Aldo completó la fase ''CORTE'' de ''Cuerpo'' para la OT OT-2026-0001 (300/300 prendas).', 'TAREA_COMPLETADA', 150001, 'ADMINISTRADOR,JEFE_PRODUCCION,SUPERVISOR', 1, '2026-05-25 13:31:47');
INSERT INTO `notificaciones` (`id_notificacion`, `titulo`, `mensaje`, `tipo`, `id_referencia`, `para_rol`, `leida`, `fecha_creacion`) VALUES (60003, '✅ Tarea completada', 'El maquinista Betsy Aldo completó la fase ''ORILLADO'' de ''Cuerpo'' para la OT OT-2026-0001 (298/300 prendas).', 'TAREA_COMPLETADA', 150002, 'ADMINISTRADOR,JEFE_PRODUCCION,SUPERVISOR', 1, '2026-05-25 13:36:21');
INSERT INTO `notificaciones` (`id_notificacion`, `titulo`, `mensaje`, `tipo`, `id_referencia`, `para_rol`, `leida`, `fecha_creacion`) VALUES (60004, '✅ Tarea completada', 'El maquinista Betsy Aldo completó la fase ''CORTE'' de ''Cuerpo'' para la OT OT-2026-0002 (2/2 unidades).', 'TAREA_COMPLETADA', 150006, 'ADMINISTRADOR,JEFE_PRODUCCION,SUPERVISOR', 1, '2026-05-25 14:16:58');
INSERT INTO `notificaciones` (`id_notificacion`, `titulo`, `mensaje`, `tipo`, `id_referencia`, `para_rol`, `leida`, `fecha_creacion`) VALUES (60005, '✅ Tarea completada', 'El maquinista Betsy Aldo completó la fase ''ORILLADO'' de ''Cuerpo'' para la OT OT-2026-0002 (10/10 unidades).', 'TAREA_COMPLETADA', 150007, 'ADMINISTRADOR,JEFE_PRODUCCION,SUPERVISOR', 1, '2026-05-25 14:20:32');
INSERT INTO `notificaciones` (`id_notificacion`, `titulo`, `mensaje`, `tipo`, `id_referencia`, `para_rol`, `leida`, `fecha_creacion`) VALUES (60006, '✅ Tarea completada', 'El maquinista Betsy Aldo completó la fase ''ENSAMBLAJE'' de ''Ensamblaje'' para la OT OT-2026-0002 (150/150 unidades).', 'TAREA_COMPLETADA', 150008, 'ADMINISTRADOR,JEFE_PRODUCCION,SUPERVISOR', 1, '2026-05-25 14:21:37');
INSERT INTO `notificaciones` (`id_notificacion`, `titulo`, `mensaje`, `tipo`, `id_referencia`, `para_rol`, `leida`, `fecha_creacion`) VALUES (60007, '✅ Tarea completada', 'El maquinista Betsy Aldo completó la fase ''CORTE'' de ''Cuerpo'' para la OT OT-2026-0003 (80/80 unidades).', 'TAREA_COMPLETADA', 150009, 'ADMINISTRADOR,JEFE_PRODUCCION,SUPERVISOR', 1, '2026-05-25 14:47:06');
INSERT INTO `notificaciones` (`id_notificacion`, `titulo`, `mensaje`, `tipo`, `id_referencia`, `para_rol`, `leida`, `fecha_creacion`) VALUES (60008, '✅ Tarea completada', 'El maquinista Betsy Aldo completó la fase ''CORTE'' de ''Copa delantera derecha'' para la OT OT-2026-0004 (2/2 unidades).', 'TAREA_COMPLETADA', 150024, 'ADMINISTRADOR,JEFE_PRODUCCION,SUPERVISOR', 1, '2026-05-25 15:35:30');
INSERT INTO `notificaciones` (`id_notificacion`, `titulo`, `mensaje`, `tipo`, `id_referencia`, `para_rol`, `leida`, `fecha_creacion`) VALUES (60009, '✅ Tarea completada', 'El maquinista Betsy Aldo completó la fase ''REMALLADO'' de ''Copa delantera derecha'' para la OT OT-2026-0004 (100/100 unidades).', 'TAREA_COMPLETADA', 150016, 'ADMINISTRADOR,JEFE_PRODUCCION,SUPERVISOR', 1, '2026-05-25 15:48:26');
INSERT INTO `notificaciones` (`id_notificacion`, `titulo`, `mensaje`, `tipo`, `id_referencia`, `para_rol`, `leida`, `fecha_creacion`) VALUES (60010, '✅ Tarea completada', 'El maquinista Betsy Aldo completó la fase ''PLANCHADO'' de ''Copa delantera derecha'' para la OT OT-2026-0004 (10/10 unidades).', 'TAREA_COMPLETADA', 150025, 'ADMINISTRADOR,JEFE_PRODUCCION,SUPERVISOR', 1, '2026-05-25 15:50:43');
INSERT INTO `notificaciones` (`id_notificacion`, `titulo`, `mensaje`, `tipo`, `id_referencia`, `para_rol`, `leida`, `fecha_creacion`) VALUES (90001, '✅ Tarea completada', 'El maquinista Betsy Aldo completó la fase ''ORILLADO'' de ''Cuerpo'' para la OT OT-2026-0003 (10/10 unidades).', 'TAREA_COMPLETADA', 150011, 'ADMINISTRADOR,JEFE_PRODUCCION,SUPERVISOR', 1, '2026-05-26 06:11:08');
INSERT INTO `notificaciones` (`id_notificacion`, `titulo`, `mensaje`, `tipo`, `id_referencia`, `para_rol`, `leida`, `fecha_creacion`) VALUES (120001, '✅ Tarea completada', 'El maquinista Betsy Aldo completó la fase ''ENSAMBLAJE'' de ''Ensamblaje'' para la OT OT-2026-0001 (300/300 unidades).', 'TAREA_COMPLETADA', 150003, 'ADMINISTRADOR,JEFE_PRODUCCION,SUPERVISOR', 1, '2026-05-26 16:27:31');

-- ---------------------------------------------------------
-- Estructura de tabla: orden_trabajo
-- ---------------------------------------------------------
DROP TABLE IF EXISTS `orden_trabajo`;
CREATE TABLE `orden_trabajo` (
  `id_ot` int NOT NULL AUTO_INCREMENT,
  `codigo_ot` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cliente` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cantidad_est` int NOT NULL,
  `estado` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `id_responsable` int NOT NULL,
  `fecha_crea` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `id_modelo` int DEFAULT NULL,
  PRIMARY KEY (`id_ot`) /*T![clustered_index] CLUSTERED */,
  UNIQUE KEY `codigo_ot` (`codigo_ot`),
  KEY `fk_ot_responsable` (`id_responsable`),
  KEY `fk_ot_modelo` (`id_modelo`),
  CONSTRAINT `fk_ot_responsable` FOREIGN KEY (`id_responsable`) REFERENCES `usuarios` (`id_usuario`),
  CONSTRAINT `fk_ot_modelo` FOREIGN KEY (`id_modelo`) REFERENCES `modelos_prenda` (`id_modelo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci AUTO_INCREMENT=390001;

-- Datos de tabla: orden_trabajo
INSERT INTO `orden_trabajo` (`id_ot`, `codigo_ot`, `cliente`, `cantidad_est`, `estado`, `id_responsable`, `fecha_crea`, `id_modelo`) VALUES (1, 'OT-2026-0001', 'Confecciones Andes S.A.C.', 300, 'FINALIZADA', 3, '2026-05-10 20:37:51', 150003);
INSERT INTO `orden_trabajo` (`id_ot`, `codigo_ot`, `cliente`, `cantidad_est`, `estado`, `id_responsable`, `fecha_crea`, `id_modelo`) VALUES (2, 'OT-2026-0002', 'Moda Lima Export', 150, 'FINALIZADA', 3, '2026-05-10 20:37:51', 150003);
INSERT INTO `orden_trabajo` (`id_ot`, `codigo_ot`, `cliente`, `cantidad_est`, `estado`, `id_responsable`, `fecha_crea`, `id_modelo`) VALUES (3, 'OT-2026-0003', 'Boutique Elegance', 80, 'EN_PROCESO', 1, '2026-05-10 20:37:52', 150003);
INSERT INTO `orden_trabajo` (`id_ot`, `codigo_ot`, `cliente`, `cantidad_est`, `estado`, `id_responsable`, `fecha_crea`, `id_modelo`) VALUES (30001, 'OT-2026-0004', 'Saga SAC', 100, 'EN_PROCESO', 1, '2026-05-11 16:06:44', 1);
INSERT INTO `orden_trabajo` (`id_ot`, `codigo_ot`, `cliente`, `cantidad_est`, `estado`, `id_responsable`, `fecha_crea`, `id_modelo`) VALUES (60001, 'OT-2026-0005', 'Ripley', 148, 'EN_PROCESO', 1, '2026-05-12 14:01:14', 150003);
INSERT INTO `orden_trabajo` (`id_ot`, `codigo_ot`, `cliente`, `cantidad_est`, `estado`, `id_responsable`, `fecha_crea`, `id_modelo`) VALUES (90001, 'OT-2026-0006', 'Saga SAC', 150, 'ANULADA', 1, '2026-05-12 18:03:39', 2);
INSERT INTO `orden_trabajo` (`id_ot`, `codigo_ot`, `cliente`, `cantidad_est`, `estado`, `id_responsable`, `fecha_crea`, `id_modelo`) VALUES (120001, 'OT-2026-0007', 'William', 342, 'EN_PROCESO', 3, '2026-05-20 02:07:07', 1);
INSERT INTO `orden_trabajo` (`id_ot`, `codigo_ot`, `cliente`, `cantidad_est`, `estado`, `id_responsable`, `fecha_crea`, `id_modelo`) VALUES (150001, 'OT-2026-0008', 'Saga SAC', 150, 'CREADA', 1, '2026-05-24 16:28:55', 1);
INSERT INTO `orden_trabajo` (`id_ot`, `codigo_ot`, `cliente`, `cantidad_est`, `estado`, `id_responsable`, `fecha_crea`, `id_modelo`) VALUES (150002, 'OT-2026-0009', 'Saga SAC', 5, 'FINALIZADA', 1, '2026-05-24 16:39:51', 2);
INSERT INTO `orden_trabajo` (`id_ot`, `codigo_ot`, `cliente`, `cantidad_est`, `estado`, `id_responsable`, `fecha_crea`, `id_modelo`) VALUES (150003, 'OT-2026-0010', 'Misoa', 20, 'CREADA', 1, '2026-05-24 16:47:05', 2);
INSERT INTO `orden_trabajo` (`id_ot`, `codigo_ot`, `cliente`, `cantidad_est`, `estado`, `id_responsable`, `fecha_crea`, `id_modelo`) VALUES (150004, 'OT-2026-0011', 'Sandro', 20, 'CREADA', 1, '2026-05-24 16:48:11', 1);
INSERT INTO `orden_trabajo` (`id_ot`, `codigo_ot`, `cliente`, `cantidad_est`, `estado`, `id_responsable`, `fecha_crea`, `id_modelo`) VALUES (210001, 'OT-2026-0012', 'Gabriel', 20, 'FINALIZADA', 1, '2026-05-25 12:56:26', 120002);
INSERT INTO `orden_trabajo` (`id_ot`, `codigo_ot`, `cliente`, `cantidad_est`, `estado`, `id_responsable`, `fecha_crea`, `id_modelo`) VALUES (240001, 'OT-2026-0013', 'Ripley', 100, 'EN_PROCESO', 1, '2026-05-25 16:56:29', 180002);
INSERT INTO `orden_trabajo` (`id_ot`, `codigo_ot`, `cliente`, `cantidad_est`, `estado`, `id_responsable`, `fecha_crea`, `id_modelo`) VALUES (270001, 'OT-2026-0014', 'ALFONSO GABRIEL VILCAHUAMAN LOZANO', 120, 'EN_PROCESO', 1, '2026-05-26 23:48:04', 210002);

-- ---------------------------------------------------------
-- Estructura de tabla: permisos
-- ---------------------------------------------------------
DROP TABLE IF EXISTS `permisos`;
CREATE TABLE `permisos` (
  `id_permiso` int NOT NULL AUTO_INCREMENT,
  `codigo` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nombre` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `modulo` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `descripcion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id_permiso`) /*T![clustered_index] CLUSTERED */,
  UNIQUE KEY `codigo` (`codigo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci AUTO_INCREMENT=300001;

-- Datos de tabla: permisos
INSERT INTO `permisos` (`id_permiso`, `codigo`, `nombre`, `modulo`, `descripcion`) VALUES (1, 'SEG_USUARIOS_VER', 'Ver usuarios', 'Seguridad', 'Listar y consultar cuentas de usuario');
INSERT INTO `permisos` (`id_permiso`, `codigo`, `nombre`, `modulo`, `descripcion`) VALUES (2, 'SEG_USUARIOS_CREAR', 'Crear usuarios', 'Seguridad', 'Registrar nuevas cuentas');
INSERT INTO `permisos` (`id_permiso`, `codigo`, `nombre`, `modulo`, `descripcion`) VALUES (3, 'SEG_USUARIOS_EDITAR', 'Editar usuarios', 'Seguridad', 'Modificar datos y rol de un usuario');
INSERT INTO `permisos` (`id_permiso`, `codigo`, `nombre`, `modulo`, `descripcion`) VALUES (4, 'SEG_USUARIOS_DEACT', 'Desactivar usuarios', 'Seguridad', 'Desactivar cuentas sin borrarlas');
INSERT INTO `permisos` (`id_permiso`, `codigo`, `nombre`, `modulo`, `descripcion`) VALUES (5, 'SEG_ROLES_VER', 'Ver roles y permisos', 'Seguridad', 'Consultar la matriz de permisos');
INSERT INTO `permisos` (`id_permiso`, `codigo`, `nombre`, `modulo`, `descripcion`) VALUES (6, 'CAT_TELAS_VER', 'Ver catálogo de telas', 'Catálogos', 'Consultar telas y materiales');
INSERT INTO `permisos` (`id_permiso`, `codigo`, `nombre`, `modulo`, `descripcion`) VALUES (7, 'CAT_TELAS_EDIT', 'Gestionar telas', 'Catálogos', 'Crear y editar telas/materiales');
INSERT INTO `permisos` (`id_permiso`, `codigo`, `nombre`, `modulo`, `descripcion`) VALUES (8, 'CAT_MODELOS_VER', 'Ver catálogo de modelos', 'Catálogos', 'Consultar fichas técnicas');
INSERT INTO `permisos` (`id_permiso`, `codigo`, `nombre`, `modulo`, `descripcion`) VALUES (9, 'CAT_MODELOS_EDIT', 'Gestionar modelos', 'Catálogos', 'Crear y editar modelos de corset');
INSERT INTO `permisos` (`id_permiso`, `codigo`, `nombre`, `modulo`, `descripcion`) VALUES (10, 'ALM_TELA_VER', 'Ver recepciones', 'Almacén', 'Ver registros de tela recibida');
INSERT INTO `permisos` (`id_permiso`, `codigo`, `nombre`, `modulo`, `descripcion`) VALUES (11, 'ALM_TELA_REGISTRAR', 'Registrar tela', 'Almacén', 'HU01: Ingresar recepción de tela');
INSERT INTO `permisos` (`id_permiso`, `codigo`, `nombre`, `modulo`, `descripcion`) VALUES (12, 'PROD_OT_VER', 'Ver órdenes de trabajo', 'Producción', 'Consultar OTs');
INSERT INTO `permisos` (`id_permiso`, `codigo`, `nombre`, `modulo`, `descripcion`) VALUES (13, 'PROD_OT_CREAR', 'Crear orden de trabajo', 'Producción', 'HU13: Generar nueva OT');
INSERT INTO `permisos` (`id_permiso`, `codigo`, `nombre`, `modulo`, `descripcion`) VALUES (16, 'PROD_MERMA_VER', 'Ver mermas', 'Producción', 'HU04: Consultar porcentajes');
INSERT INTO `permisos` (`id_permiso`, `codigo`, `nombre`, `modulo`, `descripcion`) VALUES (17, 'PROD_MERMA_REG', 'Registrar merma', 'Producción', 'HU04: Ingresar merma por tejido');
INSERT INTO `permisos` (`id_permiso`, `codigo`, `nombre`, `modulo`, `descripcion`) VALUES (18, 'PROD_CARGAS_VER', 'Ver cargas de trabajo', 'Producción', 'HU05: Consultar asignaciones');
INSERT INTO `permisos` (`id_permiso`, `codigo`, `nombre`, `modulo`, `descripcion`) VALUES (19, 'PROD_CARGAS_ASIG', 'Asignar cargas', 'Producción', 'HU05: Distribuir piezas a maquinistas');
INSERT INTO `permisos` (`id_permiso`, `codigo`, `nombre`, `modulo`, `descripcion`) VALUES (20, 'PROD_FALLAS_VER', 'Ver mapa de fallas', 'Producción', 'HU02: Consultar imperfecciones');
INSERT INTO `permisos` (`id_permiso`, `codigo`, `nombre`, `modulo`, `descripcion`) VALUES (21, 'PROD_FALLAS_REG', 'Registrar fallas', 'Producción', 'HU02: Mapear imperfecciones en tela');
INSERT INTO `permisos` (`id_permiso`, `codigo`, `nombre`, `modulo`, `descripcion`) VALUES (22, 'CAL_DEFECTOS_VER', 'Ver defectos', 'Calidad', 'HU06: Consultar registro de defectos');
INSERT INTO `permisos` (`id_permiso`, `codigo`, `nombre`, `modulo`, `descripcion`) VALUES (23, 'CAL_DEFECTOS_REG', 'Registrar defectos', 'Calidad', 'HU06: Ingresar defectos y reprocesos');
INSERT INTO `permisos` (`id_permiso`, `codigo`, `nombre`, `modulo`, `descripcion`) VALUES (24, 'DES_CONCIL_VER', 'Ver conciliaciones', 'Despacho', 'HU07: Consultar despachos');
INSERT INTO `permisos` (`id_permiso`, `codigo`, `nombre`, `modulo`, `descripcion`) VALUES (25, 'DES_CONCIL_REG', 'Registrar despacho', 'Despacho', 'HU07: Conciliar inventario y generar nota');
INSERT INTO `permisos` (`id_permiso`, `codigo`, `nombre`, `modulo`, `descripcion`) VALUES (26, 'RPT_DASHBOARD', 'Ver dashboard', 'Dashboard', 'HU14: Visualizar eficiencia de taller');
INSERT INTO `permisos` (`id_permiso`, `codigo`, `nombre`, `modulo`, `descripcion`) VALUES (27, 'RPT_MERMAS_CALIDAD', 'Reporte mermas/calidad', 'Reportes', 'HU15: Exportar reportes históricos');
INSERT INTO `permisos` (`id_permiso`, `codigo`, `nombre`, `modulo`, `descripcion`) VALUES (30001, 'PROD_MAQUINISTAS_VER', 'Ver maquinistas', 'Producción', 'Listar personal de maquila y sus especialidades');
INSERT INTO `permisos` (`id_permiso`, `codigo`, `nombre`, `modulo`, `descripcion`) VALUES (30002, 'PROD_MAQUINISTAS_GESTION', 'Gestionar maquinistas', 'Producción', 'Crear, editar y desactivar maquinistas');
INSERT INTO `permisos` (`id_permiso`, `codigo`, `nombre`, `modulo`, `descripcion`) VALUES (90001, 'ESPECIALIDADES_VER', 'Ver especialidades', 'Especialidades', 'Listar especialidades técnicas');
INSERT INTO `permisos` (`id_permiso`, `codigo`, `nombre`, `modulo`, `descripcion`) VALUES (90002, 'ESPECIALIDADES_GESTION', 'Gestionar especialidades', 'Especialidades', 'Crear, editar y eliminar especialidades');
INSERT INTO `permisos` (`id_permiso`, `codigo`, `nombre`, `modulo`, `descripcion`) VALUES (150001, 'PROD_REPOSO_VER', 'Ver tiempos de reposo', 'Producción', 'HU03: Consultar cronómetros de reposo');
INSERT INTO `permisos` (`id_permiso`, `codigo`, `nombre`, `modulo`, `descripcion`) VALUES (150002, 'PROD_REPOSO_GESTION', 'Gestionar tiempos', 'Producción', 'HU03: Iniciar/monitorear/finalizar reposo');

-- ---------------------------------------------------------
-- Estructura de tabla: pieza_ruta_fase
-- ---------------------------------------------------------
DROP TABLE IF EXISTS `pieza_ruta_fase`;
CREATE TABLE `pieza_ruta_fase` (
  `id_pieza_ruta` int NOT NULL AUTO_INCREMENT,
  `id_pieza` int DEFAULT NULL,
  `id_fase` int NOT NULL,
  `id_modelo` int DEFAULT NULL COMMENT 'FK a modelos_prenda',
  PRIMARY KEY (`id_pieza_ruta`) /*T![clustered_index] CLUSTERED */,
  KEY `fk_ruta_pieza` (`id_pieza`),
  KEY `fk_ruta_fase` (`id_fase`),
  KEY `fk_prf_modelo` (`id_modelo`),
  CONSTRAINT `fk_ruta_pieza` FOREIGN KEY (`id_pieza`) REFERENCES `piezas_modelo` (`id_pieza`) ON DELETE CASCADE,
  CONSTRAINT `fk_ruta_fase` FOREIGN KEY (`id_fase`) REFERENCES `fases_produccion` (`id_fase`) ON DELETE CASCADE,
  CONSTRAINT `fk_prf_modelo` FOREIGN KEY (`id_modelo`) REFERENCES `modelos_prenda` (`id_modelo`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci AUTO_INCREMENT=300001;

-- Datos de tabla: pieza_ruta_fase
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (30001, 240001, 1, NULL);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (30002, 240001, 3, NULL);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (30003, 240001, 5, NULL);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (30004, 240002, 1, NULL);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (30005, 240002, 3, NULL);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (30006, 240002, 5, NULL);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (30007, 240003, 1, NULL);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (30008, 240003, 2, NULL);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (30009, 240004, 1, NULL);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (30010, 240004, 2, NULL);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (30011, 240005, 1, NULL);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (30012, 240005, 2, NULL);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (30013, 240006, 1, NULL);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (30014, 240006, 2, NULL);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (90001, 300001, 1, NULL);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (90002, 300001, 5, NULL);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (90003, 300002, 1, NULL);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (90004, 300002, 5, NULL);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (90005, 300003, 1, NULL);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (90006, 300003, 5, NULL);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (90010, NULL, 6, 150003);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (90013, NULL, 6, 150003);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (90017, NULL, 6, 150003);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (120001, 330001, 1, NULL);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (120002, 330001, 2, NULL);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (120003, NULL, 6, 150003);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (120004, 330002, 1, NULL);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (120005, 330002, 3, NULL);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (120006, 330003, 3, NULL);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (120007, NULL, 6, 180002);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (150001, 360001, 1, NULL);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (150002, 360001, 3, NULL);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (150003, 360001, 5, NULL);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (150004, 360002, 1, NULL);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (150005, 360002, 3, NULL);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (150006, 360002, 5, NULL);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (150007, 360003, 1, NULL);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (150008, 360003, 3, NULL);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (150009, 360003, 5, NULL);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (150010, 360004, 1, NULL);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (150011, 360004, 2, NULL);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (150012, 360004, 5, NULL);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (150013, NULL, 6, 210002);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (180004, NULL, 6, 240002);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (180010, NULL, 6, 240002);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (180018, NULL, 6, 240002);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (210001, 420001, 1, NULL);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (210002, 420001, 2, NULL);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (210003, 420002, 1, NULL);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (210004, 420002, 2, NULL);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (210005, 420003, 3, NULL);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (210006, 420003, 4, NULL);
INSERT INTO `pieza_ruta_fase` (`id_pieza_ruta`, `id_pieza`, `id_fase`, `id_modelo`) VALUES (210007, NULL, 6, 240002);

-- ---------------------------------------------------------
-- Estructura de tabla: piezas_modelo
-- ---------------------------------------------------------
DROP TABLE IF EXISTS `piezas_modelo`;
CREATE TABLE `piezas_modelo` (
  `id_pieza` int NOT NULL AUTO_INCREMENT,
  `id_modelo` int NOT NULL,
  `nombre_pieza` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cantidad` int NOT NULL DEFAULT '1',
  PRIMARY KEY (`id_pieza`) /*T![clustered_index] CLUSTERED */,
  KEY `fk_pieza_modelo` (`id_modelo`),
  CONSTRAINT `fk_pieza_modelo` FOREIGN KEY (`id_modelo`) REFERENCES `modelos_prenda` (`id_modelo`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci AUTO_INCREMENT=510001;

-- Datos de tabla: piezas_modelo
INSERT INTO `piezas_modelo` (`id_pieza`, `id_modelo`, `nombre_pieza`, `cantidad`) VALUES (240001, 1, 'Copa delantera izquierda', 6);
INSERT INTO `piezas_modelo` (`id_pieza`, `id_modelo`, `nombre_pieza`, `cantidad`) VALUES (240002, 1, 'Copa delantera derecha', 1);
INSERT INTO `piezas_modelo` (`id_pieza`, `id_modelo`, `nombre_pieza`, `cantidad`) VALUES (240003, 1, 'Panel central', 1);
INSERT INTO `piezas_modelo` (`id_pieza`, `id_modelo`, `nombre_pieza`, `cantidad`) VALUES (240004, 1, 'Espalda ajustable', 2);
INSERT INTO `piezas_modelo` (`id_pieza`, `id_modelo`, `nombre_pieza`, `cantidad`) VALUES (240005, 2, 'Tirantes elásticos', 2);
INSERT INTO `piezas_modelo` (`id_pieza`, `id_modelo`, `nombre_pieza`, `cantidad`) VALUES (240006, 2, 'Cuerpo tubular', 1);
INSERT INTO `piezas_modelo` (`id_pieza`, `id_modelo`, `nombre_pieza`, `cantidad`) VALUES (300001, 120002, 'mangas', 2);
INSERT INTO `piezas_modelo` (`id_pieza`, `id_modelo`, `nombre_pieza`, `cantidad`) VALUES (300002, 120002, 'cuerpo', 2);
INSERT INTO `piezas_modelo` (`id_pieza`, `id_modelo`, `nombre_pieza`, `cantidad`) VALUES (300003, 120002, 'cuello', 1);
INSERT INTO `piezas_modelo` (`id_pieza`, `id_modelo`, `nombre_pieza`, `cantidad`) VALUES (330001, 150003, 'Cuerpo', 1);
INSERT INTO `piezas_modelo` (`id_pieza`, `id_modelo`, `nombre_pieza`, `cantidad`) VALUES (330002, 180002, 'prueba1', 1);
INSERT INTO `piezas_modelo` (`id_pieza`, `id_modelo`, `nombre_pieza`, `cantidad`) VALUES (330003, 180002, 'prueba2', 1);
INSERT INTO `piezas_modelo` (`id_pieza`, `id_modelo`, `nombre_pieza`, `cantidad`) VALUES (360001, 210002, 'Manga derecha', 1);
INSERT INTO `piezas_modelo` (`id_pieza`, `id_modelo`, `nombre_pieza`, `cantidad`) VALUES (360002, 210002, 'Manga izquierda', 1);
INSERT INTO `piezas_modelo` (`id_pieza`, `id_modelo`, `nombre_pieza`, `cantidad`) VALUES (360003, 210002, 'Cuero', 2);
INSERT INTO `piezas_modelo` (`id_pieza`, `id_modelo`, `nombre_pieza`, `cantidad`) VALUES (360004, 210002, 'Capucha', 1);
INSERT INTO `piezas_modelo` (`id_pieza`, `id_modelo`, `nombre_pieza`, `cantidad`) VALUES (420001, 240002, 'a', 1);
INSERT INTO `piezas_modelo` (`id_pieza`, `id_modelo`, `nombre_pieza`, `cantidad`) VALUES (420002, 240002, 'b', 2);
INSERT INTO `piezas_modelo` (`id_pieza`, `id_modelo`, `nombre_pieza`, `cantidad`) VALUES (420003, 240002, 'c', 1);

-- ---------------------------------------------------------
-- Estructura de tabla: rol_permiso
-- ---------------------------------------------------------
DROP TABLE IF EXISTS `rol_permiso`;
CREATE TABLE `rol_permiso` (
  `id_rol_permiso` int NOT NULL AUTO_INCREMENT,
  `id_rol` int NOT NULL,
  `id_permiso` int NOT NULL,
  PRIMARY KEY (`id_rol_permiso`) /*T![clustered_index] CLUSTERED */,
  UNIQUE KEY `uq_rol_permiso` (`id_rol`,`id_permiso`),
  KEY `fk_rp_permiso` (`id_permiso`),
  CONSTRAINT `fk_rp_rol` FOREIGN KEY (`id_rol`) REFERENCES `roles` (`id_rol`),
  CONSTRAINT `fk_rp_permiso` FOREIGN KEY (`id_permiso`) REFERENCES `permisos` (`id_permiso`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci AUTO_INCREMENT=300001;

-- Datos de tabla: rol_permiso
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (27, 1, 1);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (24, 1, 2);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (26, 1, 3);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (25, 1, 4);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (23, 1, 5);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (8, 1, 6);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (7, 1, 7);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (6, 1, 8);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (5, 1, 9);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (2, 1, 10);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (1, 1, 11);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (18, 1, 12);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (17, 1, 13);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (16, 1, 16);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (15, 1, 17);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (12, 1, 18);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (11, 1, 19);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (14, 1, 20);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (13, 1, 21);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (4, 1, 22);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (3, 1, 23);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (10, 1, 24);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (9, 1, 25);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (21, 1, 26);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (22, 1, 27);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (30004, 1, 30001);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (30003, 1, 30002);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (90002, 1, 90001);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (90001, 1, 90002);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (150001, 1, 150001);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (150002, 1, 150002);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (30, 2, 6);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (29, 2, 10);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (28, 2, 11);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (31, 2, 12);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (32, 2, 26);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (34, 3, 6);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (33, 3, 8);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (60013, 3, 10);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (37, 3, 12);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (36, 3, 13);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (35, 3, 16);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (180003, 3, 20);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (40, 3, 26);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (41, 3, 27);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (150003, 3, 150001);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (150004, 3, 150002);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (42, 4, 6);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (47, 4, 12);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (46, 4, 16);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (45, 4, 17);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (44, 4, 20);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (43, 4, 21);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (50, 4, 26);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (60014, 5, 10);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (57, 5, 12);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (56, 5, 18);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (55, 5, 19);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (52, 5, 22);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (51, 5, 23);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (54, 5, 24);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (53, 5, 25);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (58, 5, 26);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (59, 5, 27);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (30002, 5, 30001);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (30001, 5, 30002);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (90004, 5, 90001);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (90003, 5, 90002);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (61, 6, 12);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (60, 6, 18);
INSERT INTO `rol_permiso` (`id_rol_permiso`, `id_rol`, `id_permiso`) VALUES (62, 6, 26);

-- ---------------------------------------------------------
-- Estructura de tabla: roles
-- ---------------------------------------------------------
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles` (
  `id_rol` int NOT NULL AUTO_INCREMENT,
  `nombre_rol` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `descripcion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id_rol`) /*T![clustered_index] CLUSTERED */,
  UNIQUE KEY `nombre_rol` (`nombre_rol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci AUTO_INCREMENT=150001;

-- Datos de tabla: roles
INSERT INTO `roles` (`id_rol`, `nombre_rol`, `descripcion`) VALUES (1, 'ADMINISTRADOR', 'Acceso total al sistema, gestión de usuarios y catálogos');
INSERT INTO `roles` (`id_rol`, `nombre_rol`, `descripcion`) VALUES (2, 'JEFE_ALMACEN', 'Registro y control de tela recibida');
INSERT INTO `roles` (`id_rol`, `nombre_rol`, `descripcion`) VALUES (3, 'JEFE_PRODUCCION', 'Creación y monitoreo de órdenes de trabajo');
INSERT INTO `roles` (`id_rol`, `nombre_rol`, `descripcion`) VALUES (4, 'TIZADOR', 'Mapeo de imperfecciones, tiempos de reposo y mermas');
INSERT INTO `roles` (`id_rol`, `nombre_rol`, `descripcion`) VALUES (5, 'SUPERVISOR', 'Distribución de cargas, control de defectos y despacho');
INSERT INTO `roles` (`id_rol`, `nombre_rol`, `descripcion`) VALUES (6, 'MAQUINISTA', 'Consulta de tareas asignadas y registro de avance');

-- ---------------------------------------------------------
-- Estructura de tabla: telas
-- ---------------------------------------------------------
DROP TABLE IF EXISTS `telas`;
CREATE TABLE `telas` (
  `id_tela` int NOT NULL AUTO_INCREMENT,
  `id_ot` int NOT NULL,
  `id_registrador` int NOT NULL COMMENT 'Jefe de almacén que registra',
  `codigo_tela` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `origen` enum('CLIENTE','TALLER') COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Quién provee la tela',
  `proveedor` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `peso_guia` decimal(10,3) NOT NULL COMMENT 'Peso en kg según guía de remisión',
  `peso_real` decimal(10,3) NOT NULL COMMENT 'Peso real medido al recibirlo',
  `diferencia_peso` decimal(10,3) GENERATED ALWAYS AS (`peso_real` - `peso_guia`) STORED COMMENT 'Calculado automáticamente',
  `tipo_tejido` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `color` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `num_rollos` int NOT NULL DEFAULT '1',
  `observaciones` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `estado_calidad` enum('ACEPTADO','OBSERVADO','RECHAZADO') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'OBSERVADO',
  `requiere_reposo` tinyint(1) NOT NULL DEFAULT '0',
  `fecha_ingreso` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `id_catalogo_tela` int DEFAULT NULL,
  PRIMARY KEY (`id_tela`) /*T![clustered_index] CLUSTERED */,
  UNIQUE KEY `codigo_tela` (`codigo_tela`),
  KEY `fk_tela_ot` (`id_ot`),
  KEY `fk_tela_registrador` (`id_registrador`),
  KEY `idx_tela_ot` (`id_ot`),
  KEY `fk_tela_catalogo` (`id_catalogo_tela`),
  CONSTRAINT `fk_tela_ot` FOREIGN KEY (`id_ot`) REFERENCES `orden_trabajo` (`id_ot`),
  CONSTRAINT `fk_tela_registrador` FOREIGN KEY (`id_registrador`) REFERENCES `usuarios` (`id_usuario`),
  CONSTRAINT `fk_tela_catalogo` FOREIGN KEY (`id_catalogo_tela`) REFERENCES `catalogo_telas` (`id_catalogo`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci AUTO_INCREMENT=450001;

-- Datos de tabla: telas
INSERT INTO `telas` (`id_tela`, `id_ot`, `id_registrador`, `codigo_tela`, `origen`, `proveedor`, `peso_guia`, `peso_real`, `tipo_tejido`, `color`, `num_rollos`, `observaciones`, `estado_calidad`, `requiere_reposo`, `fecha_ingreso`, `id_catalogo_tela`) VALUES (1, 1, 2, 'TELA-2026-0001', 'CLIENTE', 'Textiles Andes S.A.C.', 120.500, 119.800, 'Franela Reactiva 30/1', 'Negro', 3, 'Material en buen estado. Embalaje íntegro. Se detecta leve diferencia de peso (-0.700 kg), dentro del rango aceptable del 1%. Sin imperfecciones visibles en inspección inicial.', 'ACEPTADO', 0, '2026-05-10 20:38:23', 1);
INSERT INTO `telas` (`id_tela`, `id_ot`, `id_registrador`, `codigo_tela`, `origen`, `proveedor`, `peso_guia`, `peso_real`, `tipo_tejido`, `color`, `num_rollos`, `observaciones`, `estado_calidad`, `requiere_reposo`, `fecha_ingreso`, `id_catalogo_tela`) VALUES (2, 2, 2, 'TELA-2026-0002', 'TALLER', 'Importaciones Lima Textil E.I.R.L.', 85.000, 92.300, 'Franela Reactiva 30/1', 'Blanco hueso', 2, 'ALERTA: diferencia de peso +7.300 kg (8.6% sobre la guía). Se solicita verificación urgente con el proveedor. Material con alta elasticidad, requiere reposo antes del corte.', 'OBSERVADO', 0, '2026-05-10 20:38:23', 1);
INSERT INTO `telas` (`id_tela`, `id_ot`, `id_registrador`, `codigo_tela`, `origen`, `proveedor`, `peso_guia`, `peso_real`, `tipo_tejido`, `color`, `num_rollos`, `observaciones`, `estado_calidad`, `requiere_reposo`, `fecha_ingreso`, `id_catalogo_tela`) VALUES (30001, 2, 1, 'TELA-2026-0003', 'CLIENTE', 'SAGA SAC', 150.000, 149.000, 'Elástico 4 vías premium', 'Rojo', 1, 'Ninguna', 'ACEPTADO', 1, '2026-05-11 15:48:29', 90001);
INSERT INTO `telas` (`id_tela`, `id_ot`, `id_registrador`, `codigo_tela`, `origen`, `proveedor`, `peso_guia`, `peso_real`, `tipo_tejido`, `color`, `num_rollos`, `observaciones`, `estado_calidad`, `requiere_reposo`, `fecha_ingreso`, `id_catalogo_tela`) VALUES (60001, 2, 1, 'TELA-2026-0004', 'CLIENTE', 'Importaciones ABC', 150.000, 150.000, 'Elástico 4 vías premium', 'Azul', 2, 'ninguna', 'ACEPTADO', 1, '2026-05-12 04:47:15', 90001);
INSERT INTO `telas` (`id_tela`, `id_ot`, `id_registrador`, `codigo_tela`, `origen`, `proveedor`, `peso_guia`, `peso_real`, `tipo_tejido`, `color`, `num_rollos`, `observaciones`, `estado_calidad`, `requiere_reposo`, `fecha_ingreso`, `id_catalogo_tela`) VALUES (60002, 1, 1, 'TELA-2026-0005', 'TALLER', 'Importaciones ABC', 200.000, 198.999, 'Elástico 4 vías premium', 'Verde', 10, 'Ninguna', 'ACEPTADO', 1, '2026-05-12 04:57:30', 90001);
INSERT INTO `telas` (`id_tela`, `id_ot`, `id_registrador`, `codigo_tela`, `origen`, `proveedor`, `peso_guia`, `peso_real`, `tipo_tejido`, `color`, `num_rollos`, `observaciones`, `estado_calidad`, `requiere_reposo`, `fecha_ingreso`, `id_catalogo_tela`) VALUES (90001, 60001, 1, 'TELA-2026-0006', 'CLIENTE', 'Importaciones ABC', 200.000, 198.999, 'Elástico 4 vías premium', 'Verde', 1, 'Ninguna', 'ACEPTADO', 1, '2026-05-12 17:01:09', 90001);
INSERT INTO `telas` (`id_tela`, `id_ot`, `id_registrador`, `codigo_tela`, `origen`, `proveedor`, `peso_guia`, `peso_real`, `tipo_tejido`, `color`, `num_rollos`, `observaciones`, `estado_calidad`, `requiere_reposo`, `fecha_ingreso`, `id_catalogo_tela`) VALUES (120001, 1, 1, 'TELA-2026-0007', 'CLIENTE', 'Importaciones ABC', 200.000, 198.999, 'Elástico 4 vías premium', 'Verde', 1, 'Ninguna', 'ACEPTADO', 1, '2026-05-12 20:48:53', 90001);
INSERT INTO `telas` (`id_tela`, `id_ot`, `id_registrador`, `codigo_tela`, `origen`, `proveedor`, `peso_guia`, `peso_real`, `tipo_tejido`, `color`, `num_rollos`, `observaciones`, `estado_calidad`, `requiere_reposo`, `fecha_ingreso`, `id_catalogo_tela`) VALUES (150001, 60001, 1, 'TELA-2026-0008', 'TALLER', 'Casana', 120.000, 121.000, 'Franela Reactiva 30/1', 'Rojo', 10, 'Ninguna', 'ACEPTADO', 0, '2026-05-23 21:46:44', 1);
INSERT INTO `telas` (`id_tela`, `id_ot`, `id_registrador`, `codigo_tela`, `origen`, `proveedor`, `peso_guia`, `peso_real`, `tipo_tejido`, `color`, `num_rollos`, `observaciones`, `estado_calidad`, `requiere_reposo`, `fecha_ingreso`, `id_catalogo_tela`) VALUES (180001, 1, 1, 'TELA-2026-0009', 'CLIENTE', 'Confecciones Andes S.A.C.', 100.000, 100.000, 'Elástico 4 vías premium', 'Azul', 1, 'Ninguna', 'ACEPTADO', 1, '2026-05-23 22:57:01', 90001);
INSERT INTO `telas` (`id_tela`, `id_ot`, `id_registrador`, `codigo_tela`, `origen`, `proveedor`, `peso_guia`, `peso_real`, `tipo_tejido`, `color`, `num_rollos`, `observaciones`, `estado_calidad`, `requiere_reposo`, `fecha_ingreso`, `id_catalogo_tela`) VALUES (210001, 90001, 1, 'TELA-2026-0010', 'CLIENTE', 'Saga SAC', 50.000, 50.000, 'elastico', 'Gris', 1, 'as', 'ACEPTADO', 0, '2026-05-23 23:23:01', 180001);
INSERT INTO `telas` (`id_tela`, `id_ot`, `id_registrador`, `codigo_tela`, `origen`, `proveedor`, `peso_guia`, `peso_real`, `tipo_tejido`, `color`, `num_rollos`, `observaciones`, `estado_calidad`, `requiere_reposo`, `fecha_ingreso`, `id_catalogo_tela`) VALUES (240001, 90001, 1, 'TELA-2026-0011', 'TALLER', 'Importaciones ABC', 150.000, 150.000, 'elastico', 'Azul', 12, '1425', 'OBSERVADO', 0, '2026-05-24 00:03:41', 180001);
INSERT INTO `telas` (`id_tela`, `id_ot`, `id_registrador`, `codigo_tela`, `origen`, `proveedor`, `peso_guia`, `peso_real`, `tipo_tejido`, `color`, `num_rollos`, `observaciones`, `estado_calidad`, `requiere_reposo`, `fecha_ingreso`, `id_catalogo_tela`) VALUES (300001, 240001, 1, 'TELA-2026-0012', 'CLIENTE', 'Ripley', 150.000, 150.000, 'prueba', 'Rojo', 2, 'ninguna', 'ACEPTADO', 1, '2026-05-25 16:57:33', 210001);
INSERT INTO `telas` (`id_tela`, `id_ot`, `id_registrador`, `codigo_tela`, `origen`, `proveedor`, `peso_guia`, `peso_real`, `tipo_tejido`, `color`, `num_rollos`, `observaciones`, `estado_calidad`, `requiere_reposo`, `fecha_ingreso`, `id_catalogo_tela`) VALUES (330001, 120001, 1, 'TELA-2026-0013', 'CLIENTE', 'William', 50.000, 50.000, 'Elástico 4 vías premium', 'Rojo', 1, 'Ninguna', 'ACEPTADO', 1, '2026-05-26 05:33:06', 90001);
INSERT INTO `telas` (`id_tela`, `id_ot`, `id_registrador`, `codigo_tela`, `origen`, `proveedor`, `peso_guia`, `peso_real`, `tipo_tejido`, `color`, `num_rollos`, `observaciones`, `estado_calidad`, `requiere_reposo`, `fecha_ingreso`, `id_catalogo_tela`) VALUES (360001, 270001, 1, 'TELA-2026-0014', 'TALLER', 'ALFONSO GABRIEL VILCAHUAMAN LOZANO', 50.000, 49.000, 'Elástico 4 vías premium', 'negro', 2, 'Ninguna', 'ACEPTADO', 1, '2026-06-11 17:19:13', 90001);

-- ---------------------------------------------------------
-- Estructura de tabla: tiempos_reposo
-- ---------------------------------------------------------
DROP TABLE IF EXISTS `tiempos_reposo`;
CREATE TABLE `tiempos_reposo` (
  `id_reposo` int NOT NULL AUTO_INCREMENT,
  `id_tela` int NOT NULL COMMENT 'Tela que entra en reposo',
  `id_usuario_inicio` int NOT NULL COMMENT 'Jefe de producción que registra el inicio',
  `fecha_inicio` datetime NOT NULL COMMENT 'Marca temporal del inicio de reposo (CA1 HU03)',
  `duracion_minutos` int NOT NULL DEFAULT '60' COMMENT 'Tiempo estimado de reposo en minutos',
  `fecha_fin_estimada` datetime GENERATED ALWAYS AS (date_add(`fecha_inicio`, interval `duracion_minutos` minute)) STORED COMMENT 'Calculado: fecha_inicio + duracion_minutos',
  `fecha_fin_real` datetime DEFAULT NULL COMMENT 'Cuándo se marcó como apto para corte',
  `estado` enum('EN_REPOSO','APTO_CORTE','CANCELADO') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'EN_REPOSO',
  `notificacion_enviada` tinyint(1) NOT NULL DEFAULT '0' COMMENT '1=notificación emitida (CA2 HU03)',
  `observaciones` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_crea` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_reposo`) /*T![clustered_index] CLUSTERED */,
  KEY `fk_reposo_tela` (`id_tela`),
  KEY `fk_reposo_usuario` (`id_usuario_inicio`),
  KEY `idx_reposo_tela` (`id_tela`),
  KEY `idx_reposo_estado` (`estado`),
  CONSTRAINT `fk_reposo_tela` FOREIGN KEY (`id_tela`) REFERENCES `telas` (`id_tela`),
  CONSTRAINT `fk_reposo_usuario` FOREIGN KEY (`id_usuario_inicio`) REFERENCES `usuarios` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci AUTO_INCREMENT=270001 COMMENT='HU03: Tiempos de reposo para telas elásticas antes del corte';

-- Datos de tabla: tiempos_reposo
INSERT INTO `tiempos_reposo` (`id_reposo`, `id_tela`, `id_usuario_inicio`, `fecha_inicio`, `duracion_minutos`, `fecha_fin_real`, `estado`, `notificacion_enviada`, `observaciones`, `fecha_crea`) VALUES (1, 2, 1, '2026-05-17T15:07:08', 60, NULL, 'APTO_CORTE', 0, 'Dato de prueba HU03 - tela Jersey completó reposo.', '2026-05-17 16:37:08');
INSERT INTO `tiempos_reposo` (`id_reposo`, `id_tela`, `id_usuario_inicio`, `fecha_inicio`, `duracion_minutos`, `fecha_fin_real`, `estado`, `notificacion_enviada`, `observaciones`, `fecha_crea`) VALUES (2, 30001, 1, '2026-05-17T15:07:32', 60, NULL, 'APTO_CORTE', 0, 'Dato de prueba HU03 - tela Jersey completó reposo.', '2026-05-17 16:37:32');
INSERT INTO `tiempos_reposo` (`id_reposo`, `id_tela`, `id_usuario_inicio`, `fecha_inicio`, `duracion_minutos`, `fecha_fin_real`, `estado`, `notificacion_enviada`, `observaciones`, `fecha_crea`) VALUES (3, 2, 1, '2026-05-17T16:55:19', 30, '2026-05-17T22:13:17', 'APTO_CORTE', 1, '', '2026-05-17 16:55:19');
INSERT INTO `tiempos_reposo` (`id_reposo`, `id_tela`, `id_usuario_inicio`, `fecha_inicio`, `duracion_minutos`, `fecha_fin_real`, `estado`, `notificacion_enviada`, `observaciones`, `fecha_crea`) VALUES (90001, 300001, 1, '2026-05-26T21:01:13', 3, '2026-05-26T21:06:11', 'APTO_CORTE', 1, '', '2026-05-26 21:01:13');
INSERT INTO `tiempos_reposo` (`id_reposo`, `id_tela`, `id_usuario_inicio`, `fecha_inicio`, `duracion_minutos`, `fecha_fin_real`, `estado`, `notificacion_enviada`, `observaciones`, `fecha_crea`) VALUES (120001, 60001, 1, '2026-06-12T20:08:27', 63, '2026-06-13T03:57:11', 'APTO_CORTE', 1, '', '2026-06-12 20:08:27');
INSERT INTO `tiempos_reposo` (`id_reposo`, `id_tela`, `id_usuario_inicio`, `fecha_inicio`, `duracion_minutos`, `fecha_fin_real`, `estado`, `notificacion_enviada`, `observaciones`, `fecha_crea`) VALUES (150001, 330001, 1, '2026-06-12T20:54:20', 30, '2026-06-13T03:57:11', 'APTO_CORTE', 1, '', '2026-06-12 20:54:20');
INSERT INTO `tiempos_reposo` (`id_reposo`, `id_tela`, `id_usuario_inicio`, `fecha_inicio`, `duracion_minutos`, `fecha_fin_real`, `estado`, `notificacion_enviada`, `observaciones`, `fecha_crea`) VALUES (180001, 360001, 1, '2026-06-13T04:00:49', 70, '2026-06-13T23:59:49', 'APTO_CORTE', 1, '', '2026-06-13 04:00:49');

-- ---------------------------------------------------------
-- Estructura de tabla: usuario_especialidad
-- ---------------------------------------------------------
DROP TABLE IF EXISTS `usuario_especialidad`;
CREATE TABLE `usuario_especialidad` (
  `id_usuario` int NOT NULL,
  `id_especialidad` int NOT NULL,
  PRIMARY KEY (`id_usuario`,`id_especialidad`) /*T![clustered_index] CLUSTERED */,
  KEY `fk_ue_especialidad` (`id_especialidad`),
  CONSTRAINT `fk_ue_usuario` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`) ON DELETE CASCADE,
  CONSTRAINT `fk_ue_especialidad` FOREIGN KEY (`id_especialidad`) REFERENCES `especialidades` (`id_especialidad`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Datos de tabla: usuario_especialidad
INSERT INTO `usuario_especialidad` (`id_usuario`, `id_especialidad`) VALUES (6, 1);
INSERT INTO `usuario_especialidad` (`id_usuario`, `id_especialidad`) VALUES (300001, 1);
INSERT INTO `usuario_especialidad` (`id_usuario`, `id_especialidad`) VALUES (210001, 2);
INSERT INTO `usuario_especialidad` (`id_usuario`, `id_especialidad`) VALUES (300001, 2);
INSERT INTO `usuario_especialidad` (`id_usuario`, `id_especialidad`) VALUES (210001, 5);
INSERT INTO `usuario_especialidad` (`id_usuario`, `id_especialidad`) VALUES (6, 90001);
INSERT INTO `usuario_especialidad` (`id_usuario`, `id_especialidad`) VALUES (300001, 90001);
INSERT INTO `usuario_especialidad` (`id_usuario`, `id_especialidad`) VALUES (300001, 210002);

-- ---------------------------------------------------------
-- Estructura de tabla: usuarios
-- ---------------------------------------------------------
DROP TABLE IF EXISTS `usuarios`;
CREATE TABLE `usuarios` (
  `id_usuario` int NOT NULL AUTO_INCREMENT,
  `username` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'BCrypt hash',
  `nombre` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `apellido` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `id_rol` int NOT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1' COMMENT '1=activo, 0=desactivado',
  `fecha_crea` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_mod` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `horario_restringido` tinyint(1) NOT NULL DEFAULT '1' COMMENT '1=con horario, 0=sin restricción',
  `horario_dias` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `horario_inicio` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `horario_fin` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `reprocesos_acum` int NOT NULL DEFAULT '0' COMMENT 'HU06: Contador acumulado de reprocesos del maquinista',
  PRIMARY KEY (`id_usuario`) /*T![clustered_index] CLUSTERED */,
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  KEY `fk_usuario_rol` (`id_rol`),
  CONSTRAINT `fk_usuario_rol` FOREIGN KEY (`id_rol`) REFERENCES `roles` (`id_rol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci AUTO_INCREMENT=390001;

-- Datos de tabla: usuarios
INSERT INTO `usuarios` (`id_usuario`, `username`, `password`, `nombre`, `apellido`, `email`, `id_rol`, `activo`, `fecha_crea`, `horario_restringido`, `horario_dias`, `horario_inicio`, `horario_fin`, `reprocesos_acum`) VALUES (1, 'admin', '$2a$12$C2CzXmrBWdX04NwtFcbaiOMuYs6oOFQNPeRA/shU5oHinl8vfoOke', 'Administrador', 'Sistema', 'admin@textil.pe', 1, 1, '2026-05-04 02:36:45', 0, '', '07:00:00', '17:00:00', 0);
INSERT INTO `usuarios` (`id_usuario`, `username`, `password`, `nombre`, `apellido`, `email`, `id_rol`, `activo`, `fecha_crea`, `horario_restringido`, `horario_dias`, `horario_inicio`, `horario_fin`, `reprocesos_acum`) VALUES (2, 'almacen1', '$2a$12$UKixegggPlSlgRU/8RRFUOE5h83jZF.l/PYQrxBdYzrU7RWdujzcG', 'Carlos', 'Quispe', 'almacen@textil.pe', 2, 0, '2026-05-04 02:37:26', 1, 'LUN,MAR,MIE', '01:00:00', '12:00:00', 0);
INSERT INTO `usuarios` (`id_usuario`, `username`, `password`, `nombre`, `apellido`, `email`, `id_rol`, `activo`, `fecha_crea`, `horario_restringido`, `horario_dias`, `horario_inicio`, `horario_fin`, `reprocesos_acum`) VALUES (3, 'jefe_prod', '$2a$12$9nZNIunQbLzc.lpj.0noYusZti08.YDvLLwwwY5p8CVW3UDi3iU9a', 'Maria', 'Torres', 'produccion@textil.pe', 3, 0, '2026-05-04 02:37:26', 1, 'LUN,MAR,MIE,JUE,VIE,SAB,DOM', '01:00:00', '23:59:00', 0);
INSERT INTO `usuarios` (`id_usuario`, `username`, `password`, `nombre`, `apellido`, `email`, `id_rol`, `activo`, `fecha_crea`, `horario_restringido`, `horario_dias`, `horario_inicio`, `horario_fin`, `reprocesos_acum`) VALUES (4, 'tizador1', '$2a$12$uPzERF8MVCUlS0J9stTi7u6.GYWk/NIlB8u5ltNn6eb4n.icZO7z2', 'Juan', 'Mendoza', 'tizador@textil.pe', 4, 0, '2026-05-04 02:37:26', 1, 'LUN,MAR,MIE,JUE,VIE,SAB,DOM', '01:00:00', '17:00:00', 0);
INSERT INTO `usuarios` (`id_usuario`, `username`, `password`, `nombre`, `apellido`, `email`, `id_rol`, `activo`, `fecha_crea`, `horario_restringido`, `horario_dias`, `horario_inicio`, `horario_fin`, `reprocesos_acum`) VALUES (5, 'supervisor1', '$2a$12$K8owDNJg/5ccoVkHZYeoAOPiztbBQ0GLE3E6HUcgeFTNHnSggaxmu', 'Rosa', 'Huanca', 'supervisor@textil.pe', 5, 0, '2026-05-04 02:37:26', 1, 'LUN,MAR,MIE,JUE,VIE,SAB', '00:02:00', '23:59:00', 0);
INSERT INTO `usuarios` (`id_usuario`, `username`, `password`, `nombre`, `apellido`, `email`, `id_rol`, `activo`, `fecha_crea`, `horario_restringido`, `horario_dias`, `horario_inicio`, `horario_fin`, `reprocesos_acum`) VALUES (6, 'maquinista1', '$2a$12$9W2K/GaYIDL1SkdFjYQ2X.YdPAcET9aW9TMCwdU83/CxrhO9aHiFm', 'Pedro Joaquin', 'Sullca', 'maquinista@textil.pe', 6, 0, '2026-05-04 02:37:26', 1, 'LUN,MAR,MIE,JUE,VIE,SAB', '05:00:00', '17:00:00', 0);
INSERT INTO `usuarios` (`id_usuario`, `username`, `password`, `nombre`, `apellido`, `email`, `id_rol`, `activo`, `fecha_crea`, `horario_restringido`, `horario_dias`, `horario_inicio`, `horario_fin`, `reprocesos_acum`) VALUES (210001, 'maquinista2', '$2a$12$JRw/mA80LJcRxm.L8ICGEeqRIR8rT7jqMqt0BD0W4E0PZpkQ12rp2', 'Betsy', 'Aldo', 'betsy@textil.pe', 6, 0, '2026-05-16 21:03:41', 1, 'LUN,MAR,MIE,JUE,VIE,SAB,DOM', '05:00:00', '23:00:00', 1);
INSERT INTO `usuarios` (`id_usuario`, `username`, `password`, `nombre`, `apellido`, `email`, `id_rol`, `activo`, `fecha_crea`, `horario_restringido`, `horario_dias`, `horario_inicio`, `horario_fin`, `reprocesos_acum`) VALUES (300001, 'maquinista3', '$2a$12$uf/ugNK2YZiuElgNxMWHIenMY4aqvKR7ynDzLtzarfbRNZFmHQbvu', 'springq', 'boot', 'maquinista3@gmail.com', 6, 0, '2026-06-11 15:51:34', 1, 'LUN,MAR,MIE,JUE,VIE,SAB', '07:00:00', '17:00:00', 0);

-- ---------------------------------------------------------
-- Estructura de vista: v_usuario_permisos
-- ---------------------------------------------------------
DROP VIEW IF EXISTS `v_usuario_permisos`;
CREATE ALGORITHM=UNDEFINED DEFINER=`3UZw7TyCfNxAm3m.root`@`%` SQL SECURITY DEFINER VIEW `v_usuario_permisos` (`id_usuario`, `username`, `nombre`, `apellido`, `id_rol`, `nombre_rol`, `codigo_permiso`, `nombre_permiso`, `modulo`) AS SELECT `u`.`id_usuario` AS `id_usuario`,`u`.`username` AS `username`,`u`.`nombre` AS `nombre`,`u`.`apellido` AS `apellido`,`r`.`id_rol` AS `id_rol`,`r`.`nombre_rol` AS `nombre_rol`,`p`.`codigo` AS `codigo_permiso`,`p`.`nombre` AS `nombre_permiso`,`p`.`modulo` AS `modulo` FROM ((`textil_db`.`usuarios` AS `u` JOIN `textil_db`.`roles` AS `r` ON `u`.`id_rol`=`r`.`id_rol`) JOIN `textil_db`.`rol_permiso` AS `rp` ON `r`.`id_rol`=`rp`.`id_rol`) JOIN `textil_db`.`permisos` AS `p` ON `rp`.`id_permiso`=`p`.`id_permiso` WHERE `u`.`activo`=1;

SET FOREIGN_KEY_CHECKS=1;
