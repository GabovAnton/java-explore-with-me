package ru.practicum.subscriptions;

import org.mapstruct.*;
import ru.practicum.user.UserService;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", uses = {UserService.class})
public interface UserSubscriptionMapper {

    @Mapping(source = "subscribedTo", target = "subscribedTo", qualifiedByName = "getUser")
    UserSubscription toEntity(UserSubscriptionDto userSubscriptionDTO);

    @Mapping(source = "subscribedToUserId", target = "subscribedTo", qualifiedByName = "getUser")
    UserSubscription fromNew(NewUserSubscriptionDto newUserSubscriptionDto);

    @Mapping(source = "subscribedTo.id", target = "subscribedTo")
    UserSubscriptionDto toDto(UserSubscription userSubscription);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "subscribedTo", target = "subscribedTo", qualifiedByName = "getUser")
    UserSubscription partialUpdate(UserSubscriptionDto userSubscriptionDTO,
            @MappingTarget UserSubscription userSubscription);

}