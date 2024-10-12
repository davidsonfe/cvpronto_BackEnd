package com.cvpronto.controllers;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class PaymentController {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    // Armazenar estado de PDF gerado em um mapa para cada sessionId
    private final Map<String, Boolean> pdfGenerationStatus = new HashMap<>();
    // Mapa para armazenar a foto por sessionId
    private final Map<String, String> photoStorage = new HashMap<>();

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey; // Define a chave da API aqui
    }

    @PostMapping("/api/checkout")
    public Map<String, String> createCheckoutSession(@RequestBody Map<String, Object> data) throws StripeException {
        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:3000/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl("http://localhost:3000/cancel")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPrice("price_1Q79bv08o8UgTVqSChQBhQZY")  // Usando o Price ID criado no Stripe
                                .build())
                .build();

        Session session = Session.create(params);

        Map<String, String> responseData = new HashMap<>();
        responseData.put("sessionId", session.getId());

        return responseData;
    }

    @GetMapping("/api/verify-payment/{sessionId}")
    public Map<String, Object> verifyPayment(@PathVariable String sessionId) throws StripeException {
        Session session = Session.retrieve(sessionId);
        Map<String, Object> response = new HashMap<>();
        response.put("paymentStatus", session.getPaymentStatus());

        // Adiciona a foto ao retorno, se houver
        String photoUrl = photoStorage.get(sessionId);
        response.put("foto", photoUrl != null ? photoUrl : ""); // Retorna a foto ou uma string vazia

        return response;
    }

    // Novo endpoint para verificar se o PDF já foi gerado
    @GetMapping("/api/pdf-generated/{sessionId}")
    public Map<String, Boolean> checkPdfGenerated(@PathVariable String sessionId) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("pdfGenerated", pdfGenerationStatus.getOrDefault(sessionId, false));
        return response;
    }

    // Método para registrar que o PDF foi gerado
    @PostMapping("/api/mark-pdf-generated/{sessionId}")
    public void markPdfGenerated(@PathVariable String sessionId) {
        pdfGenerationStatus.put(sessionId, true);
    }

    //método para armazenar a foto
    @PostMapping("/api/store-photo/{sessionId}")
    public void storePhoto(@PathVariable String sessionId, @RequestBody String photoUrl) {
        photoStorage.put(sessionId, photoUrl); // Armazena a foto associada ao sessionId
    }
}
