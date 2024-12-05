package ru.skillbox.repository.specification;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import ru.skillbox.dto.AccountSearchDto;
import ru.skillbox.entity.Account;
import ru.skillbox.security.SecurityUtils;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AccountSpecification {

    String BIRTH_DATE = "birthDate";
    String FIRST_NAME = "firstName";
    String LAST_NAME = "lastName";


    static Specification<Account> withFilter(AccountSearchDto asd) {
        return Specification.where(byIds(asd.getIds()))
                .and(byAuthor(asd.getAuthor()))
                .and(byFirstName(asd.getFirstName()))
                .and(byCity(asd.getCity()))
                .and(byCountry(asd.getCountry()))
                .and(byBlocked(asd.isBlocked()))
                .and(byDeleted(asd.isDeleted()))
                .and(byAge(asd.getAgeFrom(), asd.getAgeTo()))
                .and(excludeCurrentAccount())
        ;
    }

    static Specification<Account> byIds(List<UUID> ids) {
        return (root, query, cb) -> {
            if (ids == null || ids.isEmpty()) {
                return null;
            }

            return root.get("id").in(ids);
        };
    }

static Specification<Account> byAuthor(String author) {
    return (root, query, cb) -> {
        if (author == null || author.isEmpty()) {
            return null;
        }

        String[] parts = author.trim().toUpperCase().split("\\s+");

        if (parts.length == 1) {
            return cb.or(
                    cb.like(cb.upper(root.get(FIRST_NAME)), "%" + parts[0] + "%"),
                    cb.like(cb.upper(root.get(LAST_NAME)), "%" + parts[0] + "%")
            );
        }

        if (parts.length == 2) {
            return cb.or(
                    cb.and(
                            cb.like(cb.upper(root.get(FIRST_NAME)), "%" + parts[0] + "%"),
                            cb.like(cb.upper(root.get(LAST_NAME)), "%" + parts[1] + "%")
                    ),
                    cb.and(
                            cb.like(cb.upper(root.get(LAST_NAME)), "%" + parts[0] + "%"),
                            cb.like(cb.upper(root.get(FIRST_NAME)), "%" + parts[1] + "%")
                    )
            );
        }

        return null;
    };
}

    static Specification<Account> byFirstName(String firstName) {
        return (root, query, cb) -> {
            if (firstName == null || firstName.isEmpty()) {
                return null;
            }

            Predicate predicateOr = cb.or(
                    cb.like(root.get(FIRST_NAME), "%" + firstName.trim().toUpperCase() + "%"),
                    cb.like(root.get(LAST_NAME), "%" + firstName.trim().toUpperCase() + "%")
            );

            return firstName.split("\\s+").length == 1 ?
                    predicateOr :
                    byAuthor(firstName.toUpperCase()).toPredicate(root, query, cb);
        };
    }

    static Specification<Account> byCity(String city) {
        return (root, query, cb) -> {
            if (city == null || city.isEmpty()) {
                return null;
            }

            return cb.like(root.get("city"), "%" + city + "%");
        };
    }


    static Specification<Account> byCountry(String country) {
        return (root, query, cb) -> {
            if (country == null || country.isEmpty()) {
                return null;
            }

            return cb.like(root.get("country"), "%" + country + "%");
        };
    }

    static Specification<Account> byBlocked(Boolean isBlocked) {
        return (root, query, cb) -> {
            if (isBlocked == null) {
                return null;
            }

            return cb.equal(root.get("isBlocked"), isBlocked);
        };
    }

    static Specification<Account> byDeleted(Boolean isDeleted) {
        return (root, query, cb) -> {
            if (isDeleted == null) {
                return null;
            }

            return cb.equal(root.get("isDeleted"), isDeleted);
        };
    }

    static Specification<Account> byAge(Integer ageFrom, Integer ageTo) {
        return (root, query, cb) -> {
            if (ageFrom == null && ageTo == null) {
                return null;
            }

            LocalDate currentDate = LocalDate.now();
            LocalDate birthDateFrom = ageFrom != null ? currentDate.minusYears(ageFrom) : null;
            LocalDate birthDateTo = ageTo != null ? currentDate.minusYears(ageTo) : null;

            if (birthDateFrom != null && birthDateTo != null) {
                return cb.between(root.get(BIRTH_DATE),
                        Timestamp.valueOf(birthDateTo.atStartOfDay()),
                        Timestamp.valueOf(birthDateFrom.atStartOfDay()));
            }

            if (birthDateFrom != null) {
                return cb.lessThanOrEqualTo(root.get(BIRTH_DATE),
                        Timestamp.valueOf(birthDateFrom.atStartOfDay()));
            }

            return cb.greaterThanOrEqualTo(root.get(BIRTH_DATE),
                    Timestamp.valueOf(birthDateTo.atStartOfDay()));
        };
    }

    static Specification<Account> excludeCurrentAccount() {
        UUID currentAccountId = SecurityUtils.getUUIDFromSecurityContext();
        return (root, query, cb) -> cb.notEqual(root.get("id"), currentAccountId);
    }

}

