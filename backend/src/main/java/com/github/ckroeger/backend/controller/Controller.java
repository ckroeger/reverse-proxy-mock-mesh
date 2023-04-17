package com.github.ckroeger.backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@RestController
public class Controller {
   public Controller(@Value("${targetUrl}") String getTarget) {
      this.getTarget = getTarget;
   }

   private final String getTarget;

   private final HttpClient httpClient = HttpClient.newBuilder()
         .version(HttpClient.Version.HTTP_2)
         .build();

   @GetMapping("/datetime")
   public ResponseEntity<String> getDateTime() {
      final String message = LocalDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd.MM.yy HH:mm:ss"));
      return ResponseEntity.ok(message);
   }

   @GetMapping("/data")
   public ResponseEntity<String> getData() throws IOException, InterruptedException {
      final String response = sendGet(getTarget);
      return ResponseEntity.ok(String.format("Response from %s: %s", getTarget, response));
   }


   private String sendGet(String url) throws IOException, InterruptedException {

      HttpRequest request = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create(url))
            .setHeader("User-Agent", "Java 11 HttpClient Bot")
            .build();

      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      return response.body();

   }
}
