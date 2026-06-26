package com.vttish.bookstore.clients.service.impl;

import com.vttish.bookstore.auth.entity.enums.Role;
import com.vttish.bookstore.auth.service.UserService;
import com.vttish.bookstore.clients.entity.Client;
import com.vttish.bookstore.clients.repository.ClientRepository;
import com.vttish.bookstore.common.events.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ClientEventListener {
    private final ClientRepository clientRepository;
    private final UserService userService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUserRegistered(UserRegisteredEvent event) {
        if (event.role() == Role.CLIENT) {
            clientRepository.save(new Client(userService.getRefById(event.userId())));
        }
    }
}
