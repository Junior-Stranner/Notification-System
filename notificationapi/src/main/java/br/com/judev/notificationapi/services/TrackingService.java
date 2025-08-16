package br.com.judev.notificationapi.services;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class TrackingService {
    // Mapa para armazenar contagem de acessos em memória
    // Chave: visitorId-IP | Valor: quantidade de acessos
    private final ConcurrentHashMap<String,Integer> accessCount = new ConcurrentHashMap <>();


    /**
     * Identifica o usuário através de cookie ou gera um novo ID
     * @param request Objeto HttpServletRequest para acessar cookies e IP
     * @param response Objeto HttpServletResponse para adicionar novos cookies
     * @return String no formato "visitorId-IP"
     */
    public String identifyVisitor(HttpServletRequest request, HttpServletResponse response) {
        // Verifica se existem cookies na requisição
        Cookie[] cookies = request.getCookies() != null ? request.getCookies() : new Cookie[0];

        // Verifica cookie existente
        String visitorId = Arrays.stream(cookies)
                .filter(c -> "visitorId".equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);

        // Se não encontrou o cookie, cria um novo
        if (visitorId == null) {
            visitorId = UUID.randomUUID().toString();
            Cookie cookie = new Cookie("visitorId", visitorId);
            cookie.setMaxAge(3600); // 1 hora
            cookie.setHttpOnly(true); // Protege contra acesso via JavaScript
            cookie.setPath("/"); // Disponível para todas as rotas
            cookie.setSecure(false); // Apenas para desenvolvimento local

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
