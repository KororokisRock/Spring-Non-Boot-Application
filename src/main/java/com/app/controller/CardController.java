package com.app.controller;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.annotation.ValidateBindingResult;
import com.app.dto.CardDTO;
import com.app.dto.CardNumberDTO;
import com.app.dto.FilterPageCardDTO;
import com.app.dto.NewCardDTO;
import com.app.dto.PaginatedResponse;
import com.app.dto.TransferBetweenCardsDTO;
import com.app.service.CardService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/card")
public class CardController {
    @Autowired
    private CardService cardService;

    @GetMapping("/all")
    @ValidateBindingResult
    public ResponseEntity<?> getAllCards(@RequestBody @Valid FilterPageCardDTO filterPageCardDTO, BindingResult result) {
        PaginatedResponse<CardDTO> response = cardService.getPaginatedAllCardsAsDto(filterPageCardDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    @ValidateBindingResult
    public ResponseEntity<?> addNewCard(@RequestBody @Valid NewCardDTO newCardDTO, BindingResult result) {
        cardService.addNewCard(newCardDTO);
        return ResponseEntity.ok("Card added");
    }

    @PostMapping("/block")
    @ValidateBindingResult
    public ResponseEntity<?> blockCard(@RequestBody @Valid CardNumberDTO cardNumber, BindingResult result) {
        cardService.blockCard(cardNumber);
        return ResponseEntity.ok("Card blocked");
    }

    @PostMapping("/activate")
    @ValidateBindingResult
    public ResponseEntity<?> activeCard(@RequestBody @Valid CardNumberDTO cardNumber, BindingResult result) {
        cardService.activateCard(cardNumber);
        return ResponseEntity.ok("Card activated");
    }

    @PostMapping("/delete")
    @ValidateBindingResult
    public ResponseEntity<?> deleteCard(@RequestBody @Valid CardNumberDTO cardNumber, BindingResult result) {
        cardService.deleteCard(cardNumber);
        return ResponseEntity.ok("Card deleted");
    }

    @PostMapping("/show")
    @ValidateBindingResult
    public ResponseEntity<?> showCards(Authentication authentication, @RequestBody @Valid FilterPageCardDTO filterPageCardDTO, BindingResult result) {
        PaginatedResponse<CardDTO> response = cardService.getPaginatedAllUserCardsAsDto(filterPageCardDTO, authentication.getName(), true);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/show-full-number")
    @ValidateBindingResult
    public ResponseEntity<?> showFullNumberCards(Authentication authentication, @RequestBody @Valid FilterPageCardDTO filters) throws AuthenticationException {
        PaginatedResponse<CardDTO> response = cardService.getPaginatedAllUserCardsAsDto(filters, authentication.getName(), false);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/transfer")
    @ValidateBindingResult
    public ResponseEntity<?> transferCards(Authentication authentication, @RequestBody @Valid TransferBetweenCardsDTO transferBetweenCardsDTO, BindingResult result) {
        cardService.transferBetweenCards(authentication, transferBetweenCardsDTO);
        return ResponseEntity.ok("Transfer between cards completed successfully");
    }
}
