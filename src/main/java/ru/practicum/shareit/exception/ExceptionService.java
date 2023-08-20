package ru.practicum.shareit.exception;

import org.springframework.stereotype.Service;

@Service
public class ExceptionService {
    public void throwNotFound(String message) {
        throw new NotFoundException(message);
    }

    public void throwBadRequest(String message) {
        throw new BadRequestException(message);
    }

    public void throwConflict(String message) {
        throw new ConflictException(message);
    }

    public void throwForbidden(String message) {
        throw new ForbiddenException(message);
    }
}