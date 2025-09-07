package com.app.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.Authentication;

import com.app.dto.CardDTO;
import com.app.dto.CardNumberDTO;
import com.app.dto.FilterPageCardDTO;
import com.app.dto.NewCardDTO;
import com.app.dto.PaginatedResponse;
import com.app.dto.TransferBetweenCardsDTO;
import com.app.exception.ActivatedAlreadyException;
import com.app.exception.BlockedAlreadyException;
import com.app.exception.CardNotFoundException;
import com.app.exception.NotEnoughBalanceException;
import com.app.exception.NotYourCardException;
import com.app.exception.UserNotFoundException;
import com.app.model.Card;
import com.app.model.STATUS;
import com.app.repository.CardRepository;
import com.app.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CardService cardService;

    private Card testCard;
    private Card testCard2;
    private final String testUsername = "testuser";
    private final Integer testUserId = 1;

    @BeforeEach
    void setUp() {
        testCard = new Card();
        testCard.setId(1);
        testCard.setCardNumber("1234567890123456");
        testCard.setOwnerId(testUserId);
        testCard.setValidityPeriod(LocalDate.now().plusYears(1));
        testCard.setStatus(STATUS.ACTIVE);
        testCard.setBalance(1000.0);

        testCard2 = new Card();
        testCard2.setId(2);
        testCard2.setCardNumber("9876543210987654");
        testCard2.setOwnerId(testUserId);
        testCard2.setValidityPeriod(LocalDate.now().plusYears(1));
        testCard2.setStatus(STATUS.ACTIVE);
        testCard2.setBalance(500.0);
    }

    @Test
    void transferBetweenCards_ValidTransfer_UpdatesBalances() {
        TransferBetweenCardsDTO transfer = new TransferBetweenCardsDTO();
        transfer.setFirstCardNumber("1234567890123456");
        transfer.setSecondCardNumber("9876543210987654");
        transfer.setAmountTransferBetweenCards(200.0);

        when(authentication.getName()).thenReturn(testUsername);
        when(userRepo.findUserIdByUsername(testUsername)).thenReturn(Optional.of(testUserId));
        when(cardRepo.findByCardNumber("1234567890123456")).thenReturn(Optional.of(testCard));
        when(cardRepo.findByCardNumber("9876543210987654")).thenReturn(Optional.of(testCard2));

        cardService.transferBetweenCards(authentication, transfer);

        assertEquals(800.0, testCard.getBalance());
        assertEquals(700.0, testCard2.getBalance());
        verify(cardRepo, times(2)).save(any(Card.class));
    }

    @Test
    void transferBetweenCards_UserNotFound_ThrowsException() {
        TransferBetweenCardsDTO transfer = new TransferBetweenCardsDTO();
        when(authentication.getName()).thenReturn(testUsername);
        when(userRepo.findUserIdByUsername(testUsername)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> cardService.transferBetweenCards(authentication, transfer));
    }

    @Test
    void transferBetweenCards_FirstCardNotFound_ThrowsException() {
        TransferBetweenCardsDTO transfer = new TransferBetweenCardsDTO();
        transfer.setFirstCardNumber("1234567890123456");

        when(authentication.getName()).thenReturn(testUsername);
        when(userRepo.findUserIdByUsername(testUsername)).thenReturn(Optional.of(testUserId));
        when(cardRepo.findByCardNumber("1234567890123456")).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> cardService.transferBetweenCards(authentication, transfer));
    }

    @Test
    void transferBetweenCards_NotUsersCard_ThrowsException() {
        TransferBetweenCardsDTO transfer = new TransferBetweenCardsDTO();
        transfer.setFirstCardNumber("1234567890123456");
        testCard.setOwnerId(999);

        when(authentication.getName()).thenReturn(testUsername);
        when(userRepo.findUserIdByUsername(testUsername)).thenReturn(Optional.of(testUserId));
        when(cardRepo.findByCardNumber("1234567890123456")).thenReturn(Optional.of(testCard));

        assertThrows(NotYourCardException.class, () -> cardService.transferBetweenCards(authentication, transfer));
    }

    @Test
    void transferBetweenCards_NotEnoughBalance_ThrowsException() {
        TransferBetweenCardsDTO transfer = new TransferBetweenCardsDTO();
        transfer.setFirstCardNumber("1234567890123456");
        transfer.setSecondCardNumber("9876543210987654");
        transfer.setAmountTransferBetweenCards(2000.0);

        when(authentication.getName()).thenReturn(testUsername);
        when(userRepo.findUserIdByUsername(testUsername)).thenReturn(Optional.of(testUserId));
        when(cardRepo.findByCardNumber("1234567890123456")).thenReturn(Optional.of(testCard));

        assertThrows(NotEnoughBalanceException.class, () -> cardService.transferBetweenCards(authentication, transfer));
    }

    @Test
    void addNewCard_ValidCard_SavesCard() {
        NewCardDTO newCard = new NewCardDTO();
        newCard.setCardNumber("1234567890123456");
        newCard.setOwnerId(testUserId);
        newCard.setValidityPeriod(LocalDate.now().plusYears(1));

        cardService.addNewCard(newCard);

        verify(cardRepo).save(any(Card.class));
    }

    @Test
    void deleteCard_ValidCardNumber_DeletesCard() {
        CardNumberDTO cardNumber = new CardNumberDTO();
        cardNumber.setCardNumber("1234567890123456");

        cardService.deleteCard(cardNumber);

        verify(cardRepo).deleteByCardNumber("1234567890123456");
    }

    @Test
    void blockCard_ActiveCard_BlocksCard() {
        CardNumberDTO cardNumber = new CardNumberDTO();
        cardNumber.setCardNumber("1234567890123456");

        when(cardRepo.findByCardNumber("1234567890123456")).thenReturn(Optional.of(testCard));

        cardService.blockCard(cardNumber);

        assertEquals(STATUS.BLOCKED, testCard.getStatus());
        verify(cardRepo).save(testCard);
    }

    @Test
    void blockCard_AlreadyBlocked_ThrowsException() {
        CardNumberDTO cardNumber = new CardNumberDTO();
        cardNumber.setCardNumber("1234567890123456");
        testCard.setStatus(STATUS.BLOCKED);

        when(cardRepo.findByCardNumber("1234567890123456")).thenReturn(Optional.of(testCard));

        assertThrows(BlockedAlreadyException.class, () -> cardService.blockCard(cardNumber));
    }

    @Test
    void blockCard_CardNotFound_ThrowsException() {
        CardNumberDTO cardNumber = new CardNumberDTO();
        cardNumber.setCardNumber("1234567890123456");

        when(cardRepo.findByCardNumber("1234567890123456")).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> cardService.blockCard(cardNumber));
    }

    @Test
    void activateCard_BlockedCard_ActivatesCard() {
        CardNumberDTO cardNumber = new CardNumberDTO();
        cardNumber.setCardNumber("1234567890123456");
        testCard.setStatus(STATUS.BLOCKED);

        when(cardRepo.findByCardNumber("1234567890123456")).thenReturn(Optional.of(testCard));

        cardService.activateCard(cardNumber);

        assertEquals(STATUS.ACTIVE, testCard.getStatus());
        verify(cardRepo).save(testCard);
    }

    @Test
    void activateCard_AlreadyActive_ThrowsException() {
        CardNumberDTO cardNumber = new CardNumberDTO();
        cardNumber.setCardNumber("1234567890123456");

        when(cardRepo.findByCardNumber("1234567890123456")).thenReturn(Optional.of(testCard));

        assertThrows(ActivatedAlreadyException.class, () -> cardService.activateCard(cardNumber));
    }

    @Test
    void getPaginatedAllCardsAsDto_ReturnsPaginatedResponse() {
        FilterPageCardDTO filters = new FilterPageCardDTO();
        Page<Card> cardPage = new PageImpl<>(List.of(testCard));
        
        when(cardRepo.findByCriteria(any(), any(), any(), any(), any(), any(), any(), eq(true), any()))
            .thenReturn(cardPage);

        PaginatedResponse<CardDTO> response = cardService.getPaginatedAllCardsAsDto(filters);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals(0, response.getCurrentPage());
        assertEquals(1, response.getTotalPages());
    }

    @Test
    void getPaginatedAllUserCardsAsDto_ReturnsPaginatedResponse() {
        FilterPageCardDTO filters = new FilterPageCardDTO();
        Page<Card> cardPage = new PageImpl<>(List.of(testCard));
        
        when(cardRepo.findByCriteria(eq(testUsername), any(), any(), any(), any(), any(), any(), eq(false), any()))
            .thenReturn(cardPage);

        PaginatedResponse<CardDTO> response = cardService.getPaginatedAllUserCardsAsDto(filters, testUsername, true);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertTrue(response.getContent().get(0).getCardNumber().contains("*"));
    }

    @Test
    void getPaginatedAllUserCardsAsDto_UnmaskedCardNumber_ReturnsFullNumber() {
        FilterPageCardDTO filters = new FilterPageCardDTO();
        Page<Card> cardPage = new PageImpl<>(List.of(testCard));
        
        when(cardRepo.findByCriteria(eq(testUsername), any(), any(), any(), any(), any(), any(), eq(false), any()))
            .thenReturn(cardPage);

        PaginatedResponse<CardDTO> response = cardService.getPaginatedAllUserCardsAsDto(filters, testUsername, false);

        assertEquals("1234567890123456", response.getContent().get(0).getCardNumber());
    }
}
