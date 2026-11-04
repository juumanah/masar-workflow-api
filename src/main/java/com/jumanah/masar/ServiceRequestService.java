package com.jumanah.masar;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ServiceRequestService {

    private final ServiceRequestRepository repository;
    private final UserRepository users;

    public ServiceRequestService(
            ServiceRequestRepository repository,
            UserRepository users) {
        this.repository = repository;
        this.users = users;
    }

    @Transactional
    public ServiceRequest createRequest(CreateServiceRequest input) {
        UserEntity user = currentUser();
        requireRole(user, UserRole.REQUESTER);

        ServiceRequestEntity entity = new ServiceRequestEntity(
                input.title(),
                input.description(),
                user
        );

        return toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<ServiceRequest> getAllRequests() {
        UserEntity user = currentUser();

        List<ServiceRequestEntity> requests =
                user.getRole() == UserRole.APPROVER
                        ? repository.findAll(Sort.by("id").ascending())
                        : repository.findAllByOwner_IdOrderByIdAsc(user.getId());

        return requests.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ServiceRequest getRequestById(Long id) {
        UserEntity user = currentUser();

        ServiceRequestEntity entity =
                user.getRole() == UserRole.APPROVER
                        ? repository.findById(id).orElse(null)
                        : repository.findByIdAndOwner_Id(id, user.getId())
                                .orElse(null);

        if (entity == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Request not found"
            );
        }

        return toResponse(entity);
    }

    @Transactional
    public ServiceRequest approveRequest(Long id) {
        requireRole(currentUser(), UserRole.APPROVER);

        ServiceRequestEntity entity = findRequest(id);
        entity.approve();
        return toResponse(entity);
    }

    @Transactional
    public ServiceRequest rejectRequest(
            Long id,
            RejectServiceRequest input) {
        requireRole(currentUser(), UserRole.APPROVER);

        ServiceRequestEntity entity = findRequest(id);
        entity.reject(input.reason());
        return toResponse(entity);
    }

    private UserEntity currentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Please sign in"
            );
        }

        return users.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "User not found"
                ));
    }

    private void requireRole(UserEntity user, UserRole requiredRole) {
        if (user.getRole() != requiredRole) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You do not have permission to perform this action"
            );
        }
    }

    private ServiceRequestEntity findRequest(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Request not found"
                ));
    }

    private ServiceRequest toResponse(ServiceRequestEntity entity) {
        return new ServiceRequest(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getDecidedAt(),
                entity.getRejectionReason()
        );
    }
}