package com.kamylo.Scrtly_backend.serviceTests;

import com.kamylo.Scrtly_backend.common.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.common.handler.CustomException;
import com.kamylo.Scrtly_backend.payment.service.impl.StripeCustomerServiceImpl;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.user.repository.UserRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.param.CustomerCreateParams;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StripeCustomerServiceImplTest {

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private StripeCustomerServiceImpl service;

    private UserEntity createUser(String email, String fullName, String stripeId) {
        UserEntity u = new UserEntity();
        u.setEmail(email);
        u.setFullName(fullName);
        u.setStripeCustomerId(stripeId);
        return u;
    }

    @Test
    void ensureCustomer_createsCustomer_whenStripeIdIsNull() {
        UserEntity user = createUser("u@example.com", "User Name", null);

        Customer created = mock(Customer.class);
        when(created.getId()).thenReturn("cus_test_123");

        try (MockedStatic<Customer> mocked = mockStatic(Customer.class)) {
            mocked.when(() -> Customer.create(any(CustomerCreateParams.class))).thenReturn(created);

            service.ensureCustomer(user);

            ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
            verify(userRepo).save(captor.capture());
            UserEntity saved = captor.getValue();
            assertEquals("cus_test_123", saved.getStripeCustomerId());
        }
    }

    @Test
    void ensureCustomer_noop_whenStripeIdAlreadyPresent() {
        UserEntity user = createUser("u2@example.com", "Another", "cus_existing");

        service.ensureCustomer(user);

        verifyNoInteractions(userRepo);
    }

    static class TestStripeException extends StripeException {
        public TestStripeException() {
            super("stripe error (test)", null, null, null);
        }
    }

    @Test
    void ensureCustomer_wrappsStripeExceptionInCustomException() {
        UserEntity user = createUser("u3@example.com", "Err User", null);

        try (MockedStatic<Customer> mocked = mockStatic(Customer.class)) {
            mocked.when(() -> Customer.create(any(CustomerCreateParams.class)))
                    .thenThrow(new TestStripeException());

            CustomException ex = assertThrows(CustomException.class, () -> service.ensureCustomer(user));
            assertEquals(BusinessErrorCodes.STRIPE_API_ERROR, ex.getErrorCode());

            verifyNoInteractions(userRepo);
        }
    }
}
