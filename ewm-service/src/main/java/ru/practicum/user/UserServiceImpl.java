package ru.practicum.user;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.exception.EntityNotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Validated
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    private final UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {

        QUser qUser = QUser.user;

        JPAQuery<User> query = new JPAQuery<>(entityManager);
        int offset = from != null ? (from > 1 ? --from : from) : 0;
        long totalItems = userRepository.count() + 1;
        BooleanBuilder builder = new BooleanBuilder();

        if (ids != null) {
            BooleanExpression lookInIdSetOnly = qUser.id.in(ids);

            builder.and(lookInIdSetOnly);
            log.debug("query for users with ids in: {}  successfully constructed", ids);

        } else {
            log.debug("query for all users successfully constructed");

        }
        List<UserDto> userDtos = query
                .from(qUser)
                .where(builder)
                .limit(size != null ? size : totalItems)
                .offset(offset)
                .fetch()
                .stream()
                .map(UserMapper.INSTANCE::toDto)
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
        log.debug("query for  users successfully executed, result {}", userDtos);

        return userDtos;

    }

    @Override
    public UserDto registerUser(@Valid NewUserDto newUserRequest) {

        User user = userRepository.save(userMapper.fromNewToEntity(newUserRequest));
        log.debug("User with id: {}  successfully created", user.getId());

        return userMapper.toDto(user);
    }

    @Override
    public void delete(Long id) {

        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    "error while trying to delete user with id: " + id + "Reason: user " + "doesn't exist");
        }
        userRepository.deleteById(id);
        log.debug("user with id: {} successfully deleted", id);

    }

    @Override
    public User getUser(Long id) {

        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                "user with id:" + id + " " + "not found"));
    }

}
