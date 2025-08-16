package br.com.judev.notificationapi.services;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TrackingService {
    // Mapa para armazenar contagem de acessos em memória
    // Chave: visitorId-IP | Valor: quantidade de acessos
    private final Map<String,Integer> accessCount = new HashMap<>();


    /**
     * Identifica o usuário através de cookie ou gera um novo ID
     * @param request Objeto HttpServletRequest para acessar cookies e IP
     * @param response Objeto HttpServletResponse para adicionar novos cookies
     * @return String no formato "visitorId-IP"
     */
    public String identifyUser(HttpServletRequest request, HttpServletRequest response) {
        // Verifica se existem cookies na requisição
        Cookie[] cookies = request.getCookies() != null ? request.getCookies() : new Cookie[0];

        // Verifica cookie existente
     String visitorId = Arrays.stream(request.getCookies())
             .filter(c -> c.getName().equals("visitorId"))
             .findFirst()
             .map(Cookie::getValue)
             .orElse(null);

        // Se não encontrou o cookie, cria um novo
        if (visitorId == null) {
            visitorId = UUID.randomUUID().toString();
            Cookie cookie = new Cookie("visitorId", visitorId);
            cookie.setMaxAge(30 * 24 * 60 * 60); // Expira em 30 dias
            cookie.setHttpOnly(true); // Protege contra acesso via JavaScript
            cookie.setPath("/"); // Disponível para todas as rotas

            // Adiciona o cookie na resposta
            response.addCookie(cookie); // Corrige o erro - estava com HttpServletRequest
        }

        String clientIp = request.getRemoteAddr();

        // Retorna o identificador composto
        return visitorId + "-" + clientIp;
    }

    /**
     * Incrementa a contagem de acessos para um visitante
     * @param visitorKey Chave no formato "visitorId-IP"
     * @return Nova contagem de acessos
     */
    public int registerAccess(String visitorKey) {
        // Incrementa ou inicializa a contagem (merge é thread-safe)
        return accessCount.merge(visitorKey, 1, Integer::sum);
    }

    /**
     * Salva os acessos em arquivo (persistência simples)
     * @throws IOException Se ocorrer erro na escrita do arquivo
     */
    public void saveToFile() throws IOException {
        // Converte o mapa para linhas de texto
        String fileContent = accessCount.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("\n"));

        // Escreve no arquivo (sobrescreve se existir)
        Files.writeString(
                Path.of("access.log"),
                fileContent,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
    }
}
