package com.ucaldas.electoral.web;

import com.ucaldas.electoral.domain.Voto;
import com.ucaldas.electoral.repo.ElectoralProcessRepository;
import com.ucaldas.electoral.repo.VotoRepository;
import com.ucaldas.electoral.service.PublicResultsService;
import com.ucaldas.electoral.web.dto.VoteDtos;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ReportController {

    private final PublicResultsService publicResultsService;
    private final VotoRepository votoRepository;
    private final ElectoralProcessRepository processRepository;

    public ReportController(
            PublicResultsService publicResultsService,
            VotoRepository votoRepository,
            ElectoralProcessRepository processRepository
    ) {
        this.publicResultsService = publicResultsService;
        this.votoRepository = votoRepository;
        this.processRepository = processRepository;
    }

    @GetMapping(value = "/public/processes/{id}/report.csv", produces = "text/csv")
    public ResponseEntity<byte[]> publicReport(@PathVariable Long id) {
        processRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Proceso no encontrado."));
        VoteDtos.PublicResultsResponse agg = publicResultsService.aggregateByProceso(id);
        StringBuilder sb = new StringBuilder();
        sb.append("plancha_id,nombre,votos\n");
        for (VoteDtos.PlanchaResult r : agg.porPlancha()) {
            sb.append(r.planchaId()).append(',').append(csvEscape(r.nombre())).append(',').append(r.votos()).append('\n');
        }
        sb.append(",TOTAL,").append(agg.votosTotales()).append('\n');
        return csvResponse("reporte-publico-proceso-" + id + ".csv", sb.toString());
    }

    @GetMapping(value = "/admin/processes/{id}/report-detalle.csv", produces = "text/csv")
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> adminDetailReport(@PathVariable Long id) {
        processRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Proceso no encontrado."));
        List<Voto> votos = votoRepository.findByProceso_Id(id);
        StringBuilder sb = new StringBuilder();
        sb.append("voto_id,usuario_id,email,nombre_completo,plancha_id,plancha_nombre,fecha_utc\n");
        DateTimeFormatter fmt = DateTimeFormatter.ISO_INSTANT;
        for (Voto v : votos) {
            sb.append(v.getId()).append(',')
                    .append(v.getUsuario().getId()).append(',')
                    .append(csvEscape(v.getUsuario().getEmail())).append(',')
                    .append(csvEscape(v.getUsuario().getNombreCompleto())).append(',')
                    .append(v.getPlancha().getId()).append(',')
                    .append(csvEscape(v.getPlancha().getNombre())).append(',')
                    .append(fmt.format(v.getCreatedAt().atOffset(ZoneOffset.UTC)))
                    .append('\n');
        }
        return csvResponse("reporte-detalle-proceso-" + id + ".csv", sb.toString());
    }

    private static String csvEscape(String s) {
        if (s == null) {
            return "";
        }
        String x = s.replace("\"", "\"\"");
        if (x.contains(",") || x.contains("\n") || x.contains("\"")) {
            return "\"" + x + "\"";
        }
        return x;
    }

    private static ResponseEntity<byte[]> csvResponse(String filename, String body) {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(bytes);
    }
}
