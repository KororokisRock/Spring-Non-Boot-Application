package com.app.controller;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import javax.naming.AuthenticationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.app.annotation.ValidateBindingResult;
import com.app.aspect.ValidationAspect;
import com.app.dto.CardDTO;
import com.app.dto.CardNumberDTO;
import com.app.dto.FilterPageCardDTO;
import com.app.dto.NewCardDTO;
import com.app.dto.PaginatedResponse;
import com.app.dto.TransferBetweenCardsDTO;
import com.app.exception.ValidationValueException;
import com.app.service.CardService;

@ExtendWith(MockitoExtension.class)
class CardControllerTest {

    @Mock
    private CardService cardService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CardController cardController;

    private CardController proxiedController;
    private ValidationAspect validationAspect;

    @BeforeEach
    void setUp() {
        validationAspect = new ValidationAspect();
        AspectJProxyFactory factory = new AspectJProxyFactory(cardController);
        factory.addAspect(validationAspect);
        proxiedController = factory.getProxy();
    }

    @Test
    void getAllCards_WithValidFilters_ShouldReturnPaginatedResponse() {
        FilterPageCardDTO filters = createValidFilterPageCardDTO();
        BindingResult bindingResult = new BeanPropertyBindingResult(filters, "filters");
        
        PaginatedResponse<CardDTO> expectedResponse = createPaginatedResponse();
        when(cardService.getPaginatedAllCardsAsDto(filters)).thenReturn(expectedResponse);

        ResponseEntity<?> response = proxiedController.getAllCards(filters, bindingResult);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        
        verify(cardService, times(1)).getPaginatedAllCardsAsDto(filters);
    }

    @Test
    void getAllCards_WithValidationErrors_ShouldThrowValidationValueException() {
        FilterPageCardDTO filters = new FilterPageCardDTO();
        BindingResult bindingResult = new BeanPropertyBindingResult(filters, "filters");
        bindingResult.addError(new FieldError("filters", "sortBy", "Invalid sort field"));

        ValidationValueException exception = assertThrows(
            ValidationValueException.class,
            () -> proxiedController.getAllCards(filters, bindingResult)
        );

        assertNotNull(exception.getValidationErrors());
        assertTrue(exception.getValidationErrors().containsKey("sortBy"));
        
        verify(cardService, never()).getPaginatedAllCardsAsDto(any());
    }

    @Test
    void addNewCard_WithValidData_ShouldReturnOkResponse() {
        NewCardDTO newCardDTO = createValidNewCardDTO();
        BindingResult bindingResult = new BeanPropertyBindingResult(newCardDTO, "newCardDTO");

        ResponseEntity<?> response = proxiedController.addNewCard(newCardDTO, bindingResult);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Card added", response.getBody());
        
        verify(cardService, times(1)).addNewCard(newCardDTO);
    }

    @Test
    void blockCard_WithValidCardNumber_ShouldReturnOkResponse() {
        CardNumberDTO cardNumberDTO = createValidCardNumberDTO();
        BindingResult bindingResult = new BeanPropertyBindingResult(cardNumberDTO, "cardNumber");

        ResponseEntity<?> response = proxiedController.blockCard(cardNumberDTO, bindingResult);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Card blocked", response.getBody());
        
        verify(cardService, times(1)).blockCard(cardNumberDTO);
    }

    @Test
    void activateCard_WithValidCardNumber_ShouldReturnOkResponse() {
        CardNumberDTO cardNumberDTO = createValidCardNumberDTO();
        BindingResult bindingResult = new BeanPropertyBindingResult(cardNumberDTO, "cardNumber");

        ResponseEntity<?> response = proxiedController.activeCard(cardNumberDTO, bindingResult);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Card activated", response.getBody());
        
        verify(cardService, times(1)).activateCard(cardNumberDTO);
    }

    @Test
    void deleteCard_WithValidCardNumber_ShouldReturnOkResponse() {
        CardNumberDTO cardNumberDTO = createValidCardNumberDTO();
        BindingResult bindingResult = new BeanPropertyBindingResult(cardNumberDTO, "cardNumber");

        ResponseEntity<?> response = proxiedController.deleteCard(cardNumberDTO, bindingResult);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Card deleted", response.getBody());
        
        verify(cardService, times(1)).deleteCard(cardNumberDTO);
    }

    @Test
    void showCards_WithValidFilters_ShouldReturnPaginatedResponse() {
        FilterPageCardDTO filters = createValidFilterPageCardDTO();
        BindingResult bindingResult = new BeanPropertyBindingResult(filters, "filters");
        
        PaginatedResponse<CardDTO> expectedResponse = createPaginatedResponse();
        when(cardService.getPaginatedAllUserCardsAsDto(filters, "testuser", true))
            .thenReturn(expectedResponse);
        
        when(authentication.getName()).thenReturn("testuser");

        ResponseEntity<?> response = proxiedController.showCards(authentication, filters, bindingResult);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        
        verify(cardService, times(1))
            .getPaginatedAllUserCardsAsDto(filters, "testuser", true);
        verify(authentication, times(1)).getName();
    }

    @Test
    void showFullNumberCards_WithValidFilters_ShouldReturnPaginatedResponse() throws AuthenticationException {
        FilterPageCardDTO filters = createValidFilterPageCardDTO();
        
        PaginatedResponse<CardDTO> expectedResponse = createPaginatedResponse();
        when(cardService.getPaginatedAllUserCardsAsDto(filters, "testuser", false))
            .thenReturn(expectedResponse);

        when(authentication.getName()).thenReturn("testuser");

        ResponseEntity<?> response = proxiedController.showFullNumberCards(authentication, filters);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        
        verify(cardService, times(1))
            .getPaginatedAllUserCardsAsDto(filters, "testuser", false);
        verify(authentication, times(1)).getName();
    }

    @Test
    void transferCards_WithValidTransfer_ShouldReturnOkResponse() {
        TransferBetweenCardsDTO transferDTO = createValidTransferDTO();
        BindingResult bindingResult = new BeanPropertyBindingResult(transferDTO, "transfer");

        Mockito.doAnswer(invocation -> {
            Authentication auth = invocation.getArgument(0);
            auth.getName();
            return null;
        }).when(cardService).transferBetweenCards(any(), any());

        when(authentication.getName()).thenReturn("testuser");

        ResponseEntity<?> response = proxiedController.transferCards(authentication, transferDTO, bindingResult);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Transfer between cards completed successfully", response.getBody());
        
        verify(cardService, times(1)).transferBetweenCards(authentication, transferDTO);
        verify(authentication, times(1)).getName();
    }

    @Test
    void transferCards_WithValidationErrors_ShouldThrowValidationValueException() {
        TransferBetweenCardsDTO transferDTO = new TransferBetweenCardsDTO();
        BindingResult bindingResult = new BeanPropertyBindingResult(transferDTO, "transfer");
        bindingResult.addError(new FieldError("transfer", "firstCardNumber", "Cardnumber is required"));

        ValidationValueException exception = assertThrows(
            ValidationValueException.class,
            () -> proxiedController.transferCards(authentication, transferDTO, bindingResult)
        );

        assertNotNull(exception.getValidationErrors());
        assertTrue(exception.getValidationErrors().containsKey("firstCardNumber"));
        
        verify(cardService, never()).transferBetweenCards(any(), any());
    }

    @Test
    void controllerMethods_ShouldBeAnnotatedWithValidateBindingResult() throws NoSuchMethodException {
        assertTrue(CardController.class.getMethod("getAllCards", FilterPageCardDTO.class, BindingResult.class)
            .isAnnotationPresent(ValidateBindingResult.class));
        
        assertTrue(CardController.class.getMethod("addNewCard", NewCardDTO.class, BindingResult.class)
            .isAnnotationPresent(ValidateBindingResult.class));
        
        assertTrue(CardController.class.getMethod("blockCard", CardNumberDTO.class, BindingResult.class)
            .isAnnotationPresent(ValidateBindingResult.class));
        
        assertTrue(CardController.class.getMethod("activeCard", CardNumberDTO.class, BindingResult.class)
            .isAnnotationPresent(ValidateBindingResult.class));
        
        assertTrue(CardController.class.getMethod("deleteCard", CardNumberDTO.class, BindingResult.class)
            .isAnnotationPresent(ValidateBindingResult.class));
        
        assertTrue(CardController.class.getMethod("showCards", Authentication.class, FilterPageCardDTO.class, BindingResult.class)
            .isAnnotationPresent(ValidateBindingResult.class));
        
        assertTrue(CardController.class.getMethod("transferCards", Authentication.class, TransferBetweenCardsDTO.class, BindingResult.class)
            .isAnnotationPresent(ValidateBindingResult.class));
    }

    private FilterPageCardDTO createValidFilterPageCardDTO() {
        FilterPageCardDTO filters = new FilterPageCardDTO();
        filters.setUsername("testuser");
        filters.setDirectionSort("asc");
        filters.setSortBy("id");
        filters.setPage(0);
        filters.setSize(10);
        filters.setCardNumber("1234567890123456");
        filters.setMinEndDate(LocalDate.now().plusYears(1));
        filters.setMaxEndDate(LocalDate.now().plusYears(5));
        filters.setMinBalance(0.0);
        filters.setMaxBalance(10000.0);
        return filters;
    }

    private NewCardDTO createValidNewCardDTO() {
        NewCardDTO newCardDTO = new NewCardDTO();
        newCardDTO.setCardNumber("1234567890123456");
        newCardDTO.setOwnerId(1);
        newCardDTO.setValidityPeriod(LocalDate.now().plusYears(3));
        return newCardDTO;
    }

    private CardNumberDTO createValidCardNumberDTO() {
        CardNumberDTO cardNumberDTO = new CardNumberDTO();
        cardNumberDTO.setCardNumber("1234567890123456");
        return cardNumberDTO;
    }

    private TransferBetweenCardsDTO createValidTransferDTO() {
        TransferBetweenCardsDTO transferDTO = new TransferBetweenCardsDTO();
        transferDTO.setFirstCardNumber("1234567890123456");
        transferDTO.setSecondCardNumber("9876543210987654");
        transferDTO.setAmountTransferBetweenCards(100.0);
        return transferDTO;
    }

    private PaginatedResponse<CardDTO> createPaginatedResponse() {
        List<CardDTO> content = Arrays.asList(
            createCardDTO(1, "************3456", 1, LocalDate.now().plusYears(2), 1000.0),
            createCardDTO(2, "************7890", 1, LocalDate.now().plusYears(3), 2000.0)
        );
        
        return new PaginatedResponse<>(
            content, 0, 1, 2L, 10, false, false
        );
    }

    private CardDTO createCardDTO(Integer id, String cardNumber, Integer ownerId, 
                                 LocalDate validityPeriod, double balance) {
        CardDTO cardDTO = new CardDTO();
        cardDTO.setId(id);
        cardDTO.setCardNumber(cardNumber);
        cardDTO.setOwnerId(ownerId);
        cardDTO.setValidityPeriod(validityPeriod);
        cardDTO.setBalance(balance);
        return cardDTO;
    }
}